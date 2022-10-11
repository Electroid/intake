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

import app.ashcon.intake.argument.ArgumentException;
import app.ashcon.intake.argument.CommandArgs;
import app.ashcon.intake.parametric.Provider;
import java.lang.annotation.Annotation;
import java.time.Duration;
import java.time.format.DateTimeParseException;
import java.util.List;
import org.jetbrains.annotations.Nullable;

class DurationProvider implements Provider<Duration> {

  static final DurationProvider INSTANCE = new DurationProvider();

  @Override
  public String getName() {
    return "duration";
  }

  @Nullable
  @Override
  public Duration get(CommandArgs arguments, List<? extends Annotation> modifiers)
      throws ArgumentException {
    String query = arguments.next().toLowerCase();
    if (query.equals("oo") || query.equals("infinity")) {
      return Duration.ofNanos(Long.MAX_VALUE);
    } else if (query.matches("[0-9]*")) {
      return Duration.ofSeconds(Integer.parseInt(query));
    }
    try {
      String[] parts = query.split("d");
      if (parts.length == 1) {
        return Duration.parse((query.contains("d") ? "P" : "PT") + query);
      } else {
        return Duration.parse("P" + parts[0] + "dT" + parts[1]);
      }
    } catch (DateTimeParseException e) {
      throw new ArgumentException("Could not parse duration '" + query + "'", e);
    }
  }
}
