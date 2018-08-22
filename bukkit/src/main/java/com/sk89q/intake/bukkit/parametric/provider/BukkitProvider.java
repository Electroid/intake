package com.sk89q.intake.bukkit.parametric.provider;

import com.google.common.collect.ImmutableList;
import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.parametric.Provider;
import com.sk89q.intake.parametric.ProvisionException;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

public interface BukkitProvider<T> extends Provider<T> {

    @Nullable
    @Override
    default T get(CommandArgs arguments, List<? extends Annotation> modifiers) throws ArgumentException, ProvisionException {
        return get(arguments.getNamespace().need(CommandSender.class), arguments, modifiers);
    }

    @Nullable
    T get(CommandSender sender, CommandArgs args, List<? extends Annotation> mods) throws ArgumentException, ProvisionException;

    @Override
    default List<String> getSuggestions(String prefix, Namespace namespace, List<? extends Annotation> modifiers) {
        return getSuggestions(prefix, namespace.need(CommandSender.class), namespace, modifiers);
    }

    default List<String> getSuggestions(String prefix, CommandSender sender, Namespace namespace, List<? extends Annotation> mods) {
        return ImmutableList.of();
    }

}
