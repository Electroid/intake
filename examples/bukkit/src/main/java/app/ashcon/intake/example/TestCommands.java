package app.ashcon.intake.example;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import app.ashcon.intake.bukkit.util.BukkitUtil;
import app.ashcon.intake.parametric.annotation.Default;
import app.ashcon.intake.parametric.annotation.Range;
import app.ashcon.intake.parametric.annotation.Switch;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Comparator;
import java.util.function.Function;

public class TestCommands {

  @Command(aliases = "ping", desc = "Send a ping to the server")
  public void ping(CommandSender sender) {
    sender.sendMessage(ChatColor.YELLOW + "Pong!");
  }

  @Command(
      aliases = "near",
      desc = "Find the closest player near you",
      usage = "[radius]",
      flags = "s")
  public void near(
      @Sender Player sender,
      @Default("100") @Range(min = 0, max = 1000) int radius,
      @Switch('s') boolean squared) {
    final Location location = sender.getLocation();
    final Function<Player, Double> distance =
        player -> {
          if (squared) {
            return player.getLocation().distanceSquared(location);
          } else {
            return player.getLocation().distance(location);
          }
        };
    sender.sendMessage(
        sender.getWorld().getPlayers().stream()
            .filter(player -> player != sender && distance.apply(player) <= radius)
            .min(Comparator.comparingDouble(distance::apply))
            .map(
                player ->
                    ChatColor.GREEN
                        + BukkitUtil.getPlayerName(player, sender)
                        + " is the closest player to you!")
            .orElse(
                ChatColor.RED + "Could not find any players in a " + radius + " block radius!"));
  }
}
