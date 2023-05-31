package com.zz.blibili.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author ZhangZhe
 * 视频标签
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoTag {
    private Long id;
    private Long videoId;
    private Long tagId;
    private Date createTime;
}
