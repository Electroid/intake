package com.sk89q.intake.example;

import com.sk89q.intake.bukkit.BukkitIntake;
import org.bukkit.plugin.java.JavaPlugin;

public class ExamplePlugin extends JavaPlugin {

    @Override
    public void onLoad() {
        new BukkitIntake(this, new MathCommands());
    }

}
