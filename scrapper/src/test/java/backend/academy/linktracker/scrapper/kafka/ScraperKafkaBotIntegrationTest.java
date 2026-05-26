package backend.academy.linktracker.scrapper.kafka;

import static org.awaitility.Awaitility.await;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;

import backend.academy.linktracker.bot.controllers.KafkaBotController;
import backend.academy.linktracker.bot.properties.KafkaReceiverProperties;
import backend.academy.linktracker.bot.services.BotChatManager;
import backend.academy.linktracker.bot.services.BotUtils;
import backend.academy.linktracker.models.http.external.LinkUpdateData;
import backend.academy.linktracker.models.http.external.UpdateResponse;
import backend.academy.linktracker.models.http.internal.LinkUpdateRequest;
import backend.academy.linktracker.models.http.internal.LinkUpdateRequestItem;
import backend.academy.linktracker.scrapper.properties.KafkaSenderProperties;
import backend.academy.linktracker.scrapper.services.senders.KafkaBotRequestsSender;
import backend.academy.linktracker.scrapper.services.senders.KafkaSender;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

@SpringBootTest(
        classes = {
            KafkaSender.class,
            KafkaBotRequestsSender.class,
            KafkaBotController.class,
            BotChatManager.class
        },
        properties =
                "spring.autoconfigure.exclude="
                        + "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,"
                        + "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration,"
                        + "org.springframework.boot.liquibase.autoconfigure.LiquibaseAutoConfiguration")
@Import({KafkaIntegrationTestConfiguration.class, ScraperKafkaBotIntegrationTest.BotKafkaReceiverConfiguration.class})
@EnableConfigurationProperties(KafkaSenderProperties.class)
@EnableKafka
@ActiveProfiles("test")
class ScraperKafkaBotIntegrationTest {

    @TestConfiguration(proxyBeanMethods = false)
    static class BotKafkaReceiverConfiguration {

        @Bean(name = "kafkaReceiverProperties")
        @ConfigurationProperties(prefix = "app.kafka")
        KafkaReceiverProperties kafkaReceiverProperties() {
            return new KafkaReceiverProperties();
        }
    }

    private static final long CHAT_ID = 100L;
    private static final String LINK_URL = "https://stackoverflow.com/questions/1";

    @MockitoBean
    private BotUtils botUtils;

    @Autowired
    private KafkaBotRequestsSender kafkaBotRequestsSender;

    @DynamicPropertySource
    static void kafkaIntegrationProperties(DynamicPropertyRegistry registry) {
        String topic = "integration-scrapper-bot-" + UUID.randomUUID();
        String group = "integration-test-group-" + UUID.randomUUID();
        registry.add("app.kafka.topic", () -> topic);
        registry.add("app.kafka.group", () -> group);
        registry.add("spring.kafka.consumer.auto-offset-reset", () -> "earliest");
        registry.add("spring.kafka.consumer.key-deserializer", StringDeserializer.class::getName);
        registry.add("spring.kafka.consumer.value-deserializer", StringDeserializer.class::getName);
    }

    @Test
    void sendUpdates_scraperToKafkaToBot_processesUpdateInBotChatManager() {
        var request = new LinkUpdateRequest(CHAT_ID,
                List.of(new LinkUpdateRequestItem(
                        LINK_URL,
                        Instant.now(),
                        new LinkUpdateData(List.of(
                                new UpdateResponse("Answer", "Answer title", "author", "2026-05-26", null))))));

        kafkaBotRequestsSender.sendUpdates(request);

        await().atMost(Duration.ofSeconds(15))
                .pollInterval(Duration.ofMillis(200))
                .untilAsserted(() -> {
                    verify(botUtils, atLeastOnce())
                            .sendMessage(
                                    eq(CHAT_ID),
                                    argThat(message -> message != null && message.contains("stackoverflow.com")));
                    verify(botUtils, atLeastOnce())
                            .sendMessage(
                                    eq(CHAT_ID),
                                    argThat(message -> message != null && message.contains("Answer")));
                });
    }
}
