package com.lynn.yuaicodemother.util;

import com.lynn.yuaicodemother.model.entity.App;
import com.lynn.yuaicodemother.model.entity.ChatHistory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ClassName: MarkdownUtils
 * Description:
 *
 * @Author linz
 * @Creat 2025/10/10 21:29
 * @Version 1.00
 */
public class MarkdownUtils {
    /**
     * 构建 Markdown 文本
     */
    public static String buildMarkdownForExport(App app, List<ChatHistory> chatHistoryList) {
        StringBuilder sb = new StringBuilder();
        // 标题与元信息
        sb.append("# 对话历史导出\n\n");
        sb.append("- 应用ID: ").append(app.getId()).append("\n");
        sb.append("- 应用名称: ").append(app.getAppName() == null ? "" : app.getAppName()).append("\n");
        sb.append("- 导出时间: ").append(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(LocalDateTime.now())).append("\n");
        sb.append("- 记录数: ").append(chatHistoryList == null ? 0 : chatHistoryList.size()).append("\n\n");
        // 消息列表（按时间正序）
        if (chatHistoryList != null) {
            for (ChatHistory ch : chatHistoryList) {
                String role = "user".equalsIgnoreCase(ch.getMessageType()) ? "用户" : "AI";
                String timeStr = ch.getCreateTime() == null ? "" : DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(ch.getCreateTime());
                sb.append("## [").append(role).append("] ").append(timeStr).append("\n\n");
                String content = ch.getMessage() == null ? "" : ch.getMessage();
                if (containsCodeMarkers(content)) {
                    sb.append("```").append("\n");
                    sb.append(content).append("\n");
                    sb.append("```").append("\n\n");
                } else {
                    sb.append(content).append("\n\n");
                }
            }
        }
        sb.append("---\n");
        sb.append("导出由系统自动生成\n");
        return sb.toString();
    }

    /**
     * 简单判断是否包含代码片段标记
     */
    public static  boolean containsCodeMarkers(String content) {
        if (content == null) return false;
        String lower = content.toLowerCase();
        return lower.contains("class ") || lower.contains("public ") || lower.contains("function ")
                || lower.contains("def ") || lower.contains("{") || lower.contains(";");
    }

    /**
     * 生成安全文件名
     */
    public static  String sanitizeFileName(String input) {
        if (input == null) return "export.md";
        return input.replaceAll("[^a-zA-Z0-9._-]", "_");
    }
}
