package model;

import java.util.*;
import enums.*;

public class AcademicOfficeAdmin extends Administrator {
    private List<Course> managedCourses;
    private List<Section> managedSections;
    private List<Request> managedRequests;

    public AcademicOfficeAdmin(String adminId, String name, String email, String phone) {
        super(adminId, name, email, phone);
        this.managedCourses = new ArrayList<>();
        this.managedSections = new ArrayList<>();
        this.managedRequests = new ArrayList<>();
    }

    public void createCourse(Course course) {
        if (course != null && !managedCourses.contains(course)) {
            managedCourses.add(course);
        }
    }

    public void updateCourse(Course course) {
        if (course != null) {
            for (int i = 0; i < managedCourses.size(); i++) {
                if (managedCourses.get(i).getCourseCode().equalsIgnoreCase(course.getCourseCode())) {
                    managedCourses.set(i, course);
                    break;
                }
            }
        }
    }

    public Course searchCourse(String courseCode) {
        if (courseCode != null) {
            for (Course course : managedCourses) {
                if (course.getCourseCode().equalsIgnoreCase(courseCode)) {
                    return course;
                }
            }
        }
        return null;
    }

    public void createSection(Section section) {
        if (section != null && !managedSections.contains(section)) {
            managedSections.add(section);
        }
    }

    public void updateSection(Section section) {
        if (section != null) {
            for (int i = 0; i < managedSections.size(); i++) {
                if (managedSections.get(i).getSectionId().equalsIgnoreCase(section.getSectionId())) {
                    managedSections.set(i, section);
                    break;
                }
            }
        }
    }

    public void setCapacity(Section section, int capacity) {
        if (section != null) {
            section.setCapacity(capacity);
        }
    }

    public void assignRoom(Section section, Schedule schedule) {
        if (section != null) {
            section.setSchedule(schedule);
        }
    }

    public void assignInstructor(Section section, Instructor instructor) {
        if (section != null) {
            section.assignInstructor(instructor);
        }
    }

    public List<Request> viewRequests() {
        return managedRequests;
    }

    public void approveRequest(Request request) {
        if (request != null) {
            request.setStatus(RequestStatus.APPROVED);
        }
    }

    public void rejectRequest(Request request) {
        if (request != null) {
            request.setStatus(RequestStatus.REJECTED);
        }
    }

    @Override
    public String getRole() {
        return "Academic Office Admin";
    }
}