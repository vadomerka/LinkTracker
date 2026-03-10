package backend.academy.linktracker.models.http.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record StackOverflowUpdateResponse(List<Item> items) {
    public record Item(@JsonProperty("last_activity_date") long lastActivityDate) {}
}
