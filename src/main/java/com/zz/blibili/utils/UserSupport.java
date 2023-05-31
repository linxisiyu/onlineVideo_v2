package com.zz.blibili.utils;

import com.zz.blibili.exception.ConditionException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

/**
 * @author ZhangZhe
 * 用户token校验
 */
@Component
public class UserSupport {
    /*得到最近一次的UserId*/
    public Long getCurrentUserId(){
        ServletRequestAttributes request = (ServletRequestAttributes)RequestContextHolder.getRequestAttributes();
        //从请求头得到token
        String token = request.getRequest().getHeader("token");
        Long userId = TokenUtil.verifyToken(token);
        if (userId < 0) throw new ConditionException("非法用户！");
        return userId;
    }
}
