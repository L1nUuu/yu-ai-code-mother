package com.lynn.yuaicodemother.core;

/**
 * ClassName: AiCodeGeneratorFacade
 * Description:
 *
 * @Author linz
 * @Creat 2025/9/25 16:03
 * @Version 1.00
 */

import cn.hutool.json.JSONUtil;
import com.lynn.yuaicodemother.ai.AiCodeGeneratorService;
import com.lynn.yuaicodemother.ai.AiCodeGeneratorServiceFactory;
import com.lynn.yuaicodemother.ai.model.HtmlCodeResult;
import com.lynn.yuaicodemother.ai.model.MultiFileCodeResult;
import com.lynn.yuaicodemother.ai.model.message.AiResponseMessage;
import com.lynn.yuaicodemother.ai.model.message.ToolExecutedMessage;
import com.lynn.yuaicodemother.ai.model.message.ToolRequestMessage;
import com.lynn.yuaicodemother.core.parser.CodeParserExecutor;
import com.lynn.yuaicodemother.core.saver.CodeFileSaverExecutor;
import com.lynn.yuaicodemother.core.saver.CodeFileSaverTemplate;
import com.lynn.yuaicodemother.exception.BusinessException;
import com.lynn.yuaicodemother.exception.ErrorCode;
import com.lynn.yuaicodemother.model.entity.App;
import com.lynn.yuaicodemother.model.enums.CodeGenTypeEnum;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.service.TokenStream;
import dev.langchain4j.service.tool.ToolExecution;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.concurrent.CompletableFuture;

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
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId,codeGenTypeEnum);
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
        AiCodeGeneratorService aiCodeGeneratorService = aiCodeGeneratorServiceFactory.getAiCodeGeneratorService(appId, codeGenTypeEnum);
        return switch (codeGenTypeEnum) {
            case HTML:
                Flux<String> htmlCodeStream = aiCodeGeneratorService.generateHtmlCodeStream(userMessage); //生成结果流
                yield processCodeStream(htmlCodeStream, CodeGenTypeEnum.HTML, appId); //处理结果流（解析保存）
            case MULTI_FILE:
                Flux<String> multiFileCodeStream = aiCodeGeneratorService.generateMultiFileCodeStream(userMessage);//生成结果流
                yield processCodeStream(multiFileCodeStream, CodeGenTypeEnum.MULTI_FILE, appId);//处理结果流（解析保存）
            case VUE_PROJECT:
                TokenStream tokenStream = aiCodeGeneratorService.generateVueProjectFileCode(appId, userMessage);//生成结果流
                yield processTokenStream(tokenStream);
            default:
                String errorMessage = "不支持的生成类型" + codeGenTypeEnum.getValue();
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, errorMessage);
        };
    }

    /**
     * 将 TokenStream 转换为 Flux<String>，并传递工具调用信息
     * 核心作用：监听 TokenStream 发射的各类事件（部分响应、工具调用请求等），
     * 转换为统一的 JSON 字符串格式并通过响应式流（Flux）输出，适配响应式编程场景
     *
     * @param tokenStream 原始的流式数据源，会发射多种类型的事件（部分响应、工具调用等）
     * @return Flux<String> 包含各类消息的响应式流，每个元素为 JSON 格式字符串
     */
    private Flux<String> processTokenStream(TokenStream tokenStream) {

        // 使用 Flux.create 创建响应式流，通过 sink 控制流的元素发射、完成和错误
        return Flux.create(sink -> {
            tokenStream
                    // 1. 监听"部分响应"事件：当 TokenStream 产生部分响应（如 AI 实时生成的片段）时触发
                    .onPartialResponse((String partialResponse) -> {
                        try {
                            // 将原始字符串包装为 AiResponseMessage 对象（标记为 AI 响应类型）
                            AiResponseMessage aiResponseMessage = new AiResponseMessage(partialResponse);
                            // 序列化为 JSON 字符串并发送到 Flux 流中（前端可实时展示）
                            sink.next(JSONUtil.toJsonStr(aiResponseMessage));
                        } catch (Exception e) {
                            // 捕获序列化异常，通过 sink 传递错误，避免单个消息失败导致整个流中断
                            sink.error(new RuntimeException("序列化 AI 部分响应失败", e));
                        }
                    })

                    // 2. 监听"工具调用请求"事件：当需要调用外部工具（如文件保存）时触发
                    .onPartialToolExecutionRequest((index, toolExecutionRequest) -> {
                        try {
                            // 包装为工具请求消息对象（标记为工具调用类型）
                            ToolRequestMessage toolRequestMessage = new ToolRequestMessage(toolExecutionRequest);
                            // 序列化为 JSON 发送（前端可根据此消息触发实际工具调用）
                            sink.next(JSONUtil.toJsonStr(toolRequestMessage));
                        } catch (Exception e) {
                            sink.error(new RuntimeException("序列化工具调用请求失败", e));
                        }
                    })

                    // 3. 监听"工具执行完成"事件：当工具调用结束并返回结果时触发
                    .onToolExecuted((ToolExecution toolExecution) -> {
                        try {
                            // 包装为工具执行结果消息对象（标记为工具返回类型）
                            ToolExecutedMessage toolExecutedMessage = new ToolExecutedMessage(toolExecution);
                            // 序列化为 JSON 发送（前端可展示工具结果或继续处理）
                            sink.next(JSONUtil.toJsonStr(toolExecutedMessage));
                        } catch (Exception e) {
                            sink.error(new RuntimeException("序列化工具执行结果失败", e));
                        }
                    })

                    // 4. 监听"处理完成"事件：当 TokenStream 所有数据处理完毕时触发
                    .onCompleteResponse((ChatResponse response) -> {
                        log.info("TokenStream 所有事件处理完成");
                        sink.complete(); // 标记 Flux 流正常结束
                    })

                    // 5. 监听"错误"事件：当 TokenStream 处理过程中发生异常时触发
                    .onError((Throwable error) -> {
                        log.error("TokenStream 处理过程中发生错误", error); // 记录错误日志
                        sink.error(error); // 将错误传递给 Flux 流，终止流并通知订阅者
                    })
                    .start();

        });
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
