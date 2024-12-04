package ua.galagandevelopment.Bot.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "FBAccount")
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class FBAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String email;
    private String name;
    private String password;
}
