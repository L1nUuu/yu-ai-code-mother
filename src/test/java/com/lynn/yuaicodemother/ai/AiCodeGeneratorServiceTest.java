package com.lynn.yuaicodemother.ai;

import com.lynn.yuaicodemother.ai.model.HtmlCodeResult;
import com.lynn.yuaicodemother.ai.model.MultiFileCodeResult;
import com.lynn.yuaicodemother.core.CodeFileSaver;
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
        System.out.println(result.getHtmlCode());

    }

    @Test
    void generateMultiFileCode() {
        MultiFileCodeResult result = aiCodeGeneratorService.generateMultiFileCode("做个LYnn的留言板，不超过五十行");
        Assertions.assertNotNull( result);
    }

    @Test
    public void fileSaveTest(){

        HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode("做个LYnn的博客，不超过20行");
        Assertions.assertNotNull( htmlCodeResult);
        CodeFileSaver.saveHtmlCodeResult(htmlCodeResult); //保存 html代码文件
        MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generateMultiFileCode("做个LYnn的留言板，不超过五十行");
        Assertions.assertNotNull( multiFileCodeResult);
        CodeFileSaver.saveMultiFileCodeResult(multiFileCodeResult);// 保存 多文件代码文件
    }
}