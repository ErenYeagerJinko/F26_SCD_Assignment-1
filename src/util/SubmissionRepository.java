package util;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import enums.SubmissionStatus;
import exceptions.*;
import model.*;

public class SubmissionRepository {
    private static final String PATH = "data/submissions.txt";
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static Map<String, Submission> load(Map<String, Assignment> assignments, Map<String, Student> students, Map<String, Feedback> feedbacks) throws IOException, InvalidUserDataException {
        Map<String, Submission> submissions = new LinkedHashMap<>();
        List<String> lines = DelimitedFiles.readLines(PATH);
        for (String line : lines) {
            String[] parts = DelimitedFiles.split(line);
            if (parts.length < 9) {
                Logger.warning("Skipping malformed submission record: " + line);
                continue;
            }
            String id = parts[0].trim();
            Assignment assignment = assignments == null ? null : assignments.get(DelimitedFiles.optional(parts[1]));
            Student student = students == null ? null : students.get(DelimitedFiles.optional(parts[2]));
            LocalDate submissionDate;
            try {
                submissionDate = LocalDate.parse(parts[3].trim(), DATE);
            } catch (Exception ex) {
                Logger.warning("Skipping submission " + id + ": invalid submission date");
                continue;
            }
            String content = DelimitedFiles.optional(parts[4]);
            double marks;
            try {
                marks = Double.parseDouble(parts[5].trim());
            } catch (NumberFormatException ex) {
                marks = 0.0;
            }
            Feedback feedback = feedbacks == null ? null : feedbacks.get(DelimitedFiles.optional(parts[6]));
            SubmissionStatus status;
            try {
                status = SubmissionStatus.valueOf(parts[7].trim().toUpperCase());
            } catch (Exception ex) {
                status = SubmissionStatus.PENDING;
            }

            if (assignment == null || student == null) {
                Logger.warning("Skipping submission " + id + ": missing assignment or student");
                continue;
            }

            Submission submission = new Submission(assignment, student, content != null ? content : "");
            submission.setSubmissionId(id);
            submission.setSubmissionDate(submissionDate);
            submission.setMarks(marks);
            submission.setFeedback(feedback);
            submission.setStatus(status);
            submissions.put(id, submission);
        }
        Logger.info("Loaded " + submissions.size() + " submission(s)");
        return submissions;
    }

    public static void save(Collection<Submission> submissions) throws IOException {
        List<String> lines = new ArrayList<>();
        if (submissions != null) {
            for (Submission s : submissions) {
                if (s == null) continue;
                String assignmentId = s.getAssignment() == null ? DelimitedFiles.EMPTY : s.getAssignment().getId();
                String studentId = s.getStudent() == null ? DelimitedFiles.EMPTY : s.getStudent().getStudentId();
                String content = DelimitedFiles.sanitize(s.getContent());
                String feedbackId = s.getFeedback() == null ? DelimitedFiles.EMPTY : s.getFeedback().getFeedbackId();
                String status = s.getStatus() == null ? DelimitedFiles.EMPTY : s.getStatus().name();
                lines.add(s.getSubmissionId() + DelimitedFiles.SEPARATOR
                        + assignmentId + DelimitedFiles.SEPARATOR
                        + studentId + DelimitedFiles.SEPARATOR
                        + s.getSubmissionDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + DelimitedFiles.SEPARATOR
                        + content + DelimitedFiles.SEPARATOR
                        + s.getMarks() + DelimitedFiles.SEPARATOR
                        + feedbackId + DelimitedFiles.SEPARATOR
                        + status);
            }
        }
        DelimitedFiles.writeLines(PATH, lines);
    }
}