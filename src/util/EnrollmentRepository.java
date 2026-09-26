package util;

import java.io.*;
import java.time.*;
import java.util.*;
import enums.*;
import exceptions.*;
import model.*;

public class EnrollmentRepository {
    private static final String PATH = "data/enrollments.txt";

    public static List<Enrollment> load(Map<String, Student> students, Map<String, Section> sections)
            throws IOException, InvalidUserDataException {
        List<Enrollment> enrollments = new ArrayList<Enrollment>();
        List<String> lines = DelimitedFiles.readLines(PATH);
        for (String line : lines) {
            String[] parts = DelimitedFiles.split(line);
            if (parts.length < 5) {
                Logger.warning("Skipping malformed enrollment record: " + line);
                continue;
            }
            String enrollmentId = parts[0].trim();
            Student student = students == null ? null : students.get(parts[1].trim());
            Section section = sections == null ? null : sections.get(parts[2].trim().toUpperCase());
            if (student == null || section == null) {
                Logger.warning("Skipping enrollment " + enrollmentId + ": student or section was not found");
                continue;
            }
            LocalDate date = LocalDate.parse(parts[3].trim());
            EnrollmentStatus status = EnrollmentStatus.valueOf(parts[4].trim().toUpperCase());
            Enrollment enrollment = new Enrollment(enrollmentId, student, section, date);
            if (status != EnrollmentStatus.ACTIVE) {
                enrollment.setStatus(status);
            }
            if (status == EnrollmentStatus.ACTIVE) {
                section.restoreEnrollment(enrollment);
            }
            enrollments.add(enrollment);
        }
        Logger.info("Loaded " + enrollments.size() + " enrollment(s)");
        return enrollments;
    }

    public static void save(Collection<Enrollment> enrollments) throws IOException {
        List<String> lines = new ArrayList<String>();
        if (enrollments != null) {
            for (Enrollment enrollment : enrollments) {
                if (enrollment == null || enrollment.getStudent() == null || enrollment.getSection() == null) {
                    continue;
                }
                lines.add(enrollment.getEnrollmentId() + DelimitedFiles.SEPARATOR
                        + enrollment.getStudent().getStudentId() + DelimitedFiles.SEPARATOR
                        + enrollment.getSection().getSectionId() + DelimitedFiles.SEPARATOR
                        + enrollment.getEnrollmentDate() + DelimitedFiles.SEPARATOR
                        + enrollment.getStatus().name());
            }
        }
        DelimitedFiles.writeLines(PATH, lines);
    }

    public static void saveFromSections(Collection<Section> sections) throws IOException {
        List<Enrollment> enrollments = new ArrayList<Enrollment>();
        if (sections != null) {
            for (Section section : sections) {
                if (section != null && section.getEnrollments() != null) {
                    enrollments.addAll(section.getEnrollments());
                }
            }
        }
        save(enrollments);
    }
}
