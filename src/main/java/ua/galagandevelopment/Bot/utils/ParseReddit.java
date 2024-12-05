package ua.galagandevelopment.Bot.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.stereotype.Component;
import ua.galagandevelopment.Bot.models.Phrase;
import ua.galagandevelopment.Bot.models.Post;

import java.util.ArrayList;
import java.util.List;

@Component
public class ParseReddit {
    @PersistenceContext
    private EntityManager entityManager;
    public List<Post> init() {
        List<Post> posts = new ArrayList<>();
        List<Phrase> allPhrase = entityManager.createQuery("SELECT x FROM Phrase x", Phrase.class)
                .getResultList();
        //System.setProperty("webdriver.chrome.driver", "/home/ubuntu/bot/chromedriver");
        System.setProperty("webdriver.chrome.driver", "./chromedriver.exe");
        try {
            for (Phrase phrase : allPhrase) {
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--window-size=1280,1024");
                options.addArguments("--disable-notifications");
                WebDriver driver = new ChromeDriver(options);
                String searchUrl = "https://www.reddit.com/search/?q=" + phrase.getText() + "&sort=new";
                driver.get(searchUrl);
                Thread.sleep(3000);
                List<WebElement> postElements = driver.findElements(By.tagName("search-telemetry-tracker"));
                System.out.println("Reddit parser: " + postElements.size());
                for (WebElement postElement : postElements) {
                    try {
                        System.out.println("start select reddit post");
                        WebElement linkElement = postElement.findElement(By.cssSelector("a[data-testid='post-title']"));
                        System.out.println("link get successful: " + linkElement.getAttribute("href"));
                        String postLink = linkElement.getAttribute("href");
                        String postTitle = linkElement.getAttribute("aria-label");
                        posts.add(new Post(null, "Знайдено новий пост", postTitle, postLink, false));
                        System.out.println("New reddit post added successfully");
                    } catch (Exception e) {
                        System.out.println("No post link");
                    }
                }
                driver.quit();
            }
        } catch (Exception e) {
            System.out.println("Something went wrong in the parsing process");
            e.printStackTrace();
        }
        return posts;
    }
}
