package model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import enums.AttendanceStatus;
import enums.EnrollmentStatus;
import exceptions.InvalidUserDataException;
import exceptions.CourseFullException;
import exceptions.CourseClashException;
import exceptions.SubmissionDeadlineException;
import exceptions.InvalidRequestException;
import util.Logger;

public abstract class Student extends Person {
    private String studentId;
    private int totalCreditHours;
    private List<Enrollment> enrollments;
    private List<Request> myRequests;

    public Student(String studentId, String name, String email, String phone) throws InvalidUserDataException {
        super(name, email, phone);
        if (studentId == null || studentId.trim().isEmpty()) {
            Logger.error("Failed to create Student: Student ID cannot be null or empty");
            throw new InvalidUserDataException("Student ID cannot be null or empty.");
        }
        this.studentId = studentId.trim();
        this.totalCreditHours = 0;
        this.enrollments = new ArrayList<>();
        this.myRequests = new ArrayList<>();
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

    public void drop(Section section) throws InvalidUserDataException {
        dropSection(section);
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

    public List<Course> viewCourses() {
        Logger.info("Student " + studentId + " viewing enrolled courses");
        List<Course> courses = new ArrayList<>();
        for (Enrollment enrollment : enrollments) {
            if (enrollment != null && enrollment.getSection() != null && enrollment.getSection().getCourse() != null) {
                Course course = enrollment.getSection().getCourse();
                if (!courses.contains(course)) {
                    courses.add(course);
                }
            }
        }
        return courses;
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

    public List<Request> viewRequests() {
        Logger.info("Student " + studentId + " viewing submitted requests (count: " + myRequests.size() + ")");
        return myRequests;
    }

    public List<Attendance> viewAttendance(Section section) {
        if (section == null) {
            return Collections.emptyList();
        }
        Logger.info("Student " + studentId + " viewing attendance for section " + section.getSectionId());
        return section.getAttendanceRecordsForStudent(this);
    }

    public double viewAttendancePercentage(Section section) {
        if (section == null) {
            return 0.0;
        }
        List<Attendance> records = section.getAttendanceRecordsForStudent(this);
        if (records == null || records.isEmpty()) {
            return 0.0;
        }
        int presentCount = 0;
        for (Attendance att : records) {
            if (att != null && att.getStatus() == AttendanceStatus.PRESENT) {
                presentCount++;
            }
        }
        double pct = ((double) presentCount / records.size()) * 100.0;
        Logger.info("Student " + studentId + " calculated attendance percentage for section "
                + section.getSectionId() + ": " + String.format("%.2f", pct) + "%");
        return pct;
    }

    public List<Assignment> viewAssignments() {
        Logger.info("Student " + studentId + " viewing all assignments for enrolled sections");
        List<Assignment> all = new ArrayList<>();
        for (Enrollment e : enrollments) {
            if (e != null && e.getStatus() == EnrollmentStatus.ACTIVE && e.getSection() != null) {
                all.addAll(e.getSection().getAssignments());
            }
        }
        return all;
    }

    public List<Assignment> viewAssignments(Section section) {
        if (section == null) {
            return Collections.emptyList();
        }
        Logger.info("Student " + studentId + " viewing assignments for section " + section.getSectionId());
        return section.getAssignments();
    }

    public void submitCourseClashRequest(CourseClashRequest request) throws InvalidRequestException {
        if (request == null) {
            Logger.error("Failed to submit clash request for student " + studentId + ": Request is null");
            throw new InvalidRequestException("Course clash request cannot be null.");
        }
        if (request.getConflictingSection() == null || request.getRequestedSection() == null) {
            Logger.error("Clash request failed for student " + studentId + ": Conflicting or requested section is null");
            throw new InvalidRequestException("Conflicting section and requested section cannot be null.");
        }
        if (!request.getConflictingSection().hasClash(request.getRequestedSection())) {
            Logger.error("Clash request rejected for student " + studentId + ": Sections "
                    + request.getConflictingSection().getSectionId() + " and "
                    + request.getRequestedSection().getSectionId() + " do not have a schedule clash");
            throw new InvalidRequestException("Cannot submit clash request: sections do not have a timetable clash.");
        }

        Logger.info("Student " + studentId + " submitting verified course clash request: " + request.getRequestId());
        request.submit();
        if (!myRequests.contains(request)) {
            myRequests.add(request);
        }
        Logger.info("Course clash request " + request.getRequestId() + " submitted successfully");
    }

    public void submitRequest(Request request) throws InvalidRequestException {
        if (request == null) {
            Logger.error("Failed to submit request for student " + studentId + ": Request is null");
            throw new InvalidRequestException("Request cannot be null.");
        }
        if (request instanceof CourseClashRequest) {
            submitCourseClashRequest((CourseClashRequest) request);
            return;
        }
        request.submit();
        if (!myRequests.contains(request)) {
            myRequests.add(request);
        }
        Logger.info("Student " + studentId + " submitted request ID: " + request.getRequestId());
    }
}