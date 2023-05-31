package com.zz.blibili.service;

import com.zz.blibili.entity.FollowingGroup;
import com.zz.blibili.mapper.FollowGroupMapper;
import com.zz.blibili.mapper.UserFollowingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author ZhangZhe
 * 用户关注分组表服务
 */
@Service
public class FollowingGroupService {
    @Autowired
    private UserFollowingMapper userFollowingMapper;
    @Autowired
    private FollowGroupMapper followGroupMapper;

    /*根据关注分组类型得到关注分组表*/
    public FollowingGroup getByType(String type){
        return userFollowingMapper.getByType(type);
    }

    /*根据用户id得到关注分组表*/
    public FollowingGroup getById(Long id){
        return userFollowingMapper.getGroupById(id);
    }

    /*得到用户的分组信息*/
    public List<FollowingGroup> getByUserId(Long userId) {
        return followGroupMapper.getByUserId(userId);
    }
}
