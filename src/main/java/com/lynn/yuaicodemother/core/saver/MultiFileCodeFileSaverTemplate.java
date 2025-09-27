package com.lynn.yuaicodemother.core.saver;

import cn.hutool.core.util.StrUtil;
import com.lynn.yuaicodemother.ai.model.MultiFileCodeResult;
import com.lynn.yuaicodemother.exception.BusinessException;
import com.lynn.yuaicodemother.exception.ErrorCode;
import com.lynn.yuaicodemother.model.enums.CodeGenTypeEnum;


/**
 * 多文件代码保存器
 */
public class MultiFileCodeFileSaverTemplate extends CodeFileSaverTemplate<MultiFileCodeResult> {

    @Override
    protected void saveFiles(MultiFileCodeResult result, String baseDirPath) {
        writeToFile(baseDirPath, "index.html", result.getHtmlCode());
        writeToFile(baseDirPath, "style.css", result.getCssCode());
        writeToFile(baseDirPath, "script.js", result.getJsCode());
    }

    @Override
    protected CodeGenTypeEnum getCodeType() {
        return CodeGenTypeEnum.MULTI_FILE;
    }

    @Override
    protected void validateInput(MultiFileCodeResult result) {
        super.validateInput(result);
        if (StrUtil.isBlank(result.getHtmlCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Html代码不能为空");
        }
        if (StrUtil.isBlank(result.getCssCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "CSS代码不能为空");
        }
        if (StrUtil.isBlank(result.getJsCode())) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "JS代码不能为空");
        }
    }
}
