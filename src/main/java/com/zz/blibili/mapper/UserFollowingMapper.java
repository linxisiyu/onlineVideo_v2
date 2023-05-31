package com.zz.blibili.mapper;

import com.zz.blibili.entity.FollowingGroup;
import com.zz.blibili.entity.UserFollowing;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserFollowingMapper {

    /*根据关注分组类型得到关注分组表*/
    FollowingGroup getByType(String type);
    /*根据用户id得到关注分组表*/
    FollowingGroup getGroupById(Long id);
    /*删除用户关注人*/
    Integer deleteUserFollowing(@Param("userId") Long userId, @Param("followingId") Long followingId);
    /*添加关注人信息*/
    Integer addUserFollowding(UserFollowing userFollowing);
    /*根据用户id查询该用户关注列表*/
    List<UserFollowing> getUserFolloings(Long userId);
    /*根据用户id得到粉丝*/
    List<UserFollowing> getUserFans(Long userId);
    /*根据用户id得到用户关注*/
    List<UserFollowing> getUserFollowings(Long userId);
}
