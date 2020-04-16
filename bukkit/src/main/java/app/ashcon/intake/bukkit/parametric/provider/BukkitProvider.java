package app.ashcon.intake.bukkit.parametric.provider;

import app.ashcon.intake.argument.ArgumentException;
import app.ashcon.intake.argument.CommandArgs;
import app.ashcon.intake.argument.Namespace;
import app.ashcon.intake.parametric.Provider;
import app.ashcon.intake.parametric.ProvisionException;
import com.google.common.collect.ImmutableList;
import java.lang.annotation.Annotation;
import java.util.List;
import javax.annotation.Nullable;
import org.bukkit.command.CommandSender;

/** A {@link Provider} where the {@link CommandSender} is always present. */
public interface BukkitProvider<T> extends Provider<T> {

  @Nullable
  @Override
  default T get(CommandArgs arguments, List<? extends Annotation> modifiers)
      throws ArgumentException, ProvisionException {
    return get(arguments.getNamespace().need(CommandSender.class), arguments, modifiers);
  }

  @Nullable
  T get(CommandSender sender, CommandArgs args, List<? extends Annotation> mods)
      throws ArgumentException, ProvisionException;

  @Override
  default List<String> getSuggestions(
      String prefix, Namespace namespace, List<? extends Annotation> modifiers) {
    return getSuggestions(prefix, namespace.need(CommandSender.class), namespace, modifiers);
  }

  default List<String> getSuggestions(
      String prefix, CommandSender sender, Namespace namespace, List<? extends Annotation> mods) {
    return ImmutableList.of();
  }
}
