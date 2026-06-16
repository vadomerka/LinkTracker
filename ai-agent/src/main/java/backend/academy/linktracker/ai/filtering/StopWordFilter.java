package backend.academy.linktracker.ai.filtering;

import backend.academy.linktracker.ai.properties.AiAgentProperties;
import backend.academy.linktracker.models.kafka.RawUpdateMessage;
import org.springframework.stereotype.Component;

@Component
public class StopWordFilter implements UpdateFilter {

    private final AiAgentProperties properties;

    public StopWordFilter(AiAgentProperties properties) {
        this.properties = properties;
    }

    @Override
    public boolean shouldPass(RawUpdateMessage message) {
        if (message.description() == null) {
            return true;
        }
        String text = message.description().toLowerCase();
        return properties.getFiltering().getStopWords().stream()
                .noneMatch(word -> text.contains(word.toLowerCase()));
    }
}
