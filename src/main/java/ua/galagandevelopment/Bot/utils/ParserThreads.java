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
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
@Component
public class ParserThreads {
    @PersistenceContext
    private EntityManager entityManager;
    private static final String LOGIN = "edem.graph@gmail.com";
    private static final String PASSWORD = "misha20012";

    public List<Post> init() {
        List<Post> posts = new ArrayList<>();
        //System.setProperty("webdriver.chrome.driver", "/home/ubuntu/bot/chromedriver");
        System.setProperty("webdriver.chrome.driver", "./chromedriver.exe");
        List<Phrase> allPhrase = entityManager.createQuery("SELECT x FROM Phrase x", Phrase.class)
                .getResultList();
        for (Phrase phrase : allPhrase) {
            try {
                ChromeOptions options = new ChromeOptions();
                options.addArguments("--window-size=1280,1024");
                options.addArguments("--disable-notifications");
                WebDriver driver = new ChromeDriver(options);
                WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(5));
                String searchUrl = "https://www.threads.net/search?q=" + phrase.getText() + "&serp_type=default&filter=recent";
                driver.get(searchUrl);
                Thread.sleep(4000);
                WebElement elements;
                try {// chisto fix for razni localizations) oo, my english is very zbs :)
                    elements = driver.findElement(By.cssSelector("div[aria-label='Основний текст стовпця']"));
                }catch (Exception e){
                    try {
                        elements = driver.findElement(By.cssSelector("div[aria-label='Содержимое столбца']"));
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                       // elements = null; dodelay suda english versions
                    }
                }
                List<WebElement> timestamps = driver.findElements(By.xpath("//div[@data-pressable-container='true']"));
                System.out.println("elements: " + timestamps.size());
                for (int i = 0; i < timestamps.size(); i++) {
                    WebElement timestamp = timestamps.get(i);
                    try {
                        System.out.println("Try find post link: " + i + " " + timestamp.getText());
                        WebElement parentLink = timestamp.findElement(By.xpath(".//a[.//time]"));
                        String postUrl = parentLink.getAttribute("href");
                        System.out.println("Link find successfully, open new windows 12: " + postUrl );
                        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
                        jsExecutor.executeScript("window.open('" + postUrl + "', '_blank');");
                        String originalWindow = driver.getWindowHandle();
                        for (String windowHandle : driver.getWindowHandles()) {
                            if (!windowHandle.equals(originalWindow)) {
                                driver.switchTo().window(windowHandle);
                                break;
                            }
                        }
                        Thread.sleep(2000);
                        System.out.println("window open successfully, wait until load h1");
                        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")));
                        WebElement h1Element = driver.findElement(By.tagName("h1"));
                        String content = h1Element.getText();
                        driver.close();
                        driver.switchTo().window(originalWindow);
                        if (!content.equals(phrase.getText() + "?")) {
                            posts.add(new Post(null, "Знайдено новий пост", content, postUrl, false));
                            System.out.println("New post for threads added successfully");
                        }else
                            System.out.println("find not relevant post");
                    } catch (NumberFormatException e) {
                        System.out.println("Time error:");
                    } catch (NoSuchElementException e) {
                        System.out.println("can`t find elements: " + e.getMessage());
                    } catch (TimeoutException e) {
                        System.out.println("to loong page load: " + e.getMessage());
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
                driver.quit();
            } catch (TimeoutException e) {
                System.err.println("error time out.");
            } catch (WebDriverException e) {
                System.err.println("error WebDriver: " + e.getMessage());
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        return posts;
    }
}
