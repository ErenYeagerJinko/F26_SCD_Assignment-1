package util;

import java.io.*;
import java.util.*;
import exceptions.*;
import model.Course;

public class CourseRepository {
    private static final String PATH = "data/courses.txt";

    public static Map<String, Course> load() throws IOException, InvalidUserDataException {
        Map<String, Course> courses = new LinkedHashMap<String, Course>();
        List<String> lines = DelimitedFiles.readLines(PATH);
        for (String line : lines) {
            String[] parts = DelimitedFiles.split(line);
            if (parts.length < 3) {
                Logger.warning("Skipping malformed course record: " + line);
                continue;
            }
            String code = parts[0].trim().toUpperCase();
            String title = parts[1].trim();
            int credits;
            try {
                credits = Integer.parseInt(parts[2].trim());
            } catch (NumberFormatException ex) {
                Logger.warning("Skipping course record with invalid credit hours: " + line);
                continue;
            }
            courses.put(code, new Course(code, title, credits));
        }
        for (String line : lines) {
            String[] parts = DelimitedFiles.split(line);
            if (parts.length < 4) {
                continue;
            }
            Course course = courses.get(parts[0].trim().toUpperCase());
            if (course == null) {
                continue;
            }
            String prereqField = DelimitedFiles.optional(parts[3]);
            if (prereqField == null) {
                continue;
            }
            String[] codes = prereqField.split(",");
            for (String raw : codes) {
                Course prerequisite = courses.get(raw.trim().toUpperCase());
                if (prerequisite != null) {
                    course.addPrerequisite(prerequisite);
                } else {
                    Logger.warning("Unknown prerequisite '" + raw + "' for course " + course.getCourseCode());
                }
            }
        }
        Logger.info("Loaded " + courses.size() + " course(s)");
        return courses;
    }

    public static void save(Collection<Course> courses) throws IOException {
        List<String> lines = new ArrayList<String>();
        if (courses != null) {
            for (Course course : courses) {
                if (course == null) {
                    continue;
                }
                StringBuilder prereqs = new StringBuilder();
                boolean first = true;
                for (Course prerequisite : course.getPrerequisites()) {
                    if (prerequisite == null) {
                        continue;
                    }
                    if (!first) {
                        prereqs.append(",");
                    }
                    prereqs.append(prerequisite.getCourseCode());
                    first = false;
                }
                String prereqValue = prereqs.length() == 0 ? DelimitedFiles.EMPTY : prereqs.toString();
                lines.add(course.getCourseCode() + DelimitedFiles.SEPARATOR
                        + DelimitedFiles.sanitize(course.getTitle()) + DelimitedFiles.SEPARATOR
                        + course.getCreditHours() + DelimitedFiles.SEPARATOR
                        + prereqValue);
            }
        }
        DelimitedFiles.writeLines(PATH, lines);
    }
}
