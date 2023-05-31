package com.zz.blibili.mapper;

import com.zz.blibili.entity.Video;
import com.zz.blibili.entity.VideoComment;
import com.zz.blibili.entity.VideoTag;
import com.zz.blibili.entity.VideoView;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface VideoMapper {
    Integer addVideos(Video video);

    Integer batchAddVideoTags(List<VideoTag> videoTagList);

    Integer pageCountVideo(HashMap<String, Object> params);

    List<Video> pageListVideo(HashMap<String, Object> params);

    Video getVideoById(Long videoId);

    Integer pageCountVideoComments(Map<String, Object> params);

    List<VideoComment> pageListVideoComments(Map<String, Object> params);

    List<VideoComment> batchGetVideoCommentsByRootIds(List<Long> parentIdList);

    VideoView getVideoView(HashMap<String, Object> params);

    void addVideoView(String videoId);

    Integer getVideoViewCounts(Long videoId);
}
