package com.sk89q.intake.bukkit;

import com.sk89q.intake.bukkit.parametric.annotation.Sender;
import com.sk89q.intake.bukkit.parametric.provider.CommandSenderProvider;
import com.sk89q.intake.bukkit.parametric.provider.DynamicPlayerProvider;
import com.sk89q.intake.bukkit.parametric.provider.ProvidedPlayerProvider;
import com.sk89q.intake.bukkit.parametric.provider.WorldProvider;
import com.sk89q.intake.parametric.AbstractModule;
import com.sk89q.intake.parametric.Provider;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Default binding list of {@link Provider}s.
 */
public class BukkitModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(CommandSender.class).toProvider(new CommandSenderProvider());
        bind(Player.class).annotatedWith(Sender.class).toProvider(new ProvidedPlayerProvider());
        bind(Player.class).toProvider(new DynamicPlayerProvider());
        bind(World.class).toProvider(new WorldProvider());
    }

}
