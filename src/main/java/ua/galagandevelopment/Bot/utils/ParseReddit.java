package ua.galagandevelopment.Bot.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
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
        WebDriver driver;
        System.setProperty("webdriver.chrome.driver", "/home/ubuntu/bot/chromedriver");
        //System.setProperty("webdriver.chrome.driver", "./chromedriver.exe");
        try {
            for (Phrase phrase : allPhrase) {
                driver = new ChromeDriver();
                String searchUrl = "https://www.reddit.com/search/?q=" + phrase + "&sort=new";

                driver.get(searchUrl);
                Thread.sleep(3000);

                WebElement postsBlock = driver.findElement(By.cssSelector("reddit-feed[label='search-results-page-tab-posts']"));

                List<WebElement> postElements = postsBlock.findElements(By.cssSelector("faceplate-tracker[data-testid='search-post']"));
                for (WebElement postElement : postElements) {
                    try {
                        WebElement linkElement = postElement.findElement(By.cssSelector("a[aria-label]"));
                        String postLink = linkElement.getAttribute("href");
                        String postTitle = linkElement.getAttribute("aria-label");

                        posts.add(new Post(null, "Знайдено новий пост", postTitle, postLink, false));
                    } catch (Exception e) {
                        System.out.println("Не вдалося знайти посилання для одного з постів");
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
