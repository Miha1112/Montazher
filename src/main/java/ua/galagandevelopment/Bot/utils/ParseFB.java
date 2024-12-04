package ua.galagandevelopment.Bot.utils;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.stereotype.Component;
import ua.galagandevelopment.Bot.models.FBAccount;
import ua.galagandevelopment.Bot.models.Phrase;
import ua.galagandevelopment.Bot.models.Post;
import ua.galagandevelopment.Bot.models.XAccount;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
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
        WebDriver driver = null;
        System.setProperty("webdriver.chrome.driver", "/home/ubuntu/bot/chromedriver");
        //System.setProperty("webdriver.chrome.driver", "./chromedriver.exe");

        List<Phrase> allPhrase = entityManager.createQuery("SELECT x FROM Phrase x", Phrase.class)
                .getResultList();
        for (Phrase phrase : allPhrase) {
            String searchUrl = "https://www.facebook.com/search/posts?q="+ phrase +"test.&filters=eyJycF9hdXRob3I6MCI6IntcIm5hbWVcIjpcIm1lcmdlZF9wdWJsaWNfcG9zdHNcIixcImFyZ3NcIjpcIlwifSIsInJlY2VudF9wb3N0czowIjoie1wibmFtZVwiOlwicmVjZW50X3Bvc3RzXCIsXCJhcmdzXCI6XCJcIn0ifQ%3D%3D&locale=uk_UA";
            driver = new ChromeDriver();
            List<FBAccount> fbAccounts = entityManager.createQuery("SELECT x FROM FBAccount x", FBAccount.class)
                    .getResultList();
            FBAccount fbAccount = fbAccounts.get(0);

            String url = "https://www.facebook.com/";
            driver.get(url);
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

            try {
                WebElement email = driver.findElement(By.cssSelector("input[name='email']"));
                ;
                email.sendKeys(fbAccount.getEmail());

                Thread.sleep(1000);
                WebElement pass = driver.findElement(By.cssSelector("input[name='pass']"));
                ;

                pass.sendKeys(fbAccount.getPassword());
                Thread.sleep(1000);
                pass.sendKeys(Keys.RETURN);

                Thread.sleep(3000);
                driver.get(searchUrl);

                Thread.sleep(3000);

                driver.navigate().refresh();
                Thread.sleep(300);

                driver.navigate().refresh();
                Thread.sleep(300);

                driver.navigate().refresh();
                Thread.sleep(300);

                for (int i = 1; i <= 10; i++) {
                    try {
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

                        posts.add(new Post(null, "Знайдено новий пост", textPost, profileLink, false));

                    } catch (NoSuchElementException e) {
                        System.out.println("Не вдалося знайти блок або посилання в елементі з aria-posinset='" + i + "'");
                    }
                    if (i % 3 == 0) {
                        try {
                            JavascriptExecutor js = (JavascriptExecutor) driver;
                            js.executeScript("window.scrollBy(0, document.body.scrollHeight * 0.1);");
                            System.out.println("Сторінку прокручено на 5%");
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }

            }catch (Exception e){
                e.printStackTrace();
            }
        }

        driver.quit();
        return posts;
    }
    private String cleanText(String rawText) {
        // Видаляємо небажані фрагменти
        String cleanedText = rawText
                .replaceAll("\\d{1,2}:\\d{2} / \\d{1,2}:\\d{2}", "") // Видаляє часові мітки, як "0:00 / 6:15"
                .replaceAll("(Подобається|Коментувати|Поширити|Стежити)", "") // Видаляє слова "Подобається", "Коментувати" і т.д.
                .replaceAll("\\s{2,}", " ") // Замінює кілька пробілів одним
                .trim(); // Видаляє пробіли на початку і в кінці тексту
        return cleanedText;
    }

    public static String getClipboardContents() throws IOException {
        String result = "";
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable contents = clipboard.getContents(null);

        if (contents != null) {
            try {
                result = (String) contents.getTransferData(DataFlavor.stringFlavor);
            } catch (UnsupportedFlavorException e) {
                e.printStackTrace();
            }
        }
        return result;
    }



}
