package app.ashcon.intake.bukkit;

import app.ashcon.intake.argument.Namespace;
import app.ashcon.intake.util.auth.Authorizer;
import org.bukkit.command.CommandSender;

/**
 * An {@link Authorizer} that checks if the {@link CommandSender} has permission to execute the
 * command.
 */
public class BukkitAuthorizer implements Authorizer {

  @Override
  public boolean testPermission(Namespace namespace, String permission) {
    return namespace.need(CommandSender.class).hasPermission(permission);
  }
}
