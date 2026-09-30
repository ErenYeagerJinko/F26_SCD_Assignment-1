package util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import exceptions.InvalidUserDataException;
import model.PermanentInstructor;

public class PermanentInstructorRepository {
    private static final String PATH = "data/permanent_instructors.txt";

    public static Map<String, PermanentInstructor> load() throws IOException, InvalidUserDataException {
        Map<String, PermanentInstructor> instructors = new HashMap<>();
        List<String> lines = DelimitedFiles.readLines(PATH);
        for (String line : lines) {
            String[] parts = DelimitedFiles.split(line);
            if (parts.length < 4) {
                Logger.warning("Skipping malformed permanent instructor record: " + line);
                continue;
            }
            
            String id = parts[0].trim();
            String name = parts[1].trim();
            String email = parts[2].trim();
            String phone = parts[3].trim();

            try {
                PermanentInstructor instructor = new PermanentInstructor(id, name, email, phone);
                instructors.put(id, instructor);
            } catch (InvalidUserDataException e) {
                Logger.warning("Failed to load permanent instructor " + id + ": " + e.getMessage());
            }
        }
        Logger.info("Loaded " + instructors.size() + " permanent instructor(s)");
        return instructors;
    }

    public static void save(Collection<PermanentInstructor> instructors) throws IOException {
        List<String> lines = new ArrayList<>();
        if (instructors != null) {
            for (PermanentInstructor i : instructors) {
                if (i == null) continue;
                lines.add(i.getTeacherId() + DelimitedFiles.SEPARATOR
                        + DelimitedFiles.sanitize(i.getName()) + DelimitedFiles.SEPARATOR
                        + DelimitedFiles.sanitize(i.getEmail()) + DelimitedFiles.SEPARATOR
                        + DelimitedFiles.sanitize(i.getPhone()));
            }
        }
        DelimitedFiles.writeLines(PATH, lines);
    }
}
