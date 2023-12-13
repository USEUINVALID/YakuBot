package com.buddha;

import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.middleman.*;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class Main extends ListenerAdapter {
    public static final Path tokenPath = Path.of("token.txt");

    public static void main(String[] args) throws InterruptedException, IOException {
        // Существует ли файл с токеном?
        var token = Files.exists(tokenPath) ?
                // Если да, считываем его
                Files.readString(tokenPath) :
                // Если нет, загружаем токен из джарника
                // Я ебал сканнеры, но тут без них никак
                new Scanner(Objects.requireNonNull(Main.class.getResourceAsStream("/token.txt"))).nextLine();

        // Создаем объект бота
        var jda = JDABuilder.createDefault(token)
                // Добавляем отслеживание ивентов
                .addEventListeners(new Main())
                // Врубаем доступ к тексту сообщений и данным юзеров
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                // Делаем бота легендой
                .setStatus(OnlineStatus.IDLE)
                .setActivity(Activity.listening("Pyrokinesis Playlist"))
                .build();

        // Эта хуйня нужна чтобы бот не вырубался
        jda.awaitReady();
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        var channel = event.getJDA().getChannelById(GuildMessageChannel.class, 797818788145659914L);
        channel.sendMessage("User " + event.getUser().getAsMention() + " has left the server.").queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) {
            return;
        }

        if (event.getMessage().getContentRaw().equalsIgnoreCase("Капибара")) {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("https://tenor.com/view/capybara-orange-spa-self-self-care-gif-15986252").queue();
        }
        if (event.getMessage().getContentRaw().equalsIgnoreCase("Цинцюэ имба")) {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("https://media.discordapp.net/attachments/1076507277793706005/1183740549593841704/IMG_20231206_201306.jpg?ex=65896f4b&is=6576fa4b&hm=49fe02853fadb8b6a09eeb72bfe1fdb98f8f13aab8340499bdd3ce00e1ba5dce&=&format=webp&width=551&height=671").queue();
        }

        if (event.getMessage().getContentRaw().startsWith("Финик")) {
            var elb = event.getMessage().getContentRaw().substring(6, event.getMessage().getContentRaw().length() - 1);
            event.getMember().modifyNickname(elb).queue();
        }
        if (event.getMessage().getContentRaw().equalsIgnoreCase("пивасик")) {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("https://media.discordapp.net/attachments/1025140219525206087/1183889904334282812/MrNDF2Ah5W8.jpg?format=webp&width=671&height=671").queue();
        }
        if (event.getMessage().getContentRaw().equalsIgnoreCase("Цинцюэ имба")) {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("https://media.discordapp.net/attachments/1076507277793706005/1183740549593841704/IMG_20231206_201306.jpg?ex=65896f4b&is=6576fa4b&hm=49fe02853fadb8b6a09eeb72bfe1fdb98f8f13aab8340499bdd3ce00e1ba5dce&=&format=webp&width=551&height=671").queue();
        }

        if (event.getMessage().getContentRaw().startsWith("Пикча ")) {
            var tag = event.getMessage().getContentRaw().substring("Пикча ".length());
            var url = HentPoster.getImageUrl(tag);

            event.getMessage().reply(url).queue();
        }
    }
}