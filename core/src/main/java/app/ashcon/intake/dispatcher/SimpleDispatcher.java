/*
 * Intake, a command processing library
 * Copyright (C) sk89q <http://www.sk89q.com>
 * Copyright (C) Intake team and contributors
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by the
 * Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package app.ashcon.intake.dispatcher;

import app.ashcon.intake.CommandCallable;
import app.ashcon.intake.CommandException;
import app.ashcon.intake.CommandMapping;
import app.ashcon.intake.Description;
import app.ashcon.intake.ImmutableCommandMapping;
import app.ashcon.intake.ImmutableDescription;
import app.ashcon.intake.ImmutableParameter;
import app.ashcon.intake.InvalidUsageException;
import app.ashcon.intake.InvocationCommandException;
import app.ashcon.intake.OptionType;
import app.ashcon.intake.argument.CommandContext;
import app.ashcon.intake.argument.Namespace;
import app.ashcon.intake.parametric.ProvisionException;
import app.ashcon.intake.util.auth.AuthorizationException;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/** A simple implementation of {@link Dispatcher}. */
public class SimpleDispatcher implements Dispatcher, Lockable {

  private final Map<String, CommandMapping> commands = new HashMap<>();
  private volatile boolean locked = false;
  private Description description;

  /**
   * Lock the {@link Dispatcher} and prevents more commands from being registered, as well as purge
   * the {@link Description} cache.
   */
  @Override
  public void lock() {
    locked = true; // Prevent more commands from being registered
    description = null; // Purge the description cache
  }

  @Override
  public void registerCommand(CommandCallable callable, String... alias) {
    if (locked) {
      throw new IllegalArgumentException(
          "Can't register another command because SimpleDispatcher is locked");
    }

    CommandMapping mapping = new ImmutableCommandMapping(callable, alias);

    // Check for replacements
    for (String a : alias) {
      String lower = a.toLowerCase();
      if (commands.containsKey(lower)) {
        throw new IllegalArgumentException(
            "Can't add the command '"
                + a
                + "' because SimpleDispatcher does not support replacing commands");
      }
    }

    for (String a : alias) {
      String lower = a.toLowerCase();
      commands.put(lower, mapping);
    }
  }

  @Override
  public Set<CommandMapping> getCommands() {
    return Collections.unmodifiableSet(new HashSet<CommandMapping>(commands.values()));
  }

  @Override
  public Set<String> getAliases() {
    return Collections.unmodifiableSet(commands.keySet());
  }

  @Override
  public Set<String> getPrimaryAliases() {
    Set<String> aliases = new HashSet<String>();
    for (CommandMapping mapping : getCommands()) {
      aliases.add(mapping.getPrimaryAlias());
    }
    return Collections.unmodifiableSet(aliases);
  }

  @Override
  public boolean contains(String alias) {
    return commands.containsKey(alias.toLowerCase());
  }

  @Override
  public CommandMapping get(String alias) {
    return commands.get(alias.toLowerCase());
  }

  @Override
  public boolean call(String arguments, Namespace namespace, List<String> parentCommands)
      throws CommandException, InvocationCommandException, AuthorizationException {
    // We have permission for this command if we have permissions for subcommands
    if (!testPermission(namespace)) {
      throw new AuthorizationException();
    }

    String[] split = CommandContext.split(arguments);
    Set<String> aliases = getPrimaryAliases();

    if (aliases.isEmpty()) {
      throw new ProvisionException("There are no sub-commands for " + parentCommands);
    } else if (split.length > 0) {
      String subCommand = split[0];
      String subArguments = Joiner.on(" ").join(Arrays.copyOfRange(split, 1, split.length));
      List<String> subParents =
          ImmutableList.<String>builder().addAll(parentCommands).add(subCommand).build();
      CommandMapping mapping = get(subCommand);

      if (mapping != null) {
        try {
          mapping.getCallable().call(subArguments, namespace, subParents);
        } catch (AuthorizationException e) {
          throw e;
        } catch (CommandException e) {
          throw e;
        } catch (InvocationCommandException e) {
          throw e;
        } catch (Throwable t) {
          throw new InvocationCommandException(t);
        }

        return true;
      }
    }

    throw new InvalidUsageException(null, this, parentCommands, true);
  }

  @Override
  public List<String> getSuggestions(String arguments, Namespace locals) throws CommandException {
    String[] split = CommandContext.split(arguments);

    if (split.length <= 1) {
      String prefix = split.length > 0 ? split[0] : "";

      List<String> suggestions = new ArrayList<String>();

      for (CommandMapping mapping : getCommands()) {
        if (mapping.getCallable().testPermission(locals)) {
          for (String alias : mapping.getAllAliases()) {
            if (prefix.isEmpty() || alias.startsWith(arguments)) {
              suggestions.add(mapping.getPrimaryAlias());
              break;
            }
          }
        }
      }

      return suggestions;
    } else {
      String subCommand = split[0];
      CommandMapping mapping = get(subCommand);
      String passedArguments = Joiner.on(" ").join(Arrays.copyOfRange(split, 1, split.length));

      if (mapping != null) {
        try {
          return mapping.getCallable().getSuggestions(passedArguments, locals);
        } catch (ArrayIndexOutOfBoundsException ex) {
          // Fall through
        }
      }
      return Collections.emptyList();
    }
  }

  public Description createDescription() {
    List<String> commands = Lists.newArrayList(this.commands.keySet());
    commands.sort(String.CASE_INSENSITIVE_ORDER);

    if (commands.isEmpty()) {
      commands.add("command");
    }

    return new ImmutableDescription.Builder()
        .setParameters(
            Lists.newArrayList(
                new ImmutableParameter.Builder()
                    .setName(Joiner.on("|").join(commands))
                    .setOptionType(OptionType.positional())
                    .build()))
        .build();
  }

  @Override
  public Description getDescription() {
    if (description == null) {
      description = createDescription();
    }
    return description;
  }

  @Override
  public boolean testPermission(Namespace locals) {
    for (CommandMapping mapping : getCommands()) {
      if (mapping.getCallable().testPermission(locals)) {
        return true;
      }
    }

    return false;
  }
}
