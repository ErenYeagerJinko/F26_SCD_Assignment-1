package model;

import java.io.*;
import java.util.*;
import exceptions.*;
import util.*;

public abstract class Person {
    private String name;
    private String email;
    private String phone;

    public Person(String name, String email, String phone) throws InvalidUserDataException {
        if (name == null || name.trim().isEmpty()) {
            Logger.error("Failed to create Person: Name cannot be null or empty");
            throw new InvalidUserDataException("Name cannot be null or empty.");
        }
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            Logger.error("Failed to create Person: Invalid email format '" + email + "'");
            throw new InvalidUserDataException("Invalid email format: " + email);
        }
        if (phone == null || phone.trim().isEmpty()) {
            Logger.error("Failed to create Person: Phone cannot be null or empty");
            throw new InvalidUserDataException("Phone cannot be null or empty.");
        }

        this.name = name.trim();
        this.email = email.trim();
        this.phone = phone.trim();

        Logger.info("Person initialized: Name=" + this.name + ", Email=" + this.email + ", Phone=" + this.phone);
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public void setName(String name) throws InvalidUserDataException {
        if (name == null || name.trim().isEmpty()) {
            Logger.error("Failed to update name: Name cannot be null or empty");
            throw new InvalidUserDataException("Name cannot be null or empty.");
        }
        String oldName = this.name;
        this.name = name.trim();
        Logger.info("Updated name from '" + oldName + "' to '" + this.name + "'");
    }

    public void setEmail(String email) throws InvalidUserDataException {
        if (email == null || email.trim().isEmpty() || !email.contains("@")) {
            Logger.error("Failed to update email: Invalid email format '" + email + "'");
            throw new InvalidUserDataException("Invalid email format: " + email);
        }
        String oldEmail = this.email;
        this.email = email.trim();
        Logger.info("Updated email from '" + oldEmail + "' to '" + this.email + "'");
    }

    public void setPhone(String phone) throws InvalidUserDataException {
        if (phone == null || phone.trim().isEmpty()) {
            Logger.error("Failed to update phone: Phone cannot be null or empty");
            throw new InvalidUserDataException("Phone cannot be null or empty.");
        }
        String oldPhone = this.phone;
        this.phone = phone.trim();
        Logger.info("Updated phone from '" + oldPhone + "' to '" + this.phone + "'");
    }

    public abstract String getRole();
}