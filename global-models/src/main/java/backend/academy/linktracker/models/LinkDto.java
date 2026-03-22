package backend.academy.linktracker.models;

import java.util.List;

public record LinkDto (String url, List<String> tags) {}
