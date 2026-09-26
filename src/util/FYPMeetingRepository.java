package util;

import java.io.*;
import java.time.*;
import java.time.format.*;
import java.util.*;
import exceptions.*;
import model.*;

public class FYPMeetingRepository {
    private static final String PATH = "data/fyp_meetings.txt";
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static Map<String, FYPMeeting> load() throws IOException, InvalidUserDataException {
        Map<String, FYPMeeting> meetings = new LinkedHashMap<>();
        List<String> lines = DelimitedFiles.readLines(PATH);
        for (String line : lines) {
            String[] parts = DelimitedFiles.split(line);
            if (parts.length < 5) {
                Logger.warning("Skipping malformed FYP meeting record: " + line);
                continue;
            }
            String meetingId = parts[0].trim();
            LocalDate meetingDate;
            try {
                meetingDate = LocalDate.parse(parts[1].trim(), DATE);
            } catch (Exception ex) {
                Logger.warning("Skipping meeting " + meetingId + ": invalid date");
                continue;
            }
            String agenda = DelimitedFiles.optional(parts[2]);
            String notes = DelimitedFiles.optional(parts[3]);

            FYPMeeting meeting = new FYPMeeting(meetingId, meetingDate, agenda != null ? agenda : "", notes != null ? notes : "");
            meetings.put(meetingId, meeting);
        }
        Logger.info("Loaded " + meetings.size() + " FYP meeting(s)");
        return meetings;
    }

    public static void save(Collection<FYPMeeting> meetings) throws IOException {
        List<String> lines = new ArrayList<>();
        if (meetings != null) {
            for (FYPMeeting m : meetings) {
                if (m == null) continue;
                String agenda = DelimitedFiles.sanitize(m.getAgenda());
                String notes = DelimitedFiles.sanitize(m.getNotes());
                lines.add(m.getMeetingId() + DelimitedFiles.SEPARATOR
                        + m.getMeetingDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + DelimitedFiles.SEPARATOR
                        + agenda + DelimitedFiles.SEPARATOR
                        + notes);
            }
        }
        DelimitedFiles.writeLines(PATH, lines);
    }
}