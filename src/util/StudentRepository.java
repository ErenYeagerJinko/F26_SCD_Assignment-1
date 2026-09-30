package util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import exceptions.InvalidUserDataException;
import model.Student;
import model.NormalStudent;
import model.TeachingAssistant;

public class StudentRepository {
    private static final String PATH = "data/students.txt";

    public static Map<String, Student> load() throws IOException, InvalidUserDataException {
        Map<String, Student> students = new HashMap<>();
        List<String> lines = DelimitedFiles.readLines(PATH);
        for (String line : lines) {
            String[] parts = DelimitedFiles.split(line);
            if (parts.length < 5) {
                Logger.warning("Skipping malformed student record: " + line);
                continue;
            }
            
            String type = parts[0].trim();
            String id = parts[1].trim();
            String name = parts[2].trim();
            String email = parts[3].trim();
            String phone = parts[4].trim();

            Student student = null;
            try {
                switch (type) {
                    case "NormalStudent":
                        student = new NormalStudent(id, name, email, phone);
                        break;
                    case "TeachingAssistant":
                        student = new TeachingAssistant(id, name, email, phone);
                        break;
                    default:
                        Logger.warning("Unknown student type: " + type);
                        continue;
                }
            } catch (InvalidUserDataException e) {
                Logger.warning("Failed to load student " + id + ": " + e.getMessage());
                continue;
            }

            if (student != null) {
                students.put(id, student);
            }
        }
        Logger.info("Loaded " + students.size() + " student(s)");
        return students;
    }

    public static void save(Collection<Student> students) throws IOException {
        List<String> lines = new ArrayList<>();
        if (students != null) {
            for (Student s : students) {
                if (s == null) continue;
                String type = s.getClass().getSimpleName();
                
                lines.add(type + DelimitedFiles.SEPARATOR
                        + s.getStudentId() + DelimitedFiles.SEPARATOR
                        + DelimitedFiles.sanitize(s.getName()) + DelimitedFiles.SEPARATOR
                        + DelimitedFiles.sanitize(s.getEmail()) + DelimitedFiles.SEPARATOR
                        + DelimitedFiles.sanitize(s.getPhone()));
            }
        }
        DelimitedFiles.writeLines(PATH, lines);
    }
}
