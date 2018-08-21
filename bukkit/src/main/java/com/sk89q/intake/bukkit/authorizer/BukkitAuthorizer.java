package com.sk89q.intake.bukkit.authorizer;

import com.sk89q.intake.argument.Namespace;
import com.sk89q.intake.util.auth.Authorizer;
import org.bukkit.command.CommandSender;

public interface BukkitAuthorizer extends Authorizer {

    @Override
    default boolean testPermission(Namespace namespace, String permission) {
        return namespace.need(CommandSender.class).hasPermission(permission);
    }

}
