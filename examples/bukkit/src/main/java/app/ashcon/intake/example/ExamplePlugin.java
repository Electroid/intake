package app.ashcon.intake.example;

import app.ashcon.intake.bukkit.BukkitIntake;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {

    @Override
    public void onLoad() {
        new BukkitIntake(this, new ExampleCommands());
    }

}
