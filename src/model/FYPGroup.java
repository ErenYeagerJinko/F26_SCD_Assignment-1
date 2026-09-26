package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import util.Logger;
import exceptions.InvalidFYPGroupException;

public class FYPGroup {
    private String groupId;
    private String title;
    private String description;
    private List<Student> members;
    private PermanentInstructor supervisor;
    private List<FYPMeeting> meetings;
    private List<FYPEvaluation> evaluations;

    public FYPGroup(String groupId, String title, String description) {
        this.groupId = groupId;
        this.title = title;
        this.description = description;
        this.members = new ArrayList<>();
        this.meetings = new ArrayList<>();
        this.evaluations = new ArrayList<>();
        Logger.info("FYPGroup created: " + groupId + " (" + title + ")");
    }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public List<Student> getMembers() { return members; }
    public void setMembers(List<Student> members) { this.members = members; }
    public PermanentInstructor getSupervisor() { return supervisor; }
    public void setSupervisor(PermanentInstructor supervisor) { this.supervisor = supervisor; }
    public List<FYPMeeting> getMeetings() { return meetings; }
    public void setMeetings(List<FYPMeeting> meetings) { this.meetings = meetings; }
    public List<FYPEvaluation> getEvaluations() { return evaluations; }
    public void setEvaluations(List<FYPEvaluation> evaluations) { this.evaluations = evaluations; }

    public void addMember(Student student) throws InvalidFYPGroupException {
        if (student == null) {
            Logger.error("FYPGroup " + groupId + ": cannot add null member");
            throw new InvalidFYPGroupException("Cannot add null member to group");
        }
        if (members.contains(student)) {
            Logger.warning("FYPGroup " + groupId + ": member " + student.getStudentId() + " already in group");
            return;
        }
        if (members.size() >= 5) {
            Logger.error("FYPGroup " + groupId + ": cannot add member, group full (max 5)");
            throw new InvalidFYPGroupException("Group is full (maximum 5 members)");
        }
        members.add(student);
        Logger.info("FYPGroup " + groupId + ": member added " + student.getStudentId());
    }

    public void removeMember(Student student) throws InvalidFYPGroupException {
        if (student == null) {
            Logger.error("FYPGroup " + groupId + ": cannot remove null member");
            throw new InvalidFYPGroupException("Cannot remove null member from group");
        }
        if (members.remove(student)) {
            Logger.info("FYPGroup " + groupId + ": member removed " + student.getStudentId());
        } else {
            Logger.warning("FYPGroup " + groupId + ": member " + student.getStudentId() + " not found in group");
            throw new InvalidFYPGroupException("Member not found in group");
        }
    }

    public void assignSupervisor(PermanentInstructor supervisor) throws InvalidFYPGroupException {
        if (supervisor == null) {
            Logger.error("FYPGroup " + groupId + ": cannot assign null supervisor");
            throw new InvalidFYPGroupException("Cannot assign null supervisor");
        }
        this.supervisor = supervisor;
        Logger.info("FYPGroup " + groupId + ": supervisor assigned " + supervisor.getTeacherId());
    }

    public void addMeeting(FYPMeeting meeting) throws InvalidFYPGroupException {
        if (meeting == null) {
            Logger.error("FYPGroup " + groupId + ": cannot add null meeting");
            throw new InvalidFYPGroupException("Cannot add null meeting");
        }
        meetings.add(meeting);
        Logger.info("FYPGroup " + groupId + ": meeting added " + meeting.getMeetingId());
    }

    public void addEvaluation(FYPEvaluation evaluation) throws InvalidFYPGroupException {
        if (evaluation == null) {
            Logger.error("FYPGroup " + groupId + ": cannot add null evaluation");
            throw new InvalidFYPGroupException("Cannot add null evaluation");
        }
        if (supervisor == null) {
            Logger.error("FYPGroup " + groupId + ": cannot add evaluation, no supervisor assigned");
            throw new InvalidFYPGroupException("Cannot add evaluation: group has no supervisor");
        }
        evaluations.add(evaluation);
        Logger.info("FYPGroup " + groupId + ": evaluation added " + evaluation.getEvaluationId());
    }

    public String getDetails() {
        return "Group ID: " + groupId + ", Title: " + title + ", Description: " + description +
                ", Members: " + members.size() + ", Supervisor: " + (supervisor != null ? supervisor.getName() : "None") +
                ", Meetings: " + meetings.size() + ", Evaluations: " + evaluations.size();
    }
}