package backend.academy.linktracker.scrapper.kafka;

import backend.academy.linktracker.models.http.external.LinkUpdateData;
import backend.academy.linktracker.models.http.external.UpdateResponse;
import backend.academy.linktracker.models.http.internal.LinkUpdateRequest;
import backend.academy.linktracker.models.http.internal.LinkUpdateRequestItem;
import backend.academy.linktracker.models.kafka.RawUpdateMessage;
import backend.academy.linktracker.scrapper.properties.KafkaSenderProperties;
import backend.academy.linktracker.scrapper.services.senders.KafkaSenderService;
import backend.academy.linktracker.scrapper.services.senders.bot.KafkaBotRequestsSender;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = {
            KafkaSenderService.class,
            KafkaBotRequestsSender.class,
            ScraperKafkaBotIntegrationTest.RawUpdateCapture.class
        },
        properties =
                "spring.autoconfigure.exclude="
                        + "org.springframework.boot.jdbc.autoconfigure.DataSourceAutoConfiguration,"
                        + "org.springframework.boot.hibernate.autoconfigure.HibernateJpaAutoConfiguration,"
                        + "org.springframework.boot.liquibase.autoconfigure.LiquibaseAutoConfiguration")
@Import(KafkaIntegrationTestConfiguration.class)
@EnableConfigurationProperties(KafkaSenderProperties.class)
@EnableKafka
@TestPropertySource(properties = {
        "app.kafka.topic=link.raw-updates",
        "app.kafka.group=test-group",
        "spring.kafka.consumer.auto-offset-reset=earliest",
        "spring.kafka.consumer.key-deserializer=org.apache.kafka.common.serialization.StringDeserializer",
        "spring.kafka.consumer.value-deserializer=org.apache.kafka.common.serialization.StringDeserializer"
})
class ScraperKafkaBotIntegrationTest {

    private static final long CHAT_ID = 100L;
    private static final String LINK_URL = "https://stackoverflow.com/questions/1";

    @Autowired
    private KafkaBotRequestsSender kafkaBotRequestsSender;

    @Autowired
    private RawUpdateCapture capture;

    @Test
    void sendUpdates_publishesRawUpdateMessageToKafka() {
        var request = new LinkUpdateRequest(
                CHAT_ID,
                List.of(new LinkUpdateRequestItem(
                        LINK_URL,
                        Instant.now(),
                        new LinkUpdateData(List.of(
                                new UpdateResponse("Answer", "Answer title", "author", "2026-05-26", "Some description text"))))));

        kafkaBotRequestsSender.sendUpdates(request);

        Awaitility.await()
                .atMost(Duration.ofSeconds(15))
                .untilAsserted(() -> {
                    assertThat(capture.received.get()).isNotNull();
                    RawUpdateMessage raw = capture.received.get();
                    assertThat(raw.tgChatIds()).containsExactly(CHAT_ID);
                    assertThat(raw.author()).isEqualTo("author");
                    assertThat(raw.description()).contains("Answer title");
                });
    }

    @Component
    static class RawUpdateCapture {
        final AtomicReference<RawUpdateMessage> received = new AtomicReference<>();
        private final ObjectMapper mapper = new ObjectMapper();

        @KafkaListener(topics = "link.raw-updates", groupId = "scrapper-test-capture")
        void capture(String message) throws Exception {
            received.set(mapper.readValue(message, RawUpdateMessage.class));
        }
    }
}
