package com.zz.blibili.service.auth;

import com.zz.blibili.entity.auth.UserRole;
import com.zz.blibili.mapper.UserRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author ZhangZhe
 */
@Service
public class UserRoleService {
    @Autowired
    private UserRoleMapper userRoleMapper;

    /*根据用户id得到用户的角色权限信息*/
    public List<UserRole> getUserRoleByUserId(Long userId){
        return userRoleMapper.getUserRoleByUserId(userId);
    }

    /*注册时添加默认用户*/
    public void addUserRole(UserRole userRole){
        userRole.setCreateTime(new Date());
        userRoleMapper.addUserRole(userRole);
    }
}
