package com.lynn.yuaicodemother.langgraph4j.node;

import com.lynn.yuaicodemother.ai.AiCodeGenTypeRoutingService;
import com.lynn.yuaicodemother.ai.AiCodeGenTypeRoutingServiceFactory;
import com.lynn.yuaicodemother.langgraph4j.state.WorkflowContext;
import com.lynn.yuaicodemother.model.enums.CodeGenTypeEnum;
import com.lynn.yuaicodemother.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;

import javax.swing.*;

import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Slf4j
public class RouterNode {
    public static AsyncNodeAction<MessagesState<String>> create() {
        return node_async(state -> {
            WorkflowContext context = WorkflowContext.getContext(state);
            log.info("执行节点: 智能路由");

            CodeGenTypeEnum generationType;
            try {
                AiCodeGenTypeRoutingServiceFactory aiCodeGenTypeRoutingServiceFactory = SpringContextUtil.getBean(AiCodeGenTypeRoutingServiceFactory.class);
                AiCodeGenTypeRoutingService aiCodeGenTypeRoutingService = aiCodeGenTypeRoutingServiceFactory.createAiCodeGenTypeRoutingService();
                // 根据原始提示词进行智能路由
                generationType = aiCodeGenTypeRoutingService.routeCodeGenType(context.getOriginalPrompt());
                log.info("AI智能路由成功，选择类型：{} ({})",generationType.getValue(), generationType.getText());
            } catch (Exception e) {
                log.error("AI智能路由失败,使用默认HTML类型: {}", e.getMessage(), e);
                generationType = CodeGenTypeEnum.HTML;
            }

            // 更新状态
            context.setCurrentStep("智能路由");
            context.setGenerationType(generationType);
            return WorkflowContext.saveContext(context);
        });
    }
}
