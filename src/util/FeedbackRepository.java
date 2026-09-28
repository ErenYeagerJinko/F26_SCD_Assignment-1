package util;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import exceptions.InvalidUserDataException;
import model.Evaluator;
import model.Feedback;
import model.TeachingAssistant;
import model.PermanentInstructor;

public class FeedbackRepository {
    private static final String PATH = "data/feedback.txt";
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static Map<String, Feedback> load(Map<String, Evaluator> evaluators) throws IOException, InvalidUserDataException {
        Map<String, Feedback> feedbacks = new HashMap<>();
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

            String commentsStr = "";
            if (comments != null) {
                commentsStr = comments;
            }
            Feedback feedback = new Feedback(id, commentsStr, date);
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

                String evaluatorId = DelimitedFiles.EMPTY;
                if (f.getEvaluator() != null) {
                    if (f.getEvaluator() instanceof TeachingAssistant) {
                        evaluatorId = ((TeachingAssistant) f.getEvaluator()).getStudentId();
                    } else if (f.getEvaluator() instanceof PermanentInstructor) {
                        evaluatorId = ((PermanentInstructor) f.getEvaluator()).getTeacherId();
                    }
                }

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