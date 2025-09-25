package com.lynn.yuaicodemother.core;

import com.lynn.yuaicodemother.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;

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
}