package app.ashcon.intake.bukkit;

import app.ashcon.intake.CommandException;
import app.ashcon.intake.InvalidUsageException;
import app.ashcon.intake.InvocationCommandException;
import app.ashcon.intake.argument.Namespace;
import app.ashcon.intake.bukkit.command.BukkitCommand;
import app.ashcon.intake.bukkit.command.BukkitHelpTopic;
import app.ashcon.intake.dispatcher.Dispatcher;
import app.ashcon.intake.dispatcher.Lockable;
import app.ashcon.intake.fluent.CommandGraph;
import app.ashcon.intake.util.auth.AuthorizationException;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Omnibus API that hooks into a {@link JavaPlugin} and
 * allows implementors to register commands.
 */
public class BukkitIntake implements CommandExecutor, TabCompleter {

    static {
        Bukkit.getServer().getHelpMap().registerHelpTopicFactory(BukkitCommand.class, new BukkitHelpTopic.Factory());
    }

    private final Plugin plugin;
    private final CommandGraph commandGraph;
    private final List<Command> commands = Lists.newArrayList();

    /**
     * Create a new {@link BukkitIntake} instance
     *
     * @param plugin          The plugin's main class
     * @param commandGraph    A {@link CommandGraph} instance
     */
    public BukkitIntake(Plugin plugin, CommandGraph commandGraph) {
        Preconditions.checkNotNull(plugin, "Plugin can not be null");
        Preconditions.checkNotNull(commandGraph, "Command graph can not be null");

        this.plugin = plugin;
        this.commandGraph = commandGraph;
    }

    /**
     * Register all of the commands in the command graph
     */
    public void register() {
        Dispatcher dispatcher = getCommandGraph().getRootDispatcherNode().getDispatcher();

        if (!commands.isEmpty())
            throw new IllegalStateException("Commands have already been registered!");

        if (dispatcher instanceof Lockable)
            ((Lockable) dispatcher).lock();

        commands.addAll(dispatcher.getCommands()
                .stream()
                .map(cmd -> new BukkitCommand(plugin, this, this, cmd))
                .collect(Collectors.toList()));

        getCommandMap().registerAll(plugin.getName(), commands);
    }

    /**
     * Unregister all of the commands in the command graph
     */
    public void unregister() {
        if (commands.isEmpty())
            return;

        getCommandMap().unregisterAll(commands::contains);

        // Allow for re-registration
        commands.clear();
    }

    public CommandMap getCommandMap() {
        try {
            PluginManager manager = plugin.getServer().getPluginManager();
            Field field = manager.getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            return CommandMap.class.cast(field.get(manager));
        }
        catch (NoSuchFieldException | IllegalAccessException error) {
            throw new IllegalStateException("Intake could not find CommandMap from server", error);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            return getCommandGraph().getRootDispatcherNode().getDispatcher()
                       .call(getCommand(command, args), getNamespace(sender));
        }
        catch (AuthorizationException e) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
        }
        catch (InvocationCommandException e) {
            sender.sendMessage(ChatColor.RED + "An exception occurred while executing this command!");
            e.getCause().printStackTrace();
        }
        catch (InvalidUsageException e) {
            if (e.getMessage() != null) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
            }
            if (e.isFullHelpSuggested()) {
                sender.sendMessage(
                    ChatColor.RED + "/" + Joiner.on(' ').join(e.getAliasStack()) + " " + e.getCommand().getDescription()
                                                                                             .getUsage());
            }
        }
        catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        try {
            return getCommandGraph().getRootDispatcherNode().getDispatcher()
                       .getSuggestions(getCommand(command, args), getNamespace(sender));
        }
        catch (CommandException e) {
            return ImmutableList.of();
        }
    }

    public CommandGraph getCommandGraph() {
        return commandGraph;
    }

    protected String getCommand(Command command, String[] args) {
        return Joiner.on(' ').join(Lists.asList(command.getName(), args));
    }

    protected Namespace getNamespace(CommandSender sender) {
        return new Namespace(CommandSender.class, sender);
    }

}
