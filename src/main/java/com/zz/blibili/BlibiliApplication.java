package com.zz.blibili;

import com.zz.blibili.websocket.WebSocketService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableTransactionManagement
@EnableAsync
@EnableScheduling
public class BlibiliApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext app = SpringApplication.run(BlibiliApplication.class, args);
        WebSocketService.setApplicationContext(app);

    }

}
