package backend.academy.linktracker.scrapper.services.requests;

import backend.academy.linktracker.models.exceptions.ScrapperRequestException;
import backend.academy.linktracker.models.exceptions.UrlFormatException;
import backend.academy.linktracker.models.http.external.StackOverflowUpdateResponse;
import backend.academy.linktracker.scrapper.models.updates.StackOverflowUpdateInfo;
import backend.academy.linktracker.scrapper.properties.GithubProperties;
import backend.academy.linktracker.services.RequestsUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class StackOverflowRequestSender implements UpdateRequestSender {
    private final String token;
    private static final String root = "api.stackexchange.com";

    public String getRoot() {
        return root;
    }

    public StackOverflowRequestSender(GithubProperties properties) {
        token = properties.getToken();
    }

    // var uri = "https://api.stackexchange.com/";
    public StackOverflowUpdateInfo getResponse(String url) {
        var restClient = RestClient.create();
        try {
            var response = restClient
                    .method(HttpMethod.GET)
                    .uri(url)
                    .header("Accept", "application/json")
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, RequestsUtils::onScrapperErrors)
                    .toEntity(StackOverflowUpdateResponse.class);
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

    private StackOverflowUpdateInfo getUpdated(StackOverflowUpdateResponse res) {
        var seconds = res.items().getFirst().lastActivityDate();
        // Instant.ofEpochSecond(seconds).
        return new StackOverflowUpdateInfo();
    }
}
