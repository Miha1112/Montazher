/*
*/
package ua.galagandevelopment.Bot.utils;

import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Component;
import ua.galagandevelopment.Bot.models.Post;

import java.util.ArrayList;
import java.util.List;

@Component
public class ParseXbyAPI {

    private static final String BEARER_TOKEN = "AAAAAAAAAAAAAAAAAAAAABPXxAEAAAAAQ9qcUkA5fXHSY8faUdVI%2FxKo6gk%3DA9cWZXoyt1oZcBKmAPltdQWXRpSKBTUk1vGF0iQnEKUTgZV4BB"; // Вставте ваш Bearer Token

    public List<Post> init() {
        List<Post> posts = new ArrayList<>();

        // Пошуковий запит
        String query = "Test";
        String searchUrl = "https://api.twitter.com/2/tweets/search/recent?query=" + query + "&max_results=3";  // Можна змінити max_results

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(searchUrl)
                .addHeader("Authorization", "Bearer " + BEARER_TOKEN)
                .build();

        try {
            // Виконання запиту
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                String responseBody = response.body().string();
                JSONObject jsonResponse = new JSONObject(responseBody);

                // Перевірка наявності постів
                if (jsonResponse.has("data")) {
                    JSONArray tweets = jsonResponse.getJSONArray("data");

                    // Обробка отриманих твітів
                    for (int i = 0; i < tweets.length(); i++) {
                        JSONObject tweet = tweets.getJSONObject(i);
                        String tweetText = tweet.getString("text");
                        String postUrl = "https://x.com/status/" + tweet.getString("id"); // Побудова URL поста

                        // Додавання поста до списку
                        posts.add(new Post(null, "Знайдено нове оголошення", tweetText, postUrl, false));
                    }
                } else {
                    System.out.println("No tweets found.");
                }
            } else {
                System.out.println("Request failed: " + response.code());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return posts;
    }
}
