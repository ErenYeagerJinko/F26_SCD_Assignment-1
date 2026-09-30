package util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import exceptions.InvalidUserDataException;
import model.Person;
import model.AcademicOfficeAdmin;
import model.NormalStudent;
import model.TeachingAssistant;
import model.PermanentInstructor;
import model.VisitingInstructor;

public class PersonRepository {
    private static final String PATH = "data/persons.txt";

    public static Map<String, Person> load() throws IOException, InvalidUserDataException {
        Map<String, Person> persons = new HashMap<>();
        List<String> lines = DelimitedFiles.readLines(PATH);
        for (String line : lines) {
            String[] parts = DelimitedFiles.split(line);
            if (parts.length < 5) {
                Logger.warning("Skipping malformed person record: " + line);
                continue;
            }
            
            String type = parts[0].trim();
            String id = parts[1].trim();
            String name = parts[2].trim();
            String email = parts[3].trim();
            String phone = parts[4].trim();

            Person person = null;
            try {
                switch (type) {
                    case "AcademicOfficeAdmin":
                        person = new AcademicOfficeAdmin(id, name, email, phone);
                        break;
                    case "NormalStudent":
                        person = new NormalStudent(id, name, email, phone);
                        break;
                    case "TeachingAssistant":
                        person = new TeachingAssistant(id, name, email, phone);
                        break;
                    case "PermanentInstructor":
                        person = new PermanentInstructor(id, name, email, phone);
                        break;
                    case "VisitingInstructor":
                        person = new VisitingInstructor(id, name, email, phone);
                        break;
                    default:
                        Logger.warning("Unknown person type: " + type);
                        continue;
                }
            } catch (InvalidUserDataException e) {
                Logger.warning("Failed to load person " + id + ": " + e.getMessage());
                continue;
            }

            if (person != null) {
                persons.put(id, person);
            }
        }
        Logger.info("Loaded " + persons.size() + " person(s)");
        return persons;
    }

    public static void save(Collection<Person> persons) throws IOException {
        List<String> lines = new ArrayList<>();
        if (persons != null) {
            for (Person p : persons) {
                if (p == null) continue;
                String type = p.getClass().getSimpleName();
                String id = "";
                if (p instanceof model.Administrator) {
                    id = ((model.Administrator) p).getAdminId();
                } else if (p instanceof model.Student) {
                    id = ((model.Student) p).getStudentId();
                } else if (p instanceof model.Instructor) {
                    id = ((model.Instructor) p).getTeacherId();
                }

                lines.add(type + DelimitedFiles.SEPARATOR
                        + id + DelimitedFiles.SEPARATOR
                        + DelimitedFiles.sanitize(p.getName()) + DelimitedFiles.SEPARATOR
                        + DelimitedFiles.sanitize(p.getEmail()) + DelimitedFiles.SEPARATOR
                        + DelimitedFiles.sanitize(p.getPhone()));
            }
        }
        DelimitedFiles.writeLines(PATH, lines);
    }
}
