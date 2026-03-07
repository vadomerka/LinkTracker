package backend.academy.linktracker.scrapper.models;

import java.util.List;

public record TrackedSourceDTO(String url, List<String> tags) {}
