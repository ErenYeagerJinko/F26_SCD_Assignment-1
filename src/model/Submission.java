package model;

import java.time.LocalDate;
import enums.SubmissionStatus;
import util.Logger;
import exceptions.SubmissionDeadlineException;

public class Submission {
    private String submissionId;
    private Assignment assignment;
    private Student student;
    private LocalDate submissionDate;
    private String content;
    private double marks;
    private Feedback feedback;
    private SubmissionStatus status;

    public Submission(Assignment assignment, Student student, String content) {
        this.assignment = assignment;
        this.student = student;
        this.content = content;
        this.submissionDate = LocalDate.now();
        this.status = SubmissionStatus.PENDING;
        this.submissionId = "SUB-" + System.currentTimeMillis();
        Logger.info("Submission created: " + submissionId + " for assignment " + (assignment != null ? assignment.getId() : "null") + " by student " + (student != null ? student.getStudentId() : "null"));
    }

    public String getSubmissionId() { return submissionId; }
    public void setSubmissionId(String submissionId) { this.submissionId = submissionId; }
    public Assignment getAssignment() { return assignment; }
    public void setAssignment(Assignment assignment) { this.assignment = assignment; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
    public LocalDate getSubmissionDate() { return submissionDate; }
    public void setSubmissionDate(LocalDate submissionDate) { this.submissionDate = submissionDate; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public double getMarks() { return marks; }
    public void setMarks(double marks) { this.marks = marks; }
    public Feedback getFeedback() { return feedback; }
    public void setFeedback(Feedback feedback) { this.feedback = feedback; }
    public SubmissionStatus getStatus() { return status; }
    public void setStatus(SubmissionStatus status) { this.status = status; }

    public void submit() throws SubmissionDeadlineException {
        if (assignment != null && assignment.isDeadlinePassed()) {
            Logger.error("Submission " + submissionId + ": cannot submit, deadline has passed");
            throw new SubmissionDeadlineException("Cannot submit: deadline has passed for assignment " + assignment.getId());
        }
        this.submissionDate = LocalDate.now();
        this.status = SubmissionStatus.SUBMITTED;
        Logger.info("Submission " + submissionId + ": submitted by student " + (student != null ? student.getStudentId() : "null"));
    }

    public boolean isLate() {
        boolean late = assignment != null && submissionDate != null && submissionDate.isAfter(assignment.getDeadline());
        if (late) {
            Logger.warning("Submission " + submissionId + ": submitted late (deadline was " + (assignment != null ? assignment.getDeadline() : "unknown") + ")");
        }
        return late;
    }

    public void assignMarks(double marks) {
        this.marks = marks;
        this.status = SubmissionStatus.EVALUATED;
        Logger.info("Submission " + submissionId + ": marks assigned = " + marks);
    }

    public void addFeedback(Feedback feedback) {
        this.feedback = feedback;
        Logger.info("Submission " + submissionId + ": feedback added");
    }
}