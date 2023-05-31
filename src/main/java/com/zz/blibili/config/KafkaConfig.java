package com.zz.blibili.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zz.blibili.entity.UserFollowing;
import com.zz.blibili.entity.UserMoment;
import com.zz.blibili.entity.constant.UserMomentsConstant;
import com.zz.blibili.exception.ConditionException;
import com.zz.blibili.service.UserFollowingService;
import io.netty.util.internal.StringUtil;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;

import java.awt.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Configuration
public class KafkaConfig {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private UserFollowingService userFollowingService;

    //生产者：去主题发送消息
    public void momentsProducer(UserMoment userMoment){
        //将用户动态发送到指定主题
        kafkaTemplate.send(UserMomentsConstant.TOPIC_MOMENTS, JSONObject.toJSONString(userMoment));
    }

    //消费者：发送数据到redis中
    @KafkaListener(topics = UserMomentsConstant.TOPIC_MOMENTS)
    public void momentsConsumer(UserMoment userMoment) throws Exception{   //参数收到的value
        //从kafka中得到up主发的动态
        if (userMoment == null) throw new ConditionException("消息内容为空！");
        //得到用户粉丝
        Long userId = userMoment.getUserId();
        List<UserFollowing> fanList = userFollowingService.getUserFans(userId);
        for (UserFollowing fan : fanList) {
            //设置redis的key，粉丝的id，保存redis中
            String key = "subscribe:" + fan.getUserId();
            String str = redisTemplate.opsForValue().get(key);  //看粉丝用户里面是否已经有动态
            List<UserMoment> subscribedList;
            //将up主发的动态拼接到用户已有的动态里面
            if (StringUtil.isNullOrEmpty(str)) subscribedList = new ArrayList<>();
            else subscribedList = JSONArray.parseArray(str, UserMoment.class);
            subscribedList.add(userMoment);
            //将这个动态更新
            redisTemplate.opsForValue().set(key, JSONObject.toJSONString(subscribedList));
        }
    }
}
