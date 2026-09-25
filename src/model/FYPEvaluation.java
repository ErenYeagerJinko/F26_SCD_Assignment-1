package model;

import java.time.LocalDate;

class FYPEvaluation {
    private String evaluationId;
    private LocalDate evaluationDate;
    private double score;
    private String feedback;

    public String getEvaluationId() { return evaluationId; }
    public void setEvaluationId(String evaluationId) { this.evaluationId = evaluationId; }
    public LocalDate getEvaluationDate() { return evaluationDate; }
    public void setEvaluationDate(LocalDate evaluationDate) { this.evaluationDate = evaluationDate; }
    public double getScore() { return score; }
    public void setScore(double score) { this.score = score; }
    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }
}