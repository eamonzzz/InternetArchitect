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

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * @author eamonzzz
 * @date 2021-04-21 21:30
 */
public class KafkaDemo {

    private static final String bootstrap_servers = "kafka1:9092,kafka2:9093,kafka3:9094";
    // kafka-topics.sh --bootstrap-server kafka1:9092 --create --topic kafka-simple-api --partitions 2 --replication-factor 2
    private static final String topic = "kafka-simple-api";

    @Test
    public void producer() {

        Properties properties = new Properties();
        properties.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap_servers);
        // kafka 是一个持久化数据的MQ  数据-> byte[]，不会对数据进行干预，双方要约定编解码
        properties.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        properties.setProperty(ProducerConfig.ACKS_CONFIG, "-1");

        //properties.setProperty()
        // 创建消息生产者
        KafkaProducer kafkaProducer = new KafkaProducer<>(properties);

        // 发送消息
        // 模拟 发送 A-1 B-1 C-1 A-2 B-2 C-2 A-3 B-3 C-3 这种排列的消息
        for (; ; ) {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    String key = "iterm" + j;
                    String value = "value-" + i;
                    System.out.println(key);
                    ProducerRecord<String, String> producerRecord = new ProducerRecord<>(topic, key, value);

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
        properties.setProperty(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrap_servers);
        // kafka 是一个持久化数据的MQ  数据-> byte[]，不会对数据进行干预，双方要约定编解码
        properties.setProperty(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        properties.setProperty(ConsumerConfig.GROUP_ID_CONFIG, "consumer1");
        //latest, earliest, none
        properties.setProperty(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");

        properties.setProperty(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, "false");

        KafkaConsumer<String, String> kafkaConsumer = new KafkaConsumer<>(properties);

        kafkaConsumer.subscribe(Collections.singletonList(topic), new ConsumerRebalanceListener() {
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
            ConsumerRecords<String, String> consumerRecords = kafkaConsumer.poll(Duration.ofMillis(10000));

            Set<TopicPartition> topicPartitions = consumerRecords.partitions();
            System.out.println("--- "+consumerRecords.count()+" ---");
            for (TopicPartition topicPartition : topicPartitions) {
                // 按分区消费
                List<ConsumerRecord<String, String>> records = consumerRecords.records(topicPartition);
                for (ConsumerRecord<String, String> record : records) {
                    String recordStr = record.toString();
                    System.out.println(recordStr);
                }
            }
        }

    }


}
