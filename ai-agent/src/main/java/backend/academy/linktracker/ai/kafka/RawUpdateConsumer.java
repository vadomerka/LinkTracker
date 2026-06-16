package backend.academy.linktracker.ai.kafka;

import backend.academy.linktracker.ai.pipeline.UpdateProcessingPipeline;
import backend.academy.linktracker.models.kafka.RawUpdateMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class RawUpdateConsumer {

    private static final Logger log = LoggerFactory.getLogger(RawUpdateConsumer.class);

    private final UpdateProcessingPipeline pipeline;
    private final ProcessedUpdateProducer producer;
    private final ObjectMapper objectMapper;

    public RawUpdateConsumer(
            UpdateProcessingPipeline pipeline, ProcessedUpdateProducer producer, ObjectMapper objectMapper) {
        this.pipeline = pipeline;
        this.producer = producer;
        this.objectMapper = objectMapper;
    }

    @KafkaListener(topics = "#{@kafkaTopicsProperties.inputTopic}", groupId = "#{@kafkaTopicsProperties.groupId}")
    public void consume(String message) {
        RawUpdateMessage raw;
        try {
            raw = objectMapper.readValue(message, RawUpdateMessage.class);
        } catch (Exception e) {
            log.error("Не удалось десериализовать сообщение из link.raw-updates: {}", message, e);
            return;
        }

        pipeline.process(raw).ifPresent(producer::send);
    }
}
