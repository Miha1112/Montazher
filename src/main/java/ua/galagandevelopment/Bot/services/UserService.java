package ua.galagandevelopment.Bot.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.galagandevelopment.Bot.models.User;
import ua.galagandevelopment.Bot.repositories.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public User save(User user){
        return userRepository.save(user);
    }

    public User getById(Long id){
        return userRepository.findById(id).orElse(null);
    }

    public User getByTelegramId(Long id){
        return userRepository.findByTelegramId(id);
    }

    public void delete(User user){
        userRepository.delete(user);
    }
}
