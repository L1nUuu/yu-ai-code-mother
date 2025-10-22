package com.lynn.yuaicodemother.util;

import com.lynn.yuaicodemother.exception.ThrowUtils;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassName: WebScreenshotUtilsTest
 * Description:
 *
 * @Author linz
 * @Creat 2025/10/21 17:01
 * @Version 1.00
 */
@SpringBootTest
class WebScreenshotUtilsTest {
    @Resource
    private WebScreenshotUtils webScreenshotUtils;
    @Test
    public void webScreenshotTest(){
        String compressedUrl = webScreenshotUtils.saveWebPageScreenshot("https://www.4399.com/");
        Assertions.assertNotNull(compressedUrl);
    }
}