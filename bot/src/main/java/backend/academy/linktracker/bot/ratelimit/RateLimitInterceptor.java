package backend.academy.linktracker.bot.ratelimit;

import backend.academy.linktracker.bot.properties.RateLimitProperties;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final ConcurrentHashMap<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final RateLimitProperties props;

    public RateLimitInterceptor(RateLimitProperties props) {
        this.props = props;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String ip = resolveClientIp(request);
        Bucket bucket = buckets.computeIfAbsent(ip, k -> createBucket());
        if (bucket.tryConsume(1)) {
            return true;
        }
        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        return false;
    }

    private Bucket createBucket() {
        Bandwidth limit = Bandwidth.builder()
                .capacity(props.getCapacity())
                .refillGreedy(props.getRefillTokens(), props.getRefillPeriod())
                .build();
        return Bucket.builder().addLimit(limit).build();
    }

    private String resolveClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        return (xff != null && !xff.isBlank()) ? xff.split(",")[0].trim() : request.getRemoteAddr();
    }
}
