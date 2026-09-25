package model;

import java.io.*;
import java.util.*;
import exceptions.*;
import util.*;

public abstract class Administrator extends Person {
    private String adminId;

    public Administrator(String adminId, String name, String email, String phone) throws InvalidUserDataException {
        super(name, email, phone);
        if (adminId == null || adminId.trim().isEmpty()) {
            Logger.error("Failed to create Administrator: Admin ID cannot be null or empty");
            throw new InvalidUserDataException("Admin ID cannot be null or empty.");
        }
        this.adminId = adminId.trim();
        Logger.info("Administrator initialized with Admin ID: " + this.adminId);
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) throws InvalidUserDataException {
        if (adminId == null || adminId.trim().isEmpty()) {
            Logger.error("Failed to update Admin ID: ID cannot be null or empty");
            throw new InvalidUserDataException("Admin ID cannot be null or empty.");
        }
        String oldId = this.adminId;
        this.adminId = adminId.trim();
        Logger.info("Updated Admin ID from '" + oldId + "' to '" + this.adminId + "'");
    }
}