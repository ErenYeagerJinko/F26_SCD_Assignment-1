package model;

import java.time.*;
import enums.*;
import exceptions.*;
import util.*;

public abstract class Request {
    private String requestId;
    private LocalDate requestDate;
    private String description;
    private RequestStatus status;
    private int priority;
    private boolean submitted;

    public Request(String requestId, LocalDate requestDate, String description, int priority)
            throws InvalidRequestException {
        if (requestId == null || requestId.trim().isEmpty()) {
            Logger.error("Failed to create Request: Request ID cannot be null or empty");
            throw new InvalidRequestException("Request ID cannot be null or empty.");
        }
        if (requestDate == null) {
            Logger.error("Failed to create Request: Request date is null");
            throw new InvalidRequestException("Request date cannot be null.");
        }
        if (description == null || description.trim().isEmpty()) {
            Logger.error("Failed to create Request: Description is empty");
            throw new InvalidRequestException("Request description cannot be null or empty.");
        }
        if (priority < 0) {
            Logger.error("Failed to create Request: Negative priority " + priority);
            throw new InvalidRequestException("Request priority cannot be negative.");
        }

        this.requestId = requestId.trim();
        this.requestDate = requestDate;
        this.description = description.trim();
        this.status = RequestStatus.PENDING;
        this.priority = priority;
        this.submitted = false;
        Logger.info("Request initialized: ID=" + this.requestId + ", Priority=" + this.priority);
    }

    public String getRequestId() {
        return requestId;
    }

    public LocalDate getRequestDate() {
        return requestDate;
    }

    public String getDescription() {
        return description;
    }

    public void submit() throws InvalidRequestException {
        if (submitted) {
            Logger.error("Request " + requestId + " has already been submitted");
            throw new InvalidRequestException("Request " + requestId + " has already been submitted.");
        }
        if (status == RequestStatus.APPROVED || status == RequestStatus.REJECTED) {
            Logger.error("Request " + requestId + " cannot be submitted after it has been processed");
            throw new InvalidRequestException("Request " + requestId + " has already been processed.");
        }
        this.status = RequestStatus.PENDING;
        this.submitted = true;
        Logger.info("Request " + requestId + " submitted successfully");
    }

    public RequestStatus getStatus() {
        return status;
    }

    public void setStatus(RequestStatus status) throws InvalidRequestException {
        if (status == null) {
            Logger.error("Failed to update request " + requestId + ": Status is null");
            throw new InvalidRequestException("Request status cannot be null.");
        }
        if (this.status == RequestStatus.APPROVED || this.status == RequestStatus.REJECTED) {
            Logger.error("Request " + requestId + " has already been processed with status " + this.status);
            throw new InvalidRequestException("Request " + requestId + " has already been processed.");
        }
        this.status = status;
        Logger.info("Request " + requestId + " status set to " + status);
    }

    public int getPriority() {
        return priority;
    }

    public String getDetails() {
        return "Request " + requestId + " [" + status + ", priority " + priority + "]: " + description;
    }

    public void markLoaded(RequestStatus loadedStatus) throws InvalidRequestException {
        if (loadedStatus == null) {
            Logger.error("Failed to restore request " + requestId + ": Status is null");
            throw new InvalidRequestException("Request status cannot be null.");
        }
        this.status = loadedStatus;
        this.submitted = true;
        Logger.info("Request " + requestId + " restored from storage with status " + loadedStatus);
    }
}
