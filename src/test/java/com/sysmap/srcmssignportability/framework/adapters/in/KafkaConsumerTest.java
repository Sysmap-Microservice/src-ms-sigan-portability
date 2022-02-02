package com.sysmap.srcmssignportability.framework.adapters.in;

import com.sysmap.srcmssignportability.framework.adapters.in.dto.PortabilityInputKafka;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.TopicPartition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.ConsumerSeekAware;
import org.springframework.test.context.junit4.SpringRunner;
import com.sysmap.srcmssignportability.SrcMsSignPortabilityApplication;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;

@RunWith(SpringRunner.class)

@SpringBootTest(
        classes = SrcMsSignPortabilityApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)

class KafkaConsumerTest {

	MockConsumer<String, String> consumer;

	@Autowired
	public Config config;

	@BeforeEach
	public void setUp() {
		consumer = new MockConsumer<String, String>(OffsetResetStrategy.EARLIEST);
	}

	@Test
	public void verifyWrongTopicAlreadyInRemoteKafka() throws InterruptedException {
		assertThat(config.latch.await(20, TimeUnit.SECONDS)).isTrue();
		assertThat(config.received.get(0)).isEqualTo("{\"number\":\"44sd1558478995\",\"documentNumber\":\"441558478995\",\"portability\":{\"portabilityId\":\"7c8b4e1c-8ed6-48f3-b67f-587b4de62bf6\",\"source\":\"CLARO\",\"target\":\"VIVO\"}}");
	}

	@Test
	public void verifyFirstTopicAlreadyInRemoteKafka() throws InterruptedException {
		assertThat(config.latch.await(20, TimeUnit.SECONDS)).isTrue();
		assertThat(config.received.get(0)).isEqualTo("{\"number\":\"931313434\",\"documentNumber\":\"441558478995\",\"portability\":{\"portabilityId\":\"b5e1a821-a637-4a3a-b207-01b9f09abc7a\",\"source\":\"CLARO\",\"target\":\"VIVO\"}}");
	}

	@Configuration
	public static class Config implements ConsumerSeekAware {

		List<String> received = new ArrayList<>();
		CountDownLatch latch = new CountDownLatch(3);

		@KafkaListener(topics = "${cloudkarafka.topic}")
		public void listen(String in) {
			System.out.println(in);
			this.received.add(in);
			this.latch.countDown();
		}

		@Override
		public void registerSeekCallback(ConsumerSeekCallback callback) {
		}

		@Override
		public void onPartitionsAssigned(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
			System.out.println("Seeking to beginning");
			assignments.keySet().forEach(tp -> callback.seekToBeginning(tp.topic(), tp.partition()));
		}

		@Override
		public void onIdleContainer(Map<TopicPartition, Long> assignments, ConsumerSeekCallback callback) {
		}
	}
}