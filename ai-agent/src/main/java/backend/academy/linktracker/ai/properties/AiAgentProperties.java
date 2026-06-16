package backend.academy.linktracker.ai.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "ai-agent")
@Validated
@Getter
@Setter
public class AiAgentProperties {
    private FilteringProperties filtering = new FilteringProperties();
    private SummarizationProperties summarization = new SummarizationProperties();
}
