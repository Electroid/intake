package com.sk89q.intake.example;

import com.sk89q.intake.Command;
import com.sk89q.intake.parametric.annotation.Default;
import com.sk89q.intake.parametric.annotation.Switch;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MathCommands {

    @Command(
        aliases = {"add", "a"},
        desc = "Add two numbers together",
        usage = "<number> [number]",
        min = 1,
        max = 2
    )
    public void add(CommandSender sender, int a, @Default("0") int b) {
        int sum = a + b;
        sender.sendMessage(ChatColor.YELLOW + "" + a + "+" + b + "=" + sum);
    }

    @Command(
        aliases = {"sub", "s"},
        desc = "Subtract two numbers",
        usage = "<number> [number]",
        flags = "p",
        min = 1,
        max = 2
    )
    public void sub(CommandSender sender, int a, @Default("0") int b, @Switch('p') boolean positive) {
        int diff = a - b;
        if (positive && diff < 0) {
            diff = 0;
        }
        sender.sendMessage(ChatColor.YELLOW + "" + a + "-" + b + "=" + diff);
    }

}
