package util;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import exceptions.*;
import model.*;

public class AssignmentRepository {
    private static final String PATH = "data/assignments.txt";
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static Map<String, Assignment> load(Map<String, Section> sections, Map<String, TeachingAssistant> tas) throws IOException, InvalidUserDataException {
        Map<String, Assignment> assignments = new LinkedHashMap<>();
        List<String> lines = DelimitedFiles.readLines(PATH);
        for (String line : lines) {
            String[] parts = DelimitedFiles.split(line);
            if (parts.length < 7) {
                Logger.warning("Skipping malformed assignment record: " + line);
                continue;
            }
            String id = parts[0].trim();
            String title = parts[1].trim();
            String description = DelimitedFiles.optional(parts[2]);
            LocalDate deadline;
            try {
                deadline = LocalDate.parse(parts[3].trim(), DATE);
            } catch (Exception ex) {
                Logger.warning("Skipping assignment " + id + ": invalid deadline");
                continue;
            }
            double totalMarks;
            try {
                totalMarks = Double.parseDouble(parts[4].trim());
            } catch (NumberFormatException ex) {
                Logger.warning("Skipping assignment " + id + ": invalid total marks");
                continue;
            }
            Section section = sections == null ? null : sections.get(DelimitedFiles.optional(parts[5]));
            TeachingAssistant createdBy = tas == null ? null : tas.get(DelimitedFiles.optional(parts[6]));

            if (section == null) {
                Logger.warning("Skipping assignment " + id + ": unknown section");
                continue;
            }

            Assignment assignment = new Assignment(id, title, description != null ? description : "", deadline, totalMarks, section, createdBy);
            assignments.put(id, assignment);
        }
        Logger.info("Loaded " + assignments.size() + " assignment(s)");
        return assignments;
    }

    public static void save(Collection<Assignment> assignments) throws IOException {
        List<String> lines = new ArrayList<>();
        if (assignments != null) {
            for (Assignment a : assignments) {
                if (a == null) continue;
                String sectionId = a.getSection() == null ? DelimitedFiles.EMPTY : a.getSection().getSectionId();
                String taId = a.getCreatedBy() == null ? DelimitedFiles.EMPTY : a.getCreatedBy().getStudentId();
                String description = DelimitedFiles.sanitize(a.getDescription());
                lines.add(a.getId() + DelimitedFiles.SEPARATOR
                        + DelimitedFiles.sanitize(a.getTitle()) + DelimitedFiles.SEPARATOR
                        + description + DelimitedFiles.SEPARATOR
                        + a.getDeadline().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + DelimitedFiles.SEPARATOR
                        + a.getTotalMarks() + DelimitedFiles.SEPARATOR
                        + sectionId + DelimitedFiles.SEPARATOR
                        + taId);
            }
        }
        DelimitedFiles.writeLines(PATH, lines);
    }
}