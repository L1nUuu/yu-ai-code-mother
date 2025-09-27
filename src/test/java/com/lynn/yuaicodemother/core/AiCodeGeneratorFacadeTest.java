package com.lynn.yuaicodemother.core;

import com.lynn.yuaicodemother.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassName: AiCodeGeneratorFacadeTest
 * Description:
 *
 * @Author linz
 * @Creat 2025/9/25 16:21
 * @Version 1.00
 */
@SpringBootTest
class AiCodeGeneratorFacadeTest {

    @Resource
    private AiCodeGeneratorFacade aiCodeGeneratorFacade;

    @Test
    void generatorAndSaveCode() {
        File file = aiCodeGeneratorFacade.generatorAndSaveCode("生成一个管理系统页面，不超过100行代码", CodeGenTypeEnum.MULTI_FILE);
        Assertions.assertNotNull(file);
    }

    @Test
    void generatorAndSaveCodeStream() {
        Flux<String> codeStream = aiCodeGeneratorFacade.generatorAndSaveCodeStream("生成登录页面，总共不超过40行代码", CodeGenTypeEnum.MULTI_FILE);
        // 阻塞等待所有数据收集完成
        List<String> result = codeStream.collectList().block();
        // 验证结果
        Assertions.assertNotNull(result);
        // 拼接字符串，得到完整内容
        String completeContent = String.join("", result);
        Assertions.assertNotNull(completeContent);
    }
}