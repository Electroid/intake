package app.ashcon.intake.bukkit.parametric;

import app.ashcon.intake.bukkit.parametric.annotation.Fallback;
import org.bukkit.entity.Player;

/**
 * Various fetching behaviours of {@link Player}s.
 *
 * @see Fallback
 */
public enum Type {
    SELF,  // Player to sender
    NULL,  // Player to a null value
    THROW; // Player to a thrown exception (default)
}
