package backend.academy.linktracker.scrapper.services.requests;

import backend.academy.linktracker.models.exceptions.ScrapperRequestException;
import backend.academy.linktracker.models.exceptions.UrlFormatException;
import backend.academy.linktracker.models.http.external.GithubUpdateResponse;
import backend.academy.linktracker.scrapper.models.updates.GitHubUpdateData;
import backend.academy.linktracker.scrapper.properties.GithubProperties;
import backend.academy.linktracker.services.RequestsUtils;
import java.util.List;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

@Component
public class GitHubRequestSender implements UpdateRequestSender {
    private final String token;
    private static final String root = "api.github.com";
    private static final Pattern checkPattern = Pattern.compile("https://api\\.github\\.com/repos/[A-Za-z]+/[A-Za-z]+");
    private final RequestJsonMapper mapper;
    private RestClient restClient;

    public String getRoot() {
        return root;
    }

    public GitHubRequestSender(GithubProperties properties, RequestJsonMapper mapper) {
        token = properties.getToken();
        this.mapper = mapper;
    }

    // var uri = "https://api.github.com/repos/vadomerka/MindMines";
    public GitHubUpdateData getResponse(String url) {
        restClient = RestClient.create();
        try {
            return new GitHubUpdateData(getPRResponse(url), getIssueResponse(url));
        } catch (ScrapperRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UrlFormatException(ex.getMessage());
        }
    }

    private List<GithubUpdateResponse> getPRResponse(String url) {
        var reqUrl = checkUrl(url) + "/pulls";
        var response = getApiResponse(reqUrl);
        return mapper.mapGitPRResponse(response);
    }

    private List<GithubUpdateResponse> getIssueResponse(String url) {
        var reqUrl = checkUrl(url) + "/issues/events";
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
                .onStatus(HttpStatusCode::isError, RequestsUtils::onScrapperErrors)
                .toEntity(new ParameterizedTypeReference<@NotNull List<JsonNode>>() {});
        if (response.getBody() == null) {
            throw new NullPointerException();
        }
        return response;
    }

    private String checkUrl(String url) {
        if (!checkPattern.matcher(url).matches()) {
            throw new UrlFormatException("");
        }
        return url;
    }
}
