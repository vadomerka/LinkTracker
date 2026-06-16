package backend.academy.linktracker.ai.filtering;

import backend.academy.linktracker.ai.properties.AiAgentProperties;
import backend.academy.linktracker.ai.properties.FilteringProperties;
import backend.academy.linktracker.models.kafka.RawUpdateMessage;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CompositeUpdateFilterTest {

    private CompositeUpdateFilter compositeFilter;

    @BeforeEach
    void setUp() {
        FilteringProperties filtering = new FilteringProperties();
        filtering.setStopWords(List.of("spam"));
        filtering.setExcludedAuthors(List.of("bot-user"));
        filtering.setMinLength(20);
        AiAgentProperties props = new AiAgentProperties();
        props.setFiltering(filtering);

        compositeFilter = new CompositeUpdateFilter(
                List.of(new StopWordFilter(props), new ExcludedAuthorFilter(props), new MinLengthFilter(props)));
    }

    @Test
    void shouldPassValidUpdateThroughAllFilters() {
        RawUpdateMessage msg =
                new RawUpdateMessage(1L, "New pull request merged into main branch", "john-doe", List.of(1L));
        assertThat(compositeFilter.shouldPass(msg)).isTrue();
    }

    @Test
    void shouldBlockUpdateWithStopWord() {
        RawUpdateMessage msg = new RawUpdateMessage(2L, "Check out this spam content today", "john-doe", List.of(1L));
        assertThat(compositeFilter.shouldPass(msg)).isFalse();
    }

    @Test
    void shouldBlockUpdateFromExcludedAuthor() {
        RawUpdateMessage msg =
                new RawUpdateMessage(3L, "New pull request merged into main branch", "bot-user", List.of(1L));
        assertThat(compositeFilter.shouldPass(msg)).isFalse();
    }

    @Test
    void shouldBlockUpdateTooShort() {
        RawUpdateMessage msg = new RawUpdateMessage(4L, "Short msg", "john-doe", List.of(1L));
        assertThat(compositeFilter.shouldPass(msg)).isFalse();
    }
}
