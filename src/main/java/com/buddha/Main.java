package com.buddha;

import com.buddha.Database.UserData.Drop;
import com.buddha.Database.UserData.Drop.Rarity;
import net.dv8tion.jda.api.*;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.*;
import static net.dv8tion.jda.api.utils.TimeFormat.*;

public class Main extends ListenerAdapter {
    public static final Path propertiesPath = Path.of("bot.properties");
    public static final Random random = new Random();

    public static long leaveChannelID;
    public static List<Long> adminsIDs; // = List.of(647466682675363847L, 794627738677411861L);

    public static void main(String[] args) throws InterruptedException, IOException {
        Database.load();

        var properties = new Properties();
        properties.load(Files.exists(propertiesPath) ? // Существует ли файл с настройками?
                Files.newInputStream(propertiesPath) : // Если да, считываем его
                Objects.requireNonNull(Main.class.getResourceAsStream("/bot.properties")) // Если нет, загружаем настройки из джарника
        );

        // Достаем из настроек ID канала для сообщений о вышедших юзерах и айдишники админов
        leaveChannelID = Long.parseLong(properties.getProperty("leaveChannelID"));
        adminsIDs = Arrays.stream(properties.getProperty("adminIDs").split(", ")).map(Long::parseLong).toList();

        // Создаем объект бота и пихаем туда токен
        var jda = JDABuilder.createDefault(properties.getProperty("token"))
                // Добавляем отслеживание ивентов
                .addEventListeners(new Main())
                // Врубаем доступ к тексту сообщений и данным юзеров
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                // Делаем бота легендой
                .setStatus(OnlineStatus.IDLE)
                .setActivity(Activity.listening("Pyrokinesis Playlist"))
                .build();

        // Эта фигня нужна чтобы бот не вырубался
        jda.awaitReady();
    }



    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        var channel = event.getJDA().getChannelById(GuildMessageChannel.class, leaveChannelID);
        if (channel == null) return;

        // Оповещаем о вышедших юзерах
        channel.sendMessage(event.getUser().getAsMention() + " вышел с сервера...").queue();
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        // Не отслеживаем сообщения в ЛС
        if (!event.isFromGuild())
            return;

        // Игнорируем сообщения от ботов
        if (event.getAuthor().isBot())
            return;

        // Текст сообщения в нижнем регистре
        var content = event.getMessage().getContentRaw().toLowerCase();
        if (content.isBlank()) return;

        if (content.startsWith("дать крутки ")) {
            if (!adminsIDs.contains(event.getAuthor().getIdLong())) {
                event.getMessage().reply("Ты должен быть в списке избранных, чтобы использовать эту команду!").queue();
                return;
            }

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

        if (content.equals("собрать крутки")) {
            var data = Database.getUserData(event.getAuthor().getIdLong());
            if (System.currentTimeMillis() - data.lastCollectTime >= HOURS.toMillis(1L)) {
                data.lastCollectTime = System.currentTimeMillis();
                data.rolls += 5;

                event.getMessage().reply("Ты успешно собрал 5 бесплатных круток!").queue();
                Database.saveUserData(data);
            } else {
                event.getMessage().reply("Ты еще не можешь собрать бесплатные крутки! Следующий сбор доступен " + RELATIVE.format(data.lastCollectTime + HOURS.toMillis(1L))).queue();
            }
        }

        // Легендарная капибара
        if (content.contains("капибара"))
            event.getChannel().sendMessage("https://tenor.com/view/capybara-orange-spa-self-self-care-gif-15986252").queue();

        // Легендарный пивасик
        if (content.contains("пивасик"))
            event.getChannel().sendMessage("https://media.discordapp.net/attachments/1025140219525206087/1183889904334282812/MrNDF2Ah5W8.jpg?format=webp&width=671&height=671").queue();

        if (content.startsWith("никнейм ")) {
            var member = event.getMember();
            if (member == null)
                return; // Все сломалось, такого не может быть

            member.modifyNickname(event.getMessage().getContentRaw().substring("финик ".length())).queue();
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

                    if (data.rollsWithoutLegendary == 99) {
                        data.rollsWithoutLegendary = 0;
                        rarity = Rarity.legendary;
                    } else if (data.rollsWithoutEpic == 9) {
                        data.rollsWithoutEpic = 0;
                        rarity = Rarity.epic;
                    } else switch (rarity) {
                        case rare -> {
                            data.rollsWithoutEpic++;
                            data.rollsWithoutLegendary++;
                        }
                        case epic -> {
                            data.rollsWithoutEpic = 0;
                            data.rollsWithoutLegendary++;
                        }
                        case legendary -> {
                            data.rollsWithoutEpic++;
                            data.rollsWithoutLegendary = 0;
                        }
                    }

                    var drop = Drop.getRandomDrop(rarity);

                    data.rolls--;
                    data.drops.put(drop, data.drops.getOrDefault(drop, 0L) + 1);

                    builder.append("\n").append(rarity.emoji).append(" **").append(drop.name).append("** (").append(rarity.name).append(") - *").append(drop.description).append("*");
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
                builder.append("\n<тут ничего нет>");

            event.getMessage().reply(builder.toString()).queue();
        }
    }
}