package com.buddha;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.channel.middleman.GuildMessageChannel;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.GenericEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.member.GuildMemberRemoveEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static java.awt.SystemColor.text;

public class Main extends ListenerAdapter {
    public static void main(String[] args) throws InterruptedException, IOException {
        // Note: It is important to register your ReadyListener before building
        JDA jda = JDABuilder.createDefault(Files.readString(Path.of("token.txt")))
                .addEventListeners(new Main())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT,GatewayIntent.GUILD_MEMBERS)
                .setStatus(OnlineStatus.IDLE)
                .setActivity(Activity.listening("Pyrokinesis Playlist"))
                .build();

        // optionally block until JDA is ready
        jda.awaitReady();
    }

    @Override
    public void onGuildMemberRemove(GuildMemberRemoveEvent event) {
        var channel = event.getJDA().getChannelById(GuildMessageChannel.class,797818788145659914L);
        channel.sendMessage("User " + event.getUser().getAsMention() + " has left the server.").queue();
    }
    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        if(event.getAuthor().isBot()){
            return;
        }
        if (event.getMessage().getContentRaw().equalsIgnoreCase("Капибара")) {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("https://tenor.com/view/capybara-orange-spa-self-self-care-gif-15986252").queue();
        }
        if(event.getMessage().getContentRaw().startsWith("Финик")){
            var elb = event.getMessage().getContentRaw().substring(6, event.getMessage().getContentRaw().length() - 1);
            event.getMember().modifyNickname(elb).queue();
        }
        if (event.getMessage().getContentRaw().equalsIgnoreCase("Цинцюэ имба")) {
            MessageChannel channel = event.getChannel();
            channel.sendMessage("https://media.discordapp.net/attachments/1076507277793706005/1183740549593841704/IMG_20231206_201306.jpg?ex=65896f4b&is=6576fa4b&hm=49fe02853fadb8b6a09eeb72bfe1fdb98f8f13aab8340499bdd3ce00e1ba5dce&=&format=webp&width=551&height=671").queue();
        }
    }


}