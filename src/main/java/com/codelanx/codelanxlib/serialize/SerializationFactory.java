/*
 * Copyright (C) 2014 Codelanx, All Rights Reserved
 *
 * This work is licensed under a Creative Commons
 * Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 *
 * This program is protected software: You are free to distrubute your
 * own use of this software under the terms of the Creative Commons BY-NC-ND
 * license as published by Creative Commons in the year 2014 or as published
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
package com.codelanx.codelanxlib.serialize;

import java.util.ArrayList;
import java.util.List;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

/**
 * Class description for {@link SerializationFactory}
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 */
public class SerializationFactory {

    private static List<Class<? extends ConfigurationSerializable>> sers = new ArrayList<>();

    public static void registerClass(Class<? extends ConfigurationSerializable> clazz) {
        SerializationFactory.sers.add(clazz);
    }

    public static void registerClasses(Class<? extends ConfigurationSerializable>... clazz) {
        for (Class<? extends ConfigurationSerializable> c : clazz) {
            SerializationFactory.registerClass(c);
        }
    }

    public static void registerToBukkit() {
        sers.forEach(ConfigurationSerialization::registerClass);
    }

    public static Class[] getNativeSerializables() {
        return new Class[] {
            SInventory.class,
            SPlayerInventory.class
        };
    }
}