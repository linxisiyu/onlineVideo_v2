package com.zz.blibili.mapper;

import com.zz.blibili.entity.UserMoment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMomentsMapper {
    /*添加动态*/
    Integer addUserMoments(UserMoment userMoment);
}
