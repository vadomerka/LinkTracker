package backend.academy.linktracker.ai.filtering;

import backend.academy.linktracker.models.kafka.RawUpdateMessage;

public interface UpdateFilter {
    boolean shouldPass(RawUpdateMessage message);
}
