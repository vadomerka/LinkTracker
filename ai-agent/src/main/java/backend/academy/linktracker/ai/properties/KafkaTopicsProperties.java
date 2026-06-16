package backend.academy.linktracker.ai.properties;

import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

@Component
@ConfigurationProperties(prefix = "app.kafka")
@Validated
@Getter
@Setter
public class KafkaTopicsProperties {
    @NotEmpty
    private String inputTopic = "link.raw-updates";

    @NotEmpty
    private String outputTopic = "link.processed-updates";

    @NotEmpty
    private String groupId = "ai-agent-group";
}
