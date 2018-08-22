package com.sk89q.intake.bukkit.parametric.provider;

import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.bukkit.parametric.annotation.Default;
import com.sk89q.intake.bukkit.parametric.Type;
import com.sk89q.intake.bukkit.util.BukkitUtil;
import com.sk89q.intake.parametric.ProvisionException;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

public class DynamicPlayerProvider implements BukkitProvider<Player> {

    @Nullable
    @Override
    public Player get(CommandSender sender, CommandArgs args, List<? extends Annotation> mods) throws ArgumentException, ProvisionException {
        String query = null;
        if (args.hasNext()) {
            query = args.next();
            final Player player = BukkitUtil.getPlayer(query, sender);
            if (player != null) {
                return player;
            }
        }
        final Type type = getType(mods);
        if (type == Type.NULL) {
            return null;
        } else if (type == Type.SELF && sender instanceof Player) {
            return (Player) sender;
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
            if(mod instanceof Default) {
                return ((Default) mod).value();
            }
        }
        return Type.THROW;
    }

}
