package backend.academy.linktracker.ai.filtering;

import backend.academy.linktracker.ai.properties.AiAgentProperties;
import backend.academy.linktracker.models.kafka.RawUpdateMessage;
import org.springframework.stereotype.Component;

@Component
public class MinLengthFilter implements UpdateFilter {

    private final AiAgentProperties properties;

    public MinLengthFilter(AiAgentProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean shouldPass(RawUpdateMessage message) {
        if (message.description() == null) {
            return false;
        }
        return message.description().length() >= properties.getFiltering().getMinLength();
    }
}
