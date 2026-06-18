package backend.academy.linktracker.bot.configuration;

import backend.academy.linktracker.bot.properties.KafkaReceiverProperties;
import backend.academy.linktracker.models.exceptions.MessageValidationException;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.DeserializationException;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
@EnableKafka
public class BotKafkaConsumerConfiguration {

    @Bean("botKafkaListenerContainerFactory")
    @Primary
    public ConcurrentKafkaListenerContainerFactory<String, String> botKafkaListenerContainerFactory(
            ConsumerFactory<String, String> consumerFactory, DefaultErrorHandler kafkaErrorHandler) {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(kafkaErrorHandler);
        return factory;
    }

    @Bean
    public DefaultErrorHandler kafkaErrorHandler(
            KafkaTemplate<String, String> kafkaTemplate,
            @Qualifier("kafkaReceiverProperties") KafkaReceiverProperties properties) {
        DeadLetterPublishingRecoverer recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (ConsumerRecord<?, ?> record, Exception ex) ->
                        new TopicPartition(properties.getDlqTopic(), record.partition()));
        recoverer.setAppendOriginalHeaders(true);

        long retries = Math.max(0, properties.getRetry().getMaxAttempts() - 1L);
        FixedBackOff backOff = new FixedBackOff(properties.getRetry().getBackoffMs(), retries);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler(recoverer, backOff);
        errorHandler.addNotRetryableExceptions(DeserializationException.class, MessageValidationException.class);
        return errorHandler;
    }
}
