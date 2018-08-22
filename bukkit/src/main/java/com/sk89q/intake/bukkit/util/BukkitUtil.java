package com.sk89q.intake.bukkit.util;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

public class BukkitUtil {

    private static volatile boolean sportbukkit = true;

    public static Player getPlayer(String name, CommandSender viewer) {
        if (sportbukkit) {
            try {
                return (Player) Bukkit.class.getDeclaredMethod("getPlayer", String.class, CommandSender.class).invoke(name, viewer);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException error) {
                sportbukkit = false;
            }
        }
        return Bukkit.getPlayer(name);
    }

    public static String getPlayerName(Player player, CommandSender viewer) {
        if (sportbukkit) {
            try {
                return (String) Player.class.getDeclaredMethod("getName", CommandSender.class).invoke(player, viewer);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException error) {
                sportbukkit = false;
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
