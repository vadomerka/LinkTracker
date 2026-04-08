package backend.academy.linktracker.models.http.external;

import com.fasterxml.jackson.annotation.JsonProperty;

public record UpdateResponse(
        String type,
        String title,
        String userName,
        @JsonProperty("created_at") String createdAt,
        String description) {}
