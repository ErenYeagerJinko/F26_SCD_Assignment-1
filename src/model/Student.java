package model;

import java.io.*;
import java.util.*;
import exceptions.*;
import util.*;

public abstract class Student extends Person {
    private String studentId;
    private int totalCreditHours;
    private List<Enrollment> enrollments;

    public Student(String studentId, String name, String email, String phone) throws InvalidUserDataException {
        super(name, email, phone);
        if (studentId == null || studentId.trim().isEmpty()) {
            Logger.error("Failed to create Student: Student ID cannot be null or empty");
            throw new InvalidUserDataException("Student ID cannot be null or empty.");
        }
        this.studentId = studentId.trim();
        this.totalCreditHours = 0;
        this.enrollments = new ArrayList<>();
        Logger.info("Student initialized with ID: " + this.studentId);
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) throws InvalidUserDataException {
        if (studentId == null || studentId.trim().isEmpty()) {
            Logger.error("Failed to update Student ID: ID cannot be null or empty");
            throw new InvalidUserDataException("Student ID cannot be null or empty.");
        }
        String oldId = this.studentId;
        this.studentId = studentId.trim();
        Logger.info("Updated Student ID from '" + oldId + "' to '" + this.studentId + "'");
    }

    public int getTotalCreditHours() {
        return totalCreditHours;
    }

    public void setTotalCreditHours(int totalCreditHours) throws InvalidUserDataException {
        if (totalCreditHours < 0) {
            Logger.error("Failed to set total credit hours: Negative value " + totalCreditHours);
            throw new InvalidUserDataException("Total credit hours cannot be negative.");
        }
        this.totalCreditHours = totalCreditHours;
        Logger.info("Updated total credit hours for student " + studentId + " to: " + totalCreditHours);
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void register(Section section) throws CourseFullException, CourseClashException, InvalidUserDataException {
        if (section == null) {
            Logger.error("Registration failed for student " + studentId + ": Section is null");
            throw new InvalidUserDataException("Section cannot be null for registration.");
        }

        Logger.info("Student " + studentId + " attempting registration for section " + section.getSectionId());

        // Check for timetable clash with existing enrollments
        for (Enrollment e : enrollments) {
            if (e != null && e.getSection() != null && e.getSection().hasClash(section)) {
                Logger.error("Registration failed for student " + studentId + ": Timetable clash detected with section " + e.getSection().getSectionId());
                throw new CourseClashException("Timetable clash detected between section " + section.getSectionId() + " and enrolled section " + e.getSection().getSectionId());
            }
        }

        section.enroll(this);
        calculateTotalCreditHours();
        Logger.info("Student " + studentId + " successfully registered for section " + section.getSectionId());
    }

    public void dropSection(Section section) throws InvalidUserDataException {
        if (section == null) {
            Logger.error("Drop section failed for student " + studentId + ": Section is null");
            throw new InvalidUserDataException("Section cannot be null.");
        }

        Logger.info("Student " + studentId + " dropping section " + section.getSectionId());
        section.drop(this);
        calculateTotalCreditHours();
        Logger.info("Student " + studentId + " successfully dropped section " + section.getSectionId());
    }

    public int calculateTotalCreditHours() {
        int sum = 0;
        for (Enrollment enrollment : enrollments) {
            if (enrollment != null && enrollment.getSection() != null && enrollment.getSection().getCourse() != null) {
                sum += enrollment.getSection().getCourse().getCreditHours();
            }
        }
        this.totalCreditHours = sum;
        Logger.info("Student " + studentId + " recalculated total credit hours: " + this.totalCreditHours);
        return totalCreditHours;
    }

    public List<Schedule> viewTimetable() {
        Logger.info("Student " + studentId + " viewing timetable");
        List<Schedule> schedules = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            if (enrollment != null && enrollment.getSection() != null && enrollment.getSection().getSchedule() != null) {
                schedules.add(enrollment.getSection().getSchedule());
            }
        }
        return schedules;
    }

    public Submission submitAssignment(Assignment assignment, String content) throws SubmissionDeadlineException, InvalidUserDataException {
        if (assignment == null) {
            Logger.error("Submission failed for student " + studentId + ": Assignment is null");
            throw new InvalidUserDataException("Assignment cannot be null.");
        }
        if (content == null || content.trim().isEmpty()) {
            Logger.error("Submission failed for student " + studentId + ": Submission content is empty");
            throw new InvalidUserDataException("Submission content cannot be empty.");
        }

        Logger.info("Student " + studentId + " submitting assignment ID: " + assignment.getId());

        if (assignment.isDeadlinePassed()) {
            Logger.error("Submission rejected for student " + studentId + ": Deadline has passed for assignment " + assignment.getId());
            throw new SubmissionDeadlineException("Cannot submit assignment " + assignment.getId() + ": deadline has passed.");
        }

        Submission submission = new Submission(assignment, this, content);
        assignment.addSubmission(submission);
        Logger.info("Student " + studentId + " successfully submitted assignment ID: " + assignment.getId());
        return submission;
    }

    public void submitCourseClashRequest(CourseClashRequest request) throws InvalidRequestException {
        if (request == null) {
            Logger.error("Failed to submit clash request for student " + studentId + ": Request is null");
            throw new InvalidRequestException("Course clash request cannot be null.");
        }

        Logger.info("Student " + studentId + " submitting course clash request: " + request.getRequestId());
        request.submit();
        Logger.info("Course clash request " + request.getRequestId() + " submitted successfully");
    }
}