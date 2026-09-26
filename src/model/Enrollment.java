package model;

import java.time.*;
import enums.*;
import exceptions.*;
import util.*;

public class Enrollment {
    private String enrollmentId;
    private Student student;
    private Section section;
    private LocalDate enrollmentDate;
    private EnrollmentStatus status;

    public Enrollment(String enrollmentId, Student student, Section section, LocalDate enrollmentDate)
            throws InvalidUserDataException {
        if (enrollmentId == null || enrollmentId.trim().isEmpty()) {
            Logger.error("Failed to create Enrollment: Enrollment ID cannot be null or empty");
            throw new InvalidUserDataException("Enrollment ID cannot be null or empty.");
        }
        if (student == null) {
            Logger.error("Failed to create Enrollment: Student is null");
            throw new InvalidUserDataException("Enrollment student cannot be null.");
        }
        if (section == null) {
            Logger.error("Failed to create Enrollment: Section is null");
            throw new InvalidUserDataException("Enrollment section cannot be null.");
        }
        if (enrollmentDate == null) {
            Logger.error("Failed to create Enrollment: Enrollment date is null");
            throw new InvalidUserDataException("Enrollment date cannot be null.");
        }

        this.enrollmentId = enrollmentId.trim();
        this.student = student;
        this.section = section;
        this.enrollmentDate = enrollmentDate;
        this.status = EnrollmentStatus.ACTIVE;
        Logger.info("Enrollment initialized: ID=" + this.enrollmentId + ", Student=" + student.getStudentId()
                + ", Section=" + section.getSectionId());
    }

    public String getEnrollmentId() {
        return enrollmentId;
    }

    public Student getStudent() {
        return student;
    }

    public Section getSection() {
        return section;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public EnrollmentStatus getStatus() {
        return status;
    }

    public void setStatus(EnrollmentStatus status) throws InvalidUserDataException {
        if (status == null) {
            Logger.error("Failed to update enrollment " + enrollmentId + ": Status is null");
            throw new InvalidUserDataException("Enrollment status cannot be null.");
        }
        this.status = status;
        Logger.info("Enrollment " + enrollmentId + " status set to " + status);
    }

    public void cancel() {
        this.status = EnrollmentStatus.DROPPED;
        Logger.info("Enrollment " + enrollmentId + " cancelled for student " + student.getStudentId());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Enrollment other = (Enrollment) obj;
        return enrollmentId.equals(other.enrollmentId);
    }

    @Override
    public int hashCode() {
        return enrollmentId.hashCode();
    }
}
