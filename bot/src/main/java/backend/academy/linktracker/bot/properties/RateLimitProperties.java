package backend.academy.linktracker.bot.properties;

import java.time.Duration;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.rate-limit")
@Getter
@Setter
@NoArgsConstructor
public class RateLimitProperties {
    private long capacity = 60;
    private long refillTokens = 60;
    private Duration refillPeriod = Duration.ofMinutes(1);
}
