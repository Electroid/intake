package com.sk89q.intake.bukkit.parametric.annotation;

import com.sk89q.intake.bukkit.parametric.Type;
import com.sk89q.intake.parametric.annotation.Classifier;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Represents a {@link org.bukkit.command.CommandSender} of
 * a command, that must be a {@link org.bukkit.entity.Player}.
 */
@Classifier
@Retention(RetentionPolicy.RUNTIME)
public @interface Default {
    Type value();
}
