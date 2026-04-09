package backend.academy.linktracker.scrapper.services.requests;

import backend.academy.linktracker.models.exceptions.ScrapperRequestException;
import backend.academy.linktracker.models.exceptions.UrlFormatException;
import backend.academy.linktracker.models.http.external.LinkUpdateData;
import backend.academy.linktracker.models.http.external.UpdateResponse;
import backend.academy.linktracker.scrapper.properties.GithubProperties;
import backend.academy.linktracker.services.RequestsUtils;
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
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;

@Component
public class StackOverflowRequestSender implements UpdateRequestSender {
    private final String token;
    private static final Pattern checkPattern =
            Pattern.compile("https://api\\.stackexchange\\.com/2.3/questions/[0-9]+");
    private final RequestJsonMapper mapper;
    private final RestClient restClient;

    public StackOverflowRequestSender(GithubProperties properties, RequestJsonMapper mapper) {
        token = properties.getToken();
        this.mapper = mapper;
        this.restClient = RestClient.create();
    }

    // var uri = "https://api.stackexchange.com/";
    // https://api.stackexchange.com/2.3/questions/6268679/answers?site=stackoverflow&filter=withbody
    // https://api.stackexchange.com/2.3/questions/6268679?site=stackoverflow
    public LinkUpdateData getLinkResponse(String url) {
        try {
            if (!checkLink(url)) throw new UrlFormatException();
            return makeUpdData(url);
        } catch (ScrapperRequestException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new UrlFormatException(ex.getMessage());
        }
    }

    private LinkUpdateData makeUpdData(String url) {
        var data = new ArrayList<UpdateResponse>();
        String questionTitle = getQuestionResponse(url);
        var ans = getAnswersResponse(url, questionTitle);
        var com = getCommentsResponse(url, questionTitle);
        data.addAll(ans);
        data.addAll(com);
        data.sort(Comparator.comparing(sr -> Instant.parse(sr.createdAt())));
        return new LinkUpdateData(data);
    }

    private String getQuestionResponse(String url) {
        var reqUrl = url + "?site=stackoverflow";
        var response = getApiResponse(reqUrl);
        return mapper.mapStackQuestionResponse(response);
    }

    private List<UpdateResponse> getAnswersResponse(String url, String title) {
        var reqUrl = url + "/answers?order=asc&sort=creation&site=stackoverflow&filter=withbody";
        var response = getApiResponse(reqUrl);
        return mapper.mapStackResponse(response, "question_answer", title);
    }

    private List<UpdateResponse> getCommentsResponse(String url, String title) {
        var reqUrl = url + "/comments?order=asc&sort=creation&site=stackoverflow&filter=withbody";
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

    public boolean checkLink(String url) {
        return checkPattern.matcher(url).matches();
    }
}
