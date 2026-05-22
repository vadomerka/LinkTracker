package backend.academy.linktracker.models.http.internal;

import java.util.List;

public record LinkUpdateRequest(Long chatId, List<LinkUpdateRequestItem> links) {}
