package backend.academy.linktracker.ai.pipeline;

import backend.academy.linktracker.ai.filtering.CompositeUpdateFilter;
import backend.academy.linktracker.ai.properties.AiAgentProperties;
import backend.academy.linktracker.ai.summarization.Summarizer;
import backend.academy.linktracker.models.kafka.ProcessedUpdateMessage;
import backend.academy.linktracker.models.kafka.RawUpdateMessage;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class UpdateProcessingPipeline {

    private static final Logger log = LoggerFactory.getLogger(UpdateProcessingPipeline.class);

    private final CompositeUpdateFilter filter;
    private final Summarizer summarizer;
    private final AiAgentProperties properties;

    public UpdateProcessingPipeline(
            CompositeUpdateFilter filter, Summarizer summarizer, AiAgentProperties properties) {
        this.filter = filter;
        this.summarizer = summarizer;
        this.properties = properties;
    }

    public Optional<ProcessedUpdateMessage> process(RawUpdateMessage raw) {
        if (!filter.shouldPass(raw)) {
            log.info(
                    "Обновление {} отфильтровано (author={}, descLen={})",
                    raw.id(),
                    raw.author(),
                    raw.description() != null ? raw.description().length() : 0);
            return Optional.empty();
        }

        String description = raw.description();
        int threshold = properties.getSummarization().getThreshold();
        if (description != null && description.length() > threshold) {
            description = summarizer.summarize(description);
            log.debug("Обновление {} суммаризировано", raw.id());
        }

        return Optional.of(new ProcessedUpdateMessage(raw.id(), description, raw.tgChatIds(), "HIGH"));
    }
}
