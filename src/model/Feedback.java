package model;

import java.time.LocalDate;
import util.Logger;

public class Feedback {
    private String feedbackId;
    private Evaluator evaluator;
    private String comments;
    private LocalDate date;

    public Feedback(String feedbackId, String comments, LocalDate date) {
        this.feedbackId = feedbackId;
        this.comments = comments;
        this.date = date;
        Logger.info("Feedback created: " + feedbackId + " on " + date);
    }

    public String getFeedbackId() { return feedbackId; }
    public void setFeedbackId(String feedbackId) { this.feedbackId = feedbackId; }
    public Evaluator getEvaluator() { return evaluator; }
    public void setEvaluator(Evaluator evaluator) { this.evaluator = evaluator; }
    public String getComments() { return comments; }
    public void setComments(String comments) { this.comments = comments; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
}