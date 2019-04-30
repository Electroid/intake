package app.ashcon.intake.bukkit.parametric.annotation;

import app.ashcon.intake.parametric.annotation.Classifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Attaches to a {@link Player} parameter to signal
 * that the {@link CommandSender} should be a {@link Player}.
 */
@Classifier
@Retention(RetentionPolicy.RUNTIME)
public @interface Sender {

}
