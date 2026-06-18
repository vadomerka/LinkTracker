package backend.academy.linktracker.bot.controllers;

import backend.academy.linktracker.bot.services.LinkUpdateMessageProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaBotController {
    private static final Logger log = LoggerFactory.getLogger(KafkaBotController.class);

    private final LinkUpdateMessageProcessor processor;

    public KafkaBotController(LinkUpdateMessageProcessor processor) {
        this.processor = processor;
    }

    @KafkaListener(
            topics = "#{@kafkaReceiverProperties.topic}",
            groupId = "#{@kafkaReceiverProperties.group}",
            containerFactory = "botKafkaListenerContainerFactory")
    public void consume(String message) {
        log.info("Получено сообщение Kafka: {}", message);
        processor.process(message);
    }
}
