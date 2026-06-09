package backend.academy.linktracker.scrapper.services.senders;

import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

@Service
public class KafkaSenderService {
    private static final Logger log = LoggerFactory.getLogger(KafkaSenderService.class);

    private final KafkaTemplate<String, String> kafkaTemplate;

    public KafkaSenderService(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public SendResult<String, String> sendMessage(String topic, String key, String message) throws Exception {
        SendResult<String, String> result;
        if (key == null || key.isBlank()) {
            result = kafkaTemplate.send(topic, message).get(10, TimeUnit.SECONDS);
        } else {
            result = kafkaTemplate.send(topic, key, message).get(10, TimeUnit.SECONDS);
        }
        log.info(
                "Sent message to topic={}, partition={}, offset={}",
                topic,
                result.getRecordMetadata().partition(),
                result.getRecordMetadata().offset());
        return result;
    }
}
