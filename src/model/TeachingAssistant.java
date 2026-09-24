package model;

import java.util.*;
import java.time.*;

public class TeachingAssistant extends Student {
    private Section assignedSection;

    public TeachingAssistant(String studentId, String name, String email, String phone) {
        super(studentId, name, email, phone);
    }

    public TeachingAssistant(String studentId, String name, String email, String phone, Section assignedSection) {
        super(studentId, name, email, phone);
        this.assignedSection = assignedSection;
    }

    public Section getAssignedSection() {
        return assignedSection;
    }

    public void setAssignedSection(Section assignedSection) {
        this.assignedSection = assignedSection;
    }

    public Assignment createAssignment(String id, String title, String description, LocalDate deadline, double totalMarks) {
        if (assignedSection != null) {
            Assignment assignment = new Assignment(id, title, description, deadline, totalMarks, assignedSection, this);
            return assignment;
        }
        return null;
    }

    public List<Submission> viewSubmissions(Assignment assignment) {
        if (assignment != null) {
            return assignment.getSubmissions();
        }
        return new ArrayList<>();
    }

    public void evaluateSubmission(Submission submission, double marks) {
        if (submission != null) {
            submission.assignMarks(marks);
        }
    }

    public void giveFeedback(Submission submission, String comments) {
        if (submission != null) {
            Feedback feedback = new Feedback("FB-" + System.currentTimeMillis(), comments, LocalDate.now());
            submission.addFeedback(feedback);
        }
    }

    @Override
    public String getRole() {
        return "Teaching Assistant";
    }
}