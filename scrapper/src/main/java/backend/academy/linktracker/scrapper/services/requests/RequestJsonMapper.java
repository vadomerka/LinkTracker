package backend.academy.linktracker.scrapper.services.requests;

import backend.academy.linktracker.models.http.external.GithubUpdateResponse;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import java.util.List;

@Service
public class RequestJsonMapper {
    public List<GithubUpdateResponse> mapGitIssueResponse(ResponseEntity<@NotNull List<JsonNode>> response) {
        if (response.getBody() == null) throw new NullPointerException();
        return response.getBody().stream()
            .map(data -> new GithubUpdateResponse(
                    "issue_event",
                    data.path("issue").path("title").toPrettyString(),
                    data.path("actor").path("login").toPrettyString(),
                    data.path("created_at").toPrettyString(),
                    data.path("issue").path("body").toPrettyString()
                )
            ).toList();
    }

    public List<GithubUpdateResponse> mapGitPRResponse(ResponseEntity<@NotNull List<JsonNode>> response) {
        if (response.getBody() == null) throw new NullPointerException();
        return response.getBody().stream()
            .map(data -> new GithubUpdateResponse(
                    "pull_request",
                    data.path("title").toPrettyString(),
                    data.path("user").path("login").toPrettyString(),
                    data.path("created_at").toPrettyString(),
                    data.path("body").toPrettyString()
                )
            ).toList();
    }
}
