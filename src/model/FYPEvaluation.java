package model;

import java.time.LocalDate;
import util.Logger;
import exceptions.InvalidFYPEvaluationException;

public class FYPEvaluation {
    private String evaluationId;
    private LocalDate evaluationDate;
    private double score;
    private String feedback;

    public FYPEvaluation(String evaluationId, LocalDate evaluationDate, double score, String feedback) {
        this.evaluationId = evaluationId;
        this.evaluationDate = evaluationDate;
        this.score = score;
        this.feedback = feedback;
        Logger.info("FYPEvaluation created: " + evaluationId + " with score " + score);
    }

    public String getEvaluationId() { return evaluationId; }
    public void setEvaluationId(String evaluationId) { this.evaluationId = evaluationId; }
    public LocalDate getEvaluationDate() { return evaluationDate; }
    public void setEvaluationDate(LocalDate evaluationDate) { this.evaluationDate = evaluationDate; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public void evaluate(double score) throws InvalidFYPEvaluationException {
        if (score < 0 || score > 100) {
            Logger.error("FYPEvaluation " + evaluationId + ": invalid score " + score + " (must be 0-100)");
            throw new InvalidFYPEvaluationException("Invalid score: " + score + " (must be between 0 and 100)");
        }
        this.score = score;
        this.evaluationDate = LocalDate.now();
        Logger.info("FYPEvaluation " + evaluationId + ": evaluated with score " + score);
    }

    public void addFeedback(String feedback) {
        this.feedback = feedback;
        Logger.info("FYPEvaluation " + evaluationId + ": feedback added");
    }
}