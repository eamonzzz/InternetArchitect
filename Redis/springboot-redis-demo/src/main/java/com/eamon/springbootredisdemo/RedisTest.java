package com.eamon.springbootredisdemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.hash.Jackson2HashMapper;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author eamonzzz
 * @date 2021-03-22 20:02
 */
@Component
public class RedisTest {
    @Autowired
    RedisTemplate redisTemplate;

    @Autowired
    StringRedisTemplate stringRedisTemplate;

    @Autowired
    StringRedisTemplate redisTemplateWithJackson;

    @Autowired
    ObjectMapper objectMapper;

    public void testRedis() {
        redisTemplate.opsForValue().set("hello", "eamon");

        String hello = redisTemplate.opsForValue().get("hello").toString();
        System.out.println(hello);

        stringRedisTemplate.opsForValue().set("hello1", "world");
        String hello1 = stringRedisTemplate.opsForValue().get("hello1").toString();
        System.out.println(hello1);


        redisTemplate.opsForHash().put("eamon", "name", "eamon");
        redisTemplate.opsForHash().put("eamon", "age", 10);

        String eamon = redisTemplate.opsForHash().get("eamon", "name").toString();
        System.out.println(eamon);


        /*
        Exception in thread "main" java.lang.ClassCastException: java.lang.Integer cannot be cast to java.lang.String
            at org.springframework.data.redis.serializer.StringRedisSerializer.serialize(StringRedisSerializer.java:36)
            at org.springframework.data.redis.core.AbstractOperations.rawHashValue(AbstractOperations.java:185)
            at org.springframework.data.redis.core.DefaultHashOperations.put(DefaultHashOperations.java:189)
            at com.eamon.springbootredisdemo.RedisTest.testRedis(RedisTest.java:38)
            at com.eamon.springbootredisdemo.SpringbootRedisDemoApplication.main(SpringbootRedisDemoApplication.java:14)
         */
        //stringRedisTemplate.opsForHash().put("eamon", "name", "eamon");
        //stringRedisTemplate.opsForHash().put("eamon", "age", 10);
        //
        //String eamon1 = stringRedisTemplate.opsForHash().get("eamon", "name").toString();
        //System.out.println(eamon1);

        stringRedisTemplate.setHashValueSerializer(new Jackson2JsonRedisSerializer<Object>(Object.class));
        stringRedisTemplate.opsForHash().put("eamon", "name", "eamon");
        stringRedisTemplate.opsForHash().put("eamon", "age", 10);

        String eamon1 = stringRedisTemplate.opsForHash().get("eamon", "name").toString();
        System.out.println(eamon1);


        redisTemplateWithJackson.opsForHash().put("eamon2","name","lalala");
        redisTemplateWithJackson.opsForHash().put("eamon2","age",220);

        String eamon2 = redisTemplateWithJackson.opsForHash().get("eamon2", "name").toString();
        System.out.println(eamon2);

        Person person = new Person("eamon3",18);
        Jackson2HashMapper jhm = new Jackson2HashMapper(objectMapper, true);
        redisTemplateWithJackson.opsForHash().putAll("eamon3",jhm.toHash(person));

        Map eamon3 = redisTemplateWithJackson.opsForHash().entries("eamon3");
        Person person1 = objectMapper.convertValue(eamon3, Person.class);
        System.out.println(person1.toString());


        stringRedisTemplate.convertAndSend("eee","helloooo");
        stringRedisTemplate.getConnectionFactory().getConnection().subscribe(new MessageListener() {
            @Override
            public void onMessage(Message message, byte[] bytes) {
                byte[] body = message.getBody();
                System.out.println(new String(body));
            }
        },"eee".getBytes());

        while (true) {
            stringRedisTemplate.convertAndSend("eee","hello a ");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }


    }

}
