package util;

import java.io.*;
import java.util.*;

public class DelimitedFiles {
    public static final String SEPARATOR = "|";
    public static final String EMPTY = "-";

    public static List<String> readLines(String path) throws IOException {
        File file = new File(path);
        List<String> lines = new ArrayList<String>();
        if (!file.exists()) {
            Logger.warning("Data file not found: " + path);
            return lines;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty() && !line.startsWith("#")) {
                    lines.add(line);
                }
            }
        }
        Logger.info("Read " + lines.size() + " record(s) from " + path);
        return lines;
    }

    public static void writeLines(String path, List<String> lines) throws IOException {
        File file = new File(path);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, false))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        }
        Logger.info("Wrote " + lines.size() + " record(s) to " + path);
    }

    public static String[] split(String line) {
        return line.split("\\|", -1);
    }

    public static String sanitize(String value) {
        if (value == null || value.trim().isEmpty()) {
            return EMPTY;
        }
        return value.trim().replace(SEPARATOR, "/");
    }

    public static String optional(String value) {
        if (value == null || value.trim().isEmpty() || EMPTY.equals(value.trim())) {
            return null;
        }
        return value.trim();
    }
}
