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
import com.codelanx.codelanxlib.implementers.Formatted;
import com.codelanx.codelanxlib.internal.InternalLang;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

/**
 * Represents a single value that is dynamically retrieved from a
 * {@link FileDataType}. This value should be usable with a Formatter and
 * is typically implemented through an enum
 *
 * @since 0.1.0
 * @author 1Rogue
 * @version 0.1.0
 */
public interface Lang extends PluginFile {

    /**
     * Returns the format specifier for this {@link Lang} file. This should be
     * a constant in the enum and represent a value that is used for all
     * messages universally.
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return The format specifier for this {@link Lang}
     */
    public Lang getFormat();

    /**
     * The default value of this {@link Lang} string
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @return The default value
     */
    @Override
    public String getDefault();

    /**
     * Returns the string value used for this {@link Lang}. Color codes
     * will not be automatically converted
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @return The formatting string to be used for sending a message
     */
    @Override
    default public String get() {
        if (this.getClass().isAnonymousClass()) {
            return this.getDefault();
        }
        return String.valueOf(PluginFile.super.get());
    }

    /**
     * Formats a {@link Lang} enum constant with the supplied arguments, and
     * colors it
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param args The arguments to supply
     * @return The formatted string
     */
    default public String formatAndColor(Object... args) {
        return Lang.color(this.format(args));
    }

    /**
     * Formats a {@link Lang} enum constant with the supplied arguments
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param args The arguments to supply
     * @return The formatted string
     */
    default public String format(Object... args) {
        return String.format(this.get(), args);
    }

    /**
     * Will format a string with "PLURAL" or "PLURALA" tokens in them.
     * <br><br><ul>
     * <li> <em>PLURALA</em>: Token that will evaluate gramatically. An int
     * value of 1 will return "is &lt;amount&gt; 'word'", otherwise it will be
     * "are &lt;amount&gt; 'word'".
     * </li><li> <em>PLURAL</em>: Token that will evaluate the word. An int
     * value of 1 will return the first word, value of 2 the second word.
     * </ul>
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param amount The amount representative of the data token
     * @param args The arguments to replace any other tokens with.
     * @return The formatting string value for plurals
     */
    default public String pluralFormat(int amount, Object... args) {
        String repl = this.get();
        repl = repl.replaceAll("\\{PLURALA (.*)\\|(.*)\\}", amount == 1 ? "is " + amount + " $1" : "are " + amount + " $2");
        repl = repl.replaceAll("\\{PLURAL (.*)\\|(.*)\\}", amount == 1 ? "$1" : "$2");
        return Lang.color(String.format(repl, args));
    }

    /**
     * Returns a raw Lang object that can be used for dynamic creation of Lang
     * variables
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param format The string to wrap in a {@link Lang} object
     * @return A {@link Lang} object that will 
     */
    public static Lang createLang(String format) {
        return new Lang() {

            @Override
            public Lang getFormat() {
                return InternalLang.FORMAT.getFormat();
            }

            @Override
            public String getDefault() {
                return format;
            }

            @Override
            public String getPath() {
                return null;
            }

            @Override
            public FileDataType getConfig() {
                throw new UnsupportedOperationException("An anonymous Lang does not have a FileDataType associated with it");
            }

            @Override
            public DataHolder<FileDataType> getData() {
                throw new UnsupportedOperationException("Anonymous PluginFile classes do not have DataHolders");
            }

        };
    }

    /**
     * Returns a Lang object representing the default formatting for all
     * CodelanxLib plugins. The {@code option} parameter can be supplemented
     * with {@code null} which will default to "CL-Lib". The returned format is:
     * <br> <br>
     * {@code "&amp;f[&amp;9" + option == null ? "CL-Lib" : option + "&amp;f] %amp;s"}
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param option The optional "title" string for the format
     * @return A {@link Lang} object representing the default format
     */
    public static Lang defaultFormat(String option) {
        return Lang.createLang("&f[&9" + (option == null ? "CL-Lib" : option) + "&f] %s");
    }

    /**
     * Returns an acceptable message format for the plugin at hand
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param plugin A {@link Plugin} that needs a format
     * @return A relevant {@link Lang} format
     */
    public static Lang getFormat(Plugin plugin) {
        return plugin instanceof Formatted
                ? ((Formatted) plugin).getFormat()
                : Lang.defaultFormat(plugin.getName());
    }

    /**
     * Automatically translates strings that contain color codes using the
     * '{@code &}' symbol
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param color String with un-converted color codes
     * @return string with correct chat colors included
     */
    public static String color(String color) {
        return ChatColor.translateAlternateColorCodes('&', color);
    }

    /**
     * Sends a formatted string and prepends the {@link Lang#getFormat} to
     * it.
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param target The target to send to
     * @param message The message to colorize and send
     * @param args Arguments to supply to the {@link Lang} message
     */
    public static void sendMessage(CommandSender target, Lang message, Object... args) {
        Lang.sendMessage(target, message.getFormat(), message, args);
    }

    /**
     * Sends a message but does not include {@link Lang#getFormat}, instead it
     * uses a supplied format
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param target The target to send to
     * @param format The format provided
     * @param message The message to colorize and send
     * @param args Arguments to supply to the {@link Lang} message
     */
    public static void sendMessage(CommandSender target, Lang format, Lang message, Object... args) {
        if (target == null || format == null || message == null) {
            return;
        }
        String s = format.formatAndColor(message.format(args));
        if (!s.isEmpty()) {
            target.sendMessage(s);
        }
    }

    /**
     * Sends a raw message without additional formatting aside from translating
     * color codes
     *
     * @since 0.1.0
     * @version 0.1.0
     *
     * @param target The target to send to
     * @param message The message to colorize and send
     * @param args Arguments to supply to the {@link Lang} message
     */
    public static void sendRawMessage(CommandSender target, Lang message, Object... args) {
        if (message == null || target == null) {
            return;
        }
        String s = message.formatAndColor(args);
        if (!s.isEmpty()) {
            target.sendMessage(s);
        }
    }

    /**
     * Issues a "tellRaw" to a {@link Player} target with the supplied Lang
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param target The {@link Player} to send the JSON message to
     * @param message A {@link Lang} representing a JSON payload
     * @param args The arguments for the passed {@link Lang}
     */
    public static void tellRaw(Player target, Lang message, Object... args) {
        if (message == null || target == null) {
            return;
        }
        String s = message.format(args);
        if (!s.isEmpty()) {
            //Use command until proper api is in place
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "tellRaw " + target.getName() + " " + s);
        }
    }

    /**
     * Issues a "title" command to a {@link Player} target with the passed Lang
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param target The {@link Player} to send the JSON title to
     * @param message A {@link Lang} representing a JSON payload for the title
     * @param args The arguments for the passed {@link Lang}
     */
    public static void sendTitle(Player target, Lang message, Object... args) {
        if (message == null || target == null) {
            return;
        }
        String s = message.format(args);
        if (!s.isEmpty()) {
            //Use command until proper api is in place
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "title " + target.getName() + " " + s); //inb4 "bukkit injection"
        }
    }

    /**
     * Formats a string into either a proper word or sentence (First letter
     * capitalized, if spaces are included a period is added)
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @see Lang#proper(String, char)
     * @param raw The raw string to format
     * @return The formatted string
     */
    public static String proper(String raw) {
        return Lang.proper(raw, '.');
    }

    /**
     * Formats a string into either a proper word or sentence (First letter
     * capitalized, if spaces are included the supplied punctuation is added)
     * 
     * @since 0.1.0
     * @version 0.1.0
     * 
     * @param raw The raw string to format
     * @param punctuation Punctuation to append to anything containing spaces
     * @return The formatted string
     */
    public static String proper(String raw, char punctuation) {
        raw = raw.trim();
        return raw.isEmpty() ? "" : (Character.toUpperCase(raw.charAt(0)) + raw.substring(1).toLowerCase() + (raw.contains(" ") ? punctuation : ""));
    }

    //Convieniance methods

    public static void sendMessage(OfflinePlayer target, Lang format, Lang message, Object... args) {
        if (target.isOnline()) {
            Lang.sendMessage((CommandSender) target.getPlayer(), format, message, args);
        }
    }
    public static void sendMessage(OfflinePlayer target, Lang message, Object... args) {
        if (target.isOnline()) {
            Lang.sendMessage((CommandSender) target.getPlayer(), message.getFormat(), message, args);
        }
    }

    public static void sendRawMessage(OfflinePlayer target, Lang message, Object... args) {
        if (target.isOnline()) {
            Lang.sendRawMessage((CommandSender) target.getPlayer(), message, args);
        }
    }

    public static void sendMessage(Player target, Lang format, Lang message, Object... args) {
        Lang.sendMessage((CommandSender) target, format, message, args);
    }

    public static void sendMessage(Player target, Lang message, Object... args) {
        Lang.sendMessage((CommandSender) target, message.getFormat(), message, args);
    }

    public static void sendRawMessage(Player target, Lang message, Object... args) {
        Lang.sendRawMessage((CommandSender) target, message, args);
    }
}
