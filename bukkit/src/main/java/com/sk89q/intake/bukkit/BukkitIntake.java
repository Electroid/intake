package com.sk89q.intake.bukkit;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.sk89q.intake.CommandException;
import com.sk89q.intake.Intake;
import com.sk89q.intake.InvalidUsageException;
import com.sk89q.intake.InvocationCommandException;
import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.bukkit.authorizer.BukkitAuthorizer;
import com.sk89q.intake.bukkit.command.BukkitCommand;
import com.sk89q.intake.bukkit.command.BukkitHelpTopic;
import com.sk89q.intake.dispatcher.Dispatcher;
import com.sk89q.intake.dispatcher.SimpleDispatcher;
import com.sk89q.intake.parametric.Injector;
import com.sk89q.intake.parametric.ParametricBuilder;
import com.sk89q.intake.parametric.provider.PrimitivesModule;
import com.sk89q.intake.util.auth.AuthorizationException;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.stream.Collectors;

public class BukkitIntake implements CommandExecutor, TabCompleter {

    static {
        Bukkit.getServer().getHelpMap().registerHelpTopicFactory(BukkitCommand.class, new BukkitHelpTopic.Factory());
    }

    private final Plugin plugin;
    private final Injector injector;
    private final ParametricBuilder builder;
    private final Dispatcher dispatcher;

    public BukkitIntake(Plugin plugin, @Nullable BiConsumer<ParametricBuilder, Dispatcher> init) {
        this.plugin = plugin;
        Injector injector = Intake.createInjector();
        injector.install(new PrimitivesModule());
        injector.install(new BukkitModule());
        this.injector = injector;
        ParametricBuilder builder = new ParametricBuilder(injector);
        this.builder = builder;
        Dispatcher dispatcher = new SimpleDispatcher();
        builder.setAuthorizer(new BukkitAuthorizer(){});
        if(init != null) {
            init.accept(builder, dispatcher);
        }
        List<Command> commands = dispatcher.getCommands()
                                           .stream()
                                           .map(cmd -> new BukkitCommand(plugin, this, this, cmd))
                                           .collect(Collectors.toList());
        getCommandMap().registerAll(plugin.getName(), commands);
        this.dispatcher = dispatcher;
    }

    public BukkitIntake(Plugin plugin) {
        this(plugin, null);
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
        } catch (NoSuchFieldException | IllegalAccessException error) {
            throw new IllegalStateException("Intake could not find CommandMap from server", error);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        try {
            return getDispatcher().call(getCommand(command, args), getNamespace(sender));
        } catch (InvalidUsageException e) {
            sender.sendMessage(ChatColor.RED + "Invalid usage: " + e.getMessage());
        } catch (AuthorizationException e) {
            sender.sendMessage(ChatColor.RED + "You do not have permission to use this command");
        } catch (CommandException e) {
            sender.sendMessage(ChatColor.RED + "Command exception: " + e.getMessage());
            e.printStackTrace();
        } catch (InvocationCommandException e) {
            sender.sendMessage(ChatColor.RED + "Invocation exception: " + e.getMessage());
            e.printStackTrace();
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

    private String getCommand(Command command, String[] args) {
        return Joiner.on(' ').join(Lists.asList(command.getName(), args));
    }

    private Namespace getNamespace(CommandSender sender) {
        return new Namespace(CommandSender.class, sender);
    }

}
