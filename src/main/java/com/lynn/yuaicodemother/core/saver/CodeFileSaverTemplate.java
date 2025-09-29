package com.lynn.yuaicodemother.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.lynn.yuaicodemother.exception.BusinessException;
import com.lynn.yuaicodemother.exception.ErrorCode;
import com.lynn.yuaicodemother.model.enums.CodeGenTypeEnum;

import java.io.File;
import java.nio.charset.StandardCharsets;

/**
 * 抽象代码文件保存器 - 模板方法模式
 *
 * @param <T>
 */
public abstract class CodeFileSaverTemplate<T> {

    /**
     * 文件保存的根目录
     */
    private static final String FILE_SAVE_ROOT_DIR = System.getProperty("user.dir") + "/tmp/code_output";


    /**
     * 模版方法：保存代码的标准流程
     *
     * @param result 代码结果对象
     * @return 保存的目录
     */
    public final File saveCode(T result,Long appId) {
        //1. 验证输入
        validateInput(result);

        //2. 构建唯一目录
        String baseDirPath = buildUniqueDir(appId);

        //3. 保存文件(具体实现交给子类)
        saveFiles(result, baseDirPath);

        //4. 返回保存的目录
        return new File(baseDirPath);
    }


    /**
     * 验证输入参数（可由子类覆盖）
     *
     * @param result 代码结果对象
     */
    protected void validateInput(T result) {
        if (result == null) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成代码结果不能为空");
        }
    }

    /**
     * 构建文件的唯一路径：tmp/code_output/bizType_雪花ID
     * @param appId 应用ID
     * @return 目录路径
     */
    private String buildUniqueDir(Long appId) {
        if(appId == null){
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "应用ID不能为空");
        }

        String bizType = getCodeType().getValue();
        String uniqueDirName = StrUtil.format("{}_{}", bizType, appId);
        String dirPath = FILE_SAVE_ROOT_DIR + File.separator + uniqueDirName;
        FileUtil.mkdir(dirPath);
        return dirPath;
    }

    /**
     * 保存文件（具体实现交给子类）
     *
     * @param result      代码结果对象
     * @param baseDirPath 基础目录路径
     */
    protected abstract void saveFiles(T result, String baseDirPath);

    /**
     * 获取代码生成类型
     *
     * @return 代码生成类型枚举
     */
    protected abstract CodeGenTypeEnum getCodeType();


    /**
     * 保存单个文件
     *
     * @param dirPath
     * @param fileName
     * @param content
     */
    public final void writeToFile(String dirPath, String fileName, String content) {
        if (StrUtil.isNotBlank(content)) {
            String filePath = dirPath + File.separator + fileName;
            FileUtil.writeString(content, filePath, StandardCharsets.UTF_8);
        }
    }

}
