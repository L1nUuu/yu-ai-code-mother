package com.lynn.yuaicodemother.core.parser;

/**
 * ClassName: CodeParserExecutor
 * Description:
 *
 * @Author linz
 * @Creat 2025/9/27 13:20
 * @Version 1.00
 */

import com.lynn.yuaicodemother.exception.BusinessException;
import com.lynn.yuaicodemother.exception.ErrorCode;
import com.lynn.yuaicodemother.model.enums.CodeGenTypeEnum;

/**
 * 代码解析执行器
 * 根据不同的代码类型，执行相应的解析逻辑
 */
public class CodeParserExecutor {
    private static final HtmlCodeParser htmlCodeParser = new HtmlCodeParser();
    private static final MultiFileCodeParser multiFileCodeParser = new MultiFileCodeParser();


    /**
     * 执行代码解析
     *
     * @param codeContent     代码内容
     * @param codeGenTypeEnum 代码生成类型
     * @return 解析结果（HtmlCodeResult 或者 MultiFileCodeResult）
     */
    public static Object getCodeParser(String codeContent, CodeGenTypeEnum codeGenTypeEnum) {
        return switch (codeGenTypeEnum) {
            case HTML -> htmlCodeParser.parseCode(codeContent);

            case MULTI_FILE -> multiFileCodeParser.parseCode(codeContent);

            default -> throw new BusinessException(ErrorCode.SYSTEM_ERROR, "不支持的生成类型" + codeGenTypeEnum.getValue());
        };
    }
}
