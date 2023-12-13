package com.buddha;

import com.google.gson.Gson;
import okhttp3.OkHttpClient;
import okhttp3.Request.Builder;

import java.util.*;

public class HentaiPoster {

    public static final List<String> hentaiTags = List.of(
            "4k", "bellevid", "cum", "gif", "laugh", "pat", "spank",
            "anal", "bj", "feet", "hentai", "lesbian", "poke", "tickle",
            "animalears", "blowjob", "feet", "holo", "lewd", "pussy", "waifu",
            "baka", "boobs", "foxgirl", "hug", "lick", "slap", "wallpapers",
            "belle", "cuddle", "gasm", "kiss", "neko", "smug"
    );

    public static final String notFoundMessage =
            """
                    # Тег не найден!
                    ## Доступные теги:
                    
                    SFW Tags
                    - Kiss     Kissing images
                    - Lick     Lick
                    - Hug      Give someone a hug
                    - Baka     B-Baka!!!
                    - cry      :,(
                    - Poke     Senpai notice meeeee!
                    - Smug     What to put here?
                    - Slap     Ope
                    - Tickle   ;)
                    - Pat      Pat someone
                    - Laugh    HaHaHaHa
                    - Feed     I want foooooodddddd
                    - Cuddle   well in this context.... An intesnse hug
                                
                    # NSFW:
                    - Ass          *Reall Asses*
                    - Blowjob/BJ   *d-do I need to explain >///< (anime)*
                    - Boobs        *Real breasts*
                    - Cum          *Baby gravy!*
                    - Feet         *:eyes:*
                    - hentai       *random hentai*
                    - wallpapers   *99% sfw*
                    - spank        *NSFW for butts*
                    - gasm	       *aheago*
                    - lesbian      *girls rule!*
                    - lewd	       ***WARNING** this folder is unsorted I would not use it until we've filtered out loli/shota content*
                    - pussy	       *u-ummm >///<*
                    """;

    public static String getImageUrl(String tag) {
        if (!hentaiTags.contains(tag.toLowerCase()))
            return notFoundMessage;

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

    public static class ImageResponse {
        public String image;
    }
}
