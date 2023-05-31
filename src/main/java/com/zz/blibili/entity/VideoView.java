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
public class VideoView {
    private String id;
    private String videoId;
    private Long userId;
    private String clientId;
    private String ip;
    private Date createTime;

}
