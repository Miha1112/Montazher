package ua.galagandevelopment.Bot.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import ua.galagandevelopment.Bot.models.FBAccount;
import ua.galagandevelopment.Bot.models.Phrase;
import ua.galagandevelopment.Bot.models.Post;
import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class ParseFB {
    @PersistenceContext
    private EntityManager entityManager;
    public List<Post> init(){
        List<Post> posts = new ArrayList<>();
        //System.setProperty("webdriver.chrome.driver", "/home/ubuntu/bot/chromedriver");
        System.setProperty("webdriver.chrome.driver", "./chromedriver.exe");
        List<Phrase> allPhrase = entityManager.createQuery("SELECT x FROM Phrase x", Phrase.class)
                .getResultList();
        for (Phrase phrase : allPhrase) {
            String searchUrl = "https://www.facebook.com/search/posts?q="+ phrase.getText();
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--window-size=1280,1024");
            options.addArguments("--disable-notifications");
            WebDriver driver = new ChromeDriver(options);

            List<FBAccount> fbAccounts = entityManager.createQuery("SELECT x FROM FBAccount x", FBAccount.class)
                    .getResultList();
            FBAccount fbAccount = fbAccounts.get(0);

            String url = "https://www.facebook.com/";
            driver.get(url);

            try {
                WebElement email = driver.findElement(By.cssSelector("input[name='email']"));
                email.sendKeys(fbAccount.getEmail());
                Thread.sleep(2000);
                WebElement pass = driver.findElement(By.cssSelector("input[name='pass']"));
                pass.sendKeys(fbAccount.getPassword());
                Thread.sleep(2000);
                pass.sendKeys(Keys.RETURN);
                Thread.sleep(4000);

                String searchLink = "https://www.facebook.com/search/posts/?q=" + phrase.getText();
                driver.get(searchLink);
                for (int i = 1; i <= 10; i++) {
                    try {
                        System.out.println("Start select post");
                        WebElement parentDiv = driver.findElement(By.cssSelector("[aria-posinset='" + i + "']"));
                        System.out.println("find parent ok");

                        WebElement profileDiv = parentDiv.findElement(By.cssSelector("div[data-ad-rendering-role='profile_name']"));
                        System.out.println("find profile ok");

                        WebElement profileLinkElement = profileDiv.findElement(By.tagName("a"));
                        System.out.println("find link ok");
                        String profileLink = profileLinkElement.getAttribute("href");
                        System.out.println("all is ok");

                        String textPost = cleanText(parentDiv.getText());
                        System.out.println("no text mtfk");

                        posts.add(new Post(null, "New post", textPost, profileLink, false));
                        System.out.println("new post added successfully");

                    } catch (NoSuchElementException e) {
                        System.out.println("Can`t find post aria-posinset='" + i + "'");
                    }
                    if (i % 3 == 0) {
                        try {
                            JavascriptExecutor js = (JavascriptExecutor) driver;
                            js.executeScript("window.scrollBy(0, document.body.scrollHeight * 0.4);");
                            System.out.println("skip page at 5%, time to sleep");
                            Thread.sleep(4000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }
            driver.quit();
        }
        return posts;
    }
    private String cleanText(String rawText) {
        String cleanedText = rawText
                .replaceAll("\\d{1,2}:\\d{2} / \\d{1,2}:\\d{2}", "")
                .replaceAll("(Подобається|Коментувати|Поширити|Стежити)", "")
                .replaceAll("\\s{2,}", " ")
                .trim();
        return cleanedText;
    }
}
