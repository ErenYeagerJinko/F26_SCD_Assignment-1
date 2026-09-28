package util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import exceptions.InvalidFYPGroupException;
import exceptions.InvalidUserDataException;
import model.FYPEvaluation;
import model.FYPGroup;
import model.FYPMeeting;
import model.PermanentInstructor;
import model.Student;

public class FYPGroupRepository {
    private static final String PATH = "data/fyp_groups.txt";

    public static Map<String, FYPGroup> load(Map<String, Student> students, Map<String, PermanentInstructor> supervisors,
            Map<String, FYPMeeting> meetings, Map<String, FYPEvaluation> evaluations) throws IOException, InvalidUserDataException {
        Map<String, FYPGroup> groups = new HashMap<>();
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

            String desc = "";
            if (description != null) {
                desc = description;
            }
            FYPGroup group = new FYPGroup(groupId, title, desc);
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

                StringBuilder memberIdBuilder = new StringBuilder();
                boolean firstMember = true;
                for (Student s : g.getMembers()) {
                    if (s == null) continue;
                    if (!firstMember) {
                        memberIdBuilder.append(",");
                    }
                    memberIdBuilder.append(s.getStudentId());
                    firstMember = false;
                }
                String memberIdsStr = memberIdBuilder.toString();

                String supervisorId = DelimitedFiles.EMPTY;
                if (g.getSupervisor() != null) {
                    supervisorId = g.getSupervisor().getTeacherId();
                }

                StringBuilder meetingIdBuilder = new StringBuilder();
                boolean firstMeeting = true;
                for (FYPMeeting m : g.getMeetings()) {
                    if (m == null) continue;
                    if (!firstMeeting) {
                        meetingIdBuilder.append(",");
                    }
                    meetingIdBuilder.append(m.getMeetingId());
                    firstMeeting = false;
                }
                String meetingIdsStr = meetingIdBuilder.toString();

                StringBuilder evalIdBuilder = new StringBuilder();
                boolean firstEval = true;
                for (FYPEvaluation e : g.getEvaluations()) {
                    if (e == null) continue;
                    if (!firstEval) {
                        evalIdBuilder.append(",");
                    }
                    evalIdBuilder.append(e.getEvaluationId());
                    firstEval = false;
                }
                String evalIdsStr = evalIdBuilder.toString();

                String description = DelimitedFiles.sanitize(g.getDescription());
                lines.add(g.getGroupId() + DelimitedFiles.SEPARATOR
                        + DelimitedFiles.sanitize(g.getTitle()) + DelimitedFiles.SEPARATOR
                        + description + DelimitedFiles.SEPARATOR
                        + (memberIdsStr.isEmpty() ? DelimitedFiles.EMPTY : memberIdsStr) + DelimitedFiles.SEPARATOR
                        + supervisorId + DelimitedFiles.SEPARATOR
                        + (meetingIdsStr.isEmpty() ? DelimitedFiles.EMPTY : meetingIdsStr) + DelimitedFiles.SEPARATOR
                        + (evalIdsStr.isEmpty() ? DelimitedFiles.EMPTY : evalIdsStr));
            }
        }
        DelimitedFiles.writeLines(PATH, lines);
    }
}