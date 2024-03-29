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
package app.ashcon.intake.parametric;

import app.ashcon.intake.argument.ArgumentException;
import app.ashcon.intake.argument.CommandArgs;
import app.ashcon.intake.argument.Namespace;
import com.google.common.collect.ImmutableList;
import java.lang.annotation.Annotation;
import java.util.List;
import org.jetbrains.annotations.Nullable;

/**
 * An object that provides instances given a key and some arguments.
 *
 * <p>Providers do the heavy work of reading passed in arguments and transforming them into Java
 * objects.
 */
public interface Provider<T> {

  /**
   * Gets the human-readable name of the provider type.
   *
   * @return The name of the provider type.
   */
  default String getName() {
    if (isProvided()) {
      return "context";
    } else {
      throw new UnsupportedOperationException("Must implement name for provider");
    }
  }

  /**
   * Gets whether this provider does not actually consume values from the argument stack and instead
   * generates them otherwise.
   *
   * @return Whether values are provided without use of the arguments
   */
  default boolean isProvided() {
    return false;
  }

  /**
   * Provide a value given the arguments.
   *
   * @param arguments The arguments
   * @param modifiers The modifiers on the parameter
   * @return The value provided
   * @throws ArgumentException If there is a problem with the argument
   * @throws ProvisionException If there is a problem with the provider
   */
  @Nullable
  T get(CommandArgs arguments, List<? extends Annotation> modifiers)
      throws ArgumentException, ProvisionException;

  /**
   * Get a list of suggestions for the given parameter and user arguments.
   *
   * <p>If no suggestions could be enumerated, an empty list should be returned.
   *
   * @param prefix What the user has typed so far (may be an empty string)
   * @param namespace The namespace under which this command's suggestions are being provided
   * @param modifiers The modifiers on the parameter
   * @return A list of suggestions
   */
  default List<String> getSuggestions(
      String prefix, Namespace namespace, List<? extends Annotation> modifiers) {
    return ImmutableList.of();
  }
}
