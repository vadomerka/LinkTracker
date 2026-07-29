package backend.academy.linktracker.scrapper.services.requests;

import backend.academy.linktracker.models.http.external.UpdateResponse;
import java.time.Instant;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;

@Service
public class RequestJsonMapper {
    public List<UpdateResponse> mapGitIssueResponse(ResponseEntity<@NotNull List<JsonNode>> response) {
        if (response.getBody() == null) throw new NullPointerException();
        return response.getBody().stream()
                .map(data -> new UpdateResponse(
                        "issue_event",
                        data.path("issue").path("title").stringValue(),
                        data.path("actor").path("login").stringValue(),
                        data.path("created_at").stringValue(),
                        data.path("issue").path("body").stringValue()))
                .toList();
    }

    public List<UpdateResponse> mapGitPRResponse(ResponseEntity<@NotNull List<JsonNode>> response) {
        if (response.getBody() == null) throw new NullPointerException();
        return response.getBody().stream()
                .map(data -> new UpdateResponse(
                        "pull_request",
                        data.path("title").stringValue(),
                        data.path("user").path("login").stringValue(),
                        data.path("created_at").stringValue(),
                        data.path("body").stringValue()))
                .toList();
    }

    public String mapStackQuestionResponse(ResponseEntity<@NotNull JsonNode> response) {
        if (response.getBody() == null) throw new NullPointerException();
        return response.getBody().path("items").get(0).get("title").stringValue();
    }

    public List<UpdateResponse> mapStackResponse(
            ResponseEntity<@NotNull JsonNode> ansResponse, String type, String questionTitle) {
        if (ansResponse.getBody() == null) throw new NullPointerException();
        JsonNode node = ansResponse.getBody().path("items");
        return node.valueStream()
                .map(data -> {
                    var cdValue = data.path("creation_date").longValue();
                    var creationDate = Instant.ofEpochSecond(cdValue).toString();
                    var body = data.path("body").stringValue();
                    var bodyFragment = body.substring(0, Math.min(200, body.length()));
                    return new UpdateResponse(
                            type,
                            questionTitle,
                            data.path("owner").path("display_name").stringValue(),
                            creationDate,
                            bodyFragment);
                })
                .toList();
    }
}
