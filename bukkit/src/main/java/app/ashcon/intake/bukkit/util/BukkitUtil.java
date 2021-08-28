package app.ashcon.intake.bukkit.util;

import java.lang.reflect.InvocationTargetException;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

/** Utility methods for accessing various {@link Bukkit} APIs. */
public class BukkitUtil {

  /**
   * Some forks of {@link Bukkit}, namely SportBukkit, support a fake name patch which allows
   * different players to see different names.
   *
   * <p>Try to use the path with reflection and if it fails, assume the patch is not loaded.
   */
  private static volatile boolean canSearchByViewer = true;

  public static Player getPlayer(String name, CommandSender viewer) {
    if (canSearchByViewer) {
      try {
        return (Player)
            Bukkit.class
                .getMethod("getPlayer", String.class, CommandSender.class)
                .invoke(null, name, viewer);
      } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException error) {
        canSearchByViewer = false;
      }
    }
    return Bukkit.getPlayer(name);
  }

  public static String getPlayerName(Player player, CommandSender viewer) {
    if (canSearchByViewer) {
      try {
        return (String)
            Player.class.getMethod("getName", CommandSender.class).invoke(player, viewer);
      } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException error) {
        canSearchByViewer = false;
      }
    }
    return player.getName();
  }

  public static World getWorld(CommandSender sender) {
    if (sender instanceof Player) {
      return ((Player) sender).getWorld();
    } else if (sender instanceof Block) {
      return ((Block) sender).getWorld();
    } else if (sender instanceof Entity) {
      return ((Entity) sender).getWorld();
    } else {
      return null;
    }
  }
}
