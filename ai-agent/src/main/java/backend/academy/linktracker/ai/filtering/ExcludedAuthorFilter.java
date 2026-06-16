package backend.academy.linktracker.ai.filtering;

import backend.academy.linktracker.ai.properties.AiAgentProperties;
import backend.academy.linktracker.models.kafka.RawUpdateMessage;
import org.springframework.stereotype.Component;

@Component
public class ExcludedAuthorFilter implements UpdateFilter {

    private final AiAgentProperties properties;

    public ExcludedAuthorFilter(AiAgentProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean shouldPass(RawUpdateMessage message) {
        if (message.author() == null) {
            return true;
        }
        return properties.getFiltering().getExcludedAuthors().stream()
                .noneMatch(excluded -> excluded.equalsIgnoreCase(message.author()));
    }
}
