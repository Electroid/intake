package app.ashcon.intake.bukkit.parametric.annotation;

import app.ashcon.intake.bukkit.parametric.Type;
import app.ashcon.intake.parametric.annotation.Classifier;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Define the fetching behaviour of {@link Player}s
 * on the local server.
 *
 * If not defined, the default is {@link Type#THROW}.
 */
@Classifier
@Retention(RetentionPolicy.RUNTIME)
public @interface Player {
    Type value();
}
