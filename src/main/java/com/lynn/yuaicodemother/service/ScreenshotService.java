package com.lynn.yuaicodemother.service;

import org.springframework.stereotype.Service;

/**
 * 截图服务
 */
public interface ScreenshotService {

    /**
     * 通用的截图服务，可以得到访问地址
     * @param webUrl 网址
     * @return
     */
    String generateAndUploadScreenshot(String webUrl);

    /**
     * 删除 COS 截图
     * @param screenshotUrl 截图地址
     */
    void deleteCosScreenshot(String screenshotUrl);
}
