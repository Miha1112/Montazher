package ua.galagandevelopment.Bot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.galagandevelopment.Bot.models.XAccount;

import java.util.List;

public interface XAccountRepository extends JpaRepository<XAccount, Long> {
    XAccount getAccountByEmail(String email);
}
