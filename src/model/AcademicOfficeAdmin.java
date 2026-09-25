package model;

import java.io.*;
import java.util.*;
import enums.*;
import exceptions.*;
import util.*;

public class AcademicOfficeAdmin extends Administrator {
    private List<Course> managedCourses;
    private List<Section> managedSections;
    private List<Request> managedRequests;

    public AcademicOfficeAdmin(String adminId, String name, String email, String phone) throws InvalidUserDataException {
        super(adminId, name, email, phone);
        this.managedCourses = new ArrayList<>();
        this.managedSections = new ArrayList<>();
        this.managedRequests = new ArrayList<>();
        Logger.info("AcademicOfficeAdmin initialized: Admin ID=" + getAdminId());
    }

    public void createCourse(Course course) throws InvalidUserDataException {
        if (course == null) {
            Logger.error("Admin " + getAdminId() + " failed to create course: Course object is null");
            throw new InvalidUserDataException("Course cannot be null.");
        }
        if (!managedCourses.contains(course)) {
            managedCourses.add(course);
            Logger.info("Admin " + getAdminId() + " created course: " + course.getCourseCode());
        } else {
            Logger.warning("Admin " + getAdminId() + " attempted to create duplicate course: " + course.getCourseCode());
        }
    }

    public void updateCourse(Course course) throws InvalidUserDataException {
        if (course == null) {
            Logger.error("Admin " + getAdminId() + " failed to update course: Course object is null");
            throw new InvalidUserDataException("Course cannot be null.");
        }
        boolean found = false;
        for (int i = 0; i < managedCourses.size(); i++) {
            if (managedCourses.get(i).getCourseCode().equalsIgnoreCase(course.getCourseCode())) {
                managedCourses.set(i, course);
                found = true;
                Logger.info("Admin " + getAdminId() + " updated course: " + course.getCourseCode());
                break;
            }
        }
        if (!found) {
            Logger.warning("Admin " + getAdminId() + " attempted to update non-existent course: " + course.getCourseCode());
        }
    }

    public Course searchCourse(String courseCode) throws InvalidUserDataException {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            Logger.error("Admin " + getAdminId() + " search failed: Course code is empty");
            throw new InvalidUserDataException("Course code cannot be null or empty.");
        }
        Logger.info("Admin " + getAdminId() + " searching for course: " + courseCode);
        for (Course course : managedCourses) {
            if (course.getCourseCode().equalsIgnoreCase(courseCode.trim())) {
                Logger.info("Course found: " + course.getCourseCode());
                return course;
            }
        }
        Logger.info("Course not found: " + courseCode);
        return null;
    }

    public void createSection(Section section) throws InvalidUserDataException {
        if (section == null) {
            Logger.error("Admin " + getAdminId() + " failed to create section: Section is null");
            throw new InvalidUserDataException("Section cannot be null.");
        }
        if (!managedSections.contains(section)) {
            managedSections.add(section);
            Logger.info("Admin " + getAdminId() + " created section: " + section.getSectionId());
        }
    }

    public void updateSection(Section section) throws InvalidUserDataException {
        if (section == null) {
            Logger.error("Admin " + getAdminId() + " failed to update section: Section is null");
            throw new InvalidUserDataException("Section cannot be null.");
        }
        for (int i = 0; i < managedSections.size(); i++) {
            if (managedSections.get(i).getSectionId().equalsIgnoreCase(section.getSectionId())) {
                managedSections.set(i, section);
                Logger.info("Admin " + getAdminId() + " updated section: " + section.getSectionId());
                break;
            }
        }
    }

    public void setCapacity(Section section, int capacity) throws InvalidUserDataException {
        if (section == null) {
            Logger.error("Admin " + getAdminId() + " failed to set capacity: Section is null");
            throw new InvalidUserDataException("Section cannot be null.");
        }
        if (capacity < 0) {
            Logger.error("Admin " + getAdminId() + " failed to set capacity: Negative capacity " + capacity);
            throw new InvalidUserDataException("Capacity cannot be negative.");
        }
        Logger.info("Admin " + getAdminId() + " setting capacity of section " + section.getSectionId() + " to " + capacity);
        section.setCapacity(capacity);
        Logger.info("Capacity updated successfully for section " + section.getSectionId());
    }

    public void assignRoom(Section section, Schedule schedule) throws InvalidUserDataException {
        if (section == null || schedule == null) {
            Logger.error("Admin " + getAdminId() + " failed to assign room/schedule: Section or Schedule is null");
            throw new InvalidUserDataException("Section and Schedule cannot be null.");
        }
        Logger.info("Admin " + getAdminId() + " assigning schedule to section " + section.getSectionId());
        section.setSchedule(schedule);
        Logger.info("Schedule assigned successfully to section " + section.getSectionId());
    }

    public void assignInstructor(Section section, Instructor instructor) throws InvalidUserDataException {
        if (section == null || instructor == null) {
            Logger.error("Admin " + getAdminId() + " failed to assign instructor: Section or Instructor is null");
            throw new InvalidUserDataException("Section and Instructor cannot be null.");
        }
        Logger.info("Admin " + getAdminId() + " assigning instructor " + instructor.getTeacherId() + " to section " + section.getSectionId());
        section.assignInstructor(instructor);
        instructor.addAssignedSection(section);
        Logger.info("Instructor " + instructor.getTeacherId() + " assigned successfully to section " + section.getSectionId());
    }

    public List<Request> viewRequests() {
        Logger.info("Admin " + getAdminId() + " viewing managed academic requests");
        return managedRequests;
    }

    public void approveRequest(Request request) throws InvalidRequestException {
        if (request == null) {
            Logger.error("Admin " + getAdminId() + " failed to approve request: Request is null");
            throw new InvalidRequestException("Request cannot be null.");
        }
        Logger.info("Admin " + getAdminId() + " approving request ID: " + request.getRequestId());
        request.setStatus(RequestStatus.APPROVED);
        Logger.info("Request " + request.getRequestId() + " approved successfully");
    }

    public void rejectRequest(Request request) throws InvalidRequestException {
        if (request == null) {
            Logger.error("Admin " + getAdminId() + " failed to reject request: Request is null");
            throw new InvalidRequestException("Request cannot be null.");
        }
        Logger.info("Admin " + getAdminId() + " rejecting request ID: " + request.getRequestId());
        request.setStatus(RequestStatus.REJECTED);
        Logger.info("Request " + request.getRequestId() + " rejected successfully");
    }

    @Override
    public String getRole() {
        return "Academic Office Admin";
    }
}