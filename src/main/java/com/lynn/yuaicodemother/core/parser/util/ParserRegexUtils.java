package com.lynn.yuaicodemother.core.parser.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * ClassName: ParserRegexUtils
 * Description: 解析代码的正则工具类
 *
 * @Author linz
 * @Creat 2025/9/27 13:35
 * @Version 1.00
 */
public class ParserRegexUtils {
    // 调整正则表达式来更好地匹配代码块
    public static final Pattern HTML_CODE_PATTERN = Pattern.compile("```html\\s*\\n([\\s\\S]+?)\\n```", Pattern.CASE_INSENSITIVE);
    public static final Pattern CSS_CODE_PATTERN = Pattern.compile("```css\\s*\\n([\\s\\S]+?)\\n```", Pattern.CASE_INSENSITIVE);
    public static final Pattern JS_CODE_PATTERN = Pattern.compile("```(?:js|javascript)\\s*\\n([\\s\\S]+?)\\n```", Pattern.CASE_INSENSITIVE);

    /**
     * 根据正则模式提取代码
     *
     * @param content 原始内容
     * @param pattern 正则模式
     * @return 提取的代码
     */
    public static String extractCodeByPattern(String content, Pattern pattern) {
        Matcher matcher = pattern.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
