package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import util.Logger;

public class Assignment extends Assessment {
    private Section section;
    private TeachingAssistant createdBy;
    private List<Submission> submissions;

    public Assignment(String id, String title, String description, LocalDate deadline, double totalMarks) {
        super(id, title, description, deadline, totalMarks);
        this.submissions = new ArrayList<>();
        Logger.info("Assignment created: " + id + " (" + title + ")");
    }

    public Assignment(String id, String title, String description, LocalDate deadline, double totalMarks, Section section, TeachingAssistant createdBy) {
        super(id, title, description, deadline, totalMarks);
        this.section = section;
        this.createdBy = createdBy;
        this.submissions = new ArrayList<>();
        Logger.info("Assignment created: " + id + " (" + title + ") for section " + (section != null ? section.getSectionId() : "null") + " by TA " + (createdBy != null ? createdBy.getStudentId() : "null"));
    }

    public Section getSection() { return section; }
    public void setSection(Section section) { this.section = section; }
    public TeachingAssistant getCreatedBy() { return createdBy; }
    public void setCreatedBy(TeachingAssistant createdBy) { this.createdBy = createdBy; }

    public List<Submission> getSubmissions() {
        return submissions;
    }

    public void addSubmission(Submission submission) {
        if (submission != null && submission.getAssignment() == this) {
            submissions.add(submission);
            Logger.info("Assignment " + getId() + ": submission " + submission.getSubmissionId() + " added (validated)");
        } else if (submission != null) {
            Logger.warning("Assignment " + getId() + ": rejected submission " + submission.getSubmissionId() + " (assignment mismatch)");
        }
    }

    public boolean isDeadlinePassed() {
        boolean passed = LocalDate.now().isAfter(getDeadline());
        Logger.info("Assignment " + getId() + ": deadline check = " + passed);
        if (passed) {
            Logger.warning("Assignment " + getId() + ": deadline has passed");
        }
        return passed;
    }
}