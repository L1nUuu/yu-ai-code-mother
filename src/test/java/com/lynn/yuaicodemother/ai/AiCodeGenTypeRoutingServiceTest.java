package com.lynn.yuaicodemother.ai;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassName: AiCodeGenTypeRoutingServiceTest
 * Description:
 *
 * @Author linz
 * @Creat 2025/10/28 10:42
 * @Version 1.00
 */
@Slf4j
@SpringBootTest
class AiCodeGenTypeRoutingServiceTest {

    @Resource
    private AiCodeGenTypeRoutingService aiCodeGenTypeRoutingService;
    @Test
    void routeCodeGenType() {
        String codeGenTypeValue1 = aiCodeGenTypeRoutingService.routeCodeGenType("做一个登录页面").getValue();
        log.info("codeGenTypeValue1: {}", codeGenTypeValue1);
        String codeGenTypeValue2 = aiCodeGenTypeRoutingService.routeCodeGenType("做一个电商系统").getValue();
        log.info("codeGenTypeValue2: {}", codeGenTypeValue2);
        String codeGenTypeValue3 = aiCodeGenTypeRoutingService.routeCodeGenType("做一个公司官网，需要首页，关于我们，联系我们三个页面").getValue();
        log.info("codeGenTypeValue3: {}", codeGenTypeValue3);
    }
}