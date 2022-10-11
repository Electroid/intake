package app.ashcon.intake.bukkit.parametric.provider;

import app.ashcon.intake.argument.ArgumentException;
import app.ashcon.intake.argument.CommandArgs;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import app.ashcon.intake.parametric.ProvisionException;
import java.lang.annotation.Annotation;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

/** Provides the {@link Player} who sent the command, annotated with {@link Sender}. */
public class ProvidedPlayerProvider implements BukkitProvider<Player> {

  @Override
  public boolean isProvided() {
    return true;
  }

  @Nullable
  @Override
  public Player get(CommandSender sender, CommandArgs args, List<? extends Annotation> mods)
      throws ArgumentException, ProvisionException {
    if (sender instanceof Player) {
      return (Player) sender;
    }
    throw new ArgumentException("You must be a player to use this command");
  }
}
