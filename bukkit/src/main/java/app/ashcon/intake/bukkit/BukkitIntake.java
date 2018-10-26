package app.ashcon.intake.bukkit;

import app.ashcon.intake.CommandException;
import app.ashcon.intake.Intake;
import app.ashcon.intake.InvalidUsageException;
import app.ashcon.intake.InvocationCommandException;
import app.ashcon.intake.argument.Namespace;
import app.ashcon.intake.bukkit.command.BukkitCommand;
import app.ashcon.intake.bukkit.command.BukkitHelpTopic;
import app.ashcon.intake.dispatcher.Dispatcher;
import app.ashcon.intake.dispatcher.Lockable;
import app.ashcon.intake.fluent.CommandGraph;
import app.ashcon.intake.parametric.Injector;
import app.ashcon.intake.parametric.Module;
import app.ashcon.intake.parametric.ParametricBuilder;
import app.ashcon.intake.parametric.provider.PrimitivesModule;
import app.ashcon.intake.util.auth.AuthorizationException;
import com.google.common.base.Joiner;
import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
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
    private final Injector injector;
    private final ParametricBuilder builder;
    private final Dispatcher dispatcher;

    private BukkitIntake(Plugin plugin, CommandGraph commandGraph, BukkitAuthorizer bukkitAuthorizer,
                        Injector injector, ParametricBuilder builder, Module... modules) {
        this.plugin = plugin;
        injector.install(new BukkitModule());
        injector.install(new PrimitivesModule());
        Arrays.stream(modules).forEach(injector::install);
        this.injector = injector;
        this.builder = builder;
        builder.setAuthorizer(bukkitAuthorizer);
        this.dispatcher = commandGraph.getRootDispatcherNode().getDispatcher();
        if (dispatcher instanceof Lockable) {
            ((Lockable) dispatcher).lock();
        }
        List<Command> commands = dispatcher.getCommands()
                                     .stream()
                                     .map(cmd -> new BukkitCommand(plugin, this, this, cmd))
                                     .collect(Collectors.toList());
        getCommandMap().registerAll(plugin.getName(), commands);
    }

    public static class Builder {

        private final Plugin plugin;
        private final CommandGraph commandGraph;

        private BukkitAuthorizer bukkitAuthorizer;
        private Injector injector;
        private Function<Injector, ParametricBuilder> builderCreator;
        private List<Module> modules;

        private static final BukkitAuthorizer defaultBukkitAuthorizer = new BukkitAuthorizer();
        private static final Injector defaultInjector = Intake.createInjector();
        private static final Function<Injector, ParametricBuilder> defaultBuilderCreator = ParametricBuilder::new;
        private static final List<Module> defaultModules = new LinkedList<>();

        public Builder(Plugin plugin, CommandGraph commandGraph) {
            Preconditions.checkNotNull(plugin);
            Preconditions.checkNotNull(commandGraph);

            this.plugin = plugin;
            this.commandGraph = commandGraph;
        }

        public Builder injector(Injector injector) {
            this.injector = injector;
            return this;
        }

        public Builder builderCreator(Function<Injector, ParametricBuilder> builderCreator) {
            this.builderCreator = builderCreator;
            return this;
        }

        public Builder modules(Module... modules) {
            this.modules = Arrays.asList(modules);
            return this;
        }

        public Builder addModule(Module module) {
            if(modules == null)
                modules = new LinkedList<>();
            modules.add(module);
            return this;
        }

        public BukkitIntake build() {
            Injector injector = (this.injector == null) ? defaultInjector : this.injector;
            return new BukkitIntake(
                plugin, commandGraph,
                (bukkitAuthorizer == null) ? defaultBukkitAuthorizer : bukkitAuthorizer, injector,
                (builderCreator == null) ? defaultBuilderCreator.apply(injector) : builderCreator.apply(injector),
                (modules == null) ? defaultModules.toArray(new Module[0]) : modules.toArray(new Module[0])
            );
        }
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
            return CommandMap.class.cast(field.get(manager));
        }
        catch (NoSuchFieldException | IllegalAccessException error) {
            throw new IllegalStateException("Intake could not find CommandMap from server", error);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            return getDispatcher().call(getCommand(command, args), getNamespace(sender));
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
            return getDispatcher().getSuggestions(getCommand(command, args), getNamespace(sender));
        }
        catch (CommandException e) {
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
