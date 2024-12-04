package ua.galagandevelopment.Bot.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "botsetting")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class BotSettings {
    @Id
    private Long id = 1L;

    private int intervalMinutes;
    private boolean isRunning;
}
