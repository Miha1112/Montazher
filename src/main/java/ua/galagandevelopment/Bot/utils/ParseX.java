package ua.galagandevelopment.Bot.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
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
        System.setProperty("webdriver.chrome.driver", "/home/ubuntu/bot/chromedriver");
        //System.setProperty("webdriver.chrome.driver", "./chromedriver.exe");
        WebDriver driver = new ChromeDriver();
        List<XAccount> xAccounts = entityManager.createQuery("SELECT x FROM XAccount x", XAccount.class)
                .getResultList();
        XAccount xAccount = xAccounts.get(0);
        String url = "https://x.com/i/flow/login";
        driver.get(url);
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            driver.navigate().refresh();
            Thread.sleep(1000);
            driver.navigate().refresh();
            Thread.sleep(1000);
            driver.navigate().refresh();
            Thread.sleep(1000);

            Thread.sleep(4000);

            WebElement email = driver.findElement(By.xpath("//*[@autocomplete='username']"));
            email.sendKeys(xAccount.getEmail());
            email.sendKeys(Keys.RETURN);

            Thread.sleep(2000);
            boolean zahistOtDuraka1 = false;
            try{
                WebElement elementDurak = driver.findElement(By.cssSelector("input[name='text']"));
                if (elementDurak.isDisplayed()) {
                    zahistOtDuraka1 = true;
                }else {
                    zahistOtDuraka1 = false;
                }
            }catch (Exception e){
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
            Thread.sleep(4000);
            password.sendKeys(Keys.RETURN);
        /*  List<Phrase> allPhrase = phraseService.findAll();
            String searchPhrase = allPhrase.get(0).getText();

         */
            Thread.sleep(2000);
            String searchPhrase = "Test";
            String searchUrl = "https://x.com/search?q=" + searchPhrase + "&src=typed_query&f=live";
            driver.get(searchUrl);

            Thread.sleep(2000);
            List<WebElement> timestamps = driver.findElements(By.cssSelector("[datetime]"));
            System.out.println("elements time: " + timestamps.size());
            if (timestamps.size() == 0) {
                return null;
            }
            for (WebElement timestamp : timestamps) {

            try {
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
                    wait.until(ExpectedConditions.presenceOfElementLocated(By.tagName("span")));

                    // Отримання тексту з h1
                    WebElement h1Element = driver.findElement(By.cssSelector("[data-testid='tweetText']"));
                    String content = h1Element.getText();

                    // Закриття нового вікна і повернення до оригінального
                    driver.close();
                    driver.switchTo().window(originalWindow);

                    // Додавання поста до списку
                    posts.add(new Post(null, "Знайдено нове оголошення", content, postUrl, false));
                } catch (NoSuchElementException e) {
                    System.out.println("Не вдалося знайти елемент: " + e.getMessage());
                } catch (TimeoutException e) {
                    System.out.println("Сторінка завантажувалась надто довго: " + e.getMessage());
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }catch (Exception e) {
            e.printStackTrace();
        }
        driver.quit();
        return posts;
    }
}