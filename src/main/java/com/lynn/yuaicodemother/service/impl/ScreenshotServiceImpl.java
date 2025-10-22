package com.lynn.yuaicodemother.service.impl;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.lynn.yuaicodemother.exception.BusinessException;
import com.lynn.yuaicodemother.exception.ErrorCode;
import com.lynn.yuaicodemother.exception.ThrowUtils;
import com.lynn.yuaicodemother.manager.CosManager;
import com.lynn.yuaicodemother.service.ScreenshotService;
import com.lynn.yuaicodemother.util.WebScreenshotUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.Date;
import java.util.UUID;

/**
 * ClassName: ScreenshotServiceImpl
 * Description:
 *
 * @Author linz
 * @Creat 2025/10/21 20:39
 * @Version 1.00
 */
@Slf4j
@Service
public class ScreenshotServiceImpl implements ScreenshotService {

    @Resource
    private WebScreenshotUtils webScreenshotUtils;

    @Resource
    private CosManager cosManager;

    @Override
    public String generateAndUploadScreenshot(String webUrl) {
        // 参数校验
        ThrowUtils.throwIf(StrUtil.isBlank(webUrl), ErrorCode.PARAMS_ERROR, "网页地址不能为空");
        // 本地截图
        log.info("开始生成网页截图: {}", webUrl);
        String compressedImagePath = webScreenshotUtils.saveWebPageScreenshot(webUrl);
        ThrowUtils.throwIf(compressedImagePath == null, ErrorCode.SYSTEM_ERROR, "生成网页截图失败");
        // 上传图片到 COS
        try {
            String cosUrl = uploadScreenshotToCos(compressedImagePath);
            ThrowUtils.throwIf(cosUrl == null, ErrorCode.SYSTEM_ERROR, "上传图片到 COS 失败");
            log.info("截图上传成功，COS URL: {}", cosUrl);
            return cosUrl;
        } catch (Exception e){
            // [关键]：捕获所有上传异常(IOException, SdkException...)
            log.error("上传图片到 COS 失败, 本地路径: {}", compressedImagePath, e);
            // "转译" 为你自己的业务异常
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "上传图片到 COS 失败");
        } finally {
            // 清理本地文件
            if (StrUtil.isNotBlank(compressedImagePath)){
                cleanupLocalFile(compressedImagePath);
            }
        }
    }

    private String uploadScreenshotToCos(String localScreenshotPath){
        if (StrUtil.isBlank(localScreenshotPath)){
            return null;
        }
        File screenshotFile = new File(localScreenshotPath);
        if (!screenshotFile.exists()){
            log.error("截图文件不存在: {}", localScreenshotPath);
            return null;
        }
        // 生成 COS 对象键
        String fileName = UUID.randomUUID().toString().substring(0, 8) + "_compressed.jpg";
        String cosKey = generateScreenshotKey(fileName);
        return cosManager.uploadFile(cosKey, screenshotFile);
    }


    /**
     * 生成截图的对象存储键
     * 格式：/screenshot/2025/10/21/fileName.jpg
     * @param fileName 文件名
     * @return 对象存储键
     */
    private String generateScreenshotKey(String fileName) {
        String datePath = DateUtil.format(new Date(), "yyyy/MM/dd");
        return String.format("/screenshot/%s/%s", datePath, fileName);
    }

    /**
     * 清理本地文件
     * @param localFilePath 本地文件路径
     */
    private void cleanupLocalFile(String localFilePath) {
        File screenshotFile = new File(localFilePath);
        if (screenshotFile.exists()) {
            FileUtil.del(screenshotFile);
            log.info("清理本地文件成功：{}", localFilePath);
        }
    }
}
