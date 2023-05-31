package com.zz.blibili.entity.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRoleElementOperation {
    private Long id;
    private Long roleId;
    private Long elementOperationId;   //元素操作id
    private Date createTime;
    private AuthElementOperation authElementOperation;

}
