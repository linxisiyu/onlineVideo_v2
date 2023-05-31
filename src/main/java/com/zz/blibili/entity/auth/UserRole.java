package com.zz.blibili.entity.auth;

/**
 * @author ZhangZhe
 * @date：2022/10/22 22:46
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRole {
    private int id;
    private Long userId;
    private Long roleId;
    private Date createTime;
    //连接表字段
    private String roleName;
    private String roleCode;
}
