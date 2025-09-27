package com.lynn.yuaicodemother.service;

import com.lynn.yuaicodemother.model.dto.app.AppQueryRequest;
import com.lynn.yuaicodemother.model.entity.App;
import com.lynn.yuaicodemother.model.vo.AppVO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import com.mybatisflex.core.service.IService;

import java.util.List;

/**
 * 应用 服务层。
 *
 * @author Linz
 */
public interface AppService extends IService<App> {
    
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