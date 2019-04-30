package app.ashcon.intake.bukkit.command;

import app.ashcon.intake.CommandMapping;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

/**
 * A wrapped {@link Command} that hooks up to a {@link CommandMapping},
 * and generates all the proper help and usage information.
 */
public class BukkitCommand extends Command implements PluginIdentifiableCommand {

    protected final Plugin plugin;
    protected final CommandExecutor executor;
    protected final TabCompleter completer;
    protected final List<String> permissions;

    public BukkitCommand(Plugin plugin, CommandMapping command) {
        this(plugin, plugin, plugin, command);
    }

    public BukkitCommand(Plugin plugin, CommandExecutor executor, TabCompleter completer, CommandMapping command) {
        super(
            command.getPrimaryAlias(),
            command.getDescription().getShortDescription(),
            "/" + command.getPrimaryAlias() + " " + command.getDescription().getUsage(),
            Lists.newArrayList(command.getAllAliases())
             );
        this.plugin = plugin;
        this.executor = executor;
        this.completer = completer;
        this.permissions = command.getDescription().getPermissions();
        super.setPermission(Joiner.on(';').join(permissions));
    }

    @Override
    public Plugin getPlugin() {
        return plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        return executor.onCommand(sender, this, label, args);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        if (completer != null) {
            List<String> suggestions = completer.onTabComplete(sender, this, alias, args);
            if (suggestions != null) {
                return suggestions;
            }
        }
        return super.tabComplete(sender, alias, args);
    }

    public List<String> getPermissions() {
        return permissions;
    }

}
