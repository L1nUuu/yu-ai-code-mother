package com.lynn.yuaicodemother.controller;

import com.lynn.yuaicodemother.annotation.AuthCheck;
import com.lynn.yuaicodemother.common.BaseResponse;
import com.lynn.yuaicodemother.common.ResultUtils;
import com.lynn.yuaicodemother.constant.UserConstant;
import com.lynn.yuaicodemother.exception.ErrorCode;
import com.lynn.yuaicodemother.exception.ThrowUtils;
import com.lynn.yuaicodemother.model.dto.chathistory.ChatHistoryQueryRequest;
import com.lynn.yuaicodemother.model.entity.User;
import com.lynn.yuaicodemother.service.UserService;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.lynn.yuaicodemother.model.entity.ChatHistory;
import com.lynn.yuaicodemother.service.ChatHistoryService;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 对话历史 控制层。
 *
 * @author Linz
 */
@RestController
@RequestMapping("/chatHistory")
public class ChatHistoryController {

    @Resource
    private ChatHistoryService chatHistoryService;
    @Resource
    private UserService userService;


    /**
     * 分页查询某个应用的对话历史
     * @param appId 应用id
     * @param pageSize 每页大小
     * @param lastCreateTime 游标时间
     * @param request 请求
     * @return 对话历史分页
     */
    @GetMapping("/app/{appId}")
    public BaseResponse<Page<ChatHistory>> listAppChatHistoryByPage(@PathVariable Long appId,
                                                 @RequestParam(defaultValue = "10") int pageSize,
                                                 @RequestParam(required = false) LocalDateTime lastCreateTime,
                                                 HttpServletRequest request) {
        User loginUser = userService.getLoginUser(request);
        Page<ChatHistory> chatHistoryPage = chatHistoryService.listAppChatHistoryByPage(appId, pageSize, lastCreateTime, loginUser);
        return ResultUtils.success(chatHistoryPage);
    }

    @PostMapping("/admin/list/page/vo")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<ChatHistory>> listAllChatHistoryByPageAdmin(@RequestBody ChatHistoryQueryRequest chatHistoryQueryRequest) {
        ThrowUtils.throwIf(chatHistoryQueryRequest == null, ErrorCode.PARAMS_ERROR);
        int pageNum = chatHistoryQueryRequest.getPageNum();
        int pageSize = chatHistoryQueryRequest.getPageSize();
        // 查询数据
        QueryWrapper queryWrapper = chatHistoryService.getQueryWrapper(chatHistoryQueryRequest);
        Page<ChatHistory> result = chatHistoryService.page(Page.of(pageNum, pageSize), queryWrapper);
        return ResultUtils.success(result);
    }
}
