package backend.academy.linktracker.scrapper.services.senders;

import backend.academy.linktracker.models.http.internal.LinkUpdateRequest;
import backend.academy.linktracker.scrapper.properties.KafkaSenderProperties;
import backend.academy.linktracker.scrapper.services.outbox.OutboxWriter;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

@Service
public class OutboxKafkaBotRequestsSender implements BotRequestsSender {

    private final KafkaSenderProperties properties;
    private final OutboxWriter outboxWriter;
    private final ObjectMapper objectMapper;

    public OutboxKafkaBotRequestsSender(KafkaSenderProperties properties, OutboxWriter outboxWriter) {
        this.properties = properties;
        this.outboxWriter = outboxWriter;
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void sendUpdates(LinkUpdateRequest request) {
        String topic = properties.getTopic();
        String key = properties.getGroup();
        String payload = objectMapper.writeValueAsString(request);
        outboxWriter.enqueue(topic, key, payload);
    }
}
