package com.zz.blibili.exception;

import lombok.Data;

/**
 * @author ZhangZhe
 * 条件异常,返回异常状态码
 */

@Data
public class ConditionException extends RuntimeException{
    //序列化版本号
    private static final long serialVersionUID = 1L;
    private String code;
    public ConditionException(String code, String name){
        super(name);
        this.code = code;
    }
    public ConditionException(String name){
        super(name);
        code = "500";
    }
}
