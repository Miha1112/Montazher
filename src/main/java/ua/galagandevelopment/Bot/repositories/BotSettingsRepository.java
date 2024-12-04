package ua.galagandevelopment.Bot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.galagandevelopment.Bot.models.BotSettings;

public interface BotSettingsRepository extends JpaRepository<BotSettings, Long> {
}
