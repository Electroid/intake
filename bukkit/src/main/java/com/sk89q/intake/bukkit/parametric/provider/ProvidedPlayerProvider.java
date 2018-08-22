package com.sk89q.intake.bukkit.parametric.provider;

import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.bukkit.parametric.annotation.Sender;
import com.sk89q.intake.parametric.ProvisionException;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Provides the {@link Player} who sent the command, annotated with {@link Sender}.
 */
public class ProvidedPlayerProvider implements BukkitProvider<Player> {

    @Nullable
    @Override
    public Player get(CommandSender sender, CommandArgs args, List<? extends Annotation> mods) throws ArgumentException, ProvisionException {
        if (sender instanceof Player) {
            return (Player) sender;
        }
        throw new ArgumentException("You must be a player to use this command");
    }

}
