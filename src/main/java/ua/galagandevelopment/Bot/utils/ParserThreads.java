package ua.galagandevelopment.Bot.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import ua.galagandevelopment.Bot.models.Phrase;
import ua.galagandevelopment.Bot.models.Post;
import ua.galagandevelopment.Bot.models.XAccount;
import ua.galagandevelopment.Bot.repositories.PhraseRepository;
import ua.galagandevelopment.Bot.services.PhraseService;

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
        WebDriver driver = null;

        try {
            // Налаштування драйвера
            System.setProperty("webdriver.chrome.driver", "/home/ubuntu/bot/chromedriver");
           // System.setProperty("webdriver.chrome.driver", "./chromedriver.exe");
            driver = new ChromeDriver();

            // Налаштування WebDriverWait
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            // Перехід на сторінку пошуку
            List<Phrase> allPhrase = entityManager.createQuery("SELECT x FROM Phrase x", Phrase.class)
                    .getResultList();
            String searchPhrase = allPhrase.get(0).getText();
            String searchUrl = "https://www.threads.net/search?q=" + searchPhrase + "&serp_type=default&filter=recent";
            driver.get(searchUrl);

            // Очікування завантаження елементів
            Thread.sleep(3000);
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.cssSelector("div[aria-label='Основний текст стовпця']")));

            // Збір даних
            List<WebElement> elements = driver.findElements(By.cssSelector("div[aria-label='Основний текст стовпця']"));
            List<WebElement> timestamps = driver.findElements(By.xpath("//div[contains(text(),'хв') or contains(text(),'m')]"));
            System.out.println("elements: " + elements.size());
            System.out.println("elements: " + timestamps.size());
            for (WebElement timestamp : timestamps) {
                String timeText = timestamp.getText().replaceAll("[^\\d]", "").trim();

                try {
                    int minutesAgo = Integer.parseInt(timeText);
                    if (minutesAgo <= 20) {
                        // Знаходження URL
                        WebElement parentLink = timestamp.findElement(By.xpath("ancestor::a"));
                        String postUrl = parentLink.getAttribute("href");

                        // Відкриття нового вікна
                        JavascriptExecutor jsExecutor = (JavascriptExecutor) driver;
                        jsExecutor.executeScript("window.open('" + postUrl + "', '_blank');");

                        // Перехід до нового вікна
                        String originalWindow = driver.getWindowHandle();
                        for (String windowHandle : driver.getWindowHandles()) {
                            if (!windowHandle.equals(originalWindow)) {
                                driver.switchTo().window(windowHandle);
                                break;
                            }
                        }

                        // Очікування завантаження сторінки
                        Thread.sleep(3000);
                        wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("h1")));

                        // Отримання тексту з h1
                        WebElement h1Element = driver.findElement(By.tagName("h1"));
                        String content = h1Element.getText();

                        // Закриття нового вікна і повернення до оригінального
                        driver.close();
                        driver.switchTo().window(originalWindow);

                        // Додавання поста до списку
                        posts.add(new Post(null, "Знайдено нове оголошення", content, postUrl, false));
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Помилка обробки часу: " + timeText);
                } catch (NoSuchElementException e) {
                    System.out.println("Не вдалося знайти елемент: " + e.getMessage());
                } catch (TimeoutException e) {
                    System.out.println("Сторінка завантажувалась надто довго: " + e.getMessage());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }


        } catch (TimeoutException e) {
            System.err.println("Помилка: елементи не завантажились вчасно.");
        } catch (WebDriverException e) {
            System.err.println("Помилка роботи WebDriver: " + e.getMessage());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        driver.quit();
        return posts;
    }
}
