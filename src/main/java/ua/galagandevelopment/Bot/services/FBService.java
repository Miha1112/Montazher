package ua.galagandevelopment.Bot.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ua.galagandevelopment.Bot.models.FBAccount;
import ua.galagandevelopment.Bot.models.XAccount;
import ua.galagandevelopment.Bot.repositories.FBRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class FBService {
    private final FBRepository fbRepository;
    public FBAccount getFBAccountByEmail(String em) {return fbRepository.getAccountByEmail(em);}
    public FBAccount saveFBAccount(FBAccount fbAccount) {return fbRepository.save(fbAccount);}
    public List<FBAccount> findAll(){ return fbRepository.findAll();};
}
