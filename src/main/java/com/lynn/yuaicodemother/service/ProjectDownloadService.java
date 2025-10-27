package com.lynn.yuaicodemother.service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * ClassName: ProjectDownloadService
 * Description:
 *
 * @Author linz
 * @Creat 2025/10/23 16:06
 * @Version 1.00
 */
public interface ProjectDownloadService {

    /**
     * 下载项目为 ZIP
     * @param projectPath 项目路径
     * @param downloadFileName 下载文件名
     * @param response HTTP 响应
     */
    void downloadProjectAsZip(String projectPath, String downloadFileName, HttpServletResponse response);
}
