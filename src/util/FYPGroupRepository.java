package util;

import java.io.*;
import java.util.*;
import exceptions.*;
import model.*;

public class FYPGroupRepository {
    private static final String PATH = "data/fyp_groups.txt";

    public static Map<String, FYPGroup> load(Map<String, Student> students, Map<String, PermanentInstructor> supervisors,
            Map<String, FYPMeeting> meetings, Map<String, FYPEvaluation> evaluations) throws IOException, InvalidUserDataException {
        Map<String, FYPGroup> groups = new LinkedHashMap<>();
        List<String> lines = DelimitedFiles.readLines(PATH);
        for (String line : lines) {
            String[] parts = DelimitedFiles.split(line);
            if (parts.length < 4) {
                Logger.warning("Skipping malformed FYP group record: " + line);
                continue;
            }
            String groupId = parts[0].trim();
            String title = parts[1].trim();
            String description = DelimitedFiles.optional(parts[2]);
            String memberIds = DelimitedFiles.optional(parts[3]);
            PermanentInstructor supervisor = supervisors == null ? null : supervisors.get(DelimitedFiles.optional(parts[4]));
            String meetingIds = DelimitedFiles.optional(parts[5]);
            String evaluationIds = DelimitedFiles.optional(parts[6]);

            FYPGroup group = new FYPGroup(groupId, title, description != null ? description : "");
            group.setSupervisor(supervisor);

            if (memberIds != null) {
                String[] mids = memberIds.split(",");
                for (String mid : mids) {
                    Student s = students == null ? null : students.get(mid.trim());
                    if (s != null) {
                        try {
                            group.addMember(s);
                        } catch (InvalidFYPGroupException ex) {
                            Logger.warning("Failed to add member " + mid + " to group " + groupId + ": " + ex.getMessage());
                        }
                    }
                }
            }

            if (meetingIds != null) {
                String[] mids = meetingIds.split(",");
                for (String mid : mids) {
                    FYPMeeting m = meetings == null ? null : meetings.get(mid.trim());
                    if (m != null) {
                        try {
                            group.addMeeting(m);
                        } catch (InvalidFYPGroupException ex) {
                            Logger.warning("Failed to add meeting " + mid + " to group " + groupId + ": " + ex.getMessage());
                        }
                    }
                }
            }

            if (evaluationIds != null) {
                String[] eids = evaluationIds.split(",");
                for (String eid : eids) {
                    FYPEvaluation e = evaluations == null ? null : evaluations.get(eid.trim());
                    if (e != null) {
                        try {
                            group.addEvaluation(e);
                        } catch (InvalidFYPGroupException ex) {
                            Logger.warning("Failed to add evaluation " + eid + " to group " + groupId + ": " + ex.getMessage());
                        }
                    }
                }
            }

            groups.put(groupId, group);
        }
        Logger.info("Loaded " + groups.size() + " FYP group(s)");
        return groups;
    }

    public static void save(Collection<FYPGroup> groups) throws IOException {
        List<String> lines = new ArrayList<>();
        if (groups != null) {
            for (FYPGroup g : groups) {
                if (g == null) continue;
                String memberIds = joinIds(g.getMembers(), s -> s.getStudentId());
                String supervisorId = g.getSupervisor() == null ? DelimitedFiles.EMPTY : g.getSupervisor().getTeacherId();
                String meetingIds = joinIds(g.getMeetings(), m -> m.getMeetingId());
                String evaluationIds = joinIds(g.getEvaluations(), e -> e.getEvaluationId());
                String description = DelimitedFiles.sanitize(g.getDescription());
                lines.add(g.getGroupId() + DelimitedFiles.SEPARATOR
                        + DelimitedFiles.sanitize(g.getTitle()) + DelimitedFiles.SEPARATOR
                        + description + DelimitedFiles.SEPARATOR
                        + (memberIds.isEmpty() ? DelimitedFiles.EMPTY : memberIds) + DelimitedFiles.SEPARATOR
                        + supervisorId + DelimitedFiles.SEPARATOR
                        + (meetingIds.isEmpty() ? DelimitedFiles.EMPTY : meetingIds) + DelimitedFiles.SEPARATOR
                        + (evaluationIds.isEmpty() ? DelimitedFiles.EMPTY : evaluationIds));
            }
        }
        DelimitedFiles.writeLines(PATH, lines);
    }

    private static <T> String joinIds(Collection<T> items, java.util.function.Function<T, String> idExtractor) {
        if (items == null || items.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (T item : items) {
            if (item == null) continue;
            if (!first) sb.append(",");
            sb.append(idExtractor.apply(item));
            first = false;
        }
        return sb.toString();
    }
}