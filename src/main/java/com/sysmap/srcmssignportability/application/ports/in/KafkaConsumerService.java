package com.sysmap.srcmssignportability.application.ports.in;

import org.apache.kafka.clients.consumer.ConsumerRecord;

public interface KafkaConsumerService {

    void consume(ConsumerRecord<String, String> payload);
}
