package backend.academy.linktracker.models.http.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public record GithubUpdateResponse(
    String type,
    String name,
    String userName,
    @JsonProperty("created_at") String createdAt,
    String description) {}
