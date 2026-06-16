package backend.academy.linktracker.ai.filtering;

import backend.academy.linktracker.ai.properties.AiAgentProperties;
import backend.academy.linktracker.ai.properties.FilteringProperties;
import backend.academy.linktracker.models.kafka.RawUpdateMessage;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ExcludedAuthorFilterTest {

    private ExcludedAuthorFilter filter;

    @BeforeEach
    void setUp() {
        FilteringProperties filtering = new FilteringProperties();
        filtering.setExcludedAuthors(List.of("bot-user", "spam-bot"));
        AiAgentProperties props = new AiAgentProperties();
        props.setFiltering(filtering);
        filter = new ExcludedAuthorFilter(props);
    }

    @Test
    void shouldRejectMessageFromExcludedAuthor() {
        RawUpdateMessage msg = new RawUpdateMessage(1L, "Some valid text here", "bot-user", List.of(1L));
        assertThat(filter.shouldPass(msg)).isFalse();
    }

    @Test
    void shouldRejectMessageFromExcludedAuthorCaseInsensitive() {
        RawUpdateMessage msg = new RawUpdateMessage(2L, "Some valid text here", "BOT-USER", List.of(1L));
        assertThat(filter.shouldPass(msg)).isFalse();
    }

    @Test
    void shouldPassMessageFromAllowedAuthor() {
        RawUpdateMessage msg = new RawUpdateMessage(3L, "Some valid text here", "john-doe", List.of(1L));
        assertThat(filter.shouldPass(msg)).isTrue();
    }
}
