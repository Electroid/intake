package app.ashcon.intake.bukkit;

import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import app.ashcon.intake.bukkit.parametric.provider.CommandSenderProvider;
import app.ashcon.intake.bukkit.parametric.provider.DynamicPlayerProvider;
import app.ashcon.intake.bukkit.parametric.provider.ProvidedPlayerProvider;
import app.ashcon.intake.bukkit.parametric.provider.WorldProvider;
import app.ashcon.intake.parametric.AbstractModule;
import app.ashcon.intake.parametric.Provider;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/** Default binding list of {@link Provider}s. */
public class BukkitModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(CommandSender.class).toProvider(new CommandSenderProvider());
    bind(Player.class).annotatedWith(Sender.class).toProvider(new ProvidedPlayerProvider());
    bind(Player.class).toProvider(new DynamicPlayerProvider());
    bind(World.class).toProvider(new WorldProvider());
  }
}
