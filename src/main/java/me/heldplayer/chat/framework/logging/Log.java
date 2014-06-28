
package me.heldplayer.chat.framework.logging;

public abstract class Log {

    public abstract void log(LogLevel level, String str);

    public abstract Log getSubLog(String name);

    public abstract void rename(String name);

    public abstract String getName();

    public abstract String getFullName();

    public void log(LogLevel level, String str, Object... args) {
        this.log(level, String.format(str, args));
    }

    public void log(LogLevel level, Object obj) {
        this.log(level, "" + obj);
    }

    public void info(Object obj) {
        this.log(LogLevel.INFO, obj);
    }

    public void info(String str, Object... args) {
        this.log(LogLevel.INFO, String.format(str, args));
    }

    public void warning(Object obj) {
        this.log(LogLevel.WARNING, obj);
    }

    public void warning(String str, Object... args) {
        this.log(LogLevel.WARNING, String.format(str, args));
    }

    public void error(Object obj) {
        this.log(LogLevel.ERROR, obj);
    }

    public void error(String str, Object... args) {
        this.log(LogLevel.ERROR, String.format(str, args));
    }

    public void severe(Object obj) {
        this.log(LogLevel.FATAL, obj);
    }

    public void severe(String str, Object... args) {
        this.log(LogLevel.FATAL, String.format(str, args));
    }

    public void debug(Object obj) {
        this.log(LogLevel.DEBUG, obj);
    }

    public void debug(String str, Object... args) {
        this.log(LogLevel.DEBUG, String.format(str, args));
    }

    public void trace(Object obj) {
        this.log(LogLevel.TRACE, obj);
    }

    public void trace(String str, Object... args) {
        this.log(LogLevel.TRACE, String.format(str, args));
    }

    public static enum LogLevel {
        OFF,
        FATAL,
        ERROR,
        WARNING,
        INFO,
        DEBUG,
        TRACE,
        ALL;
    }

}
