package com.zz.blibili.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

/**
 * @author ZhangZhe
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VideoComment {
    private Long id;
    private Long videoId;
    private Long userId;
    private String comment;
    private Long replyUserId;
    private Long rootId;
    private Date createTime;
    private Date updateTime;
    private List<VideoComment> childList;
    private UserInfo userInfo;
    private UserInfo replyUserInfo;
}
