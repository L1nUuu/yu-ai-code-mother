package com.lynn.yuaicodemother.core;

/**
 * ClassName: AiCodeGeneratorFacade
 * Description:
 *
 * @Author linz
 * @Creat 2025/9/25 16:03
 * @Version 1.00
 */

import com.lynn.yuaicodemother.ai.AiCodeGeneratorService;
import com.lynn.yuaicodemother.ai.model.HtmlCodeResult;
import com.lynn.yuaicodemother.ai.model.MultiFileCodeResult;
import com.lynn.yuaicodemother.exception.BusinessException;
import com.lynn.yuaicodemother.exception.ErrorCode;
import com.lynn.yuaicodemother.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.io.File;

/**
 * Ai 代码生成门面类，组合代码生成和保存功能
 */
@Service
public class AiCodeGeneratorFacade {
    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;

    /**
     * 统一入口：根据类型生成并保存代码
     * @param userMessage
     * @param codeGenTypeEnum
     * @return
     */
    public File generatorAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum){
        if (codeGenTypeEnum == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR,"生成类型不能为空");
        }
        switch (codeGenTypeEnum){
            case HTML:
                return generateAndSaveHtmlCode(userMessage);
            case MULTI_FILE:
                return generateAndSaveMultiFileCode(userMessage);
            default:
                String errorMessage = "不支持的生成类型" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR,errorMessage);
        }
    }

    /**
     * 生成 HTML 模式的代码并保存
     * @param userMessage 用户提示词
     * @return 保存的目录
     */
    private File generateAndSaveHtmlCode(String userMessage) {
        HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(userMessage);
        return CodeFileSaver.saveHtmlCodeResult(htmlCodeResult);
    }

    /**
     * 生成 多文件 模式的代码并保存
     * @param userMessage 用户提示词
     * @return 保存的目录
     */
    private File generateAndSaveMultiFileCode(String userMessage) {
        MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generateMultiFileCode(userMessage);
        return CodeFileSaver.saveMultiFileCodeResult(multiFileCodeResult);
    }

}
