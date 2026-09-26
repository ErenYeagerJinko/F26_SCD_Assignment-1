package model;

import java.util.*;
import exceptions.*;
import util.*;

public class Course {
    private String courseCode;
    private String title;
    private int creditHours;
    private Set<Course> prerequisites;
    private List<Section> sections;

    public Course(String courseCode, String title, int creditHours) throws InvalidUserDataException {
        if (courseCode == null || courseCode.trim().isEmpty()) {
            Logger.error("Failed to create Course: Course code cannot be null or empty");
            throw new InvalidUserDataException("Course code cannot be null or empty.");
        }
        if (title == null || title.trim().isEmpty()) {
            Logger.error("Failed to create Course: Title cannot be null or empty");
            throw new InvalidUserDataException("Course title cannot be null or empty.");
        }
        if (creditHours <= 0) {
            Logger.error("Failed to create Course: Credit hours must be positive (" + creditHours + ")");
            throw new InvalidUserDataException("Credit hours must be greater than zero.");
        }

        this.courseCode = courseCode.trim().toUpperCase();
        this.title = title.trim();
        this.creditHours = creditHours;
        this.prerequisites = new HashSet<Course>();
        this.sections = new ArrayList<Section>();
        Logger.info("Course initialized: Code=" + this.courseCode + ", Title=" + this.title + ", CreditHours=" + this.creditHours);
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) throws InvalidUserDataException {
        if (title == null || title.trim().isEmpty()) {
            Logger.error("Failed to update course title for " + courseCode + ": Title is empty");
            throw new InvalidUserDataException("Course title cannot be null or empty.");
        }
        String oldTitle = this.title;
        this.title = title.trim();
        Logger.info("Updated title of course " + courseCode + " from '" + oldTitle + "' to '" + this.title + "'");
    }

    public int getCreditHours() {
        return creditHours;
    }

    public void setCreditHours(int creditHours) throws InvalidUserDataException {
        if (creditHours <= 0) {
            Logger.error("Failed to update credit hours for " + courseCode + ": Invalid value " + creditHours);
            throw new InvalidUserDataException("Credit hours must be greater than zero.");
        }
        this.creditHours = creditHours;
        Logger.info("Updated credit hours of course " + courseCode + " to " + creditHours);
    }

    public Set<Course> getPrerequisites() {
        return prerequisites;
    }

    public void addPrerequisite(Course course) throws InvalidUserDataException {
        if (course == null) {
            Logger.error("Failed to add prerequisite to " + courseCode + ": Prerequisite course is null");
            throw new InvalidUserDataException("Prerequisite course cannot be null.");
        }
        if (course.equals(this)) {
            Logger.error("Failed to add prerequisite to " + courseCode + ": Course cannot be its own prerequisite");
            throw new InvalidUserDataException("A course cannot be a prerequisite of itself.");
        }
        if (prerequisites.add(course)) {
            Logger.info("Added prerequisite " + course.getCourseCode() + " to course " + courseCode);
        }
    }

    public void addSection(Section section) throws InvalidUserDataException {
        if (section == null) {
            Logger.error("Failed to add section to " + courseCode + ": Section is null");
            throw new InvalidUserDataException("Section cannot be null.");
        }
        if (!sections.contains(section)) {
            sections.add(section);
            Logger.info("Added section " + section.getSectionId() + " to course " + courseCode);
        }
    }

    public List<Section> getSections() {
        return sections;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Course other = (Course) obj;
        return courseCode.equals(other.courseCode);
    }

    @Override
    public int hashCode() {
        return courseCode.hashCode();
    }
}
