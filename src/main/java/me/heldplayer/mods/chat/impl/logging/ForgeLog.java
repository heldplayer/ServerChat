
package me.heldplayer.mods.chat.impl.logging;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeMap;

import me.heldplayer.chat.framework.logging.Log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ForgeLog extends Log {

    private Logger log;
    private ForgeLog parent;
    private Collection<ForgeLog> children;
    private String name;

    public ForgeLog(Logger log, String name) {
        this(null, log, name);
    }

    private ForgeLog(ForgeLog parent, Logger log, String name) {
        this.parent = parent;
        this.log = log;
        this.name = name;
        this.children = new ArrayList<ForgeLog>();
    }

    @Override
    public void log(Log.LogLevel level, String str) {
        this.log.log(LogLevel.getLevel(level), str);
    }

    @Override
    public Log getSubLog(String name) {
        return new ForgeLog(this, LogManager.getLogger(this.log.getName() + "/" + name), name);
    }

    @Override
    public void rename(String name) {
        this.name = name;
        if (this.parent == null) {
            this.log = LogManager.getLogger(name);
        }
        else {
            this.log = LogManager.getLogger(this.parent.getFullName() + "/" + name);
        }
        for (ForgeLog child : this.children) {
            child.rename(child.name);
        }
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getFullName() {
        return this.parent != null ? (this.parent.getFullName() + "/" + this.getName()) : this.getName();
    }

    private static enum LogLevel {
        OFF(Log.LogLevel.OFF, Level.OFF),
        FATAL(Log.LogLevel.FATAL, Level.FATAL),
        ERROR(Log.LogLevel.ERROR, Level.ERROR),
        WARNING(Log.LogLevel.WARNING, Level.WARN),
        INFO(Log.LogLevel.INFO, Level.INFO),
        DEBUG(Log.LogLevel.DEBUG, Level.DEBUG),
        TRACE(Log.LogLevel.TRACE, Level.TRACE),
        ALL(Log.LogLevel.ALL, Level.ALL);

        private final Log.LogLevel level;
        private final Level logLevel;

        private static TreeMap<Log.LogLevel, Level> levels = new TreeMap<Log.LogLevel, Level>();

        static {
            for (LogLevel level : LogLevel.values()) {
                LogLevel.levels.put(level.level, level.logLevel);
            }
        }

        private LogLevel(Log.LogLevel level, Level logLevel) {
            this.level = level;
            this.logLevel = logLevel;
        }

        protected static Level getLevel(Log.LogLevel level) {
            return LogLevel.levels.get(level);
        }

    }

}
