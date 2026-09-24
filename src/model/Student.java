package model;

import java.util.*;

public abstract class Student extends Person {
    private String studentId;
    private int totalCreditHours;
    private List<Enrollment> enrollments;

    public Student(String studentId, String name, String email, String phone) {
        super(name, email, phone);
        this.studentId = studentId;
        this.totalCreditHours = 0;
        this.enrollments = new ArrayList<>();
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public int getTotalCreditHours() {
        return totalCreditHours;
    }

    public void setTotalCreditHours(int totalCreditHours) {
        this.totalCreditHours = totalCreditHours;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void register(Section section) {
        if (section != null) {
            section.enroll(this);
            calculateTotalCreditHours();
        }
    }

    public void dropSection(Section section) {
        if (section != null) {
            section.drop(this);
            calculateTotalCreditHours();
        }
    }

    public int calculateTotalCreditHours() {
        int sum = 0;
        for (Enrollment enrollment : enrollments) {
            if (enrollment != null && enrollment.getSection() != null && enrollment.getSection().getCourse() != null) {
                sum += enrollment.getSection().getCourse().getCreditHours();
            }
        }
        this.totalCreditHours = sum;
        return totalCreditHours;
    }

    public List<Schedule> viewTimetable() {
        List<Schedule> schedules = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            if (enrollment != null && enrollment.getSection() != null && enrollment.getSection().getSchedule() != null) {
                schedules.add(enrollment.getSection().getSchedule());
            }
        }
        return schedules;
    }

    public Submission submitAssignment(Assignment assignment, String content) {
        if (assignment != null) {
            Submission submission = new Submission(assignment, this, content);
            assignment.addSubmission(submission);
            return submission;
        }
        return null;
    }

    public void submitCourseClashRequest(CourseClashRequest request) {
        if (request != null) {
            request.submit();
        }
    }
}