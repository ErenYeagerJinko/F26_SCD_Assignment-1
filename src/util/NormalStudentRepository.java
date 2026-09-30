package util;

import java.io.*;
import java.util.*;
import exceptions.InvalidUserDataException;
import model.NormalStudent;

public class NormalStudentRepository {
    private static final String PATH = "data/normal_students.txt";

    public static Map<String, NormalStudent> load() throws IOException, InvalidUserDataException {
        Map<String, NormalStudent> students = new HashMap<>();
        List<String> lines = DelimitedFiles.readLines(PATH);
        for (String line : lines) {
            String[] parts = DelimitedFiles.split(line);
            if (parts.length < 4) {
                Logger.warning("Skipping malformed normal student record: " + line);
                continue;
            }
            
            String id = parts[0].trim();
            String name = parts[1].trim();
            String email = parts[2].trim();
            String phone = parts[3].trim();

            try {
                NormalStudent student = new NormalStudent(id, name, email, phone);
                students.put(id, student);
            } catch (InvalidUserDataException e) {
                Logger.warning("Failed to load normal student " + id + ": " + e.getMessage());
            }
        }
        Logger.info("Loaded " + students.size() + " normal student(s)");
        return students;
    }

    public static void save(Collection<NormalStudent> students) throws IOException {
        List<String> lines = new ArrayList<>();
        if (students != null) {
            for (NormalStudent s : students) {
                if (s == null) continue;
                lines.add(s.getStudentId() + DelimitedFiles.SEPARATOR
                        + DelimitedFiles.sanitize(s.getName()) + DelimitedFiles.SEPARATOR
                        + DelimitedFiles.sanitize(s.getEmail()) + DelimitedFiles.SEPARATOR
                        + DelimitedFiles.sanitize(s.getPhone()));
            }
        }
        DelimitedFiles.writeLines(PATH, lines);
    }
}
