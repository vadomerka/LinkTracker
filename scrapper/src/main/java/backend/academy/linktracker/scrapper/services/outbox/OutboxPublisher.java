package backend.academy.linktracker.scrapper.services.outbox;

import backend.academy.linktracker.scrapper.properties.OutboxProperties;
import backend.academy.linktracker.scrapper.repositories.OutboxMessageRepository;
import backend.academy.linktracker.scrapper.services.senders.KafkaSender;
import java.time.Instant;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@Component
@ConditionalOnProperty(prefix = "app.kafka.outbox", name = "enabled", havingValue = "true", matchIfMissing = true)
public class OutboxPublisher {

    private static final Logger log = LoggerFactory.getLogger(OutboxPublisher.class);

    private final OutboxMessageRepository repository;
    private final KafkaSender kafkaSender;
    private final OutboxProperties outboxProperties;
    private final TransactionTemplate transactionTemplate;

    public OutboxPublisher(
            OutboxMessageRepository repository,
            KafkaSender kafkaSender,
            OutboxProperties outboxProperties,
            PlatformTransactionManager transactionManager) {
        this.repository = repository;
        this.kafkaSender = kafkaSender;
        this.outboxProperties = outboxProperties;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    @Scheduled(fixedDelayString = "${app.kafka.outbox.poll-interval-ms:5000}")
    public void publishPendingMessages() {
        var ids = repository.findUnprocessedIds(PageRequest.of(0, outboxProperties.getBatchSize()));
        for (UUID id : ids) {
            transactionTemplate.executeWithoutResult(status -> publishOne(id));
        }
    }

    private void publishOne(UUID id) {
        var message = repository.findUnprocessedByIdForUpdate(id).orElse(null);
        if (message == null) {
            return;
        }

        try {
            kafkaSender.sendMessage(message.getTopic(), message.getMessageKey(), message.getPayload());
            message.setProcessedAt(Instant.now());
            message.setErrorMessage(null);
            log.debug("Published outbox message id={} to topic={}", id, message.getTopic());
        } catch (Exception e) {
            message.setErrorMessage(e.getMessage());
            log.error("Failed to publish outbox message id={} to topic={}", id, message.getTopic(), e);
        }
    }
}
