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
package app.ashcon.intake.argument;

import app.ashcon.intake.parametric.ProvisionException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.jetbrains.annotations.Nullable;

/**
 * This object holds contextual data for a command execution.
 *
 * <p>The purpose of a namespace is to pass non-argument data to commands such as current session
 * data and so on.
 */
public class Namespace {

  private final Map<Object, Object> locals = new HashMap<Object, Object>();

  public Namespace() {}

  public Namespace(Object initialKey, Object initialValue) {
    put(initialKey, initialValue);
  }

  /**
   * Test whether the given key exists.
   *
   * @param key The key
   * @return true If the key exists
   */
  public boolean containsKey(Object key) {
    return locals.containsKey(key);
  }

  /**
   * Test whether the given value exists.
   *
   * @param value The value
   * @return true If the value exists
   */
  public boolean containsValue(Object value) {
    return locals.containsValue(value);
  }

  /**
   * Returns the value specified by the given key.
   *
   * @param key The key
   * @return The value, which may be null, including when the key doesn't exist
   */
  @Nullable
  public Object get(Object key) {
    return locals.get(key);
  }

  /**
   * Get an object whose key will be the object's class.
   *
   * @param key The key
   * @param <T> The type of object
   * @return The value
   */
  @Nullable
  @SuppressWarnings("unchecked")
  public <T> T get(Class<T> key) {
    return (T) get((Object) key);
  }

  /**
   * Returns the value specified by the given key.
   *
   * @param key The key
   * @return The optional value
   */
  public Optional<Object> find(Object key) {
    return Optional.ofNullable(get(key));
  }

  /**
   * Get an object whose key will be the object's class.
   *
   * @param key The key
   * @param <T> The type of object
   * @return The optional value
   */
  @SuppressWarnings("unchecked")
  public <T> Optional<T> find(Class<T> key) {
    return (Optional<T>) find((Object) key);
  }

  /**
   * Returns the value specified by the given key.
   *
   * @param key The key
   * @return The value, which will not be null
   * @throws ProvisionException If the value is null
   */
  public Object need(Object key) {
    Object value = get(key);
    if (value == null) {
      throw new ProvisionException("Could not find value for '" + key + "' in namespace " + locals);
    }
    return value;
  }

  /**
   * Get an object whose key will be the object's class.
   *
   * @param key The key
   * @param <T> The type of object
   * @return The value
   * @throws ProvisionException If the value is null
   */
  @SuppressWarnings("unchecked")
  public <T> T need(Class<T> key) {
    return (T) need((Object) key);
  }

  /**
   * Set an contextual value.
   *
   * @param key Key with which the specified value is to be associated
   * @param value Value to be associated with the specified key
   * @return The previous value associated with <tt>key</tt>, or <tt>null</tt> if there was no
   *     mapping for <tt>key</tt>. (A <tt>null</tt> return can also indicate that the map previously
   *     associated <tt>null</tt> with <tt>key</tt>, if the implementation supports <tt>null</tt>
   *     values.)
   * @throws UnsupportedOperationException if the <tt>put</tt> operation is not supported by this
   *     map
   * @throws ClassCastException if the class of the specified key or value prevents it from being
   *     stored in this map
   */
  @Nullable
  public Object put(Object key, Object value) {
    return locals.put(key, value);
  }
}
