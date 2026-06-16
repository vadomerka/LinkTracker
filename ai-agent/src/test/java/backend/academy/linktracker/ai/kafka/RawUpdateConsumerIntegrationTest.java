package backend.academy.linktracker.ai.kafka;

import backend.academy.linktracker.ai.configuration.AiAgentConfiguration;
import backend.academy.linktracker.ai.filtering.CompositeUpdateFilter;
import backend.academy.linktracker.ai.filtering.ExcludedAuthorFilter;
import backend.academy.linktracker.ai.filtering.MinLengthFilter;
import backend.academy.linktracker.ai.filtering.StopWordFilter;
import backend.academy.linktracker.ai.pipeline.UpdateProcessingPipeline;
import backend.academy.linktracker.ai.properties.AiAgentProperties;
import backend.academy.linktracker.ai.properties.KafkaTopicsProperties;
import backend.academy.linktracker.ai.summarization.StubSummarizer;
import backend.academy.linktracker.models.kafka.ProcessedUpdateMessage;
import backend.academy.linktracker.models.kafka.RawUpdateMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.Duration;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
        classes = {
            RawUpdateConsumer.class,
            ProcessedUpdateProducer.class,
            UpdateProcessingPipeline.class,
            CompositeUpdateFilter.class,
            StopWordFilter.class,
            ExcludedAuthorFilter.class,
            MinLengthFilter.class,
            StubSummarizer.class,
            AiAgentConfiguration.class,
            KafkaTopicsProperties.class,
            RawUpdateConsumerIntegrationTest.ProcessedUpdateCapture.class
        },
        properties = {
            "spring.autoconfigure.exclude="
                    + "org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration,"
                    + "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration"
        })
@Import(KafkaIntegrationTestConfiguration.class)
@EnableConfigurationProperties({AiAgentProperties.class})
@EnableKafka
@TestPropertySource(
        properties = {
            "app.kafka.input-topic=link.raw-updates",
            "app.kafka.output-topic=link.processed-updates",
            "app.kafka.group-id=ai-agent-test-group",
            "ai-agent.filtering.stop-words=spam,ads",
            "ai-agent.filtering.excluded-authors=bot-user",
            "ai-agent.filtering.min-length=10",
            "ai-agent.summarization.threshold=500"
        })
class RawUpdateConsumerIntegrationTest {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private ProcessedUpdateCapture capture;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void shouldConsumeAndPublishProcessedUpdate() throws Exception {
        RawUpdateMessage raw = new RawUpdateMessage(
                42L, "New feature merged into main branch for release", "john-doe", List.of(111L, 222L));

        String json = objectMapper.writeValueAsString(raw);
        kafkaTemplate.send("link.raw-updates", "key", json);

        Awaitility.await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> {
            assertThat(capture.received.get()).isNotNull();
            ProcessedUpdateMessage processed = capture.received.get();
            assertThat(processed.id()).isEqualTo(42L);
            assertThat(processed.tgChatIds()).containsExactlyInAnyOrder(111L, 222L);
            assertThat(processed.description()).isNotBlank();
            assertThat(processed.priority()).isEqualTo("HIGH");
        });
    }

    @Test
    void shouldNotCrashOnInvalidMessage() {
        kafkaTemplate.send("link.raw-updates", "key", "{invalid json{{");

        Awaitility.await()
                .during(Duration.ofSeconds(3))
                .atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> assertThat(capture.received.get()).isNull());
    }

    @Component
    static class ProcessedUpdateCapture {
        private final AtomicReference<ProcessedUpdateMessage> received = new AtomicReference<>();
        private final ObjectMapper mapper = new ObjectMapper();

        @KafkaListener(topics = "link.processed-updates", groupId = "test-capture-group")
        void capture(String message) throws Exception {
            received.set(mapper.readValue(message, ProcessedUpdateMessage.class));
        }
    }
}
