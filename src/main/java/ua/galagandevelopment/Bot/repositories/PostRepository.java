package ua.galagandevelopment.Bot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import ua.galagandevelopment.Bot.models.Post;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, String> {
    Post findByTitle(String title);
    Post findById(int id);
    Optional<Post> findByUrl(String url); // Перевірка, чи є пост у БД
}
