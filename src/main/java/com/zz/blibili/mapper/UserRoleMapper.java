package com.zz.blibili.mapper;

import com.zz.blibili.entity.auth.UserRole;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserRoleMapper {
    /*得到用户的角色信息*/
    List<UserRole> getUserRoleByUserId(Long userId);
    /*注册添加新的用户角色*/
    Integer addUserRole(UserRole userRole);
}
