package com.sysmap.srcmssignportability.framework.adapters.in.services;

import com.sysmap.srcmssignportability.framework.adapters.in.dto.PortabilityInputKafkaNewForKafkaTests;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;

import java.time.Duration;
import java.util.Collections;
import java.util.stream.StreamSupport;

public class KafkaConsumerServiceImplNewForKafkaTests {

    private final Consumer<String, String> consumer;
    private final java.util.function.Consumer<Throwable> exceptionConsumer;
    private final java.util.function.Consumer<PortabilityInputKafkaNewForKafkaTests> portabilityInputKafkaConsumerNew;

    // standard constructor
    public KafkaConsumerServiceImplNewForKafkaTests(Consumer<String, String> consumer, java.util.function.Consumer<Throwable> exceptionConsumer, java.util.function.Consumer<PortabilityInputKafkaNewForKafkaTests> portabilityInputKafkaConsumer) {
        this.consumer = consumer;
        this.exceptionConsumer = exceptionConsumer;
        this.portabilityInputKafkaConsumerNew = portabilityInputKafkaConsumer;
    }

    public void startBySubscribing(String topic) {
        consume(() -> consumer.subscribe(Collections.singleton(topic)));
    }

    public void startByAssigning(String topic, int partition) {
        consume(() -> consumer.assign(Collections.singleton(new TopicPartition(topic, partition))));
    }

    private void consume(Runnable beforePollingTask) {
        try {
            beforePollingTask.run();
            while (true) {
                ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(1000));
                StreamSupport.stream(records.spliterator(), false)
                        .map(record -> new PortabilityInputKafkaNewForKafkaTests(record.key(), record.value()))
                        .forEach(portabilityInputKafkaConsumerNew);
                consumer.commitSync();
            }
        } catch (WakeupException e) {
            System.out.println("Shutting down...");
        } catch (RuntimeException ex) {
            exceptionConsumer.accept(ex);
        } finally {
            consumer.close();
        }
    }

    public void stop() {
        consumer.wakeup();
    }
}
