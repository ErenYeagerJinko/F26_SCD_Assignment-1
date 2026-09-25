package model;

import java.io.*;
import java.util.*;
import exceptions.*;
import util.*;

public class NormalStudent extends Student {

    public NormalStudent(String studentId, String name, String email, String phone) throws InvalidUserDataException {
        super(studentId, name, email, phone);
        Logger.info("NormalStudent initialized: ID=" + getStudentId() + ", Name=" + getName());
    }

    @Override
    public String getRole() {
        return "Normal Student";
    }
}