package com.buddha;

import com.buddha.Database.UserData.Drop;
import com.buddha.Database.UserData.Drop.Rarity;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.*;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

public class Main extends ListenerAdapter {
    public static final Path tokenPath = Path.of("token.txt");
    public static final Random random = new Random();

    public static void main(String[] args) throws InterruptedException, IOException {
        Database.load();

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
        if (event.getAuthor().isBot())
            return;

        // Текст сообщения в нижнем регистре
        var content = event.getMessage().getContentRaw().toLowerCase();
        if (content.isBlank()) return;

        if (content.startsWith("дать крутки ")) {
            var users = event.getMessage().getMentions().getUsers();

            for (var user : users)
                content = content.replace("<@" + user.getIdLong() + ">", "");

            content = content.substring("дать крутки ".length());
            content = content.strip();

            if (users.isEmpty())
                users = List.of(event.getAuthor());

            try {
                int amount = Integer.parseInt(content);

                users.forEach(user -> {
                    var data = Database.getUserData(user.getIdLong());
                    data.rolls += amount;
                    Database.saveUserData(data);
                });

                event.getMessage().reply("Успешно! Крутки следующих участников были увеличены на " + amount + "!\n" + users.stream().map(IMentionable::getAsMention).collect(Collectors.joining(", "))).queue();
            } catch (NumberFormatException e) {
                event.getMessage().reply("Неверный формат числа!").queue();
            }
        }

        if (content.contains("капибара")) {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("https://tenor.com/view/capybara-orange-spa-self-self-care-gif-15986252").queue();
        }

        if (content.contains("цинцюэ имба")) {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("https://media.discordapp.net/attachments/1076507277793706005/1183740549593841704/IMG_20231206_201306.jpg?ex=65896f4b&is=6576fa4b&hm=49fe02853fadb8b6a09eeb72bfe1fdb98f8f13aab8340499bdd3ce00e1ba5dce&=&format=webp&width=551&height=671").queue();
        }

        if (content.startsWith("финик")) {
            var name = event.getMessage().getContentRaw().substring(6, event.getMessage().getContentRaw().length() - 1);
            event.getMember().modifyNickname(name).queue();
        }

        if (content.contains("пивасик")) {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("https://media.discordapp.net/attachments/1025140219525206087/1183889904334282812/MrNDF2Ah5W8.jpg?format=webp&width=671&height=671").queue();
        }

        if (content.startsWith("пикча ")) {
            var tag = content.substring("пикча ".length());
            var url = AnimePoster.getImageUrl(tag);

            event.getMessage().reply(url).queue();
        }

        if (content.equals("крутка") || content.equals("крутки")) {
            int amount = content.equals("крутка") ? 1 : 10;

            var data = Database.getUserData(event.getAuthor().getIdLong());
            if (data.rolls >= amount) {
                var builder = new StringBuilder();
                var bestRarity = Rarity.rare;

                for (int i = 0; i < amount; i++) {
                    var rarity = Rarity.getRarity(random.nextFloat());
                    var drop = Drop.getRandomDrop(rarity);

                    data.rolls--;
                    data.drops.put(drop, data.drops.getOrDefault(drop, 0L) + 1);

                    builder.append("\n- **").append(drop.name).append("** (").append(rarity.name).append(") - *").append(drop.description).append("*");
                    if (rarity.ordinal() > bestRarity.ordinal())
                        bestRarity = rarity;
                }

                event.getMessage().reply("Ты покрутил!\nВыпало: " + builder + "\n\nОсталось круток: **" + data.rolls + "**").queue();
                event.getMessage().getChannel().sendMessage(bestRarity.animation).queue();
            } else {
                event.getMessage().reply("У тебя не хватает круток. Всего их у тебя: " + data.rolls).queue();
            }

            Database.saveUserData(data);
        }

        if (content.equals("инвентарь")) {
            var data = Database.getUserData(event.getAuthor().getIdLong());
            var builder = new StringBuilder("### Инвентарь " + event.getAuthor().getAsMention() + ":");

            builder.append("\n**Крутки:**").append("\n- ").append(data.rolls);
            builder.append("\n**Дропы:**");

            data.drops.forEach((drop, amount) -> builder.append("\n- ").append(drop.name).append(" (").append(amount).append(")"));

            if (data.drops.isEmpty())
                builder.append("\n<нихуя тут нет>");

            event.getMessage().reply(builder.toString()).queue();
        }
    }
}