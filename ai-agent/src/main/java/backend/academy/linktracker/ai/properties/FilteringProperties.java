package backend.academy.linktracker.ai.properties;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FilteringProperties {
    private List<String> stopWords = List.of();
    private List<String> excludedAuthors = List.of();
    private int minLength = 0;
}
