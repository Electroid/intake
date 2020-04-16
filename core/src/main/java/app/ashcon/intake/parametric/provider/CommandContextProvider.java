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
import app.ashcon.intake.argument.CommandContext;
import app.ashcon.intake.parametric.Provider;
import app.ashcon.intake.parametric.ProvisionException;
import java.lang.annotation.Annotation;
import java.util.List;
import javax.annotation.Nullable;

/** Provides the context. */
public final class CommandContextProvider implements Provider<CommandContext> {

  @Override
  public boolean isProvided() {
    return true;
  }

  @Nullable
  @Override
  public CommandContext get(CommandArgs arguments, List<? extends Annotation> modifiers)
      throws ArgumentException, ProvisionException {
    return arguments.getNamespace().need(CommandContext.class);
  }
}
