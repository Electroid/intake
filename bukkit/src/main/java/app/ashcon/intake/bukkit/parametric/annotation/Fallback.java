package app.ashcon.intake.bukkit.parametric.annotation;

import app.ashcon.intake.bukkit.parametric.Type;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import org.bukkit.entity.Player;

/**
 * Define the fetching behaviour of {@link Player}s
 * on the local server.
 * <p>
 * If not defined, the default is {@link Type#THROW}.
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Fallback {

    Type value();
}
