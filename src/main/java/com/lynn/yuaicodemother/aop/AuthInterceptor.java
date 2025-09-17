package com.lynn.yuaicodemother.aop;

import com.lynn.yuaicodemother.annotation.AuthCheck;
import com.lynn.yuaicodemother.exception.BusinessException;
import com.lynn.yuaicodemother.exception.ErrorCode;
import com.lynn.yuaicodemother.model.entity.User;
import com.lynn.yuaicodemother.model.enums.UserRoleEnum;
import com.lynn.yuaicodemother.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * ClassName: AuthInterceptor
 * Description:
 *
 * @Author linz
 * @Creat 2025/9/16 14:00
 * @Version 1.00
 */
@Aspect
@Component
public class AuthInterceptor {
    @Autowired
    private UserService userService;

    /**
     * 执行拦截
     * @param joinPoint 切入点
     * @param authCheck 权限校验注解
     * @return
     * @throws Throwable
     */
    @Around("@annotation(authCheck)")
    public Object doInterceptor(ProceedingJoinPoint joinPoint, AuthCheck authCheck) throws Throwable{
        String mustRole = authCheck.mustRole();// 从注解中获取mustRole 也就是访问方法必须要的角色权限
        //获取当前登录用户
        ServletRequestAttributes sra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (sra == null) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR, "无请求上下文");
        }
        HttpServletRequest request = sra.getRequest();
        User loginUser = userService.getLoginUser(request);
        //所需要的角色权限
        UserRoleEnum mustRoleEnum = UserRoleEnum.getByValue(mustRole);
        //如果不需要角色权限，则直接通过
        if(mustRoleEnum == null || mustRole.isEmpty()){
            joinPoint.proceed();
        }
        //获取当前登录用户的角色权限
        UserRoleEnum userRoleEnum = UserRoleEnum.getByValue(loginUser.getUserRole());

        //如果没有权限，直接拒绝
        if(userRoleEnum == null){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //如果要求必须有管理权限 但是当前用户没有管理权限
        if(UserRoleEnum.ADMIN.equals(mustRoleEnum) && !UserRoleEnum.ADMIN.equals(userRoleEnum)){
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        //否则就是要求有用户权限，此时用户肯定有权限了
        return joinPoint.proceed();
    }
}
