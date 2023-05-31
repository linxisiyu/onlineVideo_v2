package com.zz.blibili.websocket;

import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.RateLimiter;
import com.zz.blibili.entity.Danmu;
import com.zz.blibili.entity.constant.UserMomentsConstant;
import com.zz.blibili.utils.TokenUtil;
import io.netty.util.internal.StringUtil;

import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.KafkaListeners;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ZhangZhe
 */
@Component
@ServerEndpoint("/imserver/{token}")
public class WebSocketService {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    @Autowired
    private MongoTemplate mongoTemplate;
    private static final RateLimiter rateLimiter = RateLimiter.create(10.0);

    private final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static final AtomicInteger ONLINE_COUNT = new AtomicInteger(0);
    public static final ConcurrentHashMap<String, WebSocketService> WEBSOCKET_MAP = new ConcurrentHashMap<>();
    private Session session;
    private String sessionId;
    private Long userId;

    public static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext applicationContext){
        WebSocketService.applicationContext = applicationContext;
        LinkedList<Integer> stack = new LinkedList<>();
    }


    //打开链接
    @OnOpen
    public void openConnect(Session session, @PathParam("token") String token){
        try {
            this.sessionId = session.getId();
        }catch (Exception ignored){}
        this.session = session;
        this.userId = TokenUtil.verifyToken(token);
        if (WEBSOCKET_MAP.containsKey(sessionId)){
            WEBSOCKET_MAP.remove(sessionId);
            WEBSOCKET_MAP.put(sessionId, this);
        }else {
            WEBSOCKET_MAP.put(sessionId, this);
            ONLINE_COUNT.getAndIncrement();
        }
        logger.info("用户连接成功：" + sessionId + ", 当前在线人数为：" + ONLINE_COUNT.get());
        try {
            this.sendMessage("0");
        }catch (Exception e){
            logger.error("连接异常");
        }
    }


    //关闭连接
    @OnClose
    public void closeConnect(){
        if (WEBSOCKET_MAP.containsKey(sessionId)){
            WEBSOCKET_MAP.remove(sessionId);
            ONLINE_COUNT.getAndDecrement();
        }
        logger.info("用户退出：" + sessionId + "当前在线人数为：" + ONLINE_COUNT.get());
    }

    @OnMessage
    public void onMessage(String message) {
        logger.info("用户信息：" + session);
        if (!StringUtil.isNullOrEmpty(message)) {
            try {
                //群发消息
                for (Map.Entry<String, WebSocketService> entry : WEBSOCKET_MAP.entrySet()) {
                    WebSocketService webSocketService = entry.getValue();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("message", message);
                    jsonObject.put("sessionId", webSocketService.getSessionId());
                }
                if (this.userId != null) {
                    // 保存弹幕到 Redis
                    String danmuKey = String.format("danmu:%s:%s", this.userId, System.currentTimeMillis());
                    redisTemplate.opsForValue().set(danmuKey, message);
                    // 异步保存弹幕到 MongoDB
                    Danmu danmu = JSONObject.parseObject(message, Danmu.class);
                    danmu.setUserId(userId);
                    danmu.setCreateTime(new Date());
                    CompletableFuture.supplyAsync(() -> {
                        try {
                            kafkaTemplate.send(UserMomentsConstant.TOPIC_DANMUS_PERSIST, JSONObject.toJSONString(danmu));
                        } catch (Exception e) {
                            logger.error("保存弹幕到 Kafka 出错", e);
                        }
                        return null;
                    });
                }
            } catch (Exception e) {
                logger.error("弹幕接收出现问题");
                e.printStackTrace();
            }
        }
    }

    @KafkaListener(topics = UserMomentsConstant.TOPIC_DANMUS_PERSIST)
    public void consumeDanmu(String message) {
        Danmu danmu = JSONObject.parseObject(message, Danmu.class);
        danmu.setCreateTime(new Date());
        CompletableFuture.supplyAsync(() -> {
            try {
                rateLimiter.acquire();
                mongoTemplate.insert(danmu);
            } catch (Exception e) {
                logger.error("保存弹幕到 MongoDB 出错", e);
            }
            return null;
        });
    }


    @OnError
    public void onError(Throwable error){

    }

    public void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //在线人数查询
    @Scheduled(fixedRate = 5000)
    private void noticeOnlineCount0() throws IOException{
        for(Map.Entry<String, WebSocketService> entry : WebSocketService.WEBSOCKET_MAP.entrySet()){
            WebSocketService webSocketService = entry.getValue();
            if (webSocketService.session.isOpen()){
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("onlineCount", ONLINE_COUNT.get());
                jsonObject.put("msg", "当前在线人数为：" + ONLINE_COUNT.get());
                webSocketService.sendMessage(jsonObject.toJSONString());
            }
        }

    }

    public Session getSession() {
        return session;
    }

    public String getSessionId() {
        return sessionId;
    }
}
