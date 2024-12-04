package ua.galagandevelopment.Bot.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ua.galagandevelopment.Bot.models.XAccount;
import ua.galagandevelopment.Bot.repositories.XAccountRepository;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class XAccountService {
    private final XAccountRepository xAccountRepository;

    public XAccount getXAccountByEmail(String em) {return xAccountRepository.getAccountByEmail(em);}
    public XAccount saveXAccount(XAccount xAccount) {return xAccountRepository.save(xAccount);}
    public List<XAccount> findAll(){ return xAccountRepository.findAll();};
}
