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
import com.lynn.yuaicodemother.ai.AiCodeGeneratorServiceFactory;
import com.lynn.yuaicodemother.ai.model.HtmlCodeResult;
import com.lynn.yuaicodemother.ai.model.MultiFileCodeResult;
import com.lynn.yuaicodemother.core.parser.CodeParserExecutor;
import com.lynn.yuaicodemother.core.saver.CodeFileSaverExecutor;
import com.lynn.yuaicodemother.core.saver.CodeFileSaverTemplate;
import com.lynn.yuaicodemother.exception.BusinessException;
import com.lynn.yuaicodemother.exception.ErrorCode;
import com.lynn.yuaicodemother.model.entity.App;
import com.lynn.yuaicodemother.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * Ai 代码生成门面类，组合代码生成和保存功能
 */
@Service
@Slf4j
public class AiCodeGeneratorFacade {
    @Resource
    private AiCodeGeneratorServiceFactory aiCodeGeneratorServiceFactory;

    /**
     * 统一入口：根据类型生成并保存代码
     *
     * @param userMessage     用户提示词
     * @param codeGenTypeEnum 生成代码类型枚举
     * @param appId           应用ID
     * @return
     */
    public File generatorAndSaveCode(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "生成类型不能为空");
        }
        // 根据 appId 获取相应的 AI服务 实例
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
        return switch (codeGenTypeEnum) {
            case HTML -> {
                HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(userMessage);
                yield CodeFileSaverExecutor.executeSaveCode(htmlCodeResult, CodeGenTypeEnum.HTML, appId);
            }
            case MULTI_FILE -> {
                MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generateMultiFileCode(userMessage);
                yield CodeFileSaverExecutor.executeSaveCode(multiFileCodeResult, CodeGenTypeEnum.MULTI_FILE, appId);
            }
            default -> {
                String errorMessage = "不支持的生成类型" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
            }
        };
    }


    /**
     * 统一入口：根据类型生成并保存代码（流式）
     *
     * @param userMessage 用户提示词
     * @param codeGenTypeEnum 生成代码类型枚举
     * @param appId 应用ID
     * @return
     */
    public Flux<String> generatorAndSaveCodeStream(String userMessage, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        if (codeGenTypeEnum == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "生成类型不能为空");
        }
        // 根据 appId 获取相应的 AI服务 实例
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId);
        return switch (codeGenTypeEnum) {
            case HTML:
                Flux<String> htmlCodeStream = aiCodeGeneratorService.generateHtmlCodeStream(userMessage); //生成结果流
                yield processCodeStream(htmlCodeStream, CodeGenTypeEnum.HTML, appId); //处理结果流（解析保存）
            case MULTI_FILE:
                Flux<String> multiFileCodeStream = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);//生成结果流
                yield processCodeStream(multiFileCodeStream, CodeGenTypeEnum.MULTI_FILE, appId);//处理结果流（解析保存）
            default:
                String errorMessage = "不支持的生成类型" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
        };
    }


    /**
     * 通用流式代码处理
     *
     * @param codeStream      生成的代码流
     * @param codeGenTypeEnum 生成类型枚举
     * @return 处理后的结果流
     */
    private Flux<String> processCodeStream(Flux<String> codeStream, CodeGenTypeEnum codeGenTypeEnum, Long appId) {
        // 定义一个字符串拼接器，用于当流式返回所有的代码之后，再保存代码
        StringBuilder codebuilder = new StringBuilder();
        return codeStream
                .doOnNext(chunk -> {
                    // 实时收集代码片段
                    codebuilder.append(chunk);
                })
                .doOnComplete(() -> {
                    try {
                        // 在流完成时保存代码
                        String completeCode = codebuilder.toString();
                        //使用代码解析执行器解析代码
                        Object parsedResult = CodeParserExecutor.getCodeParser(completeCode, codeGenTypeEnum);
                        //使用代码保存执行器保存代码
                        File saveFir = CodeFileSaverExecutor.executeSaveCode(parsedResult, codeGenTypeEnum, appId);
                        log.info("保存的目录，路径为：" + saveFir.getAbsolutePath());
                    } catch (Exception e) {
                        log.error("保存失败：{}", e.getMessage());
                    }
                });
    }


    /**
     * 生成 HTML 模式的代码并保存
     *
     * @param userMessage 用户提示词
     * @return 保存的目录
     */
//    @Deprecated
//    private File generateAndSaveHtmlCode(String userMessage) {
//        HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(userMessage);
//        return CodeFileSaver.saveHtmlCodeResult(htmlCodeResult);
//    }

    /**
     * 生成 多文件 模式的代码并保存
     *
     * @param userMessage 用户提示词
     * @return 保存的目录
     */
//    @Deprecated
//    private File generateAndSaveMultiFileCode(String userMessage) {
//        MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generateMultiFileCode(userMessage);
//        return CodeFileSaver.saveMultiFileCodeResult(multiFileCodeResult);
//    }

    /**
     * 生成 HTML 模式的代码并保存（流式）
     *
     * @param userMessage 用户提示词
     * @return 保存的目录
     */
//    @Deprecated
//    private Flux<String> generateAndSaveHtmlCodeStream(String userMessage) {
//        Flux<String> result = aiCodeGeneratorService.generateHtmlCodeStream(userMessage);
//        // 定义一个字符串拼接器，用于当流式返回所有的代码之后，再保存代码
//        StringBuilder codebuilder = new StringBuilder();
//        return result
//                .doOnNext(chunk -> {
//                    // 实时收集代码片段
//                    codebuilder.append(chunk);
//                })
//                .doOnComplete(() -> {
//                    try {
//                        // 在流完成时保存代码
//                        String completeHtmlCode = codebuilder.toString();
//                        HtmlCodeResult htmlCodeResult = CodeParser.parseHtmlCode(completeHtmlCode);
//                        // 保存代码到文件
//                        File saveFir = CodeFileSaver.saveHtmlCodeResult(htmlCodeResult);
//                        log.info("保存的目录，路径为：" + saveFir.getAbsolutePath());
//                    } catch (Exception e) {
//                        log.error("保存失败：{}", e.getMessage());
//                    }
//                });
//    }
//
//    /**
//     * 生成 多文件 模式的代码并保存（流式）
//     *
//     * @param userMessage 用户提示词
//     * @return 保存的目录
//     */
//    @Deprecated
//    private Flux<String> generateAndSaveMultiFileCodeStream(String userMessage) {
//        Flux<String> result = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);
//        // 定义一个字符串拼接器，用于当流式返回所有的代码之后，再保存代码
//        StringBuilder codebuilder = new StringBuilder();
//        return result
//                .doOnNext(chunk -> {
//                    // 实时收集代码片段
//                    codebuilder.append(chunk);
//                })
//                .doOnComplete(() -> {
//                    try {
//                        // 在流完成时保存代码
//                        String completeHtmlCode = codebuilder.toString();
//                        HtmlCodeResult htmlCodeResult = CodeParser.parseHtmlCode(completeHtmlCode);
//                        // 保存代码到文件
//                        File saveFir = CodeFileSaver.saveHtmlCodeResult(htmlCodeResult);
//                        log.info("保存的目录，路径为：" + saveFir.getAbsolutePath());
//                    } catch (Exception e) {
//                        log.error("保存失败：{}", e.getMessage());
//                    }
//                });
//    }

}
