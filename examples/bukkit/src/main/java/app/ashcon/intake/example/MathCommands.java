package app.ashcon.intake.example;

import app.ashcon.intake.Command;
import app.ashcon.intake.parametric.annotation.Switch;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class MathCommands {

  @Command(aliases = "sum", desc = "Get the sum of two numbers")
  public void sum(CommandSender sender, int a, int b) {
    int sum = a + b;
    sender.sendMessage(ChatColor.YELLOW + "" + a + "+" + b + "=" + sum);
  }

  @Command(aliases = "div", desc = "Divide two numbers", flags = "r")
  public void div(CommandSender sender, double a, double b, @Switch('r') boolean round) {
    double res = a / b;
    if (round) {
      res = (int) res;
    }
    sender.sendMessage(ChatColor.YELLOW + "" + a + "/" + b + "=" + res);
  }
}
