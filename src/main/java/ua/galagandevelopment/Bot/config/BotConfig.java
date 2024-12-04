package ua.galagandevelopment.Bot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Data
public class BotConfig {
    @Value("GDParserWork") String botName;
    @Value("8187232974:AAHAjtp1u6bk9gaMi5lfe1a8VvaaBB32R9g") String token;
    @Value("id") String chatId;
}
