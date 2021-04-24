package com.eamon;

import org.apache.kafka.clients.consumer.*;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.Test;

import java.rmi.MarshalledObject;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author eamonzzz
 * @date 2021-04-21 21:30
 */
public class KafkaDemo {

    private static final String BOOTSTRAP_SERVERS = "kafka1:9092,kafka2:9093,kafka3:9094";
    /**
     * kafka-topics.sh --bootstrap-server kafka1:9092 --create --topic kafka-simple-api --partitions 2 --replication-factor 2
     */
    private static final String TOPIC = "kafka-simple-api";

    @Test
    public void producer() {

        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        // kafka 是一个持久化数据的MQ  数据-> byte[]，不会对数据进行干预，双方要约定编解码
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "-1");

        //properties.setProperty()
        // 创建消息生产者
        KafkaProducer<String,String> kafkaProducer = new KafkaProducer<>(properties);

        // 发送消息
        // 模拟 发送 A-1 B-1 C-1 A-2 B-2 C-2 A-3 B-3 C-3 这种排列的消息
        for (; ; ) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    String key = "iterm" + j;
                    String value = "value-" + i;
                    System.out.println(key);
                    ProducerRecord<String, String> producerRecord = new ProducerRecord<>(TOPIC, key, value);

                    try {
                        Future<RecordMetadata> send = kafkaProducer.send(producerRecord);
                        RecordMetadata recordMetadata = send.get();
                        int partition = recordMetadata.partition();
                        long offset = recordMetadata.offset();
                        System.out.println("消息：" + key + " = " + value + " 发送到 Partition: " + partition + " 成功, offset: " + offset);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }

                }
            }
        }


    }

    @Test
    public void consumer() {
        Properties properties = new Properties();
        // 基础配置
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, BOOTSTRAP_SERVERS);
        // kafka 是一个持久化数据的MQ  数据-> byte[]，不会对数据进行干预，双方要约定编解码
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        // 细节配置
        // 消费者是从属于消费者组的，所以需要创建组
        /*
        kafka-consumer-groups.sh --bootstrap-server kafka1:9092 --list
        # 查看 消费组的描述信息
        kafka-consumer-groups.sh --bootstrap-server kafka1:9092 --describe --group consumer1

        GROUP           TOPIC            PARTITION  CURRENT-OFFSET  LOG-END-OFFSET  LAG             CONSUMER-ID                                               HOST            CLIENT-ID
        consumer1       kafka-simple-api 0          -               100             -               consumer-consumer1-1-decbb045-5fa0-4472-a03c-d22e79c15730 /10.211.55.2    consumer-consumer1-1
        consumer1       kafka-simple-api 1          -               200             -               consumer-consumer1-1-decbb045-5fa0-4472-a03c-d22e79c15730 /10.211.55.2    consumer-consumer1-1
         */
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "consumer2");
        // kafka 是一个 MQ  也是一个 存储，那么应该从哪里拿数据？又从哪个位置拿？
        // 当Kafka中没有初始偏移量或服务器上不再存在当前偏移量时该怎么办？所以需要显示的告诉消费者 offset 的获取规则：
        // latest：如果没找到offset，则自动将偏移量重置为最新的偏移量
        // earliest：如果没找到offset，则自动将偏移量重置为最早的偏移量
        // none：如果未找到该消费者组的先前偏移量，则向该消费者抛出异常
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        // 关闭自动提交
        // 自动提交是一个异步的提交，容易造成 重复和丢失
        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");
        // 如果自动提交，有如下的配置可选，默认5秒提交一次
        //properties.setProperty(ConsumerConfig.AUTO_COMMIT_INTERVAL_MS_CONFIG, "");

        // poll 时，每次拉取多少，可按需要进行配置，默认500
        //properties.setProperty(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, "10");

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);

        // 订阅 topic  -- 上面相当于连接MySQL的基础配置，这里的订阅相当于指明要使用哪个库
        kafkaConsumer.subscribe(Collections.singletonList(TOPIC), new ConsumerRebalanceListener() {
            // 当消费者 增加或被踢出时触发的回调
            @Override
            public void onPartitionsRevoked(Collection<TopicPartition> collection) {
                System.out.println(">>> onPartitionsRevoked");
            }

            @Override
            public void onPartitionsAssigned(Collection<TopicPartition> collection) {
                System.out.println(">>> onPartitionsAssigned");
            }
        });

        while (true) {
            /*
            常识：如果想多线程处理多分区
            每 poll 一次，用一个语义：一个job启动
            一次job用多线程并行处理分区
            且，job 应该被控制为串行的，一个job没执行完时，后序的job应该要等待
             */

            // 去获取 消息
            // 可配置获取的超时时间
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(100));
            System.out.println(">>>>>>>>>>  " + consumerRecords.count());
            // 如果有数据再进行处理
            // ！！！ 优化 消费
            if (!consumerRecords.isEmpty()) {

                Set<TopicPartition> topicPartitions = consumerRecords.partitions();

                /*
                  如果手动提交 offset
                  1，按消息进度同步提交
                  2，按分区粒度同步提交
                  3，按当前poll的批次进行提交

                  思考：如果在多线程环境下
                  1，以上1，3的方式不用多线程
                  2，以上2的方式最容易想到多线程方式处理，会不会有问题？ 多线程按照分区消费时  没有问题，因为 kafka 的offset 是按照分区粒度进行维护的

                 */
                for (TopicPartition topicPartition : topicPartitions) {
                    // 按分区消费
                    List<ConsumerRecord<String, String>> records = consumerRecords.records(topicPartition);
                    for (ConsumerRecord<String, String> record : records) {
                        String recordStr = record.toString();
                        System.out.println(recordStr);

                        // 1. 按 消息的粒度进行提交,这个是最安全的，每条记录级的更新，单线程多线程都可以
                        //long offset = record.offset();
                        //HashMap<TopicPartition, OffsetAndMetadata> map = new HashMap<>(1);
                        //map.put(topicPartition, new OffsetAndMetadata(offset));
                        //kafkaConsumer.commitSync(map);
                    }
                    // 2. 按 分区粒度 提交
                    // 因为你都分区了，拿到了分区的数据集，期望的是先对数据做整体的加工，这个时候会出现小问题？
                    // -- 你怎么知道最后一条消息的offset？
                    // kafka 很傻，你拿走了多少，kafka不关心，由消费者来告诉kafka正确的最后一个小的offset

                    // 获取 分区内 最后一条的 offset
                    long offset = records.get(records.size() - 1).offset();
                    HashMap<TopicPartition, OffsetAndMetadata> map = new HashMap<>(1);
                    map.put(topicPartition, new OffsetAndMetadata(offset));
                    kafkaConsumer.commitSync(map);
                }
            }
            // 3. 按 poll 的批次提交
            //kafkaConsumer.commitSync();
        }

    }


}
