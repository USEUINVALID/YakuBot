package com.buddha;

import com.buddha.Database.UserData.Drop;
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
        if (event.getAuthor().isBot()) {
            return;
        }

        // Текст сообщения в нижнем регистре
        var content = event.getMessage().getContentRaw().toLowerCase();
        if (content.isBlank()) return;

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
            var tag = event.getMessage().getContentRaw().substring("пикча ".length());
            var url = HentaiPoster.getImageUrl(tag);

            event.getMessage().reply(url).queue();
        }

        if (content.equals("крутка")) {
            var data = Database.getUserData(event.getAuthor().getIdLong());
            if (data.rolls > 0) {
                var chance = random.nextFloat();

                var dropType = chance <= Drop.legendaryDropChance ? "Легендарный" : chance <= Drop.epicDropChance ? "Эпический" : "Редкий";
                var drops = Drop.dropTypeNames.get(dropType);

                var drop = drops.get(random.nextInt(drops.size()));

                data.rolls--;
                data.drops.add(drop);

                event.getMessage().reply("Ты покрутил!\nВыпало: **" + drop.name + "** (" + dropType + ")\n*" + drop.description + "*\n\nОсталось круток: **" + data.rolls + "**").queue();
                if(dropType.equalsIgnoreCase("Редкий")) {
                    MessageChannel channel = event.getChannel();
                    channel.sendMessage("https://media1.tenor.com/m/KGwWGVz9-XQAAAAC/genshin-impact-wish.gif").queue();
                }
                if(dropType.equalsIgnoreCase("Эпический")){
                    MessageChannel channel = event.getChannel();
                    channel.sendMessage("https://media1.tenor.com/m/JcMSVVkgfgMAAAAC/genshin-wish.gif").queue();
                }
                if(dropType.equalsIgnoreCase("Легендарный")){
                    MessageChannel channel = event.getChannel();
                    channel.sendMessage("https://media1.tenor.com/m/YQCvYWzR28wAAAAC/wishing.gif").queue();
                }


            } else {
                event.getMessage().reply("Ты проебал все крутки!\nМолодец!").queue();
            }

            Database.saveUserData(data);
        }

        if (content.equals("инвентарь")) {
            var data = Database.getUserData(event.getAuthor().getIdLong());
            var builder = new StringBuilder("### Инвентарь " + event.getAuthor().getAsMention() + ":");

            builder.append("\n**Крутки:**").append("\n- ").append(data.rolls);
            builder.append("\n**Дропы:**");

            data.drops.forEach(drop -> builder.append("\n- ").append(drop.name));

            if (data.drops.isEmpty())
                builder.append("\n<нихуя тут нет>");

            event.getMessage().reply(builder.toString()).queue();
        }
    }
}