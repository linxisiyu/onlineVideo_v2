package com.zz.blibili.controller;

import com.zz.blibili.entity.JsonResponse;
import com.zz.blibili.entity.UserMoment;
import com.zz.blibili.entity.annotation.ApiLimitedRole;
import com.zz.blibili.entity.annotation.DataLimited;
import com.zz.blibili.entity.constant.AuthRoleConstant;
import com.zz.blibili.entity.constant.UserConstant;
import com.zz.blibili.service.UserMomentsService;
import com.zz.blibili.service.UserService;
import com.zz.blibili.utils.UserSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;



@RestController
public class UserMomentController {
    @Autowired
    private UserSupport userSupport;
    @Autowired
    private UserMomentsService userMomentsService;

    @ApiLimitedRole(limitedRoleCodeList = {AuthRoleConstant.ROLE_LV0})
    @DataLimited
    @PostMapping("/user-moments")
    public JsonResponse<String> addUserMoments(@RequestBody UserMoment userMoment) throws Exception {
        //得到当前的用户id
        Long userId = userSupport.getCurrentUserId();
        //设置评论的用户id，添加进去
        userMoment.setUserId(userId);
        userMomentsService.addUserMoments(userMoment);
        return JsonResponse.success();
    }

    @GetMapping("/user-subscribed-moments")
    public JsonResponse<List<UserMoment>> getUserSubscribedMoments(){
        Long userId = userSupport.getCurrentUserId();
        List<UserMoment> list = userMomentsService.getUserSubscribedMoments(userId);
        return new JsonResponse<>(list);
    }
}

