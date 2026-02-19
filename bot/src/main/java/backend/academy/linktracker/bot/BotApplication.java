package backend.academy.linktracker.bot;

import com.pengrad.telegrambot.ExceptionHandler;
import com.pengrad.telegrambot.TelegramBot;
import com.pengrad.telegrambot.TelegramException;
import com.pengrad.telegrambot.UpdatesListener;
import com.pengrad.telegrambot.model.Update;
import com.pengrad.telegrambot.request.SendMessage;
import com.pengrad.telegrambot.response.SendResponse;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import java.util.List;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BotApplication {
    static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
//        System.out.println(System.getenv("MEGA_TELEGRAM_TOKEN"));
//        minBot();
    }

    private static void minBot() {
//        System.out.println("minibot start");
//        // Create your bot passing the token received from @BotFather
////        String token = System.getenv().size();
//        var vars = System.getenv();
//        for (var k: vars.keySet()) {
//            System.out.print(k);
//            System.out.print(" ");
//            System.out.println(vars.get(k));
//        }
        String token = "8293341493:AAGKJRB9AZWdzlvvdMGA5zg528Qx7mxl9yY";
        TelegramBot bot = new TelegramBot(token);

// Register for updates
        bot.setUpdatesListener(updates -> {
            // Send messages
            for (var u: updates) {
                long chatId = u.message().chat().id();
                SendResponse response = bot.execute(new SendMessage(chatId, "Hello!"));
                // return id of last processed update or confirm them all
            }
            return UpdatesListener.CONFIRMED_UPDATES_ALL;
// Create Exception Handler
        }, e -> {
            if (e.response() != null) {
                // got bad response from telegram
                e.response().errorCode();
                e.response().description();
            } else {
                // probably network error
                e.printStackTrace();
            }
        });


    }
}
