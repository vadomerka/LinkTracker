package backend.academy.linktracker.models.http.internal;

import tools.jackson.databind.annotation.JsonSerialize;
import java.util.List;

@JsonSerialize
public record LinkUpdateRequest(Long chatId, List<LinkUpdateRequestItem> links) {}
