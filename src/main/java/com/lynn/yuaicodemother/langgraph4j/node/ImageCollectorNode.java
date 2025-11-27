package com.lynn.yuaicodemother.langgraph4j.node;

import com.lynn.yuaicodemother.langgraph4j.ai.ImageCollectionService;
import com.lynn.yuaicodemother.langgraph4j.model.ImageResource;
import com.lynn.yuaicodemother.langgraph4j.model.enums.ImageCategoryEnum;
import com.lynn.yuaicodemother.langgraph4j.state.WorkflowContext;
import com.lynn.yuaicodemother.util.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.bsc.langgraph4j.action.AsyncNodeAction;
import org.bsc.langgraph4j.prebuilt.MessagesState;

import java.util.Arrays;
import java.util.List;

import static org.bsc.langgraph4j.action.AsyncNodeAction.node_async;

@Slf4j
public class ImageCollectorNode {
    public static AsyncNodeAction<MessagesState<String>> create() {
        return node_async(state -> {
            WorkflowContext context = WorkflowContext.getContext(state);
            String originalPrompt = context.getOriginalPrompt();
            String imageListStr = "";
            log.info("执行节点: 图片收集");

            try {
                ImageCollectionService imageCollectionService = SpringContextUtil.getBean(ImageCollectionService.class);
                // 使用ai服务进行图片收集
                imageListStr = imageCollectionService.collectImages(originalPrompt);
            } catch (Exception e) {
                log.error("图片收集失败: {}", e.getMessage(), e);
            }

            // 更新状态
            context.setCurrentStep("图片收集");
            context.setImageListStr(imageListStr);
            return WorkflowContext.saveContext(context);
        });
    }
}
