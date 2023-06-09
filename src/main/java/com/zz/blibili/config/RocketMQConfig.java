package com.zz.blibili.config;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zz.blibili.entity.UserFollowing;
import com.zz.blibili.entity.UserMoment;
import com.zz.blibili.entity.constant.UserMomentsConstant;
import com.zz.blibili.service.UserFollowingService;
import com.zz.blibili.websocket.WebSocketService;
import io.netty.util.internal.StringUtil;
import org.apache.rocketmq.client.consumer.DefaultMQPushConsumer;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyContext;
import org.apache.rocketmq.client.consumer.listener.ConsumeConcurrentlyStatus;
import org.apache.rocketmq.client.consumer.listener.MessageListener;
import org.apache.rocketmq.client.consumer.listener.MessageListenerConcurrently;
import org.apache.rocketmq.client.producer.DefaultMQProducer;
import org.apache.rocketmq.common.message.MessageExt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Configuration
public class RocketMQConfig {

    @Value("${rocketmq.name.server.address}")
    private String nameServerAddr;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private UserFollowingService userFollowingService;


    @Bean("danmuConsumer")
    public DefaultMQPushConsumer danmusConsumer() throws Exception{
        //实例化消费者
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(UserMomentsConstant.GROUP_DANMUS);
        //设置nameserver地址
        consumer.setNamesrvAddr(nameServerAddr);
        //订阅一个或多个topic，以及tag来过滤需要消费的信息
        consumer.subscribe(UserMomentsConstant.TOPIC_DANMUS, "*");
        //注册回调实现类处理从broker拉取回来的消息
        consumer.registerMessageListener((MessageListenerConcurrently) (msgs, consumeConcurrentlyContext) -> {
            MessageExt msg = msgs.get(0);
            byte[] msgByte = msg.getBody();
            String bodyStr = new String(msgByte);
            JSONObject jsonObject = JSONObject.parseObject(bodyStr);
            String sessionId = jsonObject.getString("sessionId");
            String message = jsonObject.getString("message");
            WebSocketService webSocketService = WebSocketService.WEBSOCKET_MAP.get(sessionId);
            if (webSocketService.getSession().isOpen()){
                try {
                    webSocketService.sendMessage(message);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            //标记该消息已被成功消费
            return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
        });
        consumer.start();
        return consumer;
    }


    @Bean("momentsProducer")
    public DefaultMQProducer momentsProducer() throws Exception{
        DefaultMQProducer producer = new DefaultMQProducer(UserMomentsConstant.GROUP_MOMENTS);
        producer.setNamesrvAddr(nameServerAddr);
        producer.start();
        return producer;
    }

    @Bean("momentsConsumer")
    public DefaultMQPushConsumer momentsConsumer() throws Exception{
        DefaultMQPushConsumer consumer = new DefaultMQPushConsumer(UserMomentsConstant.GROUP_MOMENTS);
        consumer.setNamesrvAddr(nameServerAddr);
        consumer.subscribe(UserMomentsConstant.TOPIC_MOMENTS, "*");
        consumer.registerMessageListener(new MessageListenerConcurrently() {
            @Override
            public ConsumeConcurrentlyStatus consumeMessage(List<MessageExt> messages, ConsumeConcurrentlyContext context){
                MessageExt msg = messages.get(0);
                if(msg == null){
                    return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
                }
                String bodyStr = new String(msg.getBody());
                UserMoment userMoment = JSONObject.toJavaObject(JSONObject.parseObject(bodyStr), UserMoment.class);
                Long userId = userMoment.getUserId();
                List<UserFollowing>fanList = userFollowingService.getUserFans(userId);
                for(UserFollowing fan : fanList){
                    String key = "subscribed-" + fan.getUserId();
                    String subscribedListStr = redisTemplate.opsForValue().get(key);
                    List<UserMoment> subscribedList;
                    if(StringUtil.isNullOrEmpty(subscribedListStr)){
                        subscribedList = new ArrayList<>();
                    }else{
                        subscribedList = JSONArray.parseArray(subscribedListStr, UserMoment.class);
                    }
                    subscribedList.add(userMoment);
                    redisTemplate.opsForValue().set(key, JSONObject.toJSONString(subscribedList));
                }
                return ConsumeConcurrentlyStatus.CONSUME_SUCCESS;
            }
        });
        consumer.start();
        return consumer;
    }
}
