package app.ashcon.intake.bukkit.command;

import org.bukkit.command.CommandSender;
import org.bukkit.help.HelpTopic;
import org.bukkit.help.HelpTopicFactory;

/**
 * Generates help text when using a {@link BukkitCommand}.
 */
public class BukkitHelpTopic extends HelpTopic {

    private final BukkitCommand command;

    public BukkitHelpTopic(BukkitCommand command) {
        this.command = command;
        this.name = "/" + command.getName();
    }

    @Override
    public boolean canSee(CommandSender sender) {
        for (String permission : command.getPermissions()) {
            if (sender.hasPermission(permission)) {
                return true;
            }
        }
        return false;
    }

    public static class Factory implements HelpTopicFactory<BukkitCommand> {

        @Override
        public HelpTopic createTopic(BukkitCommand command) {
            return new BukkitHelpTopic(command);
        }

    }

}
