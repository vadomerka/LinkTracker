package backend.academy.linktracker.bot.properties;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component("kafkaReceiverProperties")
@ConfigurationProperties(prefix = "app.kafka")
@Validated
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class KafkaReceiverProperties {
    @NotEmpty
    private String topic = "scrapper-to-bot";

    @NotEmpty
    private String group = "groupC";

    @NotEmpty
    private String dlqTopic = "scrapper-bot-dlq";

    private Retry retry = new Retry();

    @Getter
    @Setter
    @EqualsAndHashCode
    @NoArgsConstructor
    public static class Retry {
        @Min(1)
        private int maxAttempts = 3;

        @Min(0)
        private long backoffMs = 1000;
    }
}
