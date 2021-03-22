package com.eamon.springbootredisdemo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;

/**
 * @author eamonzzz
 * @date 2021-03-22 21:01
 */
@Configuration
public class RedisTemplateConfiguration {

    @Bean
    public StringRedisTemplate redisTemplateWithJackson(RedisConnectionFactory rcf) {
        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate(rcf);
        stringRedisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
        return stringRedisTemplate;
    }

}
