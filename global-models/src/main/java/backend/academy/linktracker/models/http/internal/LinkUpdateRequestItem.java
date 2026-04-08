package backend.academy.linktracker.models.http.internal;

import backend.academy.linktracker.models.http.external.LinkUpdateData;
import java.time.Instant;

public record LinkUpdateRequestItem(String url, Instant lastUpdate, LinkUpdateData data) {}
