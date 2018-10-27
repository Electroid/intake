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
package app.ashcon.intake.fluent;

import static com.google.common.base.Preconditions.checkArgument;

import app.ashcon.intake.Command;
import app.ashcon.intake.CommandCallable;
import app.ashcon.intake.CommandMapping;
import app.ashcon.intake.dispatcher.Dispatcher;
import app.ashcon.intake.dispatcher.SimpleDispatcher;
import app.ashcon.intake.group.At;
import app.ashcon.intake.group.Group;
import app.ashcon.intake.group.Root;
import app.ashcon.intake.parametric.ParametricBuilder;
import java.lang.reflect.Method;
import javax.annotation.Nonnull;

/**
 * A collection of grouped commands.
 */
public class GroupDispatcherNode extends AbstractDispatcherNode {

    public GroupDispatcherNode(CommandGraph graph, DispatcherNode parent, Dispatcher dispatcher) {
        super(graph, parent, dispatcher);
    }

    /**
     * {@inheritDoc}
     */
    public GroupDispatcherNode registerMethods(Object object) {
        return (GroupDispatcherNode) super.registerMethods(object);
    }

    /**
     * Register {@link Group}ed commands on an object.
     *
     * @param object the object containing the methods
     * @return this object
     */
    public GroupDispatcherNode registerGrouped(Object object) {
        boolean classRegistered = this.registerClass(object);
        this.registerClassMethods(object, classRegistered);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    public GroupDispatcherNode parent() {
        return (GroupDispatcherNode) super.parent();
    }

    /**
     * Get the root command graph.
     *
     * @return the root command graph
     */
    public CommandGraph graph() {
        return this.graph;
    }

    protected boolean registerClass(@Nonnull final Object object) {
        boolean result = false;

        final Group group = object.getClass().getAnnotation(Group.class);
        if (group != null) {
            result = true;
            for (final At at : group.value()) {
                checkArgument(!at.value().isEmpty(), "group cannot be empty");
                graph.getBuilder().registerMethodsAsCommands(this.getGroup(at.value()), object);
            }
        }

        if (object.getClass().getAnnotation(Root.class) != null) {
            graph.getBuilder().registerMethodsAsCommands(this.dispatcher, object);
        }

        return result;
    }

    protected void registerClassMethods(@Nonnull final Object object, final boolean classRegistered) {
        for (final Method method : object.getClass().getDeclaredMethods()) {
            final Command definition = method.getAnnotation(Command.class);
            if (definition != null) {
                final CommandCallable callable = graph.getBuilder().build(object, method);
                final Group group = method.getAnnotation(Group.class);
                if (group != null) {
                    for (At at : group.value()) {
                        checkArgument(!at.value().isEmpty(), "group cannot be empty");
                        this.getGroup(at.value()).registerCommand(callable, definition.aliases());
                    }
                    continue; // Once registered in a group, it cannot be registered at root
                }

                if (method.getAnnotation(Root.class) != null || !classRegistered) {
                    this.dispatcher.registerCommand(callable, definition.aliases());
                }
            }
        }
    }

    protected Dispatcher getGroup(String group) {
        CommandMapping mapping = this.dispatcher.get(group);
        if (mapping == null) {
            SimpleDispatcher child = new SimpleDispatcher();
            this.dispatcher.registerCommand(child, group);
            return child;
        }
        else if (mapping.getCallable() instanceof SimpleDispatcher) {
            return (SimpleDispatcher) mapping.getCallable();
        }
        else {
            throw new IllegalStateException("Can't put group at '" + group + "' because there is an existing command there");
        }
    }
}
