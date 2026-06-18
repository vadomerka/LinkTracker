package backend.academy.linktracker.bot.kafka;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import backend.academy.linktracker.bot.configuration.BotKafkaConsumerConfiguration;
import backend.academy.linktracker.bot.controllers.KafkaBotController;
import backend.academy.linktracker.bot.properties.KafkaReceiverProperties;
import backend.academy.linktracker.bot.services.BotChatManager;
import backend.academy.linktracker.bot.services.LinkUpdateMessageProcessor;
import java.time.Duration;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.parallel.Execution;
import org.junit.jupiter.api.parallel.ExecutionMode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
        classes = {
            KafkaReceiverProperties.class,
            KafkaBotController.class,
            LinkUpdateMessageProcessor.class,
            BotKafkaConsumerConfiguration.class
        })
@Import(KafkaIntegrationTestConfiguration.class)
@EnableKafka
@ActiveProfiles("test")
@Execution(ExecutionMode.SAME_THREAD)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class KafkaConsumerRetryDlqIntegrationTest {

    @MockitoBean
    private BotChatManager botChatManager;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private KafkaReceiverProperties kafkaReceiverProperties;

    @DynamicPropertySource
    static void kafkaConsumerProperties(DynamicPropertyRegistry registry) {
        String topic = "retry-integration-" + UUID.randomUUID();
        String dlqTopic = "retry-integration-dlq-" + UUID.randomUUID();
        String group = "retry-group-" + UUID.randomUUID();
        registry.add("app.kafka.topic", () -> topic);
        registry.add("app.kafka.dlq-topic", () -> dlqTopic);
        registry.add("app.kafka.group", () -> group);
        registry.add("app.kafka.retry.max-attempts", () -> "3");
        registry.add("app.kafka.retry.backoff-ms", () -> "200");
        registry.add("spring.kafka.bootstrap-servers", KafkaIntegrationTestConfiguration::bootstrapServers);
        registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");
        registry.add("spring.kafka.consumer.key-deserializer", () -> StringDeserializer.class.getName());
        registry.add(
                "spring.kafka.consumer.value-deserializer",
                () -> "org.springframework.kafka.support.serializer.ErrorHandlingDeserializer");
        registry.add(
                "spring.kafka.consumer.properties.spring.deserializer.key.delegate.class",
                () -> StringDeserializer.class.getName());
        registry.add(
                "spring.kafka.consumer.properties.spring.deserializer.value.delegate.class",
                () -> StringDeserializer.class.getName());
    }

    @Test
    void processingFailure_retriesAndSendsMessageToDlq() {
        doThrow(new RuntimeException("telegram unavailable"))
                .when(botChatManager)
                .processUpdate(any());

        String payload = """
            {"chatId":100,"links":[{"url":"https://stackoverflow.com/questions/1","lastUpdate":"2024-02-01T12:00:00Z","data":{"data":[]}}]}
            """;

        kafkaTemplate.send(kafkaReceiverProperties.getTopic(), payload);

        await().atMost(Duration.ofSeconds(20))
                .pollInterval(Duration.ofMillis(300))
                .untilAsserted(() -> assertThat(countDlqMessages()).isEqualTo(1));

        verify(botChatManager, times(3)).processUpdate(any());
    }

    @Test
    void invalidJson_sendsMessageToDlqWithoutRetry() {
        kafkaTemplate.send(kafkaReceiverProperties.getTopic(), "{invalid-json");

        await().atMost(Duration.ofSeconds(15))
                .pollInterval(Duration.ofMillis(300))
                .untilAsserted(() -> assertThat(countDlqMessages()).isEqualTo(1));

        verify(botChatManager, times(0)).processUpdate(any());
    }

    @Test
    void validationFailure_sendsMessageToDlqWithoutRetry() {
        kafkaTemplate.send(kafkaReceiverProperties.getTopic(), "{\"chatId\":100,\"links\":[]}");

        await().atMost(Duration.ofSeconds(15))
                .pollInterval(Duration.ofMillis(300))
                .untilAsserted(() -> assertThat(countDlqMessages()).isEqualTo(1));

        verify(botChatManager, times(0)).processUpdate(any());
    }

    @Test
    void deserializationFailure_sendsMessageToDlqWithoutRetry() {
        publishInvalidBytes();

        await().atMost(Duration.ofSeconds(15))
                .pollInterval(Duration.ofMillis(300))
                .untilAsserted(() -> assertThat(countDlqMessages()).isEqualTo(1));

        verify(botChatManager, times(0)).processUpdate(any());
    }

    private int countDlqMessages() {
        var consumerProps = consumerProperties();
        var dlqTopic = kafkaReceiverProperties.getDlqTopic();
        var partition = new TopicPartition(dlqTopic, 0);

        try (var consumer = new KafkaConsumer<String, String>(consumerProps)) {
            consumer.assign(List.of(partition));
            consumer.seekToBeginning(List.of(partition));

            var records = consumer.poll(Duration.ofSeconds(5));
            return records.count();
        }
    }

    private Properties consumerProperties() {
        var consumerProps = new Properties();
        consumerProps.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaTemplate
                        .getProducerFactory()
                        .getConfigurationProperties()
                        .get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        return consumerProps;
    }

    private void publishInvalidBytes() {
        var producerProps = new Properties();
        producerProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaTemplate
                        .getProducerFactory()
                        .getConfigurationProperties()
                        .get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        producerProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());
        producerProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class.getName());

        try (var producer = new KafkaProducer<byte[], byte[]>(producerProps)) {
            producer.send(new ProducerRecord<>(
                    kafkaReceiverProperties.getTopic(),
                    new byte[] {(byte) 0xC3, (byte) 0x28},
                    new byte[] {(byte) 0xFF, (byte) 0xFE, (byte) 0xFD}));
            producer.flush();
        }
    }
}
