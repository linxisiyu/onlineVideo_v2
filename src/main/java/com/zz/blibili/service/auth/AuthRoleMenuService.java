package com.zz.blibili.service.auth;

import com.zz.blibili.entity.auth.AuthRoleMenu;
import com.zz.blibili.mapper.AuthRoleMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author ZhangZhe
 */
@Service
public class AuthRoleMenuService {
    @Autowired
    private AuthRoleMenuMapper authRoleMenuMapper;

    public List<AuthRoleMenu> getAuthRoleMenusByRoleIds(Set<Long> roleIdSet) {
        return authRoleMenuMapper.getAuthRoleMenusByRoleIds(roleIdSet);
    }
}
