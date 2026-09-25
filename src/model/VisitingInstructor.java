package model;

import java.io.*;
import java.util.*;
import exceptions.*;
import util.*;

public class VisitingInstructor extends Instructor {

    public VisitingInstructor(String teacherId, String name, String email, String phone) throws InvalidUserDataException {
        super(teacherId, name, email, phone);
        Logger.info("VisitingInstructor initialized: Teacher ID=" + getTeacherId() + ", Name=" + getName());
    }

    public void assignTA(NormalStudent student, Section section) throws UnauthorizedActionException {
        Logger.error("Unauthorized operation: VisitingInstructor " + getTeacherId() + " attempted to assign TA");
        throw new UnauthorizedActionException("Visiting instructors cannot assign Teaching Assistants.");
    }

    public List<FYPGroup> viewFYPGroups() throws UnauthorizedActionException {
        Logger.error("Unauthorized operation: VisitingInstructor " + getTeacherId() + " attempted to view FYP groups");
        throw new UnauthorizedActionException("Visiting instructors cannot supervise or view FYP groups.");
    }

    public void scheduleFYPMeeting(FYPGroup group, FYPMeeting meeting) throws UnauthorizedActionException {
        Logger.error("Unauthorized operation: VisitingInstructor " + getTeacherId() + " attempted to schedule FYP meeting");
        throw new UnauthorizedActionException("Visiting instructors cannot schedule FYP meetings.");
    }

    public void evaluateFYPIdea(FYPGroup group, FYPEvaluation evaluation) throws UnauthorizedActionException {
        Logger.error("Unauthorized operation: VisitingInstructor " + getTeacherId() + " attempted to evaluate FYP idea");
        throw new UnauthorizedActionException("Visiting instructors cannot evaluate FYP ideas.");
    }

    @Override
    public String getRole() {
        return "Visiting Instructor";
    }
}