package com.lynn.yuaicodemother.core.parser;

import com.lynn.yuaicodemother.ai.model.HtmlCodeResult;
import com.lynn.yuaicodemother.core.parser.util.ParserRegexUtils;

import java.util.regex.Matcher;

/**
 * ClassName: HtmlCodeParser
 * Description:
 *
 * @Author linz
 * @Creat 2025/9/27 12:56
 * @Version 1.00
 */
public class HtmlCodeParser implements CodeParser<HtmlCodeResult>{

    @Override
    public HtmlCodeResult parseCode(String codeContent) {
        HtmlCodeResult result = new HtmlCodeResult();
        // 提取 HTML 代码
        String htmlCode = extractHtmlCode(codeContent);
        if (htmlCode != null && !htmlCode.trim().isEmpty()) {
            result.setHtmlCode(htmlCode.trim());
        } else {
            // 如果没有找到代码块，将整个内容作为HTML
            result.setHtmlCode(codeContent.trim());
        }
        return result;
    }

    /**
     * 提取HTML代码内容
     *
     * @param content 原始内容
     * @return HTML代码
     */
    private static String extractHtmlCode(String content) {
        Matcher matcher = ParserRegexUtils.HTML_CODE_PATTERN.matcher(content);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }


}
