package com.github.eajon.util;

import com.orhanobut.logger.Logger;
/**
 * @author eajon
 */
public class LoggerUtils {

    private static boolean IS_DEBUG = true;

    public static void init(boolean isDebug) {
        IS_DEBUG = isDebug;
    }

    private static boolean isLoggable() {
        return IS_DEBUG;
    }

    public static void verbose(String message, Object... args) {
        if (isLoggable()) {
            Logger.v(message, args);
        }
    }

    public static void debug(Object msg) {
        if (isLoggable()) {
            Logger.d(msg);
        }
    }

    public static void debug(String message, Object... args) {
        if (isLoggable()) {
            Logger.d(message, args);
        }
    }

    public static void info(String message, Object... args) {
        if (isLoggable()) {
            Logger.i(message, args);
        }
    }

    public static void warning(String message, Object... args) {
        if (isLoggable()) {
            Logger.w(message, args);
        }
    }

    public static void error(String message, Object... args) {
        if (isLoggable()) {
            Logger.e(message, args);
        }
    }

    public static void error(Throwable throwable, String message, Object... args) {
        if (isLoggable()) {
            Logger.e(throwable, message, args);
        }
    }

    public static void json(String json) {
        if (isLoggable()) {
            Logger.json(json);
        }
    }

    public static void xml(String xml) {
        if (isLoggable()) {
            Logger.xml(xml);
        }
    }


}
