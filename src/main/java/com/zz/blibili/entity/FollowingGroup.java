package com.zz.blibili.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author ZhangZhe
 * 用户关注的分组表
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FollowingGroup {
    private Long id;
    private Long userId;        //用户的id
    private String name;        //关注分组名称
    private String type;        //关注分组类型：0特别关注 1悄悄关注 2默认分组 3用户自定义分组
    private Date createTime;
    private Date updateTime;
    private List<UserInfo> followingUserInfoList;

}
