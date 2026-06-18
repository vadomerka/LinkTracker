package backend.academy.linktracker.scrapper.properties;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.kafka.outbox")
@Validated
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class OutboxProperties {

    private boolean enabled = true;

    private long pollIntervalMs = 5000;

    private int batchSize = 100;
}
