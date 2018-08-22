package com.sk89q.intake.bukkit.parametric.provider;

import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.parametric.ProvisionException;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

public class CommandSenderProvider implements BukkitProvider<CommandSender> {

    @Override
    public boolean isProvided() {
        return true;
    }

    @Nullable
    @Override
    public CommandSender get(CommandSender sender, CommandArgs args, List<? extends Annotation> mods) throws ProvisionException {
        return sender;
    }

}
