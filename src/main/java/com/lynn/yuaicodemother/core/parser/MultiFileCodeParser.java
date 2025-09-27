package com.lynn.yuaicodemother.core.parser;

import com.lynn.yuaicodemother.ai.model.MultiFileCodeResult;
import com.lynn.yuaicodemother.core.parser.util.ParserRegexUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.lynn.yuaicodemother.core.parser.util.ParserRegexUtils.*;

/**
 * ClassName: MultiFileCodeParser
 * Description:
 *
 * @Author linz
 * @Creat 2025/9/27 13:18
 * @Version 1.00
 */
public class MultiFileCodeParser implements CodeParser<MultiFileCodeResult>{

    @Override
    public MultiFileCodeResult parseCode(String codeContent) {
        MultiFileCodeResult result = new MultiFileCodeResult();
        // 提取各类代码
        String htmlCode = extractCodeByPattern(codeContent, ParserRegexUtils.HTML_CODE_PATTERN);
        String cssCode = extractCodeByPattern(codeContent, ParserRegexUtils.CSS_CODE_PATTERN);
        String jsCode = extractCodeByPattern(codeContent, ParserRegexUtils.JS_CODE_PATTERN);

        // 设置HTML代码
        if (htmlCode != null && !htmlCode.trim().isEmpty()) {
            result.setHtmlCode(htmlCode.trim());
        } else {
            System.out.println("HTML code extraction failed.");
        }

        // 设置CSS代码
        if (cssCode != null && !cssCode.trim().isEmpty()) {
            result.setCssCode(cssCode.trim());
        } else {
            System.out.println("CSS code extraction failed.");
        }

        // 设置JS代码
        if (jsCode != null && !jsCode.trim().isEmpty()) {
            result.setJsCode(jsCode.trim());
        } else {
            System.out.println("JS code extraction failed.");
        }

        return result;
    }

}
