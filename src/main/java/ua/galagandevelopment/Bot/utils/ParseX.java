package ua.galagandevelopment.Bot.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import ua.galagandevelopment.Bot.models.Phrase;
import ua.galagandevelopment.Bot.models.Post;
import ua.galagandevelopment.Bot.models.XAccount;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
@Component
public class ParseX {
    @PersistenceContext
    private EntityManager entityManager;
    public List<Post> init() {
        List<Post> posts = new ArrayList<>();
        //System.setProperty("webdriver.chrome.driver", "/home/ubuntu/bot/chromedriver");
        System.setProperty("webdriver.chrome.driver", "./chromedriver.exe");
        List<Phrase> allPhrase = entityManager.createQuery("SELECT x FROM Phrase x", Phrase.class)
                .getResultList();
        for (Phrase phrase : allPhrase) {
            ChromeOptions options = new ChromeOptions();
            options.addArguments("--window-size=1280,1024");
            options.addArguments("--disable-notifications");
            WebDriver driver = new ChromeDriver(options);
            List<XAccount> xAccounts = entityManager.createQuery("SELECT x FROM XAccount x", XAccount.class)
                    .getResultList();
            XAccount xAccount = xAccounts.get(0);
            String url = "https://x.com/i/flow/login";
            driver.get(url);
            try {
                driver.navigate().refresh();
                Thread.sleep(2000);
                driver.navigate().refresh();
                Thread.sleep(2000);
                WebElement email = driver.findElement(By.xpath("//*[@autocomplete='username']"));
                email.sendKeys(xAccount.getEmail());
                email.sendKeys(Keys.RETURN);
                Thread.sleep(2000);
                boolean zahistOtDuraka1 = false;
                try {
                    WebElement elementDurak = driver.findElement(By.cssSelector("input[name='text']"));
                    if (elementDurak.isDisplayed()) {
                        zahistOtDuraka1 = true;
                    } else {
                        zahistOtDuraka1 = false;
                    }
                } catch (Exception e) {
                    System.out.println("No durak defence");
                }
                if (zahistOtDuraka1) {
                    WebElement elementDurak = driver.findElement(By.cssSelector("input[name='text']"));
                    elementDurak.sendKeys(xAccount.getName());
                    Thread.sleep(500);
                    elementDurak.sendKeys(Keys.RETURN);
                }
                Thread.sleep(2000);
                WebElement password = driver.findElement(By.xpath("//*[@name='password']"));
                password.sendKeys(xAccount.getPassword());
                Thread.sleep(2000);
                password.sendKeys(Keys.RETURN);
                Thread.sleep(2000);
                String searchUrl = "https://x.com/search?q=" + phrase.getText() + "&src=typed_query&f=live";
                driver.get(searchUrl);

                Thread.sleep(2000);
                List<WebElement> timestamps = driver.findElements(By.cssSelector("[datetime]"));
                System.out.println("elements time: " + timestamps.size());
                if (timestamps.isEmpty()) {
                    System.out.println("No timestamps found");
                    return null;
                }
                for (WebElement timestamp : timestamps) {
                    try {
                        WebElement parentLink = timestamp.findElement(By.xpath("ancestor::a"));
                        String postUrl = parentLink.getAttribute("href");
                        System.out.println("add new post to array");
                        posts.add(new Post(null, "Знайдено нове оголошення", "", postUrl, false));
                    } catch (NoSuchElementException e) {
                        System.out.println("no element: " + e.getMessage());
                    } catch (TimeoutException e) {
                        System.out.println("page load timeout: " + e.getMessage());
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            driver.quit();
        }
        return posts;
    }
}