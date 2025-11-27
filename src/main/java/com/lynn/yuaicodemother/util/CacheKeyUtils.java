package com.lynn.yuaicodemother.util;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;

/**
 * ClassName: CacheKeyUtils
 * Description: 缓存Key生成工具类
 *
 * @Author linz
 * @Creat 2025/11/27 22:01
 * @Version 1.00
 */
public class CacheKeyUtils {

    /**
     * 根据对象生成缓存Key (JSON + MD5)
     * @param object 要生成key的对象
     * @return MD5哈希后的缓存key
     */
    public static String getCacheKey(Object object){
        if (object == null){
            return DigestUtil.md5Hex("null");
        }else{
            // 先转JSON 再转MD5
            String jsonStr = JSONUtil.toJsonStr(object);
            return DigestUtil.md5Hex(jsonStr);
        }
    }
}
