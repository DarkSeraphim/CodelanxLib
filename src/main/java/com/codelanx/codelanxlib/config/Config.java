/*
 * Copyright (C) 2015 Codelanx, All Rights Reserved
 *
 * This work is licensed under a Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 * This program is protected software: You are free to distrubute your
 * own use of this software under the terms of the Creative Commons BY-NC-ND
 * license as published by Creative Commons in the year 2015 or as published
 * by a later date. You may not provide the source files or provide a means
 * of running the software outside of those licensed to use it.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *
 * You should have received a copy of the Creative Commons BY-NC-ND license
 * long with this program. If not, see <https://creativecommons.org/licenses/>.
 */
package com.codelanx.codelanxlib.config;

import com.codelanx.codelanxlib.data.FileDataType;
import com.codelanx.codelanxlib.util.Reflections;
import com.google.common.primitives.Primitives;
import java.util.Collection;
import java.util.Map;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;

/**
 * Represents a single value that is dynamically retrieved from a
 * {@link FileDataType}. This value can be of any type, and the class should
 * typically be implemented through an enum
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public interface Config extends PluginFile {

    /**
     * Attempts to return the {@link Config} value as a casted type. If the
     * value cannot be casted it will attempt to return the default value. If
     * the default value is inappropriate for the class, the method will
     * throw a {@link ClassCastException}.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param <T> The type of the casting class
     * @param c The class type to cast to
     * @return A casted value, or {@code null} if unable to cast. If the passed
     *         class parameter is of a primitive type or autoboxed primitive,
     *         then a casted value of -1 is returned, or {@code false} for
     *         booleans. If the passed class parameter is for {@link String},
     *         then {@link Object#toString()} is called on the value instead
     */
    default public <T> T as(Class<T> c) {
        Validate.notNull(c, "Cannot cast to null");
        Validate.isTrue(Primitives.unwrap(c) != void.class, "Cannot cast to a void type");
        boolean primitive = Primitives.isWrapperType(c) || Primitives.isWrapperType(Primitives.wrap(c));
        Object o = this.get();
        if (primitive) {
            T back;
            if (o == null) {
                return Reflections.defaultPrimitiveValue(c);
            } else {
                back = Primitives.wrap(c).cast(o);
            }
            return back;
        }
        if (o == null) {
            return null;
        }
        if (c == String.class) {
            return (T) String.valueOf(o);
        }
        if (c.isInstance(o)) {
            return c.cast(o);
        }
        if (c.isInstance(this.getDefault())) {
            return c.cast(this.getDefault());
        }
        throw new ClassCastException("Unable to cast config value");
    }

    /**
     * Attempts to return the {@link Config} value as a casted type. If the
     * value cannot be casted it will attempt to return the default value. If
     * the default value is inappropriate for the class, the method will
     * throw a {@link ClassCastException}. This exception is also throwing if
     * the type used for the members contained within the collection are
     * incorrect
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param <T> The type of the casting class
     * @param <G> The type of the collection's contents
     * @param collection The collection type to cast to
     * @param type The generic bounding for the collection
     * @return A casted value, or {@code null} if unable to cast. If the passed
     *         class parameter is of a primitive type or autoboxed primitive,
     *         then a casted value of -1 is returned, or {@code false} for
     *         booleans. If the passed class parameter is for {@link String},
     *         then {@link Object#toString()} is called on the value instead
     */
    @SuppressWarnings("rawtypes")
    default public <G, T extends Collection<G>> T as(Class<? extends Collection> collection, Class<G> type) {
        Collection<G> col = this.as(collection);
        for (Object o : col) {
            if (!type.isInstance(o)) {
                throw new ClassCastException("Inappropriate generic type for collection");
            }
        }
        return (T) col;
    }

    /**
     * Attempts to return the {@link Config} value as a casted type. If the
     * value cannot be casted it will attempt to return the default value. If
     * the default value is inappropriate for the class, the method will
     * throw a {@link ClassCastException}. This exception is also throwing if
     * the type used for the members contained within the collection are
     * incorrect
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param <K> The type of the keys for this map
     * @param <V> The type of the values for this map
     * @param <M> The type of the map
     * @param map The class object of the map type to use
     * @param key The class object of the key types
     * @param value The class object of the value types
     * @return A casted value, or {@code null} if unable to cast. If the passed
     *         class parameter is of a primitive type or autoboxed primitive,
     *         then a casted value of -1 is returned, or {@code false} for
     *         booleans. If the passed class parameter is for {@link String},
     *         then {@link Object#toString()} is called on the value instead
     */
    @SuppressWarnings("rawtypes")
    default public <K, V, M extends Map<K, V>> M as(Class<? extends Map> map, Class<K> key, Class<V> value) {
        Map<?, ?> m = this.as(map);
        for (Map.Entry<?, ?> ent : m.entrySet()) {
            if (!key.isInstance(ent.getKey()) || !value.isInstance(ent.getValue())) {
                throw new ClassCastException("Inappropriate generic types for map");
            }
        }
        return (M) m;
    }

    /**
     * Sets a value in the {@link FileDataType}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param val The value to set
     * @return The previous {@link Config} value
     */
    default public Config set(Object val) {
        this.getConfig().set(this.getPath(), val);
        return this;
    }

    /**
     * Returns a {@link Map} representative of the passed Object that represents
     * a section of a YAML file. This method neglects the implementation of the
     * section (whether it be {@link ConfigurationSection} or just a
     * {@link Map}), and returns the appropriate value.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param o The object to interpret
     * @return A {@link Map} representing the section
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> getConfigSectionValue(Object o) {
        if (o == null) {
            return null;
        }
        Map<String, Object> map;
        if (o instanceof ConfigurationSection) {
            map = ((ConfigurationSection) o).getValues(false);
        } else if (o instanceof Map) {
            map = (Map<String, Object>) o;
        } else {
            return null;
        }
        return map;
    }

    /**
     * Retrieves an anonymous value which can utilize a
     * {@link Config} parameter to retrieve data from any source
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param <T> Represents a {@link FileDataType} passed to the method
     * @param file The {@link FileDataType} to use
     * @param config The {@link Config} value to search with
     * @return An anonymous class wrapping of the configuration and keys
     */
    public static <T extends FileDataType> Config retrieve(T file, Config config) {
        Validate.notNull(file, "File cannot be null");
        Validate.notNull(config, "Config cannot be null");
        return new Config() {

            @Override
            public String getPath() {
                return config.getPath();
            }

            @Override
            public Object getDefault() {
                return config.getDefault();
            }

            @Override
            public T getConfig() {
                return file;
            }

            @Override
            public DataHolder<FileDataType> getData() {
                throw new UnsupportedOperationException("Anonymous PluginFile classes do not have DataHolders");
            }
            
        };
    }

    /**
     * Facade method for {@link Config#retrieve(FileDataType, Config)}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param t A {@link FileDataType} to retrieve this config value from
     * @see Config#retrieve(FileDataType, Config)
     * @return A config value that can be used to retrieve values from
     */
    default public Config retrieve(FileDataType t) {
        return Config.retrieve(t, this);
    }

}