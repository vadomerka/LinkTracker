package backend.academy.linktracker.scrapper.models.requests;

import java.util.List;

public record AddSourceRequest(String url, List<String> tags, List<String> filters) {}
