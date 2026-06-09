package backend.academy.linktracker.scrapper.configuration;

import backend.academy.linktracker.scrapper.properties.HttpClientProperties;
import java.time.Duration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

@Configuration
@EnableConfigurationProperties(HttpClientProperties.class)
public class HttpClientConfig {

    @Bean
    public SimpleClientHttpRequestFactory clientHttpRequestFactory(HttpClientProperties props) {
        var factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(Duration.ofMillis(props.getConnectTimeoutMs()));
        factory.setReadTimeout(Duration.ofMillis(props.getReadTimeoutMs()));
        return factory;
    }
}
