package backend.academy.linktracker.scrapper.properties;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "app.valkey")
@Validated
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class ValkeyCacheProperties {

    private boolean enabled = true;

    private String password;

    @NotNull
    private Cluster cluster = new Cluster();

    @NotNull
    private Standalone standalone = new Standalone();

    @NotNull
    private ListCache listCache = new ListCache();

    @Getter
    @Setter
    @EqualsAndHashCode
    @NoArgsConstructor
    public static class Cluster {
        private List<String> nodes = new ArrayList<>(List.of("localhost:7001", "localhost:7002", "localhost:7003"));
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    @NoArgsConstructor
    public static class Standalone {
        private boolean enabled = false;
        private String host = "localhost";
        private int port = 6379;
    }

    @Getter
    @Setter
    @EqualsAndHashCode
    @NoArgsConstructor
    public static class ListCache {
        @NotNull
        private Duration ttl = Duration.ofMinutes(10);

        @NotEmpty
        private String keyPrefix = "list:";
    }
}
