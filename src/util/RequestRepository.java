package util;

import java.io.*;
import java.time.*;
import java.util.*;
import enums.*;
import exceptions.*;
import model.*;

public class RequestRepository {
    private static final String PATH = "data/requests.txt";
    private static final String TYPE_CLASH = "CLASH";
    private static final String TYPE_GENERIC = "GENERIC";

    public static List<Request> load(Map<String, Section> sections) throws IOException, InvalidRequestException {
        List<Request> requests = new ArrayList<Request>();
        List<String> lines = DelimitedFiles.readLines(PATH);
        for (String line : lines) {
            String[] parts = DelimitedFiles.split(line);
            if (parts.length < 6) {
                Logger.warning("Skipping malformed request record: " + line);
                continue;
            }
            String type = parts[0].trim().toUpperCase();
            String requestId = parts[1].trim();
            LocalDate date = LocalDate.parse(parts[2].trim());
            String description = parts[3].trim();
            RequestStatus status = RequestStatus.valueOf(parts[4].trim().toUpperCase());
            int priority;
            try {
                priority = Integer.parseInt(parts[5].trim());
            } catch (NumberFormatException ex) {
                Logger.warning("Skipping request " + requestId + ": invalid priority");
                continue;
            }

            Request request;
            if (TYPE_CLASH.equals(type)) {
                if (parts.length < 8) {
                    Logger.warning("Skipping clash request " + requestId + ": missing section identifiers");
                    continue;
                }
                Section conflicting = sections == null ? null : sections.get(parts[6].trim().toUpperCase());
                Section requested = sections == null ? null : sections.get(parts[7].trim().toUpperCase());
                if (conflicting == null || requested == null) {
                    Logger.warning("Skipping clash request " + requestId + ": section was not found");
                    continue;
                }
                request = new CourseClashRequest(requestId, date, description, priority, conflicting, requested);
            } else if (TYPE_GENERIC.equals(type)) {
                if (parts.length < 7) {
                    Logger.warning("Skipping generic request " + requestId + ": missing category");
                    continue;
                }
                RequestCategory category = RequestCategory.valueOf(parts[6].trim().toUpperCase());
                request = new GenericRequest(requestId, date, description, priority, category);
            } else {
                Logger.warning("Skipping request " + requestId + ": unknown type " + type);
                continue;
            }
            request.markLoaded(status);
            requests.add(request);
        }
        Logger.info("Loaded " + requests.size() + " request(s)");
        return requests;
    }

    public static void save(Collection<Request> requests) throws IOException {
        List<String> lines = new ArrayList<String>();
        if (requests != null) {
            for (Request request : requests) {
                if (request == null) {
                    continue;
                }
                if (request instanceof CourseClashRequest) {
                    CourseClashRequest clash = (CourseClashRequest) request;
                    String left = clash.getConflictingSection() == null ? DelimitedFiles.EMPTY
                            : clash.getConflictingSection().getSectionId();
                    String right = clash.getRequestedSection() == null ? DelimitedFiles.EMPTY
                            : clash.getRequestedSection().getSectionId();
                    lines.add(TYPE_CLASH + DelimitedFiles.SEPARATOR
                            + request.getRequestId() + DelimitedFiles.SEPARATOR
                            + request.getRequestDate() + DelimitedFiles.SEPARATOR
                            + DelimitedFiles.sanitize(request.getDescription()) + DelimitedFiles.SEPARATOR
                            + request.getStatus().name() + DelimitedFiles.SEPARATOR
                            + request.getPriority() + DelimitedFiles.SEPARATOR
                            + left + DelimitedFiles.SEPARATOR
                            + right);
                } else if (request instanceof GenericRequest) {
                    GenericRequest generic = (GenericRequest) request;
                    lines.add(TYPE_GENERIC + DelimitedFiles.SEPARATOR
                            + request.getRequestId() + DelimitedFiles.SEPARATOR
                            + request.getRequestDate() + DelimitedFiles.SEPARATOR
                            + DelimitedFiles.sanitize(request.getDescription()) + DelimitedFiles.SEPARATOR
                            + request.getStatus().name() + DelimitedFiles.SEPARATOR
                            + request.getPriority() + DelimitedFiles.SEPARATOR
                            + generic.getCategory().name());
                }
            }
        }
        DelimitedFiles.writeLines(PATH, lines);
    }
}
