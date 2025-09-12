package com.lynn.yuaicodemother.common;

import lombok.Data;

/**
 * ClassName: PageRequest
 * Description: 分页请求封装类（默认为降序）
 *
 * @Author linz
 * @Creat 2025/9/12 15:02
 * @Version 1.00
 */

/**
 * 这是一个分页请求参数封装类的例子，常用于后端接口接收分页和排序参数。比如，前端请求用户列表时，可以传递如下 JSON：
 * {
 *  "pageNum": 2,
 *  "pageSize": 20,
 *  "sortField": "createdAt",
 *  "sortOrder": "ascend"
 *  }
 *  这样后端就能根据这些参数返回对应页码、每页大小、排序字段和排序顺序的数据。
 */
@Data
public class PageRequest {

    /**
     * 当前页号
     */
    private int pageNum = 1;

    /**
     * 页面大小
     */
    private int pageSize = 10;

    /**
     * 排序字段
     */
    private String sortField;

    /**
     * 排序顺序（默认降序）
     */
    private String sortOrder = "descend";
}
