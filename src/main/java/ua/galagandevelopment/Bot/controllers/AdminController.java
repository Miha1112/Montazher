package ua.galagandevelopment.Bot.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ua.galagandevelopment.Bot.models.*;
import ua.galagandevelopment.Bot.repositories.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private PhraseRepository phraseRepository;
    @Autowired
    private XAccountRepository xAccountRepository;
    @Autowired
    private FBRepository fbRepository;
    @Autowired
    private BotSettingsRepository settingsRepository;

    @GetMapping("/main")
    public String adminDashboard() {
        return "admin/index";
    }
    // Ключові фрази
    @GetMapping("/phrases")
    public String viewPhrases(Model model) {
        model.addAttribute("phrases", phraseRepository.findAll());
        model.addAttribute("newPhrase", new Phrase());
        return "admin/phrases";
    }
    @DeleteMapping("/phrases/{id}")
    public String deletePhrase(@PathVariable Long id) {
        phraseRepository.deleteById(id);
        return "redirect:/admin/phrases";
    }

    @PostMapping("/phrases")
    public String addPhrase(@ModelAttribute Phrase phrase) {
        phraseRepository.save(phrase);
        return "redirect:/admin/phrases";
    }

    // Акаунти Twitter
    @GetMapping("/x-accounts")
    public String viewXAccounts(Model model) {
        model.addAttribute("xAccounts", xAccountRepository.findAll());
        model.addAttribute("newXAccount", new XAccount());
        return "admin/x-accounts";
    }

    @PostMapping("/x-accounts")
    public String addXAccount(@ModelAttribute XAccount xAccount) {
        xAccountRepository.save(xAccount);
        return "redirect:/admin/x-accounts";
    }

    // Акаунти Facebook
    @GetMapping("/fb-accounts")
    public String viewFBAccounts(Model model) {
        model.addAttribute("fbAccounts", fbRepository.findAll());
        model.addAttribute("newFBAccount", new FBAccount());
        return "admin/fb-accounts";
    }

    @PostMapping("/fb-accounts")
    public String addFBAccount(@ModelAttribute FBAccount fbAccount) {
        fbRepository.save(fbAccount);
        return "redirect:/admin/fb-accounts";
    }

    // Налаштування бота
    @GetMapping("/settings")
    public String viewSettings(Model model) {
        BotSettings settings = settingsRepository.findById(1L)
                .orElse(new BotSettings());
        model.addAttribute("settings", settings);
        return "admin/settings";
    }

    @PostMapping("/settings")
    public String updateSettings(@ModelAttribute BotSettings settings) {
        settings.setId(1L);
        settingsRepository.save(settings);
        return "redirect:/admin/settings";
    }

    @PostMapping("/settings/toggle")
    public String toggleBot() {
        BotSettings settings = settingsRepository.findById(1L)
                .orElse(new BotSettings());
        settings.setRunning(!settings.isRunning());
        settingsRepository.save(settings);
        return "redirect:/admin/settings";
    }
}
