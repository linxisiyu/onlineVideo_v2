package com.zz.blibili.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author ZhangZhe
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Danmu {
    private Long id;
    private Long userId;
    private Long videoId;
    private String content;
    private String danmuTime;
    private Date createTime;

}
