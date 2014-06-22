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
package com.codelanx.codelanxlib;

import com.codelanx.codelanxlib.command.CommandHandler;
import com.codelanx.codelanxlib.config.ConfigMarker;
import com.codelanx.codelanxlib.config.ConfigurationLoader;
import com.codelanx.codelanxlib.implementers.Commandable;
import com.codelanx.codelanxlib.implementers.Configurable;
import com.codelanx.codelanxlib.implementers.Listening;
import com.codelanx.codelanxlib.lang.InternalLang;
import com.codelanx.codelanxlib.listener.ListenerManager;
import com.codelanx.codelanxlib.util.DebugUtil;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Class description for {@link CodelanxPlugin}
 *
 * @since 1.0.0
 * @author 1Rogue
 * @version 1.0.0
 * 
 * @param <E> The implementing plugin instance
 */
public abstract class CodelanxPlugin<E extends CodelanxPlugin<E>> extends JavaPlugin implements Commandable, Configurable, Listening {

    private WeakReference<FreshVarWrap> cnfg;
    private WeakReference<String> cmd;
    protected CommandHandler<E> commands;
    protected ConfigurationLoader config;
    protected ListenerManager<E> listener;
    
    public <T extends Enum<T> & ConfigMarker<T>> CodelanxPlugin(String command, Class<T> config) {
        this.cmd = new WeakReference(command);
        this.cnfg = new WeakReference(new FreshVarWrap<>(config));
    }

    @Override
    public void onLoad() {
        DebugUtil.toggleOutput(true);
        try {
            InternalLang.init(this);
        } catch (IOException ex) {
            DebugUtil.error("Error loading internal lang system, expect errors!", ex);
        }
    }

    @Override
    public void onEnable() {
        this.getLogger().log(Level.INFO, "Loading configuration...");
        this.config = new ConfigurationLoader(this, this.cnfg.get().getInst());
        this.cnfg.clear();
        this.cnfg = null;
        
        this.getLogger().log(Level.INFO, "Enabling listeners...");
        this.listener = new ListenerManager<>((E) this);
        
        this.getLogger().log(Level.INFO, "Enabling command handler...");
        this.commands = new CommandHandler<>((E) this, this.cmd.get());
        this.cmd.clear();
        this.cmd = null;
    }

    @Override
    public void onDisable() {
        this.listener.cleanup();
        try {
            this.config.saveConfig();
        } catch (IOException ex) {
            this.getLogger().log(Level.INFO, "Error saving current configuration!");
        }
    }

    @Override
    public CommandHandler<E> getCommandHandler() {
        return this.commands;
    }

    @Override
    public ConfigurationLoader getConfiguration() {
        return this.config;
    }

    @Override
    public ListenerManager<E> getListenerManager() {
        return this.listener;
    }

    /** 
     * Helper class for procuring the fresh-type variable for the passed config
     * class
     */
    private class FreshVarWrap<T extends Enum<T> & ConfigMarker<T>> {
        
        private final Class<T> inst;
        
        public FreshVarWrap(Class<T> inst) {
            this.inst = inst;
        }

        public Class<T> getInst() {
            return this.inst;
        }
    }

}
