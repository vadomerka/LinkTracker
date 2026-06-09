package backend.academy.linktracker.scrapper.services.senders.bot;

import backend.academy.linktracker.models.http.internal.LinkUpdateRequest;
import io.github.resilience4j.circuitbreaker.CallNotPermittedException;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Component;

@Component
public class FallbackBotRequestsSender implements BotRequestsSender {

    private final HttpBotRequestsSender httpSender;
    private final KafkaBotRequestsSender kafkaSender;

    public FallbackBotRequestsSender(HttpBotRequestsSender httpSender, KafkaBotRequestsSender kafkaSender) {
        this.httpSender = httpSender;
        this.kafkaSender = kafkaSender;
    }

    @Override
    @CircuitBreaker(name = "bot-http", fallbackMethod = "sendViaKafka")
    public void sendUpdates(LinkUpdateRequest request) {
        httpSender.sendUpdates(request);
    }

    // invoked only when Circuit Breaker is OPEN (FR-10)
    void sendViaKafka(LinkUpdateRequest request, CallNotPermittedException ex) {
        kafkaSender.sendUpdates(request);
    }
}
