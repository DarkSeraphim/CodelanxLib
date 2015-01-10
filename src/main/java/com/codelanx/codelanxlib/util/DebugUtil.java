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
package com.codelanx.codelanxlib.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class description for {@link DebugUtil}
 *
 * TODO: Make class support multiple plugins
 *
 * @since 0.0.1
 * @author 1Rogue
 * @version 0.0.1
 */
public final class DebugUtil {

    private static boolean ENABLED = true;

    private DebugUtil() {
    }

    public static void print(Level level, String format, Object... args) {
        if (!DebugUtil.ENABLED) { return; }
        Logger.getLogger(DebugUtil.class.getName()).log(level,
                "[debug] {0}", String.format(format, args));
    }

    public static void print(String format, Object... args) {
        DebugUtil.print(Level.INFO, format, args);
    }

    public static void error(String message, Throwable error) {
        Logger.getLogger(DebugUtil.class.getName()).log(Level.SEVERE,
                message, error);
    }

    public static void toggleOutput(boolean output) {
        DebugUtil.ENABLED = output;
    }

}
