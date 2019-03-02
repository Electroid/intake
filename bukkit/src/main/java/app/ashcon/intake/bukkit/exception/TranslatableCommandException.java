package app.ashcon.intake.bukkit.exception;

import app.ashcon.intake.CommandException;
import network.stratus.sportpaper.api.text.TranslatableComponent;

/**
 * A command exception that can be translated later on to be sent to players.
 *
 * @author kashike
 */
public abstract class TranslatableCommandException extends CommandException {

    public abstract TranslatableComponent getComponent();
}
