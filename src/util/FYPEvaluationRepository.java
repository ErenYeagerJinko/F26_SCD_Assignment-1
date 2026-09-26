package util;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import exceptions.*;
import model.*;

public class FYPEvaluationRepository {
    private static final String PATH = "data/fyp_evaluations.txt";
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static Map<String, FYPEvaluation> load() throws IOException, InvalidUserDataException {
        Map<String, FYPEvaluation> evaluations = new LinkedHashMap<>();
        List<String> lines = DelimitedFiles.readLines(PATH);
        for (String line : lines) {
            String[] parts = DelimitedFiles.split(line);
            if (parts.length < 5) {
                Logger.warning("Skipping malformed FYP evaluation record: " + line);
                continue;
            }
            String id = parts[0].trim();
            LocalDate date;
            try {
                date = LocalDate.parse(parts[1].trim(), DATE);
            } catch (Exception ex) {
                Logger.warning("Skipping evaluation " + id + ": invalid date");
                continue;
            }
            double score;
            try {
                score = Double.parseDouble(parts[2].trim());
            } catch (NumberFormatException ex) {
                score = 0.0;
            }
            String feedback = DelimitedFiles.optional(parts[3]);

            FYPEvaluation evaluation = new FYPEvaluation(id, date, score, feedback != null ? feedback : "");
            evaluations.put(id, evaluation);
        }
        Logger.info("Loaded " + evaluations.size() + " FYP evaluation(s)");
        return evaluations;
    }

    public static void save(Collection<FYPEvaluation> evaluations) throws IOException {
        List<String> lines = new ArrayList<>();
        if (evaluations != null) {
            for (FYPEvaluation e : evaluations) {
                if (e == null) continue;
                String feedback = DelimitedFiles.sanitize(e.getFeedback());
                lines.add(e.getEvaluationId() + DelimitedFiles.SEPARATOR
                        + e.getEvaluationDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + DelimitedFiles.SEPARATOR
                        + e.getScore() + DelimitedFiles.SEPARATOR
                        + feedback);
            }
        }
        DelimitedFiles.writeLines(PATH, lines);
    }
}