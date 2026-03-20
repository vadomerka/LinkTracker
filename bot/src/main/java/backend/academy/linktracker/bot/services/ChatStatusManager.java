package backend.academy.linktracker.bot.services;

import backend.academy.linktracker.bot.models.ChatStatus;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class ChatStatusManager {
    private static final ChatStatus DEFAULT_STATUS = new ChatStatus("default", null, null);
    private final Map<Long, ChatStatus> states = new ConcurrentHashMap<>();

    public ChatStatusManager() {
    }

    public boolean isDefault(Long chatId) {
        states.putIfAbsent(chatId, DEFAULT_STATUS);
        return states.get(chatId).getCmdName().equalsIgnoreCase(DEFAULT_STATUS.getCmdName());
    }

    public ChatStatus getCommandStatus(Long chatId) {
        return states.get(chatId);
    }

    public void setChatStatus(Long chatId, ChatStatus chatStatus) {
        states.put(chatId, chatStatus);
    }

    public void setStage(Long chatId, String status) {
        states.get(chatId).setStage(status);
    }

    public void setData(Long chatId, List<String> chatData) {
        states.get(chatId).setChatData(chatData);
    }

    public void addData(Long chatId, String data) {
        states.get(chatId).getChatData().add(data);
    }

    public void addAllData(Long chatId, List<String> data) {
        states.get(chatId).getChatData().addAll(data);
    }

    public void cancelStatus(Long chatId) {
        states.put(chatId, DEFAULT_STATUS);
    }
}
