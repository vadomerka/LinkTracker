package backend.academy.linktracker.bot.models;

import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
public class ChatStatus {
    private String cmdName;
    private String stage;
    private List<String> chatData;

    public ChatStatus(String cmdName, String stage, List<String> chatData) {
        this.cmdName = cmdName;
        this.stage = stage;
        this.chatData = chatData;
    }

    public void addData(String data) {
        chatData.add(data);
    }
}
