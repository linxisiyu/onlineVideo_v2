package com.zz.blibili.service;

import com.alibaba.fastjson.JSONArray;
import com.zz.blibili.config.KafkaConfig;
import com.zz.blibili.entity.UserMoment;
import com.zz.blibili.entity.constant.UserMomentsConstant;
import com.zz.blibili.mapper.UserMomentsMapper;
import org.apache.catalina.core.ApplicationContext;
import org.apache.ibatis.annotations.Param;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.applet.AppletContext;
import java.util.Date;
import java.util.List;

/**
 * @author ZhangZhe
 */
@Service
public class UserMomentsService {
    @Autowired
    private UserMomentsMapper userMomentsMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private KafkaConfig kafkaConfig;

    public void addUserMoments(UserMoment userMoment) throws Exception{
        //将发布的动态存到数据库当中
        userMoment.setCreateTime(new Date());
        userMomentsMapper.addUserMoments(userMoment);
        //发送到生产者
        kafkaConfig.momentsProducer(userMoment);
        //发送到redis中让粉丝拿到
        kafkaConfig.momentsConsumer(userMoment);
    }

    @KafkaListener(topics = UserMomentsConstant.TOPIC_MOMENTS)
    public List<UserMoment> getUserSubscribedMoments(Long userId) {
        //将动态由生产者发送到redis
        String key = "subscribe:" + userId;
        String listStr = redisTemplate.opsForValue().get(key);
        return JSONArray.parseArray(listStr, UserMoment.class);
    }
}
