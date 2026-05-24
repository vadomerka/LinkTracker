package backend.academy.linktracker.models.http.internal;

import java.util.List;
import tools.jackson.databind.annotation.JsonSerialize;

@JsonSerialize
public record LinkUpdateRequest(Long chatId, List<LinkUpdateRequestItem> links) {}
