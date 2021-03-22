package com.eamon.springbootredisdemo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringbootRedisDemoApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(SpringbootRedisDemoApplication.class, args);
        RedisTest redisTest = ctx.getBeanFactory().getBean(RedisTest.class);

        redisTest.testRedis();

    }

}
