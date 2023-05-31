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
public class File {
    private Long id;
    private String url;
    private String type;
    private String md5;
    private Date createTime;
}
