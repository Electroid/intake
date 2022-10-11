package app.ashcon.intake.bukkit.parametric.provider;

import app.ashcon.intake.argument.CommandArgs;
import app.ashcon.intake.parametric.ProvisionException;
import java.lang.annotation.Annotation;
import java.util.List;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.Nullable;

/** Provides the {@link CommandSender} of the command. */
public class CommandSenderProvider implements BukkitProvider<CommandSender> {

  @Override
  public boolean isProvided() {
    return true;
  }

  @Nullable
  @Override
  public CommandSender get(CommandSender sender, CommandArgs args, List<? extends Annotation> mods)
      throws ProvisionException {
    return sender;
  }
}
