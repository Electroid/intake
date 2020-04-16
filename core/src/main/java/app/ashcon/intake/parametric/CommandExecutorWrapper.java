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

import static com.google.common.base.Preconditions.checkNotNull;

import app.ashcon.intake.argument.CommandArgs;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Future;

/** Wraps an ExecutorService into a CommandExecutor. */
public class CommandExecutorWrapper implements CommandExecutor {

  private final Executor executor;

  public CommandExecutorWrapper(Executor executor) {
    checkNotNull(executor, "executor");
    this.executor = executor;
  }

  @Override
  public <T> Future<T> submit(Callable<T> callable, CommandArgs args) {
    Task<T> task = new Task<>(callable);
    executor.execute(task);
    return task;
  }

  private static class Task<V> extends CompletableFuture<V> implements Runnable {
    private final Callable<V> task;

    private Task(Callable<V> task) {
      this.task = checkNotNull(task, "task");
    }

    @Override
    public void run() {
      try {
        complete(task.call());
      } catch (Throwable t) {
        completeExceptionally(t);
      }
    }
  }
}
