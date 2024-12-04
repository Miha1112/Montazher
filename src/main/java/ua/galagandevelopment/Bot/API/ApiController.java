package ua.galagandevelopment.Bot.API;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ua.galagandevelopment.Bot.services.AppTelegramBot;
import ua.galagandevelopment.Bot.services.UserService;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {
    private final UserService userService;

    private final AppTelegramBot bot;

    @PostMapping("/start")
    public ResponseEntity<String> sendMessage(){
        bot.startParseAll();
        return ResponseEntity.ok("Bot started");
    };

}
