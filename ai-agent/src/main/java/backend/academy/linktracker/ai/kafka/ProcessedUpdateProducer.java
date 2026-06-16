package backend.academy.linktracker.ai.kafka;

import backend.academy.linktracker.ai.properties.KafkaTopicsProperties;
import backend.academy.linktracker.models.kafka.ProcessedUpdateMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class ProcessedUpdateProducer {

    private static final Logger log = LoggerFactory.getLogger(ProcessedUpdateProducer.class);

    private final KafkaTopicsProperties topics;
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public ProcessedUpdateProducer(
            KafkaTopicsProperties topics, KafkaTemplate<String, String> kafkaTemplate, ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.topics = topics;
        this.objectMapper = objectMapper;
    }

    public void send(ProcessedUpdateMessage message) {
        try {
            String json = objectMapper.writeValueAsString(message);
            kafkaTemplate.send(topics.getOutputTopic(), String.valueOf(message.id()), json);
            log.debug("Опубликовано обновление {} в {}", message.id(), topics.getOutputTopic());
        } catch (Exception e) {
            log.error("Ошибка публикации обновления {} в Kafka", message.id(), e);
        }
    }
}
