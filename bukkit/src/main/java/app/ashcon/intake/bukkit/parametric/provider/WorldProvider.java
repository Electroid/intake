package app.ashcon.intake.bukkit.parametric.provider;

import app.ashcon.intake.argument.ArgumentException;
import app.ashcon.intake.argument.CommandArgs;
import app.ashcon.intake.bukkit.util.BukkitUtil;
import app.ashcon.intake.parametric.ProvisionException;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * Provides the {@link World} of the {@link CommandSender}.
 */
public class WorldProvider implements BukkitProvider<World> {

    @Override
    public boolean isProvided() {
        return true;
    }

    @Nullable
    @Override
    public World get(CommandSender sender, CommandArgs args, List<? extends Annotation> mods) throws ArgumentException, ProvisionException {
        World world = BukkitUtil.getWorld(sender);
        if (world == null) {
            throw new ArgumentException("You must be in a world to execute this command");
        }
        return world;
    }

}