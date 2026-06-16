package backend.academy.linktracker.ai.filtering;

import backend.academy.linktracker.ai.properties.AiAgentProperties;
import backend.academy.linktracker.ai.properties.FilteringProperties;
import backend.academy.linktracker.models.kafka.RawUpdateMessage;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StopWordFilterTest {

    private StopWordFilter filter;

    @BeforeEach
    void setUp() {
        FilteringProperties filtering = new FilteringProperties();
        filtering.setStopWords(List.of("spam", "ads", "promo"));
        AiAgentProperties props = new AiAgentProperties();
        props.setFiltering(filtering);
        filter = new StopWordFilter(props);
    }

    @Test
    void shouldRejectMessageContainingStopWord() {
        RawUpdateMessage msg = new RawUpdateMessage(1L, "This is a spam message", "user", List.of(1L));
        assertThat(filter.shouldPass(msg)).isFalse();
    }

    @Test
    void shouldRejectMessageContainingStopWordCaseInsensitive() {
        RawUpdateMessage msg = new RawUpdateMessage(2L, "Check out these ADS", "user", List.of(1L));
        assertThat(filter.shouldPass(msg)).isFalse();
    }

    @Test
    void shouldPassMessageWithoutStopWords() {
        RawUpdateMessage msg = new RawUpdateMessage(3L, "New feature released in version 2.0", "user", List.of(1L));
        assertThat(filter.shouldPass(msg)).isTrue();
    }
}
