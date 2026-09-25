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
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDate getDeadline() { return deadline; }
    public double getTotalMarks() { return totalMarks; }
    public List<Submission> getSubmissions() { return submissions; }
}