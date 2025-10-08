package com.lynn.yuaicodemother.service.impl;

import com.mybatisflex.spring.service.impl.ServiceImpl;
import com.lynn.yuaicodemother.model.entity.ChatHistory;
import com.lynn.yuaicodemother.mapper.ChatHistoryMapper;
import com.lynn.yuaicodemother.service.ChatHistoryService;
import org.springframework.stereotype.Service;

/**
 * 对话历史 服务层实现。
 *
 * @author Linz
 */
@Service
public class ChatHistoryServiceImpl extends ServiceImpl<ChatHistoryMapper, ChatHistory>  implements ChatHistoryService{

}
