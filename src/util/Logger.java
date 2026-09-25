package util;

import java.io.*;
import java.time.*;
import java.time.format.*;

public class Logger {
    private static final String LOG_FILE = "logs/logs.log";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    static {
        File logsDir = new File("logs");
        if (!logsDir.exists()) {
            logsDir.mkdirs();
        }
    }

    public static synchronized void log(String level, String message) {
        try {
            File logsDir = new File("logs");
            if (!logsDir.exists()) {
                logsDir.mkdirs();
            }
            try (FileWriter writer = new FileWriter(LOG_FILE, true)) {
                String timestamp = LocalDateTime.now().format(FORMATTER);
                String tag = (level != null && !level.trim().isEmpty()) ? level.trim().toUpperCase() : "INFO";
                writer.write("[" + tag + "] " + timestamp + " - " + message + "\n");
            }
        } catch (Exception e) {
            System.err.println("Logging failed: " + e.getMessage());
        }
    }

    public static void info(String message) {
        log("INFO", message);
    }

    public static void warning(String message) {
        log("WARNING", message);
    }

    public static void severe(String message) {
        log("SEVERE", message);
    }

    public static void error(String message) {
        log("ERROR", message);
    }
}