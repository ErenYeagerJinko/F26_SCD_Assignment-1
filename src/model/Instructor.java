package model;

import java.util.*;
import java.time.*;
import enums.*;

public abstract class Instructor extends Person {
    private String teacherId;
    private List<Section> assignedSections;

    public Instructor(String teacherId, String name, String email, String phone) {
        super(name, email, phone);
        this.teacherId = teacherId;
        this.assignedSections = new ArrayList<>();
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
    }

    public List<Section> getAssignedSections() {
        return assignedSections;
    }

    public void addAssignedSection(Section section) {
        if (section != null && !assignedSections.contains(section)) {
            assignedSections.add(section);
        }
    }

    public List<Course> viewCourses() {
        List<Course> courses = new ArrayList<>();
        for (Section section : assignedSections) {
            if (section != null && section.getCourse() != null && !courses.contains(section.getCourse())) {
                courses.add(section.getCourse());
            }
        }
        return courses;
    }

    public List<Section> viewSections() {
        return assignedSections;
    }

    public List<Student> viewEnrolledStudents(Section section) {
        if (section != null) {
            return section.getEnrolledStudents();
        }
        return new ArrayList<>();
    }

    public Attendance markAttendance(Student student, Section section, AttendanceStatus status) {
        if (student != null && section != null && status != null) {
            Attendance attendance = new Attendance(student, section, LocalDate.now(), status);
            return attendance;
        }
        return null;
    }

    public void updateAttendance(Attendance attendance, AttendanceStatus status) {
        if (attendance != null && status != null) {
            attendance.setStatus(status);
        }
    }

    public double calculateAttendancePercentage(Student student, Section section) {
        if (student != null && section != null) {
            List<Attendance> records = section.getAttendanceRecordsForStudent(student);
            if (records == null || records.isEmpty()) {
                return 0.0;
            }
            int presentCount = 0;
            for (Attendance att : records) {
                if (att.getStatus() == AttendanceStatus.PRESENT) {
                    presentCount++;
                }
            }
            return ((double) presentCount / records.size()) * 100.0;
        }
        return 0.0;
    }
}