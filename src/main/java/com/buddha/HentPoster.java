package com.buddha;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request.Builder;

import java.util.Objects;

public class HentPoster {

    public static String getImageUrl(String tag) {
        var gson = new Gson();
        var client = new OkHttpClient();

        try (var response = client.newCall(new Builder()
                        .url("http://api.nekos.fun:8080/api/" + tag)
                        .build())
                .execute()) {
            return gson.fromJson(Objects.requireNonNull(response.body()).string(), ImageResponse.class).image;
        } catch (Exception e) {
            return e.toString();
        }
    }

    public static class ImageResponse {
        public String image;
    }
}
