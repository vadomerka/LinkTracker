package backend.academy.linktracker.ai.summarization;

import backend.academy.linktracker.ai.properties.AiAgentProperties;
import org.springframework.stereotype.Component;

@Component
public class StubSummarizer implements Summarizer {

    private final AiAgentProperties properties;

    public StubSummarizer(AiAgentProperties properties) {
        this.properties = properties;
    }

    @Override
    public String summarize(String text) {
        int threshold = properties.getSummarization().getThreshold();
        if (text == null || text.length() <= threshold) {
            return text;
        }
        return text.substring(0, threshold) + "...";
    }
}
