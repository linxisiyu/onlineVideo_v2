package com.zz.blibili.handler;

import com.zz.blibili.entity.JsonResponse;
import com.zz.blibili.exception.ConditionException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * 全局异常处理器
 */
@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CommonGlobalExceptionHandler {
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public JsonResponse<String> commonExceptionHandler(HttpServletRequest request, Exception e){
        String message = e.getMessage();
        if (e instanceof ConditionException){
            String errCode = ((ConditionException) e).getCode();
            return new JsonResponse<>(errCode, message);
        }else {
            return new JsonResponse<>("500", message);
        }
    }
}
