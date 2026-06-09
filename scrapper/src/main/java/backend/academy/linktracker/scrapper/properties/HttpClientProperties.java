package backend.academy.linktracker.scrapper.properties;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.http")
@Getter
@Setter
@NoArgsConstructor
public class HttpClientProperties {
    private long connectTimeoutMs = 3000;
    private long readTimeoutMs = 5000;
    private List<Integer> retryableStatuses = List.of(429, 502, 503, 504);
}
