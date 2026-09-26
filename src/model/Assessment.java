package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

abstract class Assessment {
    private String id;
    private String title;
    private String description;
    private LocalDate deadline;
    private double totalMarks;
    private List<Submission> submissions;

    public Assessment(String id, String title, String description, LocalDate deadline, double totalMarks) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.totalMarks = totalMarks;
        this.submissions = new ArrayList<>();
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDate getDeadline() { return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }
    public double getTotalMarks() { return totalMarks; }
    public void setTotalMarks(double totalMarks) { this.totalMarks = totalMarks; }
    public List<Submission> getSubmissions() { return submissions; }
    public void setSubmissions(List<Submission> submissions) { this.submissions = submissions; }

    public void addSubmission(Submission submission) {
        if (submission != null) {
            submissions.add(submission);
        }
    }

    public boolean isDeadlinePassed() {
        return LocalDate.now().isAfter(deadline);
    }
}