package backend.academy.linktracker.scrapper.services.requests;

import backend.academy.linktracker.models.exceptions.ScrapperRequestException;
import java.util.List;
import backend.academy.linktracker.models.exceptions.UrlFormatException;
import backend.academy.linktracker.models.http.external.LinkUpdateData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ScrapperSenderService {
    private static final Logger logger = LoggerFactory.getLogger(ScrapperSenderService.class);
    private final List<UpdateRequestSender> senders;

    public ScrapperSenderService(List<UpdateRequestSender> senders) {
        this.senders = senders;
    }

    public LinkUpdateData getLinkUpdateData(String url) {
        LinkUpdateData ans = null;
        var s = getSenderType(url);
        if (s == null) {
            logger.info("Ссылка не соответствует формату.");
            return null;
        }
        try {
            ans = s.getResponse(url);
        } catch (ScrapperRequestException ex) {
            logger.info("Произошла ошибка при получении обновления по ссылке. {}", ex.toString());
        } catch (UrlFormatException ex) {
            logger.info("Ошибка. {}", ex.toString());
        } catch (Exception ex) {
            logger.info("Ссылка не соответствует формату. {}", ex.toString());
        }
        return ans;
    }

    public UpdateRequestSender getSenderType(String url) {
        for (var s : senders) {
            if (url.contains(s.getRoot())) {
                return s;
            }
        }
        return null;
    }
}
