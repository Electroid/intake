package app.ashcon.intake.bukkit.parametric.provider;

import app.ashcon.intake.argument.ArgumentException;
import app.ashcon.intake.argument.CommandArgs;
import app.ashcon.intake.argument.Namespace;
import app.ashcon.intake.bukkit.parametric.Type;
import app.ashcon.intake.bukkit.parametric.annotation.Fallback;
import app.ashcon.intake.bukkit.util.BukkitUtil;
import app.ashcon.intake.parametric.ProvisionException;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Provides {@link Player}s from a name query.
 */
public class DynamicPlayerProvider implements BukkitProvider<Player> {

    @Override
    public String getName() {
        return "player";
    }

    @Nullable
    @Override
    public Player get(CommandSender sender, CommandArgs args, List<? extends Annotation> mods)
        throws ArgumentException, ProvisionException {
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
        }
        else if (type == Type.SELF && sender instanceof Player) {
            return (Player) sender;
        }
        else if (query != null) {
            throw new ArgumentException("Could not find player named '" + query + "'");
        }
        else {
            throw new ArgumentException("You must provide a player name");
        }
    }

    @Override
    public List<String> getSuggestions(String prefix, CommandSender sender, Namespace namespace,
                                       List<? extends Annotation> mods) {
        return Bukkit.getOnlinePlayers()
                   .stream()
                   .map(player -> BukkitUtil.getPlayerName(player, sender))
                   .filter(name -> name.startsWith(prefix))
                   .sorted()
                   .collect(Collectors.toList());
    }

    private Type getType(List<? extends Annotation> mods) {
        for (Annotation mod : mods) {
            if (mod instanceof Fallback) {
                return ((Fallback) mod).value();
            }
        }
        return Type.THROW;
    }

}
