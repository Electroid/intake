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
import app.ashcon.intake.argument.MissingArgumentException;

import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;

class TextProvider extends StringProvider {

    static final TextProvider INSTANCE = new TextProvider();

    @Override
    public String getName() {
        return "string...";
    }

    @Nullable
    @Override
    public String get(CommandArgs arguments, List<? extends Annotation> modifiers) throws ArgumentException {
        // NPE when the command expects (more) arguments but is provided none
        StringBuilder builder;
        try {
            builder = new StringBuilder(arguments.next());
        } catch (NullPointerException e){
            throw new MissingArgumentException();
        }

        while(arguments.hasNext()) {
            // This properly handles the trailing blank character if the CommandSender accidentally included a blank character at the end
            // i.e. /potato buy 27 (blank)
            builder.append(" ").append(arguments.next());
        }
        String appendedText = builder.toString();
        validate(appendedText, modifiers);
        return appendedText;
    }

}
