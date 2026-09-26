package model;

import java.time.LocalDate;
import util.Logger;

public class Assignment extends Assessment {
    private Section section;
    private TeachingAssistant createdBy;

    public Assignment(String id, String title, String description, LocalDate deadline, double totalMarks) {
        super(id, title, description, deadline, totalMarks);
        Logger.info("Assignment created: " + id + " (" + title + ")");
    }

    public Assignment(String id, String title, String description, LocalDate deadline, double totalMarks, Section section, TeachingAssistant createdBy) {
        super(id, title, description, deadline, totalMarks);
        this.section = section;
        this.createdBy = createdBy;
        Logger.info("Assignment created: " + id + " (" + title + ") for section " + (section != null ? section.getSectionId() : "null") + " by TA " + (createdBy != null ? createdBy.getStudentId() : "null"));
    }

    public Section getSection() { return section; }
    public void setSection(Section section) { this.section = section; }
    public TeachingAssistant getCreatedBy() { return createdBy; }
    public void setCreatedBy(TeachingAssistant createdBy) { this.createdBy = createdBy; }

    @Override
    public void addSubmission(Submission submission) {
        if (submission != null && submission.getAssignment() == this) {
            super.addSubmission(submission);
            Logger.info("Assignment " + getId() + ": submission " + submission.getSubmissionId() + " added (validated)");
        } else if (submission != null) {
            Logger.warning("Assignment " + getId() + ": rejected submission " + submission.getSubmissionId() + " (assignment mismatch)");
        }
    }
}