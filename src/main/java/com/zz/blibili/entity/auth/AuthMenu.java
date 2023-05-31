package com.zz.blibili.entity.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户菜单
    id      bigint NOT NULL     主键id
    name    varchar(255) NULL   菜单项目名称
    code    varchar(50) NULL    唯一编码
    createTime  datetime NULL   创建时间
    updateTime  datetime NULL   更新时间
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthMenu {
    private Long id;
    private String name;
    private String code;
    private Date createTime;
    private Date updateTime;
}
