package com.sk89q.intake.bukkit.provider;

import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.parametric.ProvisionException;
import org.bukkit.Physical;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

public class WorldProvider implements BukkitProvider<World> {

    @Override
    public boolean isProvided() {
        return true;
    }

    @Nullable
    @Override
    public World get(CommandSender sender, CommandArgs args, List<? extends Annotation> mods) throws ArgumentException, ProvisionException {
        if(sender instanceof Physical) {
            return ((Physical) sender).getWorld();
        }
        throw new ArgumentException("You must be in a world to execute this command");
    }

}
