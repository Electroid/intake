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

import com.sk89q.intake.argument.ArgumentParseException;
import com.sk89q.intake.parametric.annotation.Range;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

interface NumberProvider<T extends Number> {

    /**
     * Try to parse numeric input as either a number or a mathematical expression.
     *
     * @param input input
     * @return a number
     * @throws ArgumentParseException thrown on parse error
     */
    @Nullable
    default Double parseNumericInput(@Nullable String input) throws ArgumentParseException {
        if (input == null || input.isEmpty()) {
            return null;
        }

        try {
            return Double.parseDouble(input);
        } catch (NumberFormatException ignored) {
            throw new ArgumentParseException(String.format("Expected '%s' to be a number", input));
        }
    }

    /**
     * Validate a number value using relevant modifiers.
     *
     * @param number the number
     * @param modifiers the list of modifiers to scan
     * @throws ArgumentParseException on a validation error
     */
    default void validate(double number, List<? extends Annotation> modifiers) throws ArgumentParseException {
        for (Annotation modifier : modifiers) {
            if (modifier instanceof Range) {
                Range range = (Range) modifier;
                if (number < range.min()) {
                    throw new ArgumentParseException(String.format("A valid value is greater than or equal to %s (you entered %s)", range.min(), number));
                } else if (number > range.max()) {
                    throw new ArgumentParseException(String.format("A valid value is less than or equal to %s (you entered %s)", range.max(), number));
                }
            }
        }
    }

    /**
     * Validate a number value using relevant modifiers.
     *
     * @param number the number
     * @param modifiers the list of modifiers to scan
     * @throws ArgumentParseException on a validation error
     */
    default void validate(int number, List<? extends Annotation> modifiers) throws ArgumentParseException {
        for (Annotation modifier : modifiers) {
            if (modifier instanceof Range) {
                Range range = (Range) modifier;
                if (number < range.min()) {
                    throw new ArgumentParseException(String.format("A valid value is greater than or equal to %s (you entered %s)", range.min(), number));
                } else if (number > range.max()) {
                    throw new ArgumentParseException(String.format("A valid value is less than or equal to %s (you entered %s)", range.max(), number));
                }
            }
        }
    }

}
