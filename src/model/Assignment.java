import java.time.LocalDate;

class Assignment extends Assessment {
    public Assignment(String id, String title, String description, LocalDate deadline, double totalMarks) {
        super(id, title, description, deadline, totalMarks);
    }
}