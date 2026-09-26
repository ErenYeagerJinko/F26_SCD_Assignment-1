package model;

import java.time.LocalDate;

public class FYPMeeting {
    private String meetingId;
    private LocalDate meetingDate;
    private String agenda;
    private String notes;

    public FYPMeeting(String meetingId, LocalDate meetingDate, String agenda, String notes) {
        this.meetingId = meetingId;
        this.meetingDate = meetingDate;
        this.agenda = agenda;
        this.notes = notes;
    }

    public String getMeetingId() { return meetingId; }
    public void setMeetingId(String meetingId) { this.meetingId = meetingId; }
    public LocalDate getMeetingDate() { return meetingDate; }
    public void setMeetingDate(LocalDate meetingDate) { this.meetingDate = meetingDate; }
    public String getAgenda() { return agenda; }
    public void setAgenda(String agenda) { this.agenda = agenda; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getMeetingDetails() {
        return "Meeting ID: " + meetingId + ", Date: " + meetingDate + ", Agenda: " + agenda + ", Notes: " + notes;
    }

    public void updateNotes(String notes) {
        this.notes = notes;
    }
}