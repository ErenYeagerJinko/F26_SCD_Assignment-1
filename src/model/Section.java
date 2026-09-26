package model;

import java.time.*;
import java.util.*;
import enums.*;
import exceptions.*;
import util.*;

public class Section {
    private String sectionId;
    private int capacity;
    private Course course;
    private Instructor instructor;
    private TeachingAssistant teachingAssistant;
    private Schedule schedule;
    private List<Enrollment> enrollments;
    private List<Attendance> attendanceRecords;

    public Section(String sectionId, int capacity, Course course) throws InvalidUserDataException {
        if (sectionId == null || sectionId.trim().isEmpty()) {
            Logger.error("Failed to create Section: Section ID cannot be null or empty");
            throw new InvalidUserDataException("Section ID cannot be null or empty.");
        }
        if (capacity < 0) {
            Logger.error("Failed to create Section: Negative capacity " + capacity);
            throw new InvalidUserDataException("Section capacity cannot be negative.");
        }
        if (course == null) {
            Logger.error("Failed to create Section: Course is null");
            throw new InvalidUserDataException("Section must belong to a course.");
        }

        this.sectionId = sectionId.trim().toUpperCase();
        this.capacity = capacity;
        this.course = course;
        this.enrollments = new ArrayList<Enrollment>();
        this.attendanceRecords = new ArrayList<Attendance>();
        course.addSection(this);
        Logger.info("Section initialized: ID=" + this.sectionId + ", Course=" + course.getCourseCode() + ", Capacity=" + this.capacity);
    }

    public String getSectionId() {
        return sectionId;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) throws InvalidUserDataException {
        if (capacity < 0) {
            Logger.error("Failed to set capacity for section " + sectionId + ": Negative value " + capacity);
            throw new InvalidUserDataException("Section capacity cannot be negative.");
        }
        int active = countActiveEnrollments();
        if (capacity < active) {
            Logger.error("Failed to set capacity for section " + sectionId + ": Capacity " + capacity + " is below active enrollments " + active);
            throw new InvalidUserDataException("Capacity cannot be lower than the number of currently enrolled students.");
        }
        this.capacity = capacity;
        Logger.info("Capacity of section " + sectionId + " set to " + capacity);
    }

    public Course getCourse() {
        return course;
    }

    public Instructor getInstructor() {
        return instructor;
    }

    public TeachingAssistant getTeachingAssistant() {
        return teachingAssistant;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public void setSchedule(Schedule schedule) throws InvalidUserDataException {
        if (schedule == null) {
            Logger.error("Failed to set schedule for section " + sectionId + ": Schedule is null");
            throw new InvalidUserDataException("Schedule cannot be null.");
        }
        this.schedule = schedule;
        Logger.info("Schedule assigned to section " + sectionId + ": " + schedule.getScheduleInfo());
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public void enroll(Student student) throws CourseFullException, CourseClashException, InvalidUserDataException {
        if (student == null) {
            Logger.error("Enrollment failed for section " + sectionId + ": Student is null");
            throw new InvalidUserDataException("Student cannot be null for enrollment.");
        }
        if (findActiveEnrollment(student) != null) {
            Logger.error("Enrollment failed for student " + student.getStudentId() + ": Already enrolled in section " + sectionId);
            throw new InvalidUserDataException("Student " + student.getStudentId() + " is already enrolled in section " + sectionId + ".");
        }
        if (isFull()) {
            Logger.error("Enrollment failed for student " + student.getStudentId() + ": Section " + sectionId + " is full");
            throw new CourseFullException("Section " + sectionId + " is full.");
        }
        if (student.getEnrollments() != null) {
            for (Enrollment existing : student.getEnrollments()) {
                if (existing != null && existing.getStatus() == EnrollmentStatus.ACTIVE && existing.getSection() != null
                        && existing.getSection().hasClash(this)) {
                    Logger.error("Enrollment failed for student " + student.getStudentId() + ": Clash between "
                            + sectionId + " and " + existing.getSection().getSectionId());
                    throw new CourseClashException("Timetable clash detected between section " + sectionId
                            + " and enrolled section " + existing.getSection().getSectionId() + ".");
                }
            }
        }

        Enrollment enrollment = new Enrollment("ENR-" + student.getStudentId() + "-" + sectionId, student, this, LocalDate.now());
        enrollments.add(enrollment);
        student.getEnrollments().add(enrollment);
        Logger.info("Student " + student.getStudentId() + " enrolled in section " + sectionId);
    }

    public void drop(Student student) throws InvalidUserDataException {
        if (student == null) {
            Logger.error("Drop failed for section " + sectionId + ": Student is null");
            throw new InvalidUserDataException("Student cannot be null.");
        }
        Enrollment enrollment = findActiveEnrollment(student);
        if (enrollment == null) {
            Logger.error("Drop failed for student " + student.getStudentId() + ": No active enrollment in section " + sectionId);
            throw new InvalidUserDataException("Student " + student.getStudentId() + " is not enrolled in section " + sectionId + ".");
        }
        enrollment.cancel();
        enrollments.remove(enrollment);
        student.getEnrollments().remove(enrollment);
        Logger.info("Student " + student.getStudentId() + " dropped from section " + sectionId);
    }

    public boolean isFull() {
        return getAvailableSeats() <= 0;
    }

    public int getAvailableSeats() {
        return capacity - countActiveEnrollments();
    }

    public void assignInstructor(Instructor instructor) throws InvalidUserDataException {
        if (instructor == null) {
            Logger.error("Failed to assign instructor to section " + sectionId + ": Instructor is null");
            throw new InvalidUserDataException("Instructor cannot be null.");
        }
        this.instructor = instructor;
        instructor.addAssignedSection(this);
        Logger.info("Instructor " + instructor.getTeacherId() + " assigned to section " + sectionId);
    }

    public void assignTA(TeachingAssistant ta) throws InvalidUserDataException {
        if (ta == null) {
            Logger.error("Failed to assign TA to section " + sectionId + ": TeachingAssistant is null");
            throw new InvalidUserDataException("Teaching assistant cannot be null.");
        }
        this.teachingAssistant = ta;
        ta.setAssignedSection(this);
        Logger.info("Teaching assistant " + ta.getStudentId() + " assigned to section " + sectionId);
    }

    public List<Student> getEnrolledStudents() {
        List<Student> students = new ArrayList<Student>();
        for (Enrollment enrollment : enrollments) {
            if (enrollment != null && enrollment.getStatus() == EnrollmentStatus.ACTIVE && enrollment.getStudent() != null) {
                students.add(enrollment.getStudent());
            }
        }
        return students;
    }

    public boolean hasClash(Section section) {
        if (section == null || section == this || this.schedule == null || section.getSchedule() == null) {
            return false;
        }
        return this.schedule.hasClash(section.getSchedule());
    }

    public void addAttendanceRecord(Attendance attendance) throws InvalidUserDataException {
        if (attendance == null) {
            Logger.error("Failed to add attendance for section " + sectionId + ": Attendance is null");
            throw new InvalidUserDataException("Attendance cannot be null.");
        }
        attendanceRecords.add(attendance);
        Logger.info("Attendance record added for section " + sectionId);
    }

    public List<Attendance> getAttendanceRecordsForStudent(Student student) {
        List<Attendance> matches = new ArrayList<Attendance>();
        if (student == null) {
            return matches;
        }
        for (Attendance record : attendanceRecords) {
            if (record != null) {
                matches.add(record);
            }
        }
        return matches;
    }

    public void restoreEnrollment(Enrollment enrollment) throws InvalidUserDataException {
        if (enrollment == null) {
            Logger.error("Failed to restore enrollment for section " + sectionId + ": Enrollment is null");
            throw new InvalidUserDataException("Enrollment cannot be null.");
        }
        if (!enrollments.contains(enrollment)) {
            enrollments.add(enrollment);
        }
        Student student = enrollment.getStudent();
        if (student != null && student.getEnrollments() != null && !student.getEnrollments().contains(enrollment)) {
            student.getEnrollments().add(enrollment);
        }
        Logger.info("Restored enrollment " + enrollment.getEnrollmentId() + " for section " + sectionId);
    }

    private Enrollment findActiveEnrollment(Student student) {
        for (Enrollment enrollment : enrollments) {
            if (enrollment != null && enrollment.getStatus() == EnrollmentStatus.ACTIVE
                    && enrollment.getStudent() != null
                    && enrollment.getStudent().getStudentId().equals(student.getStudentId())) {
                return enrollment;
            }
        }
        return null;
    }

    private int countActiveEnrollments() {
        int count = 0;
        for (Enrollment enrollment : enrollments) {
            if (enrollment != null && enrollment.getStatus() == EnrollmentStatus.ACTIVE) {
                count++;
            }
        }
        return count;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Section other = (Section) obj;
        return sectionId.equals(other.sectionId);
    }

    @Override
    public int hashCode() {
        return sectionId.hashCode();
    }
}
