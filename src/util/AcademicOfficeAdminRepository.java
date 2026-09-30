package util;

import java.io.*;
import java.util.*;
import exceptions.InvalidUserDataException;
import model.AcademicOfficeAdmin;

public class AcademicOfficeAdminRepository {
    private static final String PATH = "data/academic_office_admins.txt";

    public static Map<String, AcademicOfficeAdmin> load() throws IOException, InvalidUserDataException {
        Map<String, AcademicOfficeAdmin> admins = new HashMap<>();
        List<String> lines = DelimitedFiles.readLines(PATH);
        for (String line : lines) {
            String[] parts = DelimitedFiles.split(line);
            if (parts.length < 4) {
                Logger.warning("Skipping malformed academic office admin record: " + line);
                continue;
            }
            
            String id = parts[0].trim();
            String name = parts[1].trim();
            String email = parts[2].trim();
            String phone = parts[3].trim();

            try {
                AcademicOfficeAdmin admin = new AcademicOfficeAdmin(id, name, email, phone);
                admins.put(id, admin);
            } catch (InvalidUserDataException e) {
                Logger.warning("Failed to load academic office admin " + id + ": " + e.getMessage());
            }
        }
        Logger.info("Loaded " + admins.size() + " academic office admin(s)");
        return admins;
    }

    public static void save(Collection<AcademicOfficeAdmin> admins) throws IOException {
        List<String> lines = new ArrayList<>();
        if (admins != null) {
            for (AcademicOfficeAdmin a : admins) {
                if (a == null) continue;
                lines.add(a.getAdminId() + DelimitedFiles.SEPARATOR
                        + DelimitedFiles.sanitize(a.getName()) + DelimitedFiles.SEPARATOR
                        + DelimitedFiles.sanitize(a.getEmail()) + DelimitedFiles.SEPARATOR
                        + DelimitedFiles.sanitize(a.getPhone()));
            }
        }
        DelimitedFiles.writeLines(PATH, lines);
    }
}
