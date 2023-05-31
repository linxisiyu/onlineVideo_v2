package com.zz.blibili.service;

import com.zz.blibili.entity.*;
import com.zz.blibili.exception.ConditionException;
import com.zz.blibili.mapper.VideoMapper;
import com.zz.blibili.utils.FastDFSUtil;
import com.zz.blibili.utils.IpUtil;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.print.attribute.standard.MediaSize;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.annotation.Target;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author ZhangZhe
 */
@Service
public class VideoService {
    @Autowired
    private VideoMapper videoMapper;
    @Autowired
    private FastDFSUtil fastDFSUtil;
    @Autowired
    private UserService userService;

    /*添加视频*/
    @Transactional
    public void addVideos(Video video) {
        Date now = new Date();
        video.setCreateTime(new Date());
        videoMapper.addVideos(video);
        Long videoId = video.getId();
        List<VideoTag> tagList = video.getVideoTagList();
        tagList.forEach(item -> {
            item.setCreateTime(now);
            item.setVideoId(videoId);
        });
        videoMapper.batchAddVideoTags(tagList);
    }

    public PageResult<Video> pageListVideos(Integer size, Integer no, String area) {
        if (size == null || no == null) throw new ConditionException("参数异常！");
        HashMap<String, Object> params = new HashMap<>();   //存放分页的相关参数
        params.put("start", (no - 1) * size);   //起始位置
        params.put("limit", size);              //每页条数
        params.put("area", area);
        List<Video> list = new ArrayList<>();
        //查询符合分页条件的数量
        Integer total = videoMapper.pageCountVideo(params);
        if (total > 0) {
            list = videoMapper.pageListVideo(params);
        }
        return new PageResult<>(total, list);
    }


    public void viewVideoOnlineBySlices(HttpServletRequest request, HttpServletResponse response, String url) throws Exception {
        fastDFSUtil.viewVideoOnlineBySlices(request, response, url);
    }

    /*视频评论分页显示*/
    public PageResult<VideoComment> pageListVideoComments(Integer size, Integer no, Long videoId) {
        Video video = videoMapper.getVideoById(videoId);
        if(video == null){
            throw new ConditionException("非法视频！");
        }
        Map<String, Object> params = new HashMap<>();
        params.put("start", (no-1)*size);
        params.put("limit", size);
        params.put("videoId", videoId);
        //查询视频评论的总数
        Integer total = videoMapper.pageCountVideoComments(params);
        List<VideoComment> list = new ArrayList<>();

        if(total > 0){
            //查询一级评论，也就是rootId字段为空时的评论
            list = videoMapper.pageListVideoComments(params);
            //批量查询二级评论，先把一级评论的集合转换为的新的list，只包含评论id
            List<Long> parentIdList = list.stream().map(VideoComment::getId).collect(Collectors.toList());
            //根据二级评论的id去查询对应的二级评论实体类
            List<VideoComment> childCommentList = videoMapper.batchGetVideoCommentsByRootIds(parentIdList);

            //批量查询用户信息
            Set<Long> userIdList = list.stream().map(VideoComment::getUserId).collect(Collectors.toSet());
            Set<Long> replyUserIdList = childCommentList.stream().map(VideoComment::getReplyUserId).collect(Collectors.toSet());
            userIdList.addAll(replyUserIdList);

            //根据userid找到对应的用户详细信息，并转换为以userId为key，详细信息为value的map
            List<UserInfo> userInfoList = userService.batchGetUserInfoByUserIds(userIdList);
            Map<Long, UserInfo> userInfoMap =
                    userInfoList.stream().collect(Collectors.toMap(UserInfo :: getUserId, userInfo -> userInfo));

            //遍历一级评论列表，将二级评论的父id与一级评论的id相同的放在一起
            list.forEach(comment -> {
                Long id = comment.getId();
                List<VideoComment> childList = new ArrayList<>();
                childCommentList.forEach(child -> {
                    if(id.equals(child.getRootId())){
                        child.setUserInfo(userInfoMap.get(child.getUserId()));
                        child.setReplyUserInfo(userInfoMap.get(child.getReplyUserInfo()));
                        childList.add(child);
                    }
                });
                comment.setChildList(childList);
                comment.setUserInfo(userInfoMap.get(comment.getUserId()));
            });
        }
        return new PageResult<>(total, list);
    }

    //添加观看记录
    public void addVideoView(VideoView videoView, HttpServletRequest request) {
        Long userId = videoView.getUserId();
        String videoId = videoView.getVideoId();
        //生成clientId
        String agent = request.getHeader("User-Agent");
        UserAgent userAgent = UserAgent.parseUserAgentString(agent);
        String clientId = String.valueOf(userAgent.getId());
        String ip = IpUtil.getIP(request);
        HashMap<String, Object> params = new HashMap<>();
        if (userId != null){
            params.put("userId", userId);
        }else {
            params.put("ip", ip);
            params.put("clientId", clientId);
        }
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        params.put("today", sdf.format(now));
        params.put("videoId", videoId);

        //添加观看记录
        VideoView dbVideoView =videoMapper.getVideoView(params);
        if (dbVideoView == null) {
            videoView.setId(ip);
            videoView.setClientId(clientId);
            videoView.setClientId(clientId);
            videoView.setCreateTime(new Date());
            videoMapper.addVideoView(videoId);
        }

    }


    public Integer getVideoViewCounts(Long videoId) {
        return videoMapper.getVideoViewCounts(videoId);
    }

}
