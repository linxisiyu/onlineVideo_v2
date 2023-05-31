package com.zz.blibili.service.auth;

import com.zz.blibili.entity.auth.AuthRoleElementOperation;
import com.zz.blibili.mapper.AuthRoleElementOperationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

/**
 * @author ZhangZhe
 * 角色进行操作
 */
@Service
public class AuthRoleElementOperationService {
    @Autowired
    private AuthRoleElementOperationMapper authRoleElementOperationMapper;

    public List<AuthRoleElementOperation> getRoleElementOperationsByRoleIds(Set<Long> roleIdSet) {
        return authRoleElementOperationMapper.getRoleElementOperationsByRoleIds(roleIdSet);
    }
}
