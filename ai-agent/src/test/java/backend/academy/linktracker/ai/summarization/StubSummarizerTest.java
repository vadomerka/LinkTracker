package backend.academy.linktracker.ai.summarization;

import backend.academy.linktracker.ai.properties.AiAgentProperties;
import backend.academy.linktracker.ai.properties.SummarizationProperties;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class StubSummarizerTest {

    private StubSummarizer summarizer;
    private static final int THRESHOLD = 50;

    @BeforeEach
    void setUp() {
        SummarizationProperties sumProps = new SummarizationProperties();
        sumProps.setThreshold(THRESHOLD);
        AiAgentProperties props = new AiAgentProperties();
        props.setSummarization(sumProps);
        summarizer = new StubSummarizer(props);
    }

    @Test
    void shouldSummarizeLongText() {
        String longText = "A".repeat(THRESHOLD + 10);
        String result = summarizer.summarize(longText);
        assertThat(result).hasSize(THRESHOLD + 3);
        assertThat(result).endsWith("...");
        assertThat(result).doesNotContain(longText);
    }

    @Test
    void shouldNotSummarizeShortText() {
        String shortText = "A".repeat(THRESHOLD - 1);
        String result = summarizer.summarize(shortText);
        assertThat(result).isEqualTo(shortText);
        assertThat(result).doesNotEndWith("...");
    }

    @Test
    void shouldNotSummarizeTextAtExactThreshold() {
        String text = "A".repeat(THRESHOLD);
        String result = summarizer.summarize(text);
        assertThat(result).isEqualTo(text);
    }
}
