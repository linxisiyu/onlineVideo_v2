package com.zz.blibili.entity.auth;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * 用户（不同角色）可操作元素
 * elementName varchar(255) NULL    页面元素名称
 * elementCode varchar(50) NULL     页面元素唯一编码
 * operationType varchar(5) NULL    操作类型：0可点击 1可见
 * createTime datetime NULL         创建时间
 * updateTime datetime NULL         更新时间
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthElementOperation {
    private Long id;
    private String elementName;
    private String elementCode;
    private String operationType;
    private Date createTime;
    private Date updateTime;
}
