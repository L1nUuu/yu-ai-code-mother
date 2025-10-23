package com.lynn.yuaicodemother.manager;

import com.lynn.yuaicodemother.config.CosClientConfig;
import com.lynn.yuaicodemother.exception.ErrorCode;
import com.lynn.yuaicodemother.exception.ThrowUtils;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.model.PutObjectResult;
import dev.langchain4j.agent.tool.P;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * ClassName: CosManager
 * Description: COS 对象存储管理器
 *
 * @Author linz
 * @Creat 2025/10/21 20:22
 * @Version 1.00
 */
@Component
@Slf4j
public class CosManager {
    @Resource
    private CosClientConfig cosClientConfig;

    @Resource
    private COSClient cosClient;

    /**
     * 上传对象
     *
     * @param key  唯一键
     * @param file 文件
     * @return 上传结果
     */
    public PutObjectResult putObject(String key, File file) {
        PutObjectRequest putObjectRequest = new PutObjectRequest(cosClientConfig.getBucket(), key, file);
        PutObjectResult putObjectResult = cosClient.putObject(putObjectRequest);
        return putObjectResult;
    }

    /**
     * 删除对象
     *
     * @param cosUrl 唯一键
     * @return 删除结果
     */
    public String deleteFile(String cosUrl) {
        String cosKey = getCosKeyFromCosUrl(cosUrl);
        ThrowUtils.throwIf(cosKey == null, ErrorCode.SYSTEM_ERROR, "COS 唯一键解析失败");
        cosClient.deleteObject(cosClientConfig.getBucket(), cosKey);
        log.info("cos截图删除成功：{}", cosKey);
        return "删除成功";
    }


    /**
     * 从一个完整的 cosUrl 中获取 COS 唯一键 (即 URL 路径)
     * @param cosUrl 完整的 COS URL
     * @return COS 唯一键 (例如: /screenshot/2025/10/22/fileName.jpg)
     */
    private String getCosKeyFromCosUrl(String cosUrl) {
        try {
            // 使用 java.net.URL 类来解析 URL
            URL url = new URL(cosUrl);
            // .getPath() 方法会返回 URL 中主机名之后的部分
            return url.getPath();
        } catch (MalformedURLException e) {
            // 处理 URL 格式错误的异常
            e.printStackTrace();
            // 或者抛出一个运行时异常
            // throw new RuntimeException("无效的 URL: " + cosUrl, e);
            return null; // 或者返回空字符串，取决于你的错误处理逻辑
        }
    }
    public String uploadFile(String key, File file) {
        PutObjectResult result = putObject(key, file);
        if (result != null) {
            String url = String.format("%s%s", cosClientConfig.getHost(), key);
            log.info("文件上传到 COS 成功：{} -> {}", file.getName(), url);
            return url;
        } else {
            log.error("文件上传到 COS 失败：{}", file.getName());
            return null;
        }
    }
}
