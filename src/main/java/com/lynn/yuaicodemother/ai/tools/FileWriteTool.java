package com.lynn.yuaicodemother.ai.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONObject;
import com.lynn.yuaicodemother.constant.AppConstant;
import com.lynn.yuaicodemother.model.entity.App;
import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import dev.langchain4j.service.MemoryId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * ClassName: FileWriteTool
 * Description:
 *
 * @Author linz
 * @Creat 2025/10/13 08:54
 * @Version 1.00
 */
@Component
@Slf4j
public class FileWriteTool extends BaseTool{

    @Tool("写入文件到指定路径")
    public String writeFile(
            @P("文件的相对路径") String relativePath,
            @P("要写入的文件内容") String content,
            @ToolMemoryId Long appId){
        try {
            Path path = Paths.get(relativePath);
            if (!path.isAbsolute()){
                // 相对路径处理，创建基于appId的绝对路径
                String projectDirName = "vue_project_" + appId;
                Path projectRoot = Paths.get(AppConstant.CODE_OUTPUT_ROOT_DIR,projectDirName);
                // resolve方法用于将相对路径解析为绝对路径，基于projectRoot目录
                path = projectRoot.resolve(path);
            }
            // 创建父目录（如果不存在）
            Path parentDir = path.getParent();
            if(parentDir != null){
                Files.createDirectories(parentDir);
            }
            // 写入文件内容
            Files.write(path,content.getBytes(),
                    StandardOpenOption.CREATE,
                    StandardOpenOption.TRUNCATE_EXISTING);
            // 注意返回的是相对路径，不能返回绝对路径给用户
            return "文件写入成功：" + relativePath;
        }catch (IOException e){
            String errorMessage = "文件写入失败：" + relativePath + "，错误：" + e.getMessage();
            log.error(errorMessage,e);
            return errorMessage;
        }
    }

    @Override
    public String getToolName() {
        return "writeFile";
    }

    @Override
    public String getDisplayName() {
        return "写入文件";
    }

    @Override
    public String generateToolExecutedResult(JSONObject arguments) {
        String relativePath = arguments.getStr("relativePath");
        String suffix = FileUtil.getSuffix(relativePath);
        String content = arguments.getStr("content");
        return String.format("""
                        [工具调用] %s %s
                        ```%s
                        %s
                        ```
                        """, getDisplayName(), relativePath, suffix, content);
    }
}
