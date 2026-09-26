package util;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import exceptions.*;
import model.*;

public class FeedbackRepository {
    private static final String PATH = "data/feedback.txt";
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static Map<String, Feedback> load(Map<String, Evaluator> evaluators) throws IOException, InvalidUserDataException {
        Map<String, Feedback> feedbacks = new LinkedHashMap<>();
        List<String> lines = DelimitedFiles.readLines(PATH);
        for (String line : lines) {
            String[] parts = DelimitedFiles.split(line);
            if (parts.length < 5) {
                Logger.warning("Skipping malformed feedback record: " + line);
                continue;
            }
            String id = parts[0].trim();
            Evaluator evaluator = evaluators == null ? null : evaluators.get(DelimitedFiles.optional(parts[1]));
            String comments = DelimitedFiles.optional(parts[2]);
            LocalDate date;
            try {
                date = LocalDate.parse(parts[3].trim(), DATE);
            } catch (Exception ex) {
                Logger.warning("Skipping feedback " + id + ": invalid date");
                continue;
            }

            Feedback feedback = new Feedback(id, comments != null ? comments : "", date);
            feedback.setEvaluator(evaluator);
            feedbacks.put(id, feedback);
        }
        Logger.info("Loaded " + feedbacks.size() + " feedback(s)");
        return feedbacks;
    }

    public static void save(Collection<Feedback> feedbacks) throws IOException {
        List<String> lines = new ArrayList<>();
        if (feedbacks != null) {
            for (Feedback f : feedbacks) {
                if (f == null) continue;
                String evaluatorId = f.getEvaluator() == null ? DelimitedFiles.EMPTY : 
                    (f.getEvaluator() instanceof model.TeachingAssistant ? ((model.TeachingAssistant) f.getEvaluator()).getStudentId() :
                     f.getEvaluator() instanceof model.PermanentInstructor ? ((model.PermanentInstructor) f.getEvaluator()).getTeacherId() : DelimitedFiles.EMPTY);
                String comments = DelimitedFiles.sanitize(f.getComments());
                lines.add(f.getFeedbackId() + DelimitedFiles.SEPARATOR
                        + evaluatorId + DelimitedFiles.SEPARATOR
                        + comments + DelimitedFiles.SEPARATOR
                        + f.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
            }
        }
        DelimitedFiles.writeLines(PATH, lines);
    }
}