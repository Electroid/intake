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
package app.ashcon.intake.parametric.provider;

import static com.google.common.base.Preconditions.checkNotNull;

import app.ashcon.intake.argument.ArgumentException;
import app.ashcon.intake.argument.ArgumentParseException;
import app.ashcon.intake.argument.CommandArgs;
import app.ashcon.intake.argument.Namespace;
import app.ashcon.intake.parametric.Provider;
import app.ashcon.intake.parametric.ProvisionException;
import com.google.common.collect.Lists;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.regex.Pattern;
import org.jetbrains.annotations.Nullable;

/**
 * Searches an enum for a near-matching value.
 *
 * <p>When comparing for a match, both the search and test entry have non-alphanumeric characters
 * stripped.
 *
 * @param <T> The type of the enum
 */
public class EnumProvider<T extends Enum<T>> implements Provider<T> {

  private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^A-Za-z0-9]");
  private final Class<T> enumClass;

  /**
   * Create a new instance.
   *
   * @param enumClass The enum for the class
   */
  public EnumProvider(Class<T> enumClass) {
    checkNotNull(enumClass, "enumClass");
    this.enumClass = enumClass;
  }

  private static String simplify(String t) {
    return NON_ALPHANUMERIC.matcher(t.toLowerCase()).replaceAll("");
  }

  @Override
  public String getName() {
    return enumClass.getSimpleName().toLowerCase();
  }

  @Nullable
  @Override
  public T get(CommandArgs arguments, List<? extends Annotation> modifiers)
      throws ArgumentException, ProvisionException {
    String name = arguments.next();
    String test = simplify(name);

    for (T entry : enumClass.getEnumConstants()) {
      if (simplify(entry.name()).equalsIgnoreCase(test)) {
        return entry;
      }
    }

    throw new ArgumentParseException(
        "No matching value found in the '" + enumClass.getSimpleName() + "' list.");
  }

  @Override
  public List<String> getSuggestions(
      String prefix, Namespace namespace, List<? extends Annotation> modifiers) {
    List<String> suggestions = Lists.newArrayList();
    String test = simplify(prefix);

    for (T entry : enumClass.getEnumConstants()) {
      String name = simplify(entry.name());
      if (name.toLowerCase().startsWith(test.toLowerCase())) {
        suggestions.add(entry.name().toLowerCase());
      }
    }

    return suggestions;
  }
}
