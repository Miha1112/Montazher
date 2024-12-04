package ua.galagandevelopment.Bot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.galagandevelopment.Bot.models.User;


public interface UserRepository extends JpaRepository<User, Long> {
    User findByTelegramId(Long id);
}
