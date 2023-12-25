package com.buddha;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request.Builder;

import java.util.*;
import java.util.stream.Collectors;

public class AnimePoster {

    public static final Map<String, String> animeTags = new HashMap<>() {{
        put("animalears", "Обожаю ушки!!");
        put("baka", "B-Baka!!!");
        put("cry", "Н-не плачь, пожалуйста!");
        put("cuddle", "Нууу в этом контексте... Очень сильные обнимашки!");
        put("feed", "Я хочу жраааааааааааать");
        put("foxgirl", "Лисодевочки рулят!");
        put("hug", "Обнимаааааааааааааааааааашкииииииии");
        put("kiss", "Я стесняюсь...");
        put("laugh", "Смешно, смеемся");
        put("lick", "Не надо меня облизывать!");
        put("neko", "Кowkoдевочки тоже неплохие!");
        put("pat", "А можно меня тоже погладить?");
        put("poke", "Семпай, заметьте меняяяяяяяяя");
        put("slap", "Получай!");
        put("smug", "Нууууу... ладно");
        put("tickle", ";)");
        put("waifu", "Лучшие аниме-девочки");
        put("wallpapers", "Обои на твой рабочий стол");
    }};

    public static final String notFoundMessage = """
            # Тег не найден!
            ## Доступные теги:
            """;

    public static String getImageUrl(String tag) {
        if (!animeTags.containsKey(tag.toLowerCase()))
            return notFoundMessage + animeTags.keySet()
                    .stream()
                    .sorted()
                    .map(AnimePoster::formatTag)
                    .collect(Collectors.joining("\n"));

        var gson = new Gson();
        var client = new OkHttpClient();

        try (var response = client.newCall(new Builder()
                        .url("http://api.nekos.fun:8080/api/" + tag.toLowerCase())
                        .build())
                .execute()) {
            return response.isSuccessful() ? gson.fromJson(Objects.requireNonNull(response.body()).string(), ImageResponse.class).image : "Что-то пошло не так...";
        } catch (Exception e) {
            return e.toString();
        }
    }

    private static String formatTag(String tag) {
        return "- " + tag + " - " + animeTags.get(tag);
    }

    private static class ImageResponse {
        public String image;
    }
}
