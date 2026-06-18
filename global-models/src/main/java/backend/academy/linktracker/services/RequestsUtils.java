package backend.academy.linktracker.services;

import backend.academy.linktracker.models.exceptions.GitHubRequestException;
import backend.academy.linktracker.models.exceptions.ScrapperRequestException;
import backend.academy.linktracker.models.http.internal.ApiErrorResponse;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpResponse;
import tools.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class RequestsUtils {
    public static void onScrapperErrors(HttpRequest req, ClientHttpResponse res) throws IOException {
        String body = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
        ApiErrorResponse errorBody = new ObjectMapper().readValue(body, ApiErrorResponse.class);
        throw new ScrapperRequestException(errorBody.description());
    }

    public static void onGitHubErrors(HttpRequest req, ClientHttpResponse res) throws IOException {
        String body = new String(res.getBody().readAllBytes(), StandardCharsets.UTF_8);
        ApiErrorResponse errorBody = new ObjectMapper().readValue(body, ApiErrorResponse.class);
        throw new GitHubRequestException(errorBody.description());
    }
}
