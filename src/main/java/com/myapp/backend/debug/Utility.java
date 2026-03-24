package com.myapp.backend.debug;
public class Utility {

    // Reset
    public static final String RESET = "\u001B[0m";

    // Colori base
    public static final String BLACK = "\n\u001B[30m";
    public static final String RED = "\n\u001B[31m";
    public static final String GREEN = "\n\u001B[32m";
    public static final String YELLOW = "\n\u001B[33m";
    public static final String BLUE = "\n\u001B[34m";
    public static final String PURPLE = "\n\u001B[35m";
    public static final String CYAN = "\n\u001B[36m";
    public static final String WHITE = "\n\u001B[37m";

    // Metodi pronti all’uso
    public static void info(String message) {
        System.out.println(GREEN + "[INFO] " + message + RESET);
    }

    public static void warn(String message) {
        System.out.println(YELLOW + "[WARN] " + message + RESET);
    }

    public static void error(String message) {
        System.out.println(RED + "[ERROR] " + message + RESET);
    }

    public static void debug(String message) {
        System.out.println(CYAN + "[DEBUG] " + message + RESET);
    }

    public static void kafka(String message) {
        System.out.println(PURPLE + "[KAFKA] " + message + RESET);
    }

    public static void custom(String color, String label, String message) {
        System.out.println(color + "[" + label + "] " + message + RESET);
    }
}