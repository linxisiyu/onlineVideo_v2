package com.zz.blibili.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author ZhangZhe
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInfo {
    private Long id;
    private Long userId;
    private String nick;
    private String avatar;
    private String sign;
    private String gender;
    private String birth;
    private Date createTime;
    private Date updateTime;
    private Boolean followed;
}












