package com.zz.blibili.mapper;

import com.zz.blibili.entity.auth.AuthRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AuthRoleMapper {
    /*根据编号得到用户的角色（权限）信息*/
    AuthRole getRoleByCode(String code);
}
