package backend.academy.linktracker.scrapper.services.requests;

import backend.academy.linktracker.models.exceptions.ScrapperRequestException;
import backend.academy.linktracker.models.exceptions.UrlFormatException;
import backend.academy.linktracker.models.http.external.GithubPRUpdateResponse;
import backend.academy.linktracker.scrapper.models.updates.GitHubUpdateInfo;
import backend.academy.linktracker.scrapper.properties.GithubProperties;
import backend.academy.linktracker.services.RequestsUtils;
import java.util.List;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GitHubRequestSender implements UpdateRequestSender {
    private final String token;
    private static final String root = "api.github.com";
    private static final Pattern checkPattern = Pattern.compile("https://api\\.github\\.com/repos/[A-Za-z]+/[A-Za-z]+");

    public String getRoot() {
        return root;
    }

    public GitHubRequestSender(GithubProperties properties) {
        token = properties.getToken();
    }

    // var uri = "https://api.github.com/repos/vadomerka/MindMines";
    public GitHubUpdateInfo getResponse(String url) {
        var restClient = RestClient.create();
        try {
            var reqUrl = checkUrl(url);
            var response = restClient
                    .method(HttpMethod.GET)
                    .uri(reqUrl)
                    .header("Authorization", "token " + token)
                    .header("Accept", "application/vnd.github.v3+json")
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, RequestsUtils::onScrapperErrors)
                    .toEntity(new ParameterizedTypeReference<@NotNull List<GithubPRUpdateResponse>>() {});
            if (response.getBody() == null) {
                throw new NullPointerException();
            }
            return getUpdated(url, response.getBody());
        } catch (ScrapperRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UrlFormatException(ex.getMessage());
        }
    }

    private String checkUrl(String url) {
        if (!checkPattern.matcher(url).matches()) {
            throw new UrlFormatException("");
        }
        return url + "/pulls";
    }

    private GitHubUpdateInfo getUpdated(String url, List<GithubPRUpdateResponse> items) {
        return new GitHubUpdateInfo(url, items.size(), items);
    }
}
