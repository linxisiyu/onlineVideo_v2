package com.zz.blibili.service.auth;

import com.zz.blibili.entity.auth.AuthRole;
import com.zz.blibili.entity.auth.AuthRoleElementOperation;
import com.zz.blibili.entity.auth.AuthRoleMenu;
import com.zz.blibili.mapper.AuthRoleMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author ZhangZhe
 */
@Service
public class AuthRoleService {
    @Autowired
    private AuthRoleMapper authRoleMapper;
    @Autowired
    private AuthRoleElementOperationService authRoleElementOperationService;
    @Autowired
    private AuthRoleMenuService authRoleMenuService;

    /*根据角色id*/
    public List<AuthRoleElementOperation> getRoleElementOperationsByRoleIds(Set<Long> roleIdSet) {
        return authRoleElementOperationService.getRoleElementOperationsByRoleIds(roleIdSet);
    }

    public List<AuthRoleMenu> getAuthRoleMenusByRoleIds(Set<Long> roleIdSet) {
        return authRoleMenuService.getAuthRoleMenusByRoleIds(roleIdSet);
    }

    /*添加默认等级，找到用户角色为lv0的*/
    public AuthRole getRoleByCode(String code){
        return authRoleMapper.getRoleByCode(code);
    }
}
