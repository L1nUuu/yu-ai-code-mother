package com.lynn.yuaicodemother.ai;

import com.lynn.yuaicodemother.ai.model.HtmlCodeResult;
import com.lynn.yuaicodemother.ai.model.MultiFileCodeResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassName: AiCodeGeneratorServiceTest
 * Description:
 *
 * @Author linz
 * @Creat 2025/9/21 21:57
 * @Version 1.00
 */
@SpringBootTest
class AiCodeGeneratorServiceTest {
    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;

    @Test
    void generateHtmlCode() {
        HtmlCodeResult result = aiCodeGeneratorService.generateHtmlCode("做个LYnn的博客，不超过20行");
        Assertions.assertNotNull( result);
    }

    @Test
    void generateMultiFileCode() {
        MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode("做个LYnn的留言板，不超过五十行");
        Assertions.assertNotNull( result);
    }
}