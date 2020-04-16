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

import static com.google.common.base.Preconditions.checkNotNull;

import app.ashcon.intake.parametric.ParametricBuilder;
import java.util.function.Function;

/**
 * A fluent interface to creating a command graph.
 *
 * <p>A command graph may have multiple commands, and multiple sub-commands below that, and possibly
 * below that.
 */
public class CommandGraph<T extends AbstractDispatcherNode> {

  private T rootDispatcherNode;
  private ParametricBuilder builder;

  /**
   * Constructor to be used by children that do not have either the rootDispatcherNode or builder on
   * initialization
   *
   * <p>Note: The rootDispatcherNode and builder must still be initialized
   */
  protected CommandGraph() {}

  /**
   * Create a new {@link CommandGraph} instance
   *
   * @param rootDispatcherNodeCreator the function responsible for creating a root dispatcher node.
   *     The function's parameter is a reference to this CommandGraph class
   */
  public CommandGraph(
      ParametricBuilder builder, Function<CommandGraph, T> rootDispatcherNodeCreator) {
    checkNotNull(builder, "builder can not be null");
    checkNotNull(rootDispatcherNodeCreator, "root dispatcher can not be null");

    this.builder = builder;
    this.rootDispatcherNode = rootDispatcherNodeCreator.apply(this);
  }

  /**
   * Get the {@link T}.
   *
   * @return the root dispatcher node, or null.
   */
  public T getRootDispatcherNode() {
    return rootDispatcherNode;
  }

  /**
   * Set the {@link T}
   *
   * @param rootDispatcherNode the root dispatcher node
   */
  protected void setRootDispatcherNode(T rootDispatcherNode) {
    this.rootDispatcherNode = rootDispatcherNode;
  }

  /**
   * Get the {@link ParametricBuilder}.
   *
   * @return the builder, or null.
   */
  public ParametricBuilder getBuilder() {
    return builder;
  }

  /**
   * Set the {@link ParametricBuilder}
   *
   * @param builder the parametric builder
   */
  protected void setBuilder(ParametricBuilder builder) {
    this.builder = builder;
  }
}
