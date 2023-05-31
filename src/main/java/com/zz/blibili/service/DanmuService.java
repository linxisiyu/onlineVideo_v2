package com.zz.blibili.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zz.blibili.entity.Danmu;
import com.zz.blibili.mapper.DanmuMapper;
import io.netty.util.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author ZhangZhe
 */
@Service
public class DanmuService {
    @Autowired
    private DanmuMapper danmuMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    public void addDanmu(Danmu danmu){
        danmuMapper.addDanmu(danmu);
    }

    //异步调用
    @Async
    public void asyncAddDanmu(Danmu danmu){
        danmuMapper.addDanmu(danmu);
    }

    public List<Danmu> getDanmus(Map<String, Object> params){
        return danmuMapper.getDanmus(params);
    }

    /*添加弹幕到redis中*/
    public void addDanmusToRedis(Danmu danmu){
        String key = "danmu-video-" + danmu.getVideoId();
        String value = redisTemplate.opsForValue().get(key);
        List<Danmu> list = new ArrayList<>();
        if (!StringUtil.isNullOrEmpty(value)){
            list = JSONArray.parseArray(value, Danmu.class);
        }
        list.add(danmu);
        redisTemplate.opsForValue().set(key, JSONObject.toJSONString(danmu));
    }

}
