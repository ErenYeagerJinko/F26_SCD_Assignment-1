package util;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import enums.*;
import exceptions.*;
import model.*;

public class SectionRepository {
    private static final String PATH = "data/sections.txt";
    private static final DateTimeFormatter TIME = DateTimeFormatter.ofPattern("HH:mm");

    public static Map<String, Section> load(Map<String, Course> courses, Map<String, Instructor> instructors,
            Map<String, TeachingAssistant> assistants) throws IOException, InvalidUserDataException {
        Map<String, Section> sections = new LinkedHashMap<String, Section>();
        List<String> lines = DelimitedFiles.readLines(PATH);
        for (String line : lines) {
            String[] parts = DelimitedFiles.split(line);
            if (parts.length < 9) {
                Logger.warning("Skipping malformed section record: " + line);
                continue;
            }
            String sectionId = parts[0].trim().toUpperCase();
            Course course = courses == null ? null : courses.get(parts[1].trim().toUpperCase());
            if (course == null) {
                Logger.warning("Skipping section " + sectionId + ": unknown course " + parts[1]);
                continue;
            }
            int capacity;
            try {
                capacity = Integer.parseInt(parts[2].trim());
            } catch (NumberFormatException ex) {
                Logger.warning("Skipping section " + sectionId + ": invalid capacity");
                continue;
            }
            Section section = new Section(sectionId, capacity, course);

            String teacherId = DelimitedFiles.optional(parts[3]);
            if (teacherId != null && instructors != null && instructors.containsKey(teacherId)) {
                section.assignInstructor(instructors.get(teacherId));
            }

            String taId = DelimitedFiles.optional(parts[4]);
            if (taId != null && assistants != null && assistants.containsKey(taId)) {
                section.assignTA(assistants.get(taId));
            }

            String dayValue = DelimitedFiles.optional(parts[5]);
            String startValue = DelimitedFiles.optional(parts[6]);
            String endValue = DelimitedFiles.optional(parts[7]);
            String room = DelimitedFiles.optional(parts[8]);
            if (dayValue != null && startValue != null && endValue != null && room != null) {
                Schedule schedule = new Schedule(Day.valueOf(dayValue.toUpperCase()), LocalTime.parse(startValue, TIME),
                        LocalTime.parse(endValue, TIME), room);
                section.setSchedule(schedule);
            }
            sections.put(section.getSectionId(), section);
        }
        Logger.info("Loaded " + sections.size() + " section(s)");
        return sections;
    }

    public static void save(Collection<Section> sections) throws IOException {
        List<String> lines = new ArrayList<String>();
        if (sections != null) {
            for (Section section : sections) {
                if (section == null) {
                    continue;
                }
                String teacherId = section.getInstructor() == null ? DelimitedFiles.EMPTY : section.getInstructor().getTeacherId();
                String taId = section.getTeachingAssistant() == null ? DelimitedFiles.EMPTY : section.getTeachingAssistant().getStudentId();
                String day = DelimitedFiles.EMPTY;
                String start = DelimitedFiles.EMPTY;
                String end = DelimitedFiles.EMPTY;
                String room = DelimitedFiles.EMPTY;
                if (section.getSchedule() != null) {
                    day = section.getSchedule().getDay().name();
                    start = section.getSchedule().getStartTime().format(TIME);
                    end = section.getSchedule().getEndTime().format(TIME);
                    room = DelimitedFiles.sanitize(section.getSchedule().getRoom());
                }
                String courseCode = section.getCourse() == null ? DelimitedFiles.EMPTY : section.getCourse().getCourseCode();
                lines.add(section.getSectionId() + DelimitedFiles.SEPARATOR
                        + courseCode + DelimitedFiles.SEPARATOR
                        + section.getCapacity() + DelimitedFiles.SEPARATOR
                        + teacherId + DelimitedFiles.SEPARATOR
                        + taId + DelimitedFiles.SEPARATOR
                        + day + DelimitedFiles.SEPARATOR
                        + start + DelimitedFiles.SEPARATOR
                        + end + DelimitedFiles.SEPARATOR
                        + room);
            }
        }
        DelimitedFiles.writeLines(PATH, lines);
    }
}
