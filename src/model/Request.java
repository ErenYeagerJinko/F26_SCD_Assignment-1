import java.time.LocalDate;

abstract class Request {
    private String requestId;
    private LocalDate requestDate;
    private String description;
    private RequestStatus status;
    private int priority;

    public Request(String requestId, LocalDate requestDate, String description, RequestStatus status, int priority) {
        this.requestId = requestId;
        this.requestDate = requestDate;
        this.description = description;
        this.status = status;
        this.priority = priority;
    }

    public String getRequestId() { return requestId; }
    public LocalDate getRequestDate() { return requestDate; }
    public String getDescription() { return description; }
    public RequestStatus getStatus() { return status; }
    public int getPriority() { return priority; }
}