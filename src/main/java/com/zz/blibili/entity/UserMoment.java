package com.zz.blibili.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;

import java.util.Date;

/**
 * @author ZhangZhe
 * 用户动态提醒
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserMoment {
    private Long id;
    private Long userId;
    private String type;        //动态类型
    private Long contentId;     //动态类型
    private Date createTime;
    private Date updateTime;
}
