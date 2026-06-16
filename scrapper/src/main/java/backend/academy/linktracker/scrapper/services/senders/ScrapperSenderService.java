package backend.academy.linktracker.scrapper.services.senders;

import backend.academy.linktracker.models.exceptions.ScrapperRequestException;
import backend.academy.linktracker.models.exceptions.UrlFormatException;
import backend.academy.linktracker.models.http.external.LinkUpdateData;
import backend.academy.linktracker.scrapper.services.senders.update.UpdateRequestSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class ScrapperSenderService {
    private static final Logger logger = LoggerFactory.getLogger(ScrapperSenderService.class);
    private final List<UpdateRequestSender> senders;

    public ScrapperSenderService(List<UpdateRequestSender> senders) {
        this.senders = senders;
    }

    public LinkUpdateData getLinkUpdateData(String url) {
        LinkUpdateData ans = null;
        var dataRequestSender = getSenderType(url);
        if (dataRequestSender == null) {
            logger.info("Ссылка не соответствует формату.");
            return null;
        }
        try {
            ans = dataRequestSender.getLinkResponse(url);
        } catch (ScrapperRequestException ex) {
            logger.error("Ошибка при получении обновления url={}: {}", url, ex.getMessage(), ex);
        } catch (UrlFormatException ex) {
            logger.warn("Ссылка не распознана url={}: {}", url, ex.getMessage());
        } catch (Exception ex) {
            logger.error("Неожиданная ошибка url={}: {}", url, ex.getMessage(), ex);
        }
        return ans;
    }

    public UpdateRequestSender getSenderType(String url) {
        for (var s : senders) {
            if (s.checkLink(url)) {
                return s;
            }
        }
        return null;
    }
}
