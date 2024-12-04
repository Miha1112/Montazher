package ua.galagandevelopment.Bot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.galagandevelopment.Bot.models.FBAccount;

public interface FBRepository extends JpaRepository<FBAccount, Long> {
    FBAccount getAccountByEmail(String email);
}
