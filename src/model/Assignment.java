package model;

import java.time.LocalDate;

public class Assignment extends Assessment {
    private Section section;
    private TeachingAssistant createdBy;

    public Assignment(String id, String title, String description, LocalDate deadline, double totalMarks) {
        super(id, title, description, deadline, totalMarks);
    }

    public Assignment(String id, String title, String description, LocalDate deadline, double totalMarks, Section section, TeachingAssistant createdBy) {
        super(id, title, description, deadline, totalMarks);
        this.section = section;
        this.createdBy = createdBy;
    }

    public Section getSection() { return section; }
    public void setSection(Section section) { this.section = section; }
    public TeachingAssistant getCreatedBy() { return createdBy; }
    public void setCreatedBy(TeachingAssistant createdBy) { this.createdBy = createdBy; }

    @Override
    public void addSubmission(Submission submission) {
        if (submission != null && submission.getAssignment() == this) {
            super.addSubmission(submission);
        }
    }
}