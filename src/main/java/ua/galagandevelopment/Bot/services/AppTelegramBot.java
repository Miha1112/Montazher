package ua.galagandevelopment.Bot.services;

import jakarta.persistence.EntityManager;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ua.galagandevelopment.Bot.config.BotConfig;
import ua.galagandevelopment.Bot.models.*;
import ua.galagandevelopment.Bot.repositories.FBRepository;
import ua.galagandevelopment.Bot.repositories.PostRepository;
import ua.galagandevelopment.Bot.repositories.XAccountRepository;
import ua.galagandevelopment.Bot.utils.ParseFB;
import ua.galagandevelopment.Bot.utils.ParseReddit;
import ua.galagandevelopment.Bot.utils.ParseX;
import ua.galagandevelopment.Bot.utils.ParserThreads;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppTelegramBot  extends TelegramLongPollingBot {
    private final BotConfig config;
    private final UserService userService;
    private final PostRepository postRepository;
    private final PhraseService phraseService;
    private final XAccountRepository xRepository;
    private final FBRepository fbRepository;
    private final ParseX parseX;
    EntityManager entityManager;
    private final ParserThreads parserThreads;
    private String email;
    private String password;
    private String name;
    private boolean fbSwicher;
    private int bootStatus = 0;
    private final ParseFB parseFB;
    private final ParseReddit parseReddit;
    @Override
    public String getBotUsername() {
        return config.getBotName();
    }

    @Override
    public String getBotToken() {
        return config.getToken();
    }

    @Override
    public void onUpdateReceived(@NotNull Update update) {
        Long chatId;
        User user = userService.getByTelegramId(update.getMessage().getFrom().getId());

        if (update.hasMessage()) {
            chatId = update.getMessage().getChatId();
            System.out.println(chatId+"   44444444");

            if(update.getMessage().hasText()){
                if(user != null)
                    checkText(update, chatId, user);

                if(user == null && update.getMessage().getText().equals("/start"))
                    registration(update);
            }
        }
    }

    private void checkText(Update update, Long chatId, User user) {
        switch (bootStatus){
            case 1:
                if (!update.getMessage().getText().equals("/add_phrase")){
                    AddKeysPhrase(update.getMessage().getText());
                    sendMessageToChat(chatId,"Phrase added");
                }
                break;
            case 2:
                if (!update.getMessage().getText().equals("/addXAccount")){
                    email = update.getMessage().getText();
                    sendMessageToChat(chatId,"Nice, i have write email, now send me a password");
                    bootStatus = 3;
                }
            break;
            case 3:
                if (!update.getMessage().getText().equals("/addXAccount")){
                    password = update.getMessage().getText();
                    sendMessageToChat(chatId,"Nice, i have write password, now send username");
                    bootStatus = 4;

                }
            break;
            case 4:{
                if (!update.getMessage().getText().equals("/addXAccount") &&!fbSwicher) {
                    name = update.getMessage().getText();
                    addXAccount();
                    sendMessageToChat(chatId, "XAccount added");
                    bootStatus = 0;
                }else if (fbSwicher){
                    name = update.getMessage().getText();
                    addFBAccount();
                    sendMessageToChat(chatId, "FB account added");
                }
            }
        }
        if (update.getMessage().getText().equals("/parse_threads")) {
            sendMessageToChat(chatId,"Start parsing threads");
            List<Post> posts = parserThreads.init();
            if (posts != null) {
                for (Post post : posts) {
                    if (!postRepository.findByUrl(post.getUrl()).isPresent()) {
                        postRepository.save(post); // Збереження нового поста
                        String text = post.getTitle() + "\n" + post.getContent() + "\n" + post.getUrl();
                        sendMessageToChat(chatId, text);
                        post.setIsParsed(true);
                        postRepository.save(post); // Оновлення статусу
                    }
                }
            }else {
                sendMessageToChat(chatId,"No new post");
            }
        }else if (update.getMessage().getText().equals("/add_phrase")){
            if (bootStatus == 0) {
                bootStatus = 1;
                sendMessageToChat(chatId, "Now you can send key-phrase, when end of phrase send a command one more time");
            }else if (bootStatus == 1) {
                 bootStatus = 0;
                 sendMessageToChat(chatId,"Off add key-phrase mode");
            }

        }else if (update.getMessage().getText().equals("/addXAccount")) {
            if (bootStatus != 2) {
                bootStatus = 2;
                sendMessageToChat(chatId,"Input email, then input password, after that account has bean added to database");
            }
        } else if (update.getMessage().getText().equals("/parse_x")) {
            sendMessageToChat(chatId,"Start parsing X");
            List<Post> posts = parseX.init();
            if (posts != null) {
                for (Post post : posts) {
                    if (!postRepository.findByUrl(post.getUrl()).isPresent()) {
                        postRepository.save(post); // Збереження нового поста
                        String text = post.getTitle() + "\n" + post.getContent() + "\n" + post.getUrl();
                        sendMessageToChat(chatId, text);
                        post.setIsParsed(true);
                        postRepository.save(post); // Оновлення статусу
                    }
                }
            }else {
                sendMessageToChat(chatId,"No new Post");
            }
        }else if (update.getMessage().getText().equals("/parse_fb")) {
            sendMessageToChat(chatId,"Start parsing FB");
            List<Post> posts = parseFB.init();
            if (posts != null) {
                for (Post post : posts) {
                    if (!postRepository.findByUrl(post.getUrl()).isPresent()) {
                        postRepository.save(post); // Збереження нового поста
                        String text = post.getTitle() + "\n" + post.getContent() + "\n" + post.getUrl();
                        sendMessageToChat(chatId, text);
                        post.setIsParsed(true);
                        postRepository.save(post); // Оновлення статусу
                    }
                }
            }else {
                sendMessageToChat(chatId,"No new Post");
            }
        }else if (update.getMessage().getText().equals("/addFBAccount")) {
            if (bootStatus != 2) {
                bootStatus = 2;
                sendMessageToChat(chatId,"Input email, then input password, after that account has bean added to database");
                fbSwicher = true;
            }
        }else if (update.getMessage().getText().equals("/parse_reddit")) {
            sendMessageToChat(chatId,"Start parsing Reddit");
            List<Post> posts = parseReddit.init();
            if (posts != null) {
                for (Post post : posts) {
                    if (!postRepository.findByUrl(post.getUrl()).isPresent()) {
                        postRepository.save(post); // Збереження нового поста
                        String text = post.getTitle() + "\n" + post.getContent() + "\n" + post.getUrl();
                        sendMessageToChat(chatId, text);
                        post.setIsParsed(true);
                        postRepository.save(post); // Оновлення статусу
                    }
                }
            }else {
                sendMessageToChat(chatId,"No new Post");
            }
        }
    }

    private void AddKeysPhrase(String phrase_text) {
        Phrase phrase = new Phrase();
        phrase.setText(phrase_text);
        phraseService.save(phrase);
    }
    private void addXAccount(){
        XAccount xAccount = new XAccount();
        xAccount.setEmail(email);
        xAccount.setPassword(password);
        xAccount.setName(name);
        xRepository.save(xAccount);
        email ="";
        password = "";
        name = "";
    }
    private void addFBAccount(){
        FBAccount fbAccount = new FBAccount();
        fbAccount.setEmail(email);
        fbAccount.setPassword(password);
        fbAccount.setName(name);
        fbRepository.save(fbAccount);
        email ="";
        password = "";
        name = "";
    }

    private void registration(Update update){
        User user = new User();
        user.setTelegramId(update.getMessage().getChatId());

        org.telegram.telegrambots.meta.api.objects.User telegramUser = update.getMessage().getFrom();
        String nickname = telegramUser.getUserName();
        if(nickname != null)
            user.setNickname(nickname);

        String name = telegramUser.getFirstName();
        if(telegramUser.getLastName() != null)
            name = name.concat(" " + telegramUser.getLastName());

        if(name != null)
            user.setName(name);

        userService.save(user);
    }
    public void startParseAll(){
      /* List<BotSettings> settings = entityManager.createQuery("SELECT x FROM BotSettings x", BotSettings.class)
               .getResultList();
       BotSettings set = settings.get(0);
       int interval = set.getIntervalMinutes() * 60 * 1000;//1000 milisec koef
       Timer timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                List<Post> postsX = parseX.init();
                sender(postsX);
                System.out.println("Send post x");

                List<Post> postsReddit = parseReddit.init();
                sender(postsReddit);
                System.out.println("Send post reddit");

                List<Post> postsFB = parseFB.init();
                sender(postsFB);
                System.out.println("Send post fb");

                List<Post> postsThreads = parserThreads.init();
                sender(postsThreads);
                System.out.println("Send post threads");
            }
        };
        timer.scheduleAtFixedRate(task, 0, interval);//run code with custom interval*/
        List<Post> postsX = parseX.init();
        sender(postsX);
        System.out.println("Send post x");

        List<Post> postsReddit = parseReddit.init();
        sender(postsReddit);
        System.out.println("Send post reddit");

        List<Post> postsFB = parseFB.init();
        sender(postsFB);
        System.out.println("Send post fb");

        List<Post> postsThreads = parserThreads.init();
        sender(postsThreads);
        System.out.println("Send post threads");
    }
    private void sender(List<Post> posts){
        if (posts != null) {
            try {
                for (Post post : posts) {
                    if (!postRepository.findByUrl(post.getUrl()).isPresent()) {
                        postRepository.save(post);
                        String text = post.getTitle() + "\n" + post.getContent() + "\n" + post.getUrl();
                        sendMessageToChat(-1002386765028L, text);
                        post.setIsParsed(true);
                        postRepository.save(post);
                        Thread.sleep(3000);
                    }else{
                        System.out.println("Old post detected, stariy hlam ide gulyat, to tak nada a ne parser polomavsya");
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
                System.out.println(e.getMessage() + "error in send");
            }
        }else
            System.out.println("Ya poluchil posts = null, opyat parser cherez zhopu robit");
    }

    private void sendMessageToChat( Long chatId,String text){
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        try {
            execute(message);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void sendKeyboardToChat(Long chatId, String text, ReplyKeyboardMarkup keyboardMarkup){
        SendMessage response = new SendMessage();
        response.setChatId(chatId);
        response.setText(text);
        response.setReplyMarkup(keyboardMarkup);

        try {
            execute(response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendInlineMessage(Long chatId, String text, InlineKeyboardMarkup inlineKeyboardMarkup){
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(text);
        sendMessage.setReplyMarkup(inlineKeyboardMarkup);
        try {
            if(chatId > 0)
                execute(sendMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteMessage(Long chatId, Integer messageId) {
        DeleteMessage deleteMessage = new DeleteMessage();
        deleteMessage.setChatId(chatId);
        deleteMessage.setMessageId(messageId);

        try {
            execute(deleteMessage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
