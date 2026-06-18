package backend.academy.linktracker.scrapper.services.outbox;

import backend.academy.linktracker.scrapper.models.entities.OutboxMessageEntity;
import backend.academy.linktracker.scrapper.repositories.OutboxMessageRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class OutboxWriter {

    private final OutboxMessageRepository repository;

    public OutboxWriter(OutboxMessageRepository repository) {
        this.repository = repository;
    }

    public void enqueue(String topic, String messageKey, String payload) {
        var message = new OutboxMessageEntity();
        message.setId(UUID.randomUUID());
        message.setTopic(topic);
        message.setMessageKey(messageKey);
        message.setPayload(payload);
        repository.save(message);
    }
}
