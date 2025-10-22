package com.lynn.yuaicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.lynn.yuaicodemother.constant.AppConstant;
import com.lynn.yuaicodemother.core.AiCodeGeneratorFacade;
import com.lynn.yuaicodemother.core.builder.VueProjectBuilder;
import com.lynn.yuaicodemother.core.handler.StreamHandlerExecutor;
import com.lynn.yuaicodemother.exception.BusinessException;
import com.lynn.yuaicodemother.exception.ErrorCode;
import com.lynn.yuaicodemother.exception.ThrowUtils;
import com.lynn.yuaicodemother.model.dto.app.AppQueryRequest;
import com.lynn.yuaicodemother.model.entity.App;
import com.lynn.yuaicodemother.model.entity.User;
import com.lynn.yuaicodemother.model.enums.ChatHistoryMessageTypeEnum;
import com.lynn.yuaicodemother.model.enums.CodeGenTypeEnum;
import com.lynn.yuaicodemother.model.vo.AppVO;
import com.lynn.yuaicodemother.model.vo.UserVO;
import com.lynn.yuaicodemother.service.AppService;
import com.lynn.yuaicodemother.mapper.AppMapper;
import com.lynn.yuaicodemother.service.ChatHistoryService;
import com.lynn.yuaicodemother.service.ScreenshotService;
import com.lynn.yuaicodemother.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 应用 服务层实现。
 *
 * @author Linz
 */
@Service
@Slf4j
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {
    @Resource
    private UserService userService;
    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;
    @Resource
    private ChatHistoryService chatHistoryService;
    @Resource
    private StreamHandlerExecutor streamHandlerExecutor;
    @Resource
    private VueProjectBuilder vueProjectBuilder;
    @Resource
    private ScreenshotService screenshotService;

    @Override
    public Flux<String> chatToGenCode(Long appId, String message, User loginUser) {
        // 1.参数校验
        ThrowUtils.throwIf(appId == null || appId <= 0, ErrorCode.PARAMS_ERROR, "应用ID错误");
        ThrowUtils.throwIf(StrUtil.isBlank(message), ErrorCode.PARAMS_ERROR, "提示词不能为空");
        // 2.查询应用信息
        App app = this.getById(appId);
        ThrowUtils.throwIf(app == null, ErrorCode.NOT_FOUND_ERROR, "应用不存在");
        // 3.权限校验，仅本人才能和应用对话
        if (!loginUser.getId().equals(app.getUserId())) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无权访问该应用");
        }
        // 4.获取应用的代码生成类型
        String codeGenType = app.getCodeGenType();
        // 5.生成代码
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenType);
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的代码生成类型");
        }
        // 6.在调用AI生成代码之前，先保存用户消息到数据库
        chatHistoryService.addChatMessage(appId, message, ChatHistoryMessageTypeEnum.USER.getValue(), loginUser.getId());
        // 7.调用AI生成代码（流式）
        Flux<String> contentFlux = aiCodeGeneratorFacade.generatorAndSaveCodeStream(message, codeGenTypeEnum, appId);
        // 8.收集AI响应的内容，并且在完成后保存记录到对话历史
        return streamHandlerExecutor.doExecute(contentFlux, chatHistoryService, appId, loginUser, codeGenTypeEnum);
    }

    @Override
    public String deployApp(Long appId, User loginUser) {
        // 1.参数校验
        if (appId == null || appId <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用ID错误");
        }
        if (loginUser == null) {
            throw new BusinessException(ErrorCode.NOT_LOGIN_ERROR);
        }
        // 2.查询应用信息
        App app = this.getById(appId);
        // 3.权限校验，只有本人才能部署应用
        Long loginUserId = loginUser.getId();
        ThrowUtils.throwIf(!app.getUserId().equals(loginUserId), ErrorCode.NO_AUTH_ERROR, "无权部署该应用");
        // 4.检查是否已有deployKey，如果没有则生成六位deployKey（字母加数字）
        String deployKey = app.getDeployKey();
        if (StrUtil.isBlank(deployKey)) {
            deployKey = RandomUtil.randomString(6);

        }
        // 5.获取代码类型，获取原始代码生成路径
        String codeGenType = app.getCodeGenType();
        String sourceDirName = codeGenType + "_" + appId;
        String sourceDirPath = AppConstant.CODE_OUTPUT_ROOT_DIR + File.separator + sourceDirName;
        File sourceDir = new File(sourceDirPath);
        // 6.检查路径是否存在
        if (!sourceDir.exists() || !sourceDir.isDirectory()) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用代码不存在，请先生成代码");
        }
        // 7. 如果是Vue 项目，则需要先构建项目，成功之后再复制dist文件夹到部署目录
        CodeGenTypeEnum codeGenTypeEnum = CodeGenTypeEnum.getEnumByValue(codeGenType);
        if (codeGenTypeEnum == CodeGenTypeEnum.VUE_PROJECT) {
            // 7.1 构建项目
            boolean buildResult = vueProjectBuilder.buildProject(sourceDirPath);
            if (!buildResult) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Vue 构建失败，请重试");
            }
            // 7.2 检查 dist 目录是否存在
            String distDirPath = sourceDirPath + File.separator + "dist";
            File distDir = new File(distDirPath);
            if (!distDir.exists() || !distDir.isDirectory()) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Vue 项目构建完成但未生成dist目录，请重试");
            }
            // 7.3 复制dist文件夹到部署目录
            sourceDir = distDir;
            log.info("Vue 项目构建成功，将部署 dist 目录：{}", distDir.getAbsolutePath());
        }
        // 8.复制文件到部署目录
        String deployDirPath = AppConstant.CODE_DEPLOY_ROOT_DIR + File.separator + deployKey;
        try {
            FileUtil.copyContent(sourceDir, new File(deployDirPath), true);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用部署失败" + e.getMessage());
        }
        // 9.更新数据库
        app.setDeployKey(deployKey); //设置deployKey
        app.setDeployedTime(LocalDateTime.now()); //设置部署时间
        boolean updateResult = this.updateById(app);
        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "更新应用部署信息失败");
        // 10.返回可访问的URL地址
        String appDeployUrl = AppConstant.CODE_DEPLOY_HOST + "/" + deployKey;
        // 11. 异步生成截图并且更新应用封面
        generateAppScreenshotAsync(appId, appDeployUrl);

        return appDeployUrl;
    }

    @Override
    public void generateAppScreenshotAsync(Long appId, String appDeployUrl) {
        // 启用虚拟线程并执行
        Thread.ofVirtual()
                .name("screenshotVirtualThread-" + System.currentTimeMillis())
                .start(() -> {
                    try {
                        // 调用截图服务生成截图并上传
                        String screenshotUrl = screenshotService.generateAndUploadScreenshot(appDeployUrl);
                        // 更新数据库的封面
                        App app = new App();
                        app.setId(appId);
                        app.setCover(screenshotUrl);
                        boolean updateResult = this.updateById(app);
                        // 这里抛出的异常也会被下面的 catch 捕获
                        ThrowUtils.throwIf(!updateResult, ErrorCode.OPERATION_ERROR, "更新应用封面为截图失败");

                        log.info("异步更新应用封面成功, appId: {}", appId);

                    } catch (Exception e) {
                        log.error("异步更新应用封面失败, appId: {}, url: {}", appId, appDeployUrl, e);
                        // TODO: 在这里可以添加重试逻辑，或者将失败任务记录到数据库
                    }
                });
    }

    @Override
    public void validApp(App app, boolean add) {
        if (app == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        String appName = app.getAppName();
        String initPrompt = app.getInitPrompt();

        // 创建时，参数不能为空
        if (add) {
            if (StrUtil.hasBlank(appName, initPrompt)) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR);
            }
        } else {
            if (appName != null && appName.isEmpty()) {
                throw new BusinessException(ErrorCode.PARAMS_ERROR, "应用名称不能为空");
            }
        }
    }

    @Override
    public AppVO getAppVO(App app) {
        if (app == null) {
            return null;
        }
        AppVO appVO = new AppVO();
        BeanUtil.copyProperties(app, appVO);
        // 关联查询用户信息
        Long userId = app.getUserId();
        if (userId != null) {
            User user = userService.getById(userId);
            UserVO userVO = userService.getUserVO(user);
            appVO.setUser(userVO);
        }
        return appVO;
    }


    // appVO里有userVO，根据appList的每一个userId获取userVO并封装到里面
    @Override
    public List<AppVO> getAppVOList(List<App> appList) {
        if (CollUtil.isEmpty(appList)) {
            return new ArrayList<>();
        }
        // 批量获取用户信息，避免 N+1 查询问题
        Set<Long> userIds = appList.stream()
                .map(App::getUserId)
                .collect(Collectors.toSet());
        Map<Long, UserVO> userVOMap = userService.listByIds(userIds).stream()
                .collect(Collectors.toMap(User::getId, userService::getUserVO));
        return appList.stream().map(app -> {
            AppVO appVO = getAppVO(app);
            UserVO userVO = userVOMap.get(app.getUserId());
            appVO.setUser(userVO);
            return appVO;
        }).collect(Collectors.toList());
    }


    @Override
    public QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "请求参数为空");
        }
        Long id = appQueryRequest.getId();
        String appName = appQueryRequest.getAppName();
        String cover = appQueryRequest.getCover();
        String initPrompt = appQueryRequest.getInitPrompt();
        String codeGenType = appQueryRequest.getCodeGenType();
        String deployKey = appQueryRequest.getDeployKey();
        Integer priority = appQueryRequest.getPriority();
        Long userId = appQueryRequest.getUserId();
        String sortField = appQueryRequest.getSortField();
        String sortOrder = appQueryRequest.getSortOrder();
        return QueryWrapper.create()
                .eq("id", id)
                .like("appName", appName)
                .like("cover", cover)
                .like("initPrompt", initPrompt)
                .eq("codeGenType", codeGenType)
                .eq("deployKey", deployKey)
                .eq("priority", priority)
                .eq("userId", userId)
                .orderBy(sortField, "ascend".equals(sortOrder));
    }


    @Override
    public Page<App> pageMyApp(AppQueryRequest appQueryRequest, Long userId) {
        if (appQueryRequest == null || userId == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long pageNum = appQueryRequest.getPageNum();
        long pageSize = appQueryRequest.getPageSize();
        if (pageSize > 20) {
            pageSize = 20;
        }

        QueryWrapper queryWrapper = this.getQueryWrapper(appQueryRequest);
        // 只查询自己的应用
        queryWrapper.eq("userId", userId);

        return this.page(new Page<>(pageNum, pageSize), queryWrapper);
    }

    @Override
    public Page<App> pageFeaturedApp(AppQueryRequest appQueryRequest) {
        if (appQueryRequest == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }

        long pageNum = appQueryRequest.getPageNum();
        long pageSize = appQueryRequest.getPageSize();
        if (pageSize > 20) {
            pageSize = 20;
        }

        QueryWrapper queryWrapper = this.getQueryWrapper(appQueryRequest);
        // 精选应用：优先级大于0的应用
        queryWrapper.gt("priority", 0);
        // 按优先级降序排列
        queryWrapper.orderBy("priority", false);

        return this.page(new Page<>(pageNum, pageSize), queryWrapper);
    }


    /**
     * 删除应用时，关联删除对话历史
     *
     * @param id 应用id
     * @return 删除应用结果
     */
    @Override
    public boolean removeById(Serializable id) {
        if (id == null) {
            return false;
        }
        long appId = Long.parseLong(id.toString());
        if (appId <= 0) {
            return false;
        }
        try {
            chatHistoryService.deleteByAppId(appId);
        } catch (Exception e) {
            log.error("删除应用关联的对话历史失败：{}", e.getMessage());
        }
        // 删除应用
        return super.removeById(id);
    }
}