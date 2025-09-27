package com.lynn.yuaicodemother.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.lynn.yuaicodemother.exception.BusinessException;
import com.lynn.yuaicodemother.exception.ErrorCode;
import com.lynn.yuaicodemother.model.dto.app.AppQueryRequest;
import com.lynn.yuaicodemother.model.entity.App;
import com.lynn.yuaicodemother.model.entity.User;
import com.lynn.yuaicodemother.model.enums.UserRoleEnum;
import com.lynn.yuaicodemother.model.vo.AppVO;
import com.lynn.yuaicodemother.model.vo.UserVO;
import com.lynn.yuaicodemother.service.AppService;
import com.lynn.yuaicodemother.mapper.AppMapper;
import com.lynn.yuaicodemother.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.spring.service.impl.ServiceImpl;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

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
public class AppServiceImpl extends ServiceImpl<AppMapper, App> implements AppService {
    @Resource
    private UserService userService;
    
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
}