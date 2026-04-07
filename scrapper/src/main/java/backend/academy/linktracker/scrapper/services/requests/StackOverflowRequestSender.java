package backend.academy.linktracker.scrapper.services.requests;

import backend.academy.linktracker.models.exceptions.ScrapperRequestException;
import backend.academy.linktracker.models.exceptions.UrlFormatException;
import backend.academy.linktracker.models.http.external.StackOverflowUpdateResponse;
import backend.academy.linktracker.scrapper.models.updates.StackOverflowUpdateData;
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
public class StackOverflowRequestSender implements UpdateRequestSender {
    private final String token;
    private static final String root = "api.stackexchange.com";
    private static final Pattern checkPattern =
            Pattern.compile("https://api\\.stackexchange\\.com/2.3/questions/[0-9]+");
    private final RequestJsonMapper mapper;
    private RestClient restClient;

    public String getRoot() {
        return root;
    }

    public StackOverflowRequestSender(GithubProperties properties, RequestJsonMapper mapper) {
        token = properties.getToken();
        this.mapper = mapper;
    }

    // var uri = "https://api.stackexchange.com/";
    // https://api.stackexchange.com/2.3/questions/6268679/answers?site=stackoverflow&filter=withbody
    // https://api.stackexchange.com/2.3/questions/6268679?site=stackoverflow
    public StackOverflowUpdateData getResponse(String url) {
        restClient = RestClient.create();
        try {
            String questionTitle = getQuestionResponse(url);
            return new StackOverflowUpdateData(
                    getAnswersResponse(url, questionTitle), getCommentsResponse(url, questionTitle));
        } catch (ScrapperRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UrlFormatException(ex.getMessage());
        }
    }

    private String getQuestionResponse(String url) {
        var reqUrl = checkUrl(url) + "?site=stackoverflow";
        var response = getApiResponse(reqUrl);
        return mapper.mapStackQuestionResponse(response);
    }

    private List<StackOverflowUpdateResponse> getAnswersResponse(String url, String title) {
        var reqUrl = checkUrl(url) + "/answers?site=stackoverflow&filter=withbody";
        var response = getApiResponse(reqUrl);
        return mapper.mapStackResponse(response, "question_answer", title);
    }

    private List<StackOverflowUpdateResponse> getCommentsResponse(String url, String title) {
        var reqUrl = checkUrl(url) + "?site=stackoverflow";
        var response = getApiResponse(reqUrl);
        return mapper.mapStackResponse(response, "question_comment", title);
    }

    private ResponseEntity<@NotNull JsonNode> getApiResponse(String url) {
        var response = restClient
                .method(HttpMethod.GET)
                .uri(url)
                .header("Accept", "application/json")
                .retrieve()
                .onStatus(HttpStatusCode::isError, RequestsUtils::onScrapperErrors)
                .toEntity(new ParameterizedTypeReference<@NotNull JsonNode>() {});
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
