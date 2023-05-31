package com.zz.blibili.mapper;

import com.zz.blibili.entity.FollowingGroup;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FollowGroupMapper {

    /*得到该用户下面的关注分组情况*/
    List<FollowingGroup> getByUserId(Long userId);
}
