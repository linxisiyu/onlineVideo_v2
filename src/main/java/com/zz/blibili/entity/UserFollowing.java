package com.zz.blibili.entity;

import jdk.nashorn.internal.runtime.logging.Logger;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author ZhangZhe
 * 用户关注
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserFollowing {
    private Long id;
    private Long userId;        //用户的id
    private Long followingId;    //关注用户的id
    private Long groupId;        //关注分组的id
    private Date createTime;
    private UserInfo userInfo;

}
