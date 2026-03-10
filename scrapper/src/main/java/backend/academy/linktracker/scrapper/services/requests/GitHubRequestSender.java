package backend.academy.linktracker.scrapper.services.requests;

import backend.academy.linktracker.models.exceptions.ScrapperRequestException;
import backend.academy.linktracker.models.exceptions.UrlFormatException;
import backend.academy.linktracker.models.http.external.GithubUpdateResponse;
import backend.academy.linktracker.scrapper.properties.GithubProperties;
import backend.academy.linktracker.services.RequestsUtils;
import java.time.Instant;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GitHubRequestSender implements UpdateRequestSender {
    private final String token;
    private static final String root = "api.github.com";

    public String getRoot() {
        return root;
    }

    public GitHubRequestSender(GithubProperties properties) {
        token = properties.getToken();
    }

    // var uri = "https://api.github.com/repos/vadomerka/MindMines";
    public Instant getResponse(String url) {
        var restClient = RestClient.create();
        try {
            var response = restClient
                    .method(HttpMethod.GET)
                    .uri(url)
                    .header("Authorization", "token " + token)
                    .header("Accept", "application/vnd.github.v3+json")
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, RequestsUtils::onScrapperErrors)
                    .toEntity(GithubUpdateResponse.class);
            if (response.getBody() == null) {
                throw new NullPointerException();
            }
            return getUpdated(response.getBody());
        } catch (ScrapperRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UrlFormatException(ex.getMessage());
        }
    }

    private Instant getUpdated(GithubUpdateResponse res) {
        return Instant.parse(res.updatedAt());
    }
}
