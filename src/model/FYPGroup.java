package model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

class FYPGroup {
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
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public List<Student> getMembers() { return members; }
    public PermanentInstructor getSupervisor() { return supervisor; }
    public void setSupervisor(PermanentInstructor supervisor) { this.supervisor = supervisor; }
    public List<FYPMeeting> getMeetings() { return meetings; }
    public List<FYPEvaluation> getEvaluations() { return evaluations; }
}