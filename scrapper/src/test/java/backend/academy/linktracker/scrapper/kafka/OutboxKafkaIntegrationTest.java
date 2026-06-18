package backend.academy.linktracker.scrapper.kafka;

import static org.assertj.core.api.Assertions.assertThat;

import backend.academy.linktracker.models.http.external.LinkUpdateData;
import backend.academy.linktracker.models.http.external.UpdateResponse;
import backend.academy.linktracker.models.http.internal.LinkUpdateRequest;
import backend.academy.linktracker.models.http.internal.LinkUpdateRequestItem;
import backend.academy.linktracker.scrapper.TestcontainersConfiguration;
import backend.academy.linktracker.scrapper.properties.KafkaSenderProperties;
import backend.academy.linktracker.scrapper.repositories.OutboxMessageRepository;
import backend.academy.linktracker.scrapper.services.outbox.OutboxPublisher;
import backend.academy.linktracker.scrapper.services.senders.OutboxKafkaBotRequestsSender;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Properties;
import java.util.UUID;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
@Import({KafkaIntegrationTestConfiguration.class, TestcontainersConfiguration.class})
@ActiveProfiles("test")
class OutboxKafkaIntegrationTest {

    @Autowired
    private OutboxKafkaBotRequestsSender outboxKafkaBotRequestsSender;

    @Autowired
    private OutboxPublisher outboxPublisher;

    @Autowired
    private OutboxMessageRepository outboxMessageRepository;

    @Autowired
    private KafkaSenderProperties kafkaSenderProperties;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @DynamicPropertySource
    static void outboxIntegrationProperties(DynamicPropertyRegistry registry) {
        String topic = "outbox-integration-" + UUID.randomUUID();
        String group = "outbox-group-" + UUID.randomUUID();
        registry.add("app.kafka.topic", () -> topic);
        registry.add("app.kafka.group", () -> group);
        registry.add("app.kafka.outbox.enabled", () -> "true");
        registry.add("spring.kafka.bootstrap-servers", KafkaIntegrationTestConfiguration::bootstrapServers);
        registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");
        registry.add(
                "spring.kafka.consumer.key-deserializer",
                () -> StringDeserializer.class.getName());
        registry.add(
                "spring.kafka.consumer.value-deserializer",
                () -> StringDeserializer.class.getName());
    }

    @Test
    void outboxWriter_publisher_deliversMessageToKafka() {
        var before = outboxMessageRepository.findAll();
        var request = new LinkUpdateRequest(
                42L,
                List.of(new LinkUpdateRequestItem(
                        "https://github.com/user/repo",
                        Instant.parse("2024-02-01T12:00:00Z"),
                        new LinkUpdateData(List.of(
                                new UpdateResponse("Issue", "Issue title", "author", "2024-02-01", null))))));

        transactionTemplate.executeWithoutResult(status -> outboxKafkaBotRequestsSender.sendUpdates(request));

        var after = outboxMessageRepository.findAll();
        assertThat(after.size() - before.size());
        assertThat(after)
                .first()
                .satisfies(msg -> {
                    assertThat(msg.getPayload()).contains("github.com");
                });

        outboxPublisher.publishPendingMessages();

        assertThat(outboxMessageRepository.findAll())
                .first()
                .satisfies(msg -> assertThat(msg.getProcessedAt()).isNotNull());

        var consumerProps = new Properties();
        consumerProps.put(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                kafkaTemplate.getProducerFactory().getConfigurationProperties().get(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG));
        consumerProps.put(ConsumerConfig.GROUP_ID_CONFIG, UUID.randomUUID().toString());
        consumerProps.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        consumerProps.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
        consumerProps.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());

        try (var consumer = new KafkaConsumer<String, String>(consumerProps)) {
            consumer.subscribe(List.of(kafkaSenderProperties.getTopic()));

            var records = consumer.poll(Duration.ofSeconds(15));
            assertThat(records.count()).isEqualTo(1);
            assertThat(records.iterator().next().value()).contains("github.com");
        }
    }
}
