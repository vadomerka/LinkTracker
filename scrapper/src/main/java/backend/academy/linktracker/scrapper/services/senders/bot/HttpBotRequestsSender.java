package backend.academy.linktracker.scrapper.services.senders.bot;

import backend.academy.linktracker.models.http.internal.LinkUpdateRequest;
import backend.academy.linktracker.scrapper.properties.HttpClientProperties;
import backend.academy.linktracker.scrapper.properties.TelegramProperties;
import backend.academy.linktracker.scrapper.resilience.RetryableException;
import backend.academy.linktracker.services.RequestsUtils;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.List;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class HttpBotRequestsSender implements BotRequestsSender {
    protected final RestClient restClient;
    private final List<Integer> retryableStatuses;

    public HttpBotRequestsSender(
            TelegramProperties properties,
            SimpleClientHttpRequestFactory factory,
            HttpClientProperties httpClientProperties) {
        this.retryableStatuses = httpClientProperties.getRetryableStatuses();
        this.restClient = RestClient.builder()
                .baseUrl(properties.getTgUrl())
                .requestFactory(factory)
                .build();
    }

    @Override
    @Retry(name = "bot-http")
    public void sendUpdates(LinkUpdateRequest request) {
        restClient
                .method(HttpMethod.POST)
                .uri(String.format("/tg-chat/%d", request.chatId()))
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(
                        status -> retryableStatuses.contains(status.value()),
                        (req, resp) -> {
                            throw new RetryableException(
                                    "Retryable HTTP error: " + resp.getStatusCode().value());
                        })
                .onStatus(HttpStatusCode::isError, RequestsUtils::onScrapperErrors)
                .toEntity(String.class);
    }
}
