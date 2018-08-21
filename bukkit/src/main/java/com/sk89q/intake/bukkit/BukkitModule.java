package com.sk89q.intake.bukkit;

import com.sk89q.intake.bukkit.annotation.Sender;
import com.sk89q.intake.bukkit.provider.CommandSenderProvider;
import com.sk89q.intake.bukkit.provider.DynamicPlayerProvider;
import com.sk89q.intake.bukkit.provider.ProvidedPlayerProvider;
import com.sk89q.intake.bukkit.provider.WorldProvider;
import com.sk89q.intake.parametric.AbstractModule;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class BukkitModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(CommandSender.class).toProvider(new CommandSenderProvider());
        bind(Player.class).annotatedWith(Sender.class).toProvider(new ProvidedPlayerProvider());
        bind(Player.class).toProvider(new DynamicPlayerProvider());
        bind(World.class).toProvider(new WorldProvider());
    }

}
