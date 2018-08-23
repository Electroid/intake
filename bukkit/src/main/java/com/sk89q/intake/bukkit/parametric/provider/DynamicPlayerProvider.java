package com.sk89q.intake.bukkit.parametric.provider;

import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.bukkit.parametric.annotation.Player;
import com.sk89q.intake.bukkit.parametric.Type;
import com.sk89q.intake.bukkit.util.BukkitUtil;
import com.sk89q.intake.parametric.ProvisionException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Provides {@link org.bukkit.entity.Player}s from a name query.
 */
public class DynamicPlayerProvider implements BukkitProvider<org.bukkit.entity.Player> {

    @Override
    public String getName() {
        return "player";
    }

    @Nullable
    @Override
    public org.bukkit.entity.Player get(CommandSender sender, CommandArgs args, List<? extends Annotation> mods) throws ArgumentException, ProvisionException {
        String query = null;
        if (args.hasNext()) {
            query = args.next();
            final org.bukkit.entity.Player player = BukkitUtil.getPlayer(query, sender);
            if (player != null) {
                return player;
            }
        }
        final Type type = getType(mods);
        if (type == Type.NULL) {
            return null;
        } else if (type == Type.SELF && sender instanceof org.bukkit.entity.Player) {
            return (org.bukkit.entity.Player) sender;
        } else if (query != null) {
            throw new ArgumentException("Could not find player named '" + query + "'");
        } else {
            throw new ArgumentException("You must provide a player name");
        }
    }

    @Override
    public List<String> getSuggestions(String prefix, CommandSender sender, Namespace namespace, List<? extends Annotation> mods) {
        return Bukkit.getOnlinePlayers()
                     .stream()
                     .map(player -> BukkitUtil.getPlayerName(player, sender))
                     .filter(name -> name.startsWith(prefix))
                     .sorted()
                     .collect(Collectors.toList());
    }

    private Type getType(List<? extends Annotation> mods) {
        for (Annotation mod : mods) {
            if(mod instanceof Player) {
                return ((Player) mod).value();
            }
        }
        return Type.THROW;
    }

}
