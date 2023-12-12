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

public class Main extends ListenerAdapter {
    public static void main(String[] args) throws InterruptedException, IOException {
        // Note: It is important to register your ReadyListener before building
        JDA jda = JDABuilder.createDefault(Files.readString(Path.of("token.txt")))
                .addEventListeners(new Main())
                .enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS)
                .setStatus(OnlineStatus.IDLE)
                .setActivity(Activity.listening("Pyrokinesis Playlist"))
                .build();

        // optionally block until JDA is ready
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

        if (event.getMessage().getContentRaw().startsWith("Финик")) {
            var elb = event.getMessage().getContentRaw().substring(6, event.getMessage().getContentRaw().length() - 2);
            event.getMember().modifyNickname(elb).queue();
        }

        if (event.getMessage().getContentRaw().startsWith("Пикча ")) {
            var tag = event.getMessage().getContentRaw().substring("Пикча ".length());
            var url = HentPoster.getImageUrl(tag);

            event.getMessage().reply(url).queue();
        }
    }
}