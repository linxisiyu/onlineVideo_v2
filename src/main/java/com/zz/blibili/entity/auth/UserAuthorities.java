package com.zz.blibili.entity.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAuthorities {
    List<AuthRoleElementOperation> roleElementOperationList;
    List<AuthRoleMenu> roleMenuList;

}
