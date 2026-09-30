package util;

import java.io.*;
import java.util.*;
import exceptions.InvalidUserDataException;
import model.TeachingAssistant;

public class TeachingAssistantRepository {
    private static final String PATH = "data/teaching_assistants.txt";

    public static Map<String, TeachingAssistant> load() throws IOException, InvalidUserDataException {
        Map<String, TeachingAssistant> tas = new HashMap<>();
        List<String> lines = DelimitedFiles.readLines(PATH);
        for (String line : lines) {
            String[] parts = DelimitedFiles.split(line);
            if (parts.length < 4) {
                Logger.warning("Skipping malformed teaching assistant record: " + line);
                continue;
            }
            
            String id = parts[0].trim();
            String name = parts[1].trim();
            String email = parts[2].trim();
            String phone = parts[3].trim();

            try {
                TeachingAssistant ta = new TeachingAssistant(id, name, email, phone);
                tas.put(id, ta);
            } catch (InvalidUserDataException e) {
                Logger.warning("Failed to load teaching assistant " + id + ": " + e.getMessage());
            }
        }
        Logger.info("Loaded " + tas.size() + " teaching assistant(s)");
        return tas;
    }

    public static void save(Collection<TeachingAssistant> tas) throws IOException {
        List<String> lines = new ArrayList<>();
        if (tas != null) {
            for (TeachingAssistant ta : tas) {
                if (ta == null) continue;
                lines.add(ta.getStudentId() + DelimitedFiles.SEPARATOR
                        + DelimitedFiles.sanitize(ta.getName()) + DelimitedFiles.SEPARATOR
                        + DelimitedFiles.sanitize(ta.getEmail()) + DelimitedFiles.SEPARATOR
                        + DelimitedFiles.sanitize(ta.getPhone()));
            }
        }
        DelimitedFiles.writeLines(PATH, lines);
    }
}
