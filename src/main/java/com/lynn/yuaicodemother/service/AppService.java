package com.lynn.yuaicodemother.service;

import com.lynn.yuaicodemother.model.dto.app.AppQueryRequest;
import com.lynn.yuaicodemother.model.entity.App;
import com.lynn.yuaicodemother.model.entity.User;
import com.lynn.yuaicodemother.model.vo.AppVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author Linz
 */
public interface AppService extends IService<App> {

    /**
     * 通过对话生成代码
     * @param appId 应用ID
     * @param message 对话消息
     * @param loginUser 登录用户
     * @return 生成的代码流
     */
    Flux<String> chatToGenCode(Long appId, String message, User loginUser);

    /**
     * 应用部署
     * @param appId 应用ID
     * @param loginUser 登录用户
     * @return 可访问的部署地址(链接)
     */
    String deployApp(Long appId, User loginUser);

    /**
     * 异步生成应用截图并更新到数据库
     * @param appId 应用ID
     * @param appDeployUrl 应用部署地址
     */
    void generateAppScreenshotAsync(Long appId, String appDeployUrl);

    /**
     * 校验应用数据
     *
     * @param app 应用数据
     * @param add 是否为新增操作
     */
    void validApp(App app, boolean add);
    
    /**
     * 获取应用视图对象
     *
     * @param app 应用实体
     * @return 应用视图对象
     */
    AppVO getAppVO(App app);
    
    /**
     * 获取应用视图对象列表
     *
     * @param appList 应用实体列表
     * @return 应用视图对象列表
     */
    List<AppVO> getAppVOList(List<App> appList);
    
    /**
     * 获取查询条件
     *
     * @param appQueryRequest 查询请求参数
     * @return 查询条件
     */
    QueryWrapper getQueryWrapper(AppQueryRequest appQueryRequest);
    
    /**
     * 分页查询应用（普通用户）
     *
     * @param appQueryRequest 查询请求参数
     * @param userId 用户ID
     * @return 应用分页结果
     */
    Page<App> pageMyApp(AppQueryRequest appQueryRequest, Long userId);
    
    /**
     * 分页查询精选应用（普通用户）
     *
     * @param appQueryRequest 查询请求参数
     * @return 应用分页结果
     */
    Page<App> pageFeaturedApp(AppQueryRequest appQueryRequest);
}