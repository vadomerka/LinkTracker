package backend.academy.linktracker.scrapper.services.requests;

import backend.academy.linktracker.models.GitHubResponse;
import backend.academy.linktracker.scrapper.properties.GithubProperties;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import backend.academy.linktracker.services.RequestsUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

@Service
public class GitHubRequestSender {
    private String token;

    public GitHubRequestSender(GithubProperties properties) {
        token = properties.getToken();
    }

    public ResponseEntity<GitHubResponse> getResponse(String url) {
        var restClient = RestClient.create();
//        var uri = "https://api.github.com/repos/vadomerka/MindMines";
        return restClient.method(HttpMethod.GET)
            .uri(url)
            .header("Authorization", "token " + token)
            .header("Accept", "application/vnd.github.v3+json")
            .retrieve()
            .onStatus(HttpStatusCode::isError, RequestsUtils::onScrapperErrors)
            .toEntity(GitHubResponse.class);
    }
}
