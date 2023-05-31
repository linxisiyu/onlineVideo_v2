package com.zz.blibili.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;

import java.util.Date;

/**
 * @author ZhangZhe
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String phone;
    private String email;
    private String password;
    private String salt;
    private Date createTime;
    private Date updateTime;
    private UserInfo userInfo;
}