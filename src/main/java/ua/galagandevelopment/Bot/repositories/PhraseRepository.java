package ua.galagandevelopment.Bot.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.galagandevelopment.Bot.models.Phrase;

@Repository
public interface PhraseRepository extends JpaRepository<Phrase, Long> {
    Phrase findByText(String text);
    Phrase findById(long id);
}
