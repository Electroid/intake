package com.sk89q.intake.bukkit.parametric;

import org.bukkit.entity.Player;

/**
 * Various fetching behaviours of {@link Player}s.
 *
 * @see com.sk89q.intake.bukkit.parametric.annotation.Player
 */
public enum Type {
    SELF,  // Player to sender
    NULL,  // Player to a null value
    THROW; // Player to a thrown exception (default)
}
