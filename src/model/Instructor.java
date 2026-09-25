package model;

import java.io.*;
import java.util.*;
import java.time.*;
import enums.*;
import exceptions.*;
import util.*;

public abstract class Instructor extends Person {
    private String teacherId;
    private List<Section> assignedSections;

    public Instructor(String teacherId, String name, String email, String phone) throws InvalidUserDataException {
        super(name, email, phone);
        if (teacherId == null || teacherId.trim().isEmpty()) {
            Logger.error("Failed to create Instructor: Teacher ID cannot be null or empty");
            throw new InvalidUserDataException("Teacher ID cannot be null or empty.");
        }
        this.teacherId = teacherId.trim();
        this.assignedSections = new ArrayList<>();
        Logger.info("Instructor initialized with Teacher ID: " + this.teacherId);
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) throws InvalidUserDataException {
        if (teacherId == null || teacherId.trim().isEmpty()) {
            Logger.error("Failed to update Teacher ID: ID cannot be null or empty");
            throw new InvalidUserDataException("Teacher ID cannot be null or empty.");
        }
        String oldId = this.teacherId;
        this.teacherId = teacherId.trim();
        Logger.info("Updated Teacher ID from '" + oldId + "' to '" + this.teacherId + "'");
    }

    public List<Section> getAssignedSections() {
        return assignedSections;
    }

    public void addAssignedSection(Section section) throws InvalidUserDataException {
        if (section == null) {
            Logger.error("Instructor " + teacherId + " failed to add assigned section: Section is null");
            throw new InvalidUserDataException("Section cannot be null.");
        }
        if (!assignedSections.contains(section)) {
            assignedSections.add(section);
            Logger.info("Instructor " + teacherId + " assigned to section: " + section.getSectionId());
        }
    }

    public List<Course> viewCourses() {
        Logger.info("Instructor " + teacherId + " viewing assigned courses");
        List<Course> courses = new ArrayList<>();
        for (Section section : assignedSections) {
            if (section != null && section.getCourse() != null && !courses.contains(section.getCourse())) {
                courses.add(section.getCourse());
            }
        }
        return courses;
    }

    public List<Section> viewSections() {
        Logger.info("Instructor " + teacherId + " viewing assigned sections");
        return assignedSections;
    }

    public List<Student> viewEnrolledStudents(Section section) throws InvalidUserDataException {
        if (section == null) {
            Logger.error("Instructor " + teacherId + " failed to view enrolled students: Section is null");
            throw new InvalidUserDataException("Section cannot be null.");
        }
        Logger.info("Instructor " + teacherId + " viewing enrolled students for section: " + section.getSectionId());
        return section.getEnrolledStudents();
    }

    public Attendance markAttendance(Student student, Section section, AttendanceStatus status) throws InvalidUserDataException {
        if (student == null) {
            Logger.error("Instructor " + teacherId + " failed to mark attendance: Student is null");
            throw new InvalidUserDataException("Student cannot be null for attendance marking.");
        }
        if (section == null) {
            Logger.error("Instructor " + teacherId + " failed to mark attendance: Section is null");
            throw new InvalidUserDataException("Section cannot be null for attendance marking.");
        }
        if (status == null) {
            Logger.error("Instructor " + teacherId + " failed to mark attendance: Status is null");
            throw new InvalidUserDataException("Attendance status cannot be null.");
        }

        Logger.info("Instructor " + teacherId + " marking attendance for student " + student.getStudentId() + " in section " + section.getSectionId() + " as " + status);
        Attendance attendance = new Attendance(student, section, LocalDate.now(), status);
        Logger.info("Attendance successfully marked for student " + student.getStudentId());
        return attendance;
    }

    public void updateAttendance(Attendance attendance, AttendanceStatus status) throws InvalidUserDataException {
        if (attendance == null) {
            Logger.error("Instructor " + teacherId + " failed to update attendance: Attendance record is null");
            throw new InvalidUserDataException("Attendance record cannot be null.");
        }
        if (status == null) {
            Logger.error("Instructor " + teacherId + " failed to update attendance: New status is null");
            throw new InvalidUserDataException("New attendance status cannot be null.");
        }

        Logger.info("Instructor " + teacherId + " updating attendance status to: " + status);
        attendance.setStatus(status);
        Logger.info("Attendance record updated successfully to " + status);
    }

    public double calculateAttendancePercentage(Student student, Section section) throws InvalidUserDataException {
        if (student == null || section == null) {
            Logger.error("Instructor " + teacherId + " failed to calculate attendance percentage: Student or Section is null");
            throw new InvalidUserDataException("Student and Section cannot be null.");
        }

        Logger.info("Instructor " + teacherId + " calculating attendance percentage for student " + student.getStudentId() + " in section " + section.getSectionId());
        List<Attendance> records = section.getAttendanceRecordsForStudent(student);
        if (records == null || records.isEmpty()) {
            Logger.info("No attendance records found for student " + student.getStudentId() + ", percentage is 0.0%");
            return 0.0;
        }

        int presentCount = 0;
        for (Attendance att : records) {
            if (att != null && att.getStatus() == AttendanceStatus.PRESENT) {
                presentCount++;
            }
        }
        double percentage = ((double) presentCount / records.size()) * 100.0;
        Logger.info("Attendance percentage for student " + student.getStudentId() + " is " + String.format("%.2f", percentage) + "%");
        return percentage;
    }
}