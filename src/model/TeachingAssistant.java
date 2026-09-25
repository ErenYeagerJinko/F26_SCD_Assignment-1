package model;

import java.io.*;
import java.util.*;
import java.time.*;
import exceptions.*;
import util.*;

public class TeachingAssistant extends Student {
    private Section assignedSection;

    public TeachingAssistant(String studentId, String name, String email, String phone) throws InvalidUserDataException {
        super(studentId, name, email, phone);
        Logger.info("TeachingAssistant initialized with ID: " + getStudentId());
    }

    public TeachingAssistant(String studentId, String name, String email, String phone, Section assignedSection) throws InvalidUserDataException {
        super(studentId, name, email, phone);
        this.assignedSection = assignedSection;
        Logger.info("TeachingAssistant initialized with ID: " + getStudentId() + " and assigned section: " + (assignedSection != null ? assignedSection.getSectionId() : "none"));
    }

    public Section getAssignedSection() {
        return assignedSection;
    }

    public void setAssignedSection(Section assignedSection) {
        this.assignedSection = assignedSection;
        Logger.info("Assigned section for TA " + getStudentId() + " updated to: " + (assignedSection != null ? assignedSection.getSectionId() : "null"));
    }

    public Assignment createAssignment(String id, String title, String description, LocalDate deadline, double totalMarks)
            throws UnauthorizedActionException, AssessmentException, InvalidUserDataException {
        if (assignedSection == null) {
            Logger.error("TA " + getStudentId() + " attempted to create assignment without being assigned to a section");
            throw new UnauthorizedActionException("TeachingAssistant is not assigned to any section and cannot create assignments.");
        }
        if (id == null || id.trim().isEmpty() || title == null || title.trim().isEmpty()) {
            Logger.error("TA " + getStudentId() + " failed to create assignment: ID or Title is empty");
            throw new InvalidUserDataException("Assignment ID and Title cannot be null or empty.");
        }
        if (totalMarks <= 0) {
            Logger.error("TA " + getStudentId() + " failed to create assignment: Total marks must be positive (" + totalMarks + ")");
            throw new InvalidUserDataException("Total marks must be greater than zero.");
        }
        if (deadline == null) {
            Logger.error("TA " + getStudentId() + " failed to create assignment: Deadline is null");
            throw new InvalidUserDataException("Assignment deadline cannot be null.");
        }

        Logger.info("TA " + getStudentId() + " creating assignment ID: " + id + " ('" + title + "') for section " + assignedSection.getSectionId());
        Assignment assignment = new Assignment(id, title, description, deadline, totalMarks, assignedSection, this);
        Logger.info("Assignment " + id + " successfully created by TA " + getStudentId());
        return assignment;
    }

    public List<Submission> viewSubmissions(Assignment assignment) throws AssessmentException {
        if (assignment == null) {
            Logger.error("TA " + getStudentId() + " failed to view submissions: Assignment is null");
            throw new AssessmentException("Assignment cannot be null.");
        }
        Logger.info("TA " + getStudentId() + " viewing submissions for assignment ID: " + assignment.getId());
        return assignment.getSubmissions();
    }

    public void evaluateSubmission(Submission submission, double marks) throws AssessmentException, InvalidUserDataException {
        if (submission == null) {
            Logger.error("TA " + getStudentId() + " failed to evaluate submission: Submission is null");
            throw new AssessmentException("Submission cannot be null.");
        }
        if (marks < 0) {
            Logger.error("TA " + getStudentId() + " failed to evaluate submission: Negative marks (" + marks + ")");
            throw new InvalidUserDataException("Evaluation marks cannot be negative.");
        }
        Logger.info("TA " + getStudentId() + " evaluating submission ID " + submission.getSubmissionId() + " with marks: " + marks);
        submission.assignMarks(marks);
        Logger.info("Submission ID " + submission.getSubmissionId() + " successfully evaluated by TA " + getStudentId());
    }

    public void giveFeedback(Submission submission, String comments) throws AssessmentException, InvalidUserDataException {
        if (submission == null) {
            Logger.error("TA " + getStudentId() + " failed to give feedback: Submission is null");
            throw new AssessmentException("Submission cannot be null.");
        }
        if (comments == null || comments.trim().isEmpty()) {
            Logger.error("TA " + getStudentId() + " failed to give feedback: Comments are empty");
            throw new InvalidUserDataException("Feedback comments cannot be empty.");
        }

        Logger.info("TA " + getStudentId() + " providing feedback for submission ID: " + submission.getSubmissionId());
        Feedback feedback = new Feedback("FB-" + System.currentTimeMillis(), comments.trim(), LocalDate.now());
        submission.addFeedback(feedback);
        Logger.info("Feedback successfully added to submission ID: " + submission.getSubmissionId());
    }

    @Override
    public String getRole() {
        return "Teaching Assistant";
    }
}