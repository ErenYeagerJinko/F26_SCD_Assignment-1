package util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import exceptions.InvalidUserDataException;
import model.VisitingInstructor;

public class VisitingInstructorRepository {
    private static final String PATH = "data/visiting_instructors.txt";

    public static Map<String, VisitingInstructor> load() throws IOException, InvalidUserDataException {
        Map<String, VisitingInstructor> instructors = new HashMap<>();
        List<String> lines = DelimitedFiles.readLines(PATH);
        for (String line : lines) {
            String[] parts = DelimitedFiles.split(line);
            if (parts.length < 4) {
                Logger.warning("Skipping malformed visiting instructor record: " + line);
                continue;
            }
            
            String id = parts[0].trim();
            String name = parts[1].trim();
            String email = parts[2].trim();
            String phone = parts[3].trim();

            try {
                VisitingInstructor instructor = new VisitingInstructor(id, name, email, phone);
                instructors.put(id, instructor);
            } catch (InvalidUserDataException e) {
                Logger.warning("Failed to load visiting instructor " + id + ": " + e.getMessage());
            }
        }
        Logger.info("Loaded " + instructors.size() + " visiting instructor(s)");
        return instructors;
    }

    public static void save(Collection<VisitingInstructor> instructors) throws IOException {
        List<String> lines = new ArrayList<>();
        if (instructors != null) {
            for (VisitingInstructor i : instructors) {
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
