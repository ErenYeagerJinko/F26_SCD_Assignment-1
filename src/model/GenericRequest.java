package model;

import java.time.*;
import enums.*;
import exceptions.*;
import util.*;

public class GenericRequest extends Request {
    private RequestCategory category;

    public GenericRequest(String requestId, LocalDate requestDate, String description, int priority,
            RequestCategory category) throws InvalidRequestException {
        super(requestId, requestDate, description, priority);
        if (category == null) {
            Logger.error("Failed to create GenericRequest " + requestId + ": Category is null");
            throw new InvalidRequestException("Request category cannot be null.");
        }
        this.category = category;
        Logger.info("GenericRequest initialized: " + requestId + ", Category=" + category);
    }

    public RequestCategory getCategory() {
        return category;
    }

    @Override
    public String getDetails() {
        return super.getDetails() + " | Category: " + category;
    }
}
