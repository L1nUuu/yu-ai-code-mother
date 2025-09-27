package com.lynn.yuaicodemother.core.parser;

/**
 * ClassName: CodeParser
 * Description: 代码解析器策略接口
 *
 * @Author linz
 * @Creat 2025/9/27 12:54
 * @Version 1.00
 */
public interface CodeParser<T> {
    /**
     * 解析代码
     *
     * @param codeContent 原始代码内容
     * @return T 解析后的结果对象
     */
    T parseCode(String codeContent);
}
