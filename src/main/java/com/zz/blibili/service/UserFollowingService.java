package com.zz.blibili.service;

import com.zz.blibili.entity.FollowingGroup;
import com.zz.blibili.entity.User;
import com.zz.blibili.entity.UserFollowing;
import com.zz.blibili.entity.UserInfo;
import com.zz.blibili.entity.constant.UserConstant;
import com.zz.blibili.exception.ConditionException;
import com.zz.blibili.mapper.UserFollowingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author ZhangZhe
 * 用户关注服务
 */
@Service
public class UserFollowingService {
    @Autowired
    private UserFollowingMapper userFollowingMapper;
    @Autowired
    private FollowingGroupService followingGroupService;
    @Autowired
    private UserService userService;

    /*添加用户的关注人*/
    @Transactional
    public void addUserFollowings(UserFollowing userFollowing){
        //拿到用户关注分组，看放到哪个分组中
        Long groupId = userFollowing.getGroupId();
        //为空，添加到默认分组
        if (groupId == null){
            FollowingGroup followingGroup = followingGroupService.
                    getByType(UserConstant.USER_FOLLOWING_GROUP_TYPE_DEFAULT);
            userFollowing.setGroupId(followingGroup.getId());
        }else {
            FollowingGroup followingGroup = followingGroupService.getById(groupId);
            if (followingGroup == null) throw new ConditionException("关注分组不存在！");
        }
        //拿到关注用户id，去查询该人是否存在
        Long followingId = userFollowing.getFollowingId();
        User user = userService.getUserById(followingId);
        if (user == null) throw new ConditionException("关注的用户不存在！");
        //删除关联关系
        userFollowingMapper.deleteUserFollowing(userFollowing.getUserId(), followingId);
        userFollowing.setCreateTime(new Date());
        userFollowingMapper.addUserFollowding(userFollowing);
    }

    /*获取关注的用户列表*/
    public List<FollowingGroup> getUserFollowings(Long userId){
        //根据用户的id，查询关注人列表
        List<UserFollowing> list = userFollowingMapper.getUserFolloings(userId);
        //得到关注人的id集合，并得到对应集合
        Set<Long> followingIdSet = list.stream().map(UserFollowing::getFollowingId).collect(Collectors.toSet());
        //得到关注人的详细信息
        ArrayList<UserInfo> userInfoList = new ArrayList<>();
        if (followingIdSet.size() > 0){
            userInfoList = userService.getUserInfoByUserIds(followingIdSet);
        }

        //根据关注用户id，查询用户基本信息
        for (UserFollowing userFollowing : list){
            for (UserInfo userInfo : userInfoList){
                if (userFollowing.getFollowingId().equals(userInfo.getUserId()))
                    userFollowing.setUserInfo(userInfo);
            }
        }
        //得到用户的分组信息
        List<FollowingGroup> groupList = followingGroupService.getByUserId(userId);
        //全部关注的分组（前端展示）
        FollowingGroup allGroup = new FollowingGroup();
        allGroup.setName(UserConstant.USER_FOLLOWING_GROUP_ALL_NAME);
        //全部关注的所有人的信息
        allGroup.setFollowingUserInfoList(userInfoList);

        ArrayList<FollowingGroup> result = new ArrayList<>();
        result.add(allGroup);   //添加全部关注的分组
        for (FollowingGroup group : groupList){
            ArrayList<UserInfo> infoList = new ArrayList<>();
            for(UserFollowing userFollowing : list){
                if(group.getId().equals(userFollowing.getGroupId()))
                    infoList.add(userFollowing.getUserInfo());
            }
            group.setFollowingUserInfoList(infoList);
            result.add(group);
        }
        return result;
    }

    /*得到用户粉丝列表*/
    public List<UserFollowing> getUserFans(Long userId){
        //反向得到粉丝列表
        List<UserFollowing> fanList = userFollowingMapper.getUserFans(userId);
        //得到粉丝id信息
        Set<Long> fanIdSet = fanList.stream().map(UserFollowing::getUserId).collect(Collectors.toSet());
        List<UserInfo> userInfoList = new ArrayList<>();
        //根据用户粉丝id集合得到粉丝的详细信息
        if(fanIdSet.size() > 0) userInfoList = userService.getUserInfoByUserIds(fanIdSet);
        List<UserFollowing> followingList = userFollowingMapper.getUserFollowings(userId);

        for(UserFollowing fan : fanList){
            for(UserInfo userInfo : userInfoList){
                if(fan.getUserId().equals(userInfo.getUserId())){
                    userInfo.setFollowed(false);
                    fan.setUserInfo(userInfo);
                }
            }
            for(UserFollowing following : followingList){
                //互相关注条件
                if(following.getFollowingId().equals(fan.getUserId()))
                    fan.getUserInfo().setFollowed(true);
            }
        }
        return fanList;
    }
}
