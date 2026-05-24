package backend.academy.linktracker.scrapper.services.senders;

import backend.academy.linktracker.models.exceptions.ScrapperRequestException;
import backend.academy.linktracker.models.exceptions.UrlFormatException;
import backend.academy.linktracker.models.http.external.LinkUpdateData;
import backend.academy.linktracker.models.http.external.UpdateResponse;
import backend.academy.linktracker.scrapper.properties.GithubProperties;
import backend.academy.linktracker.scrapper.services.requests.RequestJsonMapper;
import backend.academy.linktracker.services.RequestsUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Pattern;

@Component
public class GitHubRequestSender implements UpdateRequestSender {
    private static final Pattern checkPattern = Pattern.compile("https://api\\.github\\.com/repos/[A-Za-z]+/[A-Za-z]+");
    private final String token;
    private final RequestJsonMapper mapper;
    private RestClient restClient;

    public GitHubRequestSender(GithubProperties properties, RequestJsonMapper mapper) {
        token = properties.getToken();
        this.mapper = mapper;
    }

    // var uri = "https://api.github.com/repos/vadomerka/MindMines";
    public LinkUpdateData getLinkResponse(String url) {
        restClient = RestClient.create();
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
                .onStatus(HttpStatusCode::isError, RequestsUtils::onScrapperErrors)
                .toEntity(new ParameterizedTypeReference<@NotNull List<JsonNode>>() {});
        if (response.getBody() == null) {
            throw new NullPointerException();
        }
        return response;
    }

    public boolean checkLink(String url) {
        return checkPattern.matcher(url).matches();
    }
}
