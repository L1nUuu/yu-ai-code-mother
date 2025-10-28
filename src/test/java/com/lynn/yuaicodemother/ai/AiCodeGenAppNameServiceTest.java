package com.lynn.yuaicodemother.ai;

import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ClassName: AiCodeGenAppNameServiceTest
 * Description:
 *
 * @Author linz
 * @Creat 2025/10/28 11:33
 * @Version 1.00
 */
@Slf4j
@SpringBootTest
class AiCodeGenAppNameServiceTest {
    @Resource
    private AiCodeGenAppNameService aiCodeGenAppNameService;

    @Test
    void getAppName() {
        String prompt = "给我生成一个类似于4399的页面";
        String appName1 = aiCodeGenAppNameService.getAppName(prompt);
        log.info("prompt: {} -> {}", prompt, appName1);

        String prompt2 = "给我生成一个登录的页面";
        String appName2 = aiCodeGenAppNameService.getAppName(prompt2);
        log.info("prompt: {} -> {}", prompt2, appName2);

        String prompt3 = "搭建一个小型网上商店：含商品列表与详情、购物车与下单流程、订单页面、常见问题与售后支持、优惠券模块、SEO优化、响应式布局；商品图片需支持懒加载，整体风格简洁现代。";
        String appName3 = aiCodeGenAppNameService.getAppName(prompt3);
        log.info("prompt: {} -> {}", prompt3, appName3);
    }
}