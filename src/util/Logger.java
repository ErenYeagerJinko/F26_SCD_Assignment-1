package util;

import java.io.*;
import java.util.logging.*;

public class Logger {
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger("CampusManagementSystem");
    private static FileHandler fileHandler;

    static {
        try {
            File logsDir = new File("logs");
            if (!logsDir.exists()) {
                logsDir.mkdirs();
            }
            fileHandler = new FileHandler("logs/app.log", true);
            fileHandler.setFormatter(new SimpleFormatter());
            logger.addHandler(fileHandler);
        } catch (IOException e) {
            System.err.println("Failed to initialize Logger FileHandler: " + e.getMessage());
        }
    }

    public static void info(String message) {
        logger.info(message);
    }

    public static void warning(String message) {
        logger.warning(message);
    }

    public static void severe(String message) {
        logger.severe(message);
    }

    public static void error(String message) {
        logger.severe(message);
    }

    public static void log(Level level, String message) {
        logger.log(level, message);
    }
}