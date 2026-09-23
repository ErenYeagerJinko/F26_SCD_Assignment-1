import java.time.LocalDate;

class FYPMeeting {
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
    public LocalDate getMeetingDate() { return meetingDate; }
    public String getAgenda() { return agenda; }
    public String getNotes() { return notes; }
}