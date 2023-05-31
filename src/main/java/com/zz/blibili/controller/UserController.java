package com.zz.blibili.controller;

import com.zz.blibili.entity.JsonResponse;
import com.zz.blibili.entity.User;
import com.zz.blibili.entity.UserInfo;
import com.zz.blibili.service.UserService;
import com.zz.blibili.utils.RSAUtil;
import com.zz.blibili.utils.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.security.interfaces.RSAPublicKey;
import java.util.Map;


@RestController
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private UserSupport userSupport;

    /*获取rsa公钥*/
    @GetMapping("/rsa-pks")
    public JsonResponse<String> getRsaPublicKey(){
        String publicKey = RSAUtil.getPublicKeyStr();
        return new JsonResponse<>(publicKey);
    }

    /*用户注册*/
    @PostMapping("/users")
    public JsonResponse<String> addUser(@RequestBody User user){
        userService.addUser(user);
        return JsonResponse.success();
    }

    /*登录得到用户请求头信息*/
    @GetMapping("/users")
    public JsonResponse<User> getUserInfo(){
        Long userId = userSupport.getCurrentUserId();
        User user = userService.getUserInfo(userId);
        return new JsonResponse<>(user);
    }

    /*用户登录*/
    @PostMapping("/user-tokens")
    public JsonResponse<String> login(@RequestBody User user) throws Exception {
        String token = userService.login(user);
        return new JsonResponse<>(token);
    }



    /*更新用户基本信息*/
    @PutMapping("/users")
    public JsonResponse<String> updateUsers(@RequestBody User user) throws Exception {
        Long userId = userSupport.getCurrentUserId();
        user.setId(userId);
        userService.updateUsers(user);
        return JsonResponse.success();
    }

    /*更新用户详细信息*/
    @PutMapping("/user-infos")
    public JsonResponse<String> updateUserInfos(@RequestBody UserInfo userInfo) throws Exception {
        Long userId = userSupport.getCurrentUserId();
        userInfo.setUserId(userId);
        userService.updateUserinfos(userInfo);
        return JsonResponse.success();
    }

    /*登录升级,双token*/
    @PostMapping("/user-dts")
    public JsonResponse<Map<String, Object>> loginForDts (@RequestBody User user) throws Exception {
        Map<String, Object> map =  userService.loginForDts(user);
        return new JsonResponse<>(map);
    }

    /*退出登录*/
    @DeleteMapping("/refresh-tokens")
    public JsonResponse<String> logout(HttpServletRequest request){
        String refreshToken = request.getHeader("refreshToken");
        Long userId = userSupport.getCurrentUserId();
        userService.logOut(refreshToken, userId);
        return JsonResponse.success();
    }

    /*刷新token*/
    @PostMapping("/access-tokens")
    public JsonResponse<String> refreshAccessToken(HttpServletRequest request) throws Exception {
        String refreshToken = request.getHeader("refreshToken");
        String accessToken = userService.refreshAccessToken(refreshToken);
        return new JsonResponse<>(accessToken);
    }


}
