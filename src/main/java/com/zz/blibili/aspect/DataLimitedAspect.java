package com.zz.blibili.aspect;


import com.zz.blibili.entity.UserMoment;
import com.zz.blibili.entity.annotation.ApiLimitedRole;
import com.zz.blibili.entity.auth.UserRole;
import com.zz.blibili.entity.constant.AuthRoleConstant;
import com.zz.blibili.exception.ConditionException;
import com.zz.blibili.service.auth.UserRoleService;
import com.zz.blibili.utils.UserSupport;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Order(1)
@Component
@Aspect

public class DataLimitedAspect {
    @Autowired
    private UserSupport userSupport;
    @Autowired
    private UserRoleService userRoleService;

    @Pointcut("@annotation(com.zz.blibili.entity.annotation.DataLimited)")
    public void check(){}

    @Before("check()")
    public void doBefore(JoinPoint joinpoint){
        Long userId = userSupport.getCurrentUserId();
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        Object[] args = joinpoint.getArgs();
        for (Object arg : args){
            if (arg instanceof UserMoment){
                UserMoment userMoment = (UserMoment) arg;
                String type = userMoment.getType();
                if (roleCodeSet.contains(AuthRoleConstant.ROLE_LV0) && !"0".equals(type)){
                    throw new ConditionException("参数异常，角色权限不足！");
                }
            }
        }
    }
}
