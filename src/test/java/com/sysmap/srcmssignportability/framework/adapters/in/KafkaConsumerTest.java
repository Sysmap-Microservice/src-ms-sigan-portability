package com.sysmap.srcmssignportability.framework.adapters.in;

import com.sysmap.srcmssignportability.domain.entities.Portability;
import com.sysmap.srcmssignportability.framework.adapters.in.dto.PortabilityInputKafkaNewForKafkaTests;
import com.sysmap.srcmssignportability.framework.adapters.in.services.KafkaConsumerServiceImplNewForKafkaTests;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.MockConsumer;
import org.apache.kafka.clients.consumer.OffsetResetStrategy;
import org.apache.kafka.common.KafkaException;
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

@RunWith(SpringRunner.class)

@SpringBootTest(
        classes = SrcMsSignPortabilityApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)

class KafkaConsumerTest {

	private static final String TOPIC = "i28hrd7l-portability";
	private static final int PARTITION = 0;
	private MockConsumer<String,String> consumer;
	private List<PortabilityInputKafkaNewForKafkaTests> updates;
	private KafkaConsumerServiceImplNewForKafkaTests kafkaConsumerService;
	private Throwable pollException;

	@Autowired
	public Config config;

	@BeforeEach
	void setUp() {
		consumer = new MockConsumer<>(OffsetResetStrategy.EARLIEST);
		updates = new ArrayList<>();
		kafkaConsumerService = new KafkaConsumerServiceImplNewForKafkaTests(consumer,
				ex -> this.pollException = ex, updates::add);
	}

	@Test
	void whenStartingByAssigningTopicPartition_thenExpectUpdatesAreConsumedCorrectly() {
		consumer.schedulePollTask(() -> consumer.addRecord(record(TOPIC, PARTITION,"931313434","441558478995",null)));
		consumer.schedulePollTask(() -> kafkaConsumerService.stop());

		HashMap<TopicPartition, Long> startOffsets = new HashMap<>();
		TopicPartition tp = new TopicPartition(TOPIC, PARTITION);
		startOffsets.put(tp, 0L);
		consumer.updateBeginningOffsets(startOffsets);

		kafkaConsumerService.startByAssigning(TOPIC, PARTITION);

		assertThat(consumer.closed()).isTrue();
		System.out.println(this.updates);
	}

	@Test
	void whenStartingBySubscribingToTopic_thenExpectUpdatesAreConsumedCorrectly() {
		consumer.schedulePollTask(() -> {
			consumer.rebalance(Collections.singletonList(new TopicPartition(TOPIC, 0)));
			consumer.addRecord(record(TOPIC, PARTITION,"931313434","441558478995", null));
		});
		consumer.schedulePollTask(() -> kafkaConsumerService.stop());

		HashMap<TopicPartition, Long> startOffsets = new HashMap<>();
		TopicPartition tp = new TopicPartition(TOPIC, PARTITION);
		startOffsets.put(tp, 0L);
		consumer.updateBeginningOffsets(startOffsets);

		kafkaConsumerService.startBySubscribing(TOPIC);

		assertThat(updates).asList().isNotNull();
		assertThat(consumer.closed()).isTrue();
		System.out.println(this.updates);
	}

	@Test
	void whenStartingBySubscribingToTopicAndExceptionOccurs_thenExpectExceptionIsHandledCorrectly() {
		consumer.schedulePollTask(() -> consumer.setPollException(new KafkaException("poll exception")));
		consumer.schedulePollTask(() -> kafkaConsumerService.stop());

		HashMap<TopicPartition, Long> startOffsets = new HashMap<>();
		TopicPartition tp = new TopicPartition(TOPIC, 0);
		startOffsets.put(tp, 0L);
		consumer.updateBeginningOffsets(startOffsets);

		kafkaConsumerService.startBySubscribing(TOPIC);

		assertThat(pollException).isInstanceOf(KafkaException.class).hasMessage("poll exception");
		assertThat(consumer.closed()).isTrue();
		System.out.println(this.updates);
	}

	@Test
	public void verifyWrongTopicAlreadyInRemoteKafka() throws InterruptedException {
		assertThat(config.latch.await(20, TimeUnit.SECONDS)).isTrue();
		assertThat(config.received.get(0)).isNotEqualTo("{\"number\":\"44sd1558478995\",\"documentNumber\":\"441558478995\",\"portability\":{\"portabilityId\":\"7c8b4e1c-8ed6-48f3-b67f-587b4de62bf6\",\"source\":\"CLARO\",\"target\":\"VIVO\"}}");
	}

	@Test
	public void verifyFirstTopicAlreadyInRemoteKafka() throws InterruptedException {
		assertThat(config.latch.await(20, TimeUnit.SECONDS)).isTrue();
		assertThat(config.received.get(0)).isNotNull();
	}

	private ConsumerRecord<String, String> record(String topic, int partition, String number, String documentNumber, Portability portability) {
		return new ConsumerRecord<>(topic, partition, 0, number, documentNumber);
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