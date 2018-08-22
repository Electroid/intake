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

package com.sk89q.intake.parametric.provider;

import com.sk89q.intake.argument.ArgumentException;
import com.sk89q.intake.argument.CommandArgs;
import com.sk89q.intake.parametric.Provider;

import java.lang.annotation.Annotation;
import java.util.List;
import java.util.OptionalDouble;

class OptionalDoubleProvider implements Provider<OptionalDouble>, NumberProvider<Double> {

    static final OptionalDoubleProvider INSTANCE = new OptionalDoubleProvider();

    @Override
    public OptionalDouble get(CommandArgs arguments, List<? extends Annotation> modifiers) throws ArgumentException {
        if (arguments.hasNext()) {
            Double v = parseNumericInput(arguments.next());
            if (v != null) {
                return OptionalDouble.of(v);
            }
        }
        return OptionalDouble.empty();
    }

}
