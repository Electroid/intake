package app.ashcon.intake.bukkit;

import app.ashcon.intake.CommandException;
import app.ashcon.intake.Intake;
import app.ashcon.intake.InvalidUsageException;
import app.ashcon.intake.InvocationCommandException;
import app.ashcon.intake.argument.Namespace;
import app.ashcon.intake.bukkit.command.BukkitCommand;
import app.ashcon.intake.bukkit.command.BukkitHelpTopic;
import app.ashcon.intake.dispatcher.Dispatcher;
import app.ashcon.intake.dispatcher.SimpleDispatcher;
import app.ashcon.intake.fluent.CommandGraph;
import app.ashcon.intake.parametric.Injector;
import app.ashcon.intake.parametric.ParametricBuilder;
import app.ashcon.intake.parametric.provider.PrimitivesModule;
import app.ashcon.intake.util.auth.AuthorizationException;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
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

import java.lang.reflect.Field;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Omnibus API that hooks into a {@link JavaPlugin} and
 * allows implementors to register commands.
 */
public class BukkitIntake implements CommandExecutor, TabCompleter {

    static {
        Bukkit.getServer().getHelpMap().registerHelpTopicFactory(BukkitCommand.class, new BukkitHelpTopic.Factory());
    }

    private final Plugin plugin;
    private final Injector injector;
    private final ParametricBuilder builder;
    private final Dispatcher dispatcher;

    public BukkitIntake(Plugin plugin, Consumer<CommandGraph> init) {
        this.plugin = plugin;
        Injector injector = Intake.createInjector();
        injector.install(new PrimitivesModule());
        injector.install(new BukkitModule());
        this.injector = injector;
        ParametricBuilder builder = new ParametricBuilder(injector);
        builder.setAuthorizer(new BukkitAuthorizer());
        this.builder = builder;
        CommandGraph graph = new CommandGraph().builder(builder);
        init.accept(graph);
        this.dispatcher = graph.getDispatcher();
        if (dispatcher instanceof SimpleDispatcher) {
            ((SimpleDispatcher) dispatcher).lock();
        }
        List<Command> commands = dispatcher.getCommands()
                .stream()
                .map(cmd -> new BukkitCommand(plugin, this, this, cmd))
                .collect(Collectors.toList());
        getCommandMap().registerAll(plugin.getName(), commands);
    }

    public BukkitIntake(Plugin plugin, Object... commands) {
        this(plugin, graph -> Stream.of(commands).forEachOrdered(command -> graph.groupedCommands().registerGrouped(command)));
    }

    public Injector getInjector() {
        return injector;
    }

    public ParametricBuilder getBuilder() {
        return builder;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public CommandMap getCommandMap() {
        try {
            PluginManager manager = plugin.getServer().getPluginManager();
            Field field = manager.getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            return (CommandMap) field.get(manager);
        } catch (NoSuchFieldException | IllegalAccessException error) {
            throw new IllegalStateException("Intake could not find CommandMap from server", error);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            return getDispatcher().call(getCommand(command, args), getNamespace(sender));
        } catch (AuthorizationException e) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command!");
        } catch (InvocationCommandException e) {
            sender.sendMessage(ChatColor.RED + "An exception occurred while executing this command!");
            e.getCause().printStackTrace();
        } catch (InvalidUsageException e) {
            if (e.getMessage() != null) {
                sender.sendMessage(ChatColor.RED + e.getMessage());
            }
            if (e.isFullHelpSuggested()) {
                sender.sendMessage(ChatColor.RED + "/" + Joiner.on(' ').join(e.getAliasStack()) + " " + e.getCommand().getDescription().getUsage());
            }
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + e.getMessage());
        }
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        try {
            return getDispatcher().getSuggestions(getCommand(command, args), getNamespace(sender));
        } catch (CommandException e) {
            return ImmutableList.of();
        }
    }

    protected String getCommand(Command command, String[] args) {
        return Joiner.on(' ').join(Lists.asList(command.getName(), args));
    }

    protected Namespace getNamespace(CommandSender sender) {
        return new Namespace(CommandSender.class, sender);
    }

}