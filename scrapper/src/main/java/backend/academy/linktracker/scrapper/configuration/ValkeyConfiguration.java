package backend.academy.linktracker.scrapper.configuration;

import backend.academy.linktracker.scrapper.properties.ValkeyCacheProperties;
import io.lettuce.core.cluster.ClusterClientOptions;
import io.lettuce.core.cluster.ClusterTopologyRefreshOptions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisPassword;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.StringRedisTemplate;
import java.time.Duration;
import java.util.List;

@Configuration
@EnableConfigurationProperties(ValkeyCacheProperties.class)
@ConditionalOnProperty(prefix = "app.valkey", name = "enabled", havingValue = "true", matchIfMissing = true)
public class ValkeyConfiguration {

    @Bean
    @Primary
    RedisConnectionFactory valkeyConnectionFactory(ValkeyCacheProperties properties) {
        if (properties.getStandalone().isEnabled()) {
            RedisStandaloneConfiguration standaloneConfiguration = new RedisStandaloneConfiguration(
                    properties.getStandalone().getHost(),
                    properties.getStandalone().getPort());
            if (properties.getPassword() != null && !properties.getPassword().isBlank()) {
                standaloneConfiguration.setPassword(RedisPassword.of(properties.getPassword()));
            }
            return new LettuceConnectionFactory(standaloneConfiguration);
        }
        List<String> nodes = properties.getCluster().getNodes();
        if (nodes == null || nodes.isEmpty()) {
            throw new IllegalStateException(
                    "app.valkey.cluster.nodes must be configured when standalone mode is disabled");
        }
        RedisClusterConfiguration clusterConfiguration = new RedisClusterConfiguration(nodes);
        if (properties.getPassword() != null && !properties.getPassword().isBlank()) {
            clusterConfiguration.setPassword(RedisPassword.of(properties.getPassword()));
        }
        LettuceClientConfiguration clientConfiguration = LettuceClientConfiguration.builder()
                .commandTimeout(Duration.ofSeconds(5))
                .clientOptions(ClusterClientOptions.builder()
                        .topologyRefreshOptions(ClusterTopologyRefreshOptions.builder()
                                .enablePeriodicRefresh(Duration.ofSeconds(30))
                                .build())
                        .build())
                .build();
        return new LettuceConnectionFactory(clusterConfiguration, clientConfiguration);
    }

    @Bean
    StringRedisTemplate stringRedisTemplate(RedisConnectionFactory valkeyConnectionFactory) {
        return new StringRedisTemplate(valkeyConnectionFactory);
    }
}
