package com.hello.utils;

public class LogUtil {
    public static void info(String msg) { System.out.println("[INFO] " + msg); }
    public static void warning(String msg) { System.out.println("[WARN] " + msg); }
    public static void error(String msg) { System.err.println("[ERROR] " + msg); }
    public static void error(String msg, Throwable t) { System.err.println("[ERROR] " + msg); t.printStackTrace(); }
    public static void debug(String msg) { System.out.println("[DEBUG] " + msg); }
    public static void logUserAction(String user, String action, String details) { System.out.println("[USER] " + user + " " + action + " " + details); }
    public static void logSecurity(String event, String details) { System.out.println("[SECURITY] " + event + " " + details); }
} 