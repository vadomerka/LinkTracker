package backend.academy.linktracker.ai.filtering;

import backend.academy.linktracker.ai.properties.AiAgentProperties;
import backend.academy.linktracker.ai.properties.FilteringProperties;
import backend.academy.linktracker.models.kafka.RawUpdateMessage;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class MinLengthFilterTest {

    private MinLengthFilter filter;

    @BeforeEach
    void setUp() {
        FilteringProperties filtering = new FilteringProperties();
        filtering.setMinLength(20);
        AiAgentProperties props = new AiAgentProperties();
        props.setFiltering(filtering);
        filter = new MinLengthFilter(props);
    }

    @Test
    void shouldRejectMessageShorterThanMinLength() {
        RawUpdateMessage msg = new RawUpdateMessage(1L, "Too short", "user", List.of(1L));
        assertThat(filter.shouldPass(msg)).isFalse();
    }

    @Test
    void shouldRejectMessageExactlyAtBoundary() {
        String text = "A".repeat(19);
        RawUpdateMessage msg = new RawUpdateMessage(2L, text, "user", List.of(1L));
        assertThat(filter.shouldPass(msg)).isFalse();
    }

    @Test
    void shouldPassMessageMeetingMinLength() {
        String text = "A".repeat(20);
        RawUpdateMessage msg = new RawUpdateMessage(3L, text, "user", List.of(1L));
        assertThat(filter.shouldPass(msg)).isTrue();
    }

    @Test
    void shouldPassMessageLongerThanMinLength() {
        RawUpdateMessage msg =
                new RawUpdateMessage(4L, "This is a sufficiently long message text", "user", List.of(1L));
        assertThat(filter.shouldPass(msg)).isTrue();
    }
}
