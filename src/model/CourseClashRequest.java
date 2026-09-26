package model;

import java.time.*;
import exceptions.*;
import util.*;

public class CourseClashRequest extends Request {
    private Section conflictingSection;
    private Section requestedSection;

    public CourseClashRequest(String requestId, LocalDate requestDate, String description, int priority,
            Section conflictingSection, Section requestedSection) throws InvalidRequestException {
        super(requestId, requestDate, description, priority);
        if (conflictingSection == null || requestedSection == null) {
            Logger.error("Failed to create CourseClashRequest " + requestId + ": One or both sections are null");
            throw new InvalidRequestException("Conflicting section and requested section cannot be null.");
        }
        if (conflictingSection.equals(requestedSection)) {
            Logger.error("Failed to create CourseClashRequest " + requestId + ": Sections are identical");
            throw new InvalidRequestException("Conflicting section and requested section must be different.");
        }
        this.conflictingSection = conflictingSection;
        this.requestedSection = requestedSection;
        Logger.info("CourseClashRequest initialized: " + requestId + " between "
                + conflictingSection.getSectionId() + " and " + requestedSection.getSectionId());
    }

    public Section getConflictingSection() {
        return conflictingSection;
    }

    public Section getRequestedSection() {
        return requestedSection;
    }

    public String getConflictDetails() {
        String clash = "none recorded";
        if (conflictingSection.hasClash(requestedSection)) {
            clash = "schedules overlap";
        }
        return "Conflict between section " + conflictingSection.getSectionId()
                + " (" + scheduleText(conflictingSection) + ") and section "
                + requestedSection.getSectionId() + " (" + scheduleText(requestedSection) + "): " + clash;
    }

    @Override
    public String getDetails() {
        return super.getDetails() + " | " + getConflictDetails();
    }

    private String scheduleText(Section section) {
        if (section.getSchedule() == null) {
            return "no schedule";
        }
        return section.getSchedule().getScheduleInfo();
    }
}
