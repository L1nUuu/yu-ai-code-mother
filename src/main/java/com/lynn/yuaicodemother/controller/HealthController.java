package com.lynn.yuaicodemother.controller;

import com.lynn.yuaicodemother.common.BaseResponse;
import com.lynn.yuaicodemother.common.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * ClassName: HealthController
 * Description:
 *
 * @Author linz
 * @Creat 2025/9/12 14:44
 * @Version 1.00
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @GetMapping("/")
    public BaseResponse<String> healthCheck() {
        return ResultUtils.success("ok");
    }
}
