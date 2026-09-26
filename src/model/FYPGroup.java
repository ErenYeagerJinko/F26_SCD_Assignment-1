package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

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

    public void addMember(Student student) {
        if (student != null && !members.contains(student)) {
            members.add(student);
        }
    }

    public void removeMember(Student student) {
        members.remove(student);
    }

    public void assignSupervisor(PermanentInstructor supervisor) {
        this.supervisor = supervisor;
    }

    public void addMeeting(FYPMeeting meeting) {
        if (meeting != null) {
            meetings.add(meeting);
        }
    }

    public void addEvaluation(FYPEvaluation evaluation) {
        if (evaluation != null) {
            evaluations.add(evaluation);
        }
    }

    public String getDetails() {
        return "Group ID: " + groupId + ", Title: " + title + ", Description: " + description +
                ", Members: " + members.size() + ", Supervisor: " + (supervisor != null ? supervisor.getName() : "None") +
                ", Meetings: " + meetings.size() + ", Evaluations: " + evaluations.size();
    }
}