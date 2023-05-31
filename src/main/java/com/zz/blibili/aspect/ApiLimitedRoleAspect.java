package com.zz.blibili.aspect;


import com.zz.blibili.entity.annotation.ApiLimitedRole;
import com.zz.blibili.entity.auth.UserRole;
import com.zz.blibili.exception.ConditionException;
import com.zz.blibili.service.auth.UserRoleService;
import com.zz.blibili.utils.UserSupport;
import org.aopalliance.intercept.Joinpoint;
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

public class ApiLimitedRoleAspect {
    @Autowired
    private UserSupport userSupport;
    @Autowired
    private UserRoleService userRoleService;

    @Pointcut("@annotation(com.zz.blibili.entity.annotation.ApiLimitedRole)")
    public void check(){}

    /**
     *
     @Order(1)表示设置当前切面的优先级为1，优先级越小，越先执行。
     @Component表示将当前类注册到Spring容器中，以便在需要的地方使用该类的实例。
     @Aspect表示当前类是一个切面类，用于实现AOP的拦截与增强。
     @Autowired表示自动注入，将UserSupport和UserRoleService两个Bean注入到当前类中。
     @Pointcut表示定义一个切点，用于定义哪些方法需要被拦截，这里的切点是带有@ApiLimitedRole注解的方法。
   */
    @Before("check() && @annotation(apiLimitedRole)")
    public void doBefore(JoinPoint joinpoint, ApiLimitedRole apiLimitedRole){
        Long userId = userSupport.getCurrentUserId();
        List<UserRole> userRoleList = userRoleService.getUserRoleByUserId(userId);

        String[] limitedRoleCodeList = apiLimitedRole.limitedRoleCodeList();
        Set<String> limitedRoleCodeSet = Arrays.stream(limitedRoleCodeList).collect(Collectors.toSet());
        Set<String> roleCodeSet = userRoleList.stream().map(UserRole::getRoleCode).collect(Collectors.toSet());
        roleCodeSet.retainAll(limitedRoleCodeSet);
        if (roleCodeSet.size() > 0) throw new ConditionException("权限不足！");
    }
}
