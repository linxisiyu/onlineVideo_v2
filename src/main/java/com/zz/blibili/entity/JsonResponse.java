package com.zz.blibili.entity;

import lombok.Data;

/**
 * json转换的相应状态
 * @param <T>
 */
@Data
public class JsonResponse<T> {
    private String code;
    private String msg;
    private T data;

    public JsonResponse(String code, String msg){
        this.code = code;
        this.msg = msg;
    }
    public JsonResponse(T data){
        this.data = data;
        msg = "成功！";
        code = "0";
    }

    //成功
    public static JsonResponse<String> success(){
        return new JsonResponse<>(null);
    }
    //成功给前端返回参数
    public static JsonResponse<String> success(String data){
        return new JsonResponse<>(data);
    }

    //失败
    public static JsonResponse<String> fail(){
        return new JsonResponse<>("1", "失败！");
    }
    //定制化失败参数（特定返回码）
    public static JsonResponse<String> fail(String code, String msg){
        return new JsonResponse<>(code, msg);
    }



}
