package backend.academy.linktracker.ai.filtering;

import backend.academy.linktracker.models.kafka.RawUpdateMessage;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CompositeUpdateFilter {

    private final List<UpdateFilter> filters;

    public CompositeUpdateFilter(List<UpdateFilter> filters) {
        this.filters = filters;
    }

    public boolean shouldPass(RawUpdateMessage message) {
        return filters.stream().allMatch(f -> f.shouldPass(message));
    }
}
