package ua.galagandevelopment.Bot.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.galagandevelopment.Bot.models.Phrase;
import ua.galagandevelopment.Bot.repositories.PhraseRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PhraseService {
    private final PhraseRepository phraseRepository;

    public Phrase save(Phrase phrase) {return phraseRepository.save(phrase);}
    public Phrase findById(Long id) {return phraseRepository.findById(id).orElse(null);}
    public List<Phrase> findAll() {return phraseRepository.findAll();}
    public Phrase findByText(String text) {return phraseRepository.findByText(text);}
}
