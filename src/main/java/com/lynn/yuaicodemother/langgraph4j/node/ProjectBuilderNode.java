package com.lynn.yuaicodemother.langgraph4j.node;

import com.lynn.yuaicodemother.core.builder.VueProjectBuilder;
import com.lynn.yuaicodemother.exception.BusinessException;
import com.lynn.yuaicodemother.exception.ErrorCode;
import com.lynn.yuaicodemother.langgraph4j.state.WorkflowContext;
import com.lynn.yuaicodemother.model.enums.CodeGenTypeEnum;
import com.lynn.yuaicodemother.util.SpringContextUtil;
import dev.langchain4j.agent.tool.P;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;

import java.io.File;

import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Slf4j
public class ProjectBuilderNode {
    public static AsyncNodeAction<MessagesState<String>> create() {
        return node_async(state -> {
            WorkflowContext context = WorkflowContext.getContext(state);
            log.info("执行节点: 项目构建");
            
            String buildResultDir = "";
            CodeGenTypeEnum generationType = context.getGenerationType();
            String generatedCodeDir = context.getGeneratedCodeDir();
            // 2.一定为Vue项目，则使用VueProjectBuilder进行构建（因为非Vue项目不走这个节点）
            try {
                VueProjectBuilder vueProjectBuilder = SpringContextUtil.getBean(VueProjectBuilder.class);
                // 执行项目构建(npm install + npm run build)
                boolean buildResult = vueProjectBuilder.buildProject(generatedCodeDir);

                if (buildResult){
                    // 构建成功
                    buildResultDir = generatedCodeDir + File.separator + "dist";
                    log.info("项目构建成功，dist目录: {}", buildResultDir);
                }else {
                    // 构建失败
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"Vue项目构建失败");
                }
            } catch (Exception e) {
                log.error("Vue项目构建异常：{}", e.getMessage(), e);
                buildResultDir = generatedCodeDir; // 异常时返回原路径
            }
            
            // 更新状态
            context.setCurrentStep("项目构建");
            context.setBuildResultDir(buildResultDir);
            log.info("项目构建完成，结果目录: {}", buildResultDir);
            return WorkflowContext.saveContext(context);
        });
    }
}
