package model;

import java.time.LocalDate;
import enums.SubmissionStatus;

class Submission {
    private String submissionId;
    private Assignment assignment;
    private Student student;
    private LocalDate submissionDate;
    private String content;
    private double marks;
    private Feedback feedback;
    private SubmissionStatus status;

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

    public void submit() {
        this.submissionDate = LocalDate.now();
        this.status = SubmissionStatus.SUBMITTED;
    }

    public boolean isLate() {
        return assignment != null && submissionDate != null && submissionDate.isAfter(assignment.getDeadline());
    }

    public void assignMarks(double marks) {
        this.marks = marks;
        this.status = SubmissionStatus.EVALUATED;
    }

    public void addFeedback(Feedback feedback) {
        this.feedback = feedback;
    }
}