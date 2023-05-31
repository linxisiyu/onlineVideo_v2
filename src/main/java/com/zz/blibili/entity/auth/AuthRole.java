package com.zz.blibili.entity.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthRole {
    private Long id;
    private String name;
    private String code;    //角色编号
    private Date createTime;
    private Date updateTime;
}
