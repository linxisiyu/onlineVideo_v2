package com.zz.blibili.controller;

import com.zz.blibili.entity.JsonResponse;
import com.zz.blibili.entity.auth.UserAuthorities;
import com.zz.blibili.service.auth.UserAuthService;
import com.zz.blibili.utils.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;



@RestController
public class UserAuthController {
    @Autowired
    private  UserSupport userSupport;
    @Autowired
    private UserAuthService userAuthService;

    @GetMapping("/user-authorities")
    public JsonResponse<UserAuthorities> getUserAuthorities(){
        Long userId = userSupport.getCurrentUserId();
        UserAuthorities userAuthorities = userAuthService.getUserAuthorities(userId);
        return new JsonResponse<>(userAuthorities);
    }

}
