package com.zz.blibili.controller;

import com.zz.blibili.entity.*;
import com.zz.blibili.service.ElasticSearchService;
import com.zz.blibili.service.UserService;
import com.zz.blibili.service.VideoService;
import com.zz.blibili.utils.UserSupport;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;


@RestController
public class VideoController {
    @Autowired
    private VideoService videoService;
    @Autowired
    private UserSupport userSupport;
    @Autowired
    private ElasticSearchService elasticSearchService;

    //添加视频
    @PostMapping("/videos")
    public JsonResponse<String> addVideos(@RequestBody Video video){
        Long userId = userSupport.getCurrentUserId();
        video.setUserId(userId);
        videoService.addVideos(video);

        //在es中添加一条视频数据
        elasticSearchService.addVideo(video);
        return JsonResponse.success();
    }

    /**
     * 视频分页查询
     * @param size  总页数
     * @param no    第几页
     * @param area  视频分区
     * @return
     */
    @GetMapping("/getVideos")
    public JsonResponse<PageResult<Video>> pageListVideos(Integer size, Integer no, String area){
        PageResult<Video> result = videoService.pageListVideos(size, no, area);
        return new JsonResponse(result);
    }

    /*在线播放视频（请求下载视频）*/
    @GetMapping("/video-slices")
    public void viewVideoOnlineBySlices(HttpServletRequest request,
                                        HttpServletResponse response,
                                        String url) throws Exception {
        videoService.viewVideoOnlineBySlices(request, response, url);

    }

    /*添加播放记录*/
    @PostMapping("/video-views")
    public JsonResponse<String> addVideoView(@RequestBody VideoView videoView, HttpServletRequest request){
        Long userId;
        try {
            userId = userSupport.getCurrentUserId();
            videoView.setUserId(userId);
            videoService.addVideoView(videoView, request);
        }catch (Exception e){
            videoService.addVideoView(videoView, request);
        }
        return JsonResponse.success();
    }

    //查询视频播放量
    @GetMapping("/video-view-counts")
    public JsonResponse<Integer> getVideoViewCounts(@RequestParam Long videoId){
        Integer count = videoService.getVideoViewCounts(videoId);
        return new JsonResponse<>(count);
    }

    /*todo: 视频帧截取生成黑白剪映*/
//    @GetMapping("/video-frames")
//    public JsonResponse<List<VideoBinaryPicture>> captureVideoFrame(@RequestParam Long videoId,
//                                                                    @RequestParam String fileMd5){
//        List<VideoBinaryPicture> list = videoService.converVideoToImage(videoId, fileMd5);
//        return new JsonResponse<>(list);
//    }
}
