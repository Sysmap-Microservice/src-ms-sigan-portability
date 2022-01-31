package com.sysmap.srcmssignportability.framework.adapters.in.services;

import com.sysmap.srcmssignportability.application.ports.in.SignPortabilityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class KafkaConsumerServiceImpl {

    @Autowired
    private SignPortabilityService signPortabilityService;

    @Value("${cloudkarafka.topic}")
    private String topicName;

    @KafkaListener(topics = "${cloudkarafka.topic}")
    public void consume(ConsumerRecord<String, String> payload){
        log.info("Tópico: {}", topicName);
        log.info("key: {}", payload.key());
        log.info("Headers: {}", payload.headers());
        log.info("Partion: {}", payload.partition());
        log.info("Order: {}", payload.value());

        signPortabilityService.savePortabilityInfo(payload.value());

    }
}
