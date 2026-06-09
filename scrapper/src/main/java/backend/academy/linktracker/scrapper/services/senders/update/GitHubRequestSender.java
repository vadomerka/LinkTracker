package backend.academy.linktracker.scrapper.services.senders.update;

import backend.academy.linktracker.models.exceptions.ScrapperRequestException;
import backend.academy.linktracker.models.exceptions.UrlFormatException;
import backend.academy.linktracker.models.http.external.LinkUpdateData;
import backend.academy.linktracker.models.http.external.UpdateResponse;
import backend.academy.linktracker.scrapper.properties.GithubProperties;
import backend.academy.linktracker.scrapper.properties.HttpClientProperties;
import backend.academy.linktracker.scrapper.resilience.RetryableException;
import backend.academy.linktracker.scrapper.services.requests.RequestJsonMapper;
import backend.academy.linktracker.services.RequestsUtils;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

@Component
public class GitHubRequestSender implements UpdateRequestSender {
    private static final Pattern checkPattern =
            Pattern.compile("https://api\\.github\\.com/repos/[A-Za-z]+/[A-Za-z]+");
    private final String token;
    private final RequestJsonMapper mapper;
    private final RestClient restClient;
    private final List<Integer> retryableStatuses;

    public GitHubRequestSender(
            GithubProperties properties,
            RequestJsonMapper mapper,
            SimpleClientHttpRequestFactory factory,
            HttpClientProperties httpClientProperties) {
        this.token = properties.getToken();
        this.mapper = mapper;
        this.retryableStatuses = httpClientProperties.getRetryableStatuses();
        this.restClient = RestClient.builder().requestFactory(factory).build();
    }

    @Override
    @Retry(name = "github")
    @CircuitBreaker(name = "github")
    public LinkUpdateData getLinkResponse(String url) {
        try {
            if (!checkLink(url)) throw new UrlFormatException();
            return makeUpdData(url);
        } catch (ScrapperRequestException | RetryableException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UrlFormatException(ex.getMessage());
        }
    }

    private LinkUpdateData makeUpdData(String url) {
        var data = new ArrayList<UpdateResponse>();
        var ans = getPRResponse(url);
        var com = getIssueResponse(url);
        data.addAll(ans);
        data.addAll(com);
        data.sort(Comparator.comparing(sr -> Instant.parse(sr.createdAt())));
        return new LinkUpdateData(data);
    }

    private List<UpdateResponse> getPRResponse(String url) {
        var reqUrl = url + "/pulls";
        var response = getApiResponse(reqUrl);
        return mapper.mapGitPRResponse(response);
    }

    private List<UpdateResponse> getIssueResponse(String url) {
        var reqUrl = url + "/issues/events";
        var response = getApiResponse(reqUrl);
        return mapper.mapGitIssueResponse(response);
    }

    private ResponseEntity<@NotNull List<JsonNode>> getApiResponse(String url) {
        var response = restClient
                .method(HttpMethod.GET)
                .uri(url)
                .header("Authorization", "token " + token)
                .header("Accept", "application/vnd.github.v3+json")
                .retrieve()
                .onStatus(
                        status -> retryableStatuses.contains(status.value()),
                        (req, resp) -> {
                            throw new RetryableException(
                                    "Retryable HTTP error: " + resp.getStatusCode().value());
                        })
                .onStatus(HttpStatusCode::isError, RequestsUtils::onScrapperErrors)
                .toEntity(new ParameterizedTypeReference<@NotNull List<JsonNode>>() {});
        if (response.getBody() == null) {
            throw new NullPointerException();
        }
        return response;
    }

    @Override
    public boolean checkLink(String url) {
        return checkPattern.matcher(url).matches();
    }
}
