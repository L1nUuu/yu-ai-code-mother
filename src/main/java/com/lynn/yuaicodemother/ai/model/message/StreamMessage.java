package com.lynn.yuaicodemother.ai.model.message;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ClassName: StreamMessage
 * Description: 消息类型响应基类
 *
 * @Author linz
 * @Creat 2025/10/16 15:49
 * @Version 1.00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StreamMessage {

    /**
     * 消息类型
     */
    private String type;
}
