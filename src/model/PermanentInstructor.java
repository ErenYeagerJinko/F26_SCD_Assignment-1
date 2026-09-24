package model;

import java.util.*;
import java.time.*;

public class PermanentInstructor extends Instructor implements Evaluator {
    private List<FYPGroup> supervisedGroups;

    public PermanentInstructor(String teacherId, String name, String email, String phone) {
        super(teacherId, name, email, phone);
        this.supervisedGroups = new ArrayList<>();
    }

    public void assignTA(NormalStudent student, Section section) {
        if (student != null && section != null) {
            TeachingAssistant ta = new TeachingAssistant(
                student.getStudentId(),
                student.getName(),
                student.getEmail(),
                student.getPhone(),
                section
            );
            section.assignTA(ta);
        }
    }

    public List<FYPGroup> viewFYPGroups() {
        return supervisedGroups;
    }

    public String viewFYPGroupDetails(FYPGroup group) {
        if (group != null) {
            return group.getDetails();
        }
        return "";
    }

    public void scheduleFYPMeeting(FYPGroup group, FYPMeeting meeting) {
        if (group != null && meeting != null) {
            group.addMeeting(meeting);
        }
    }

    public void evaluateFYPIdea(FYPGroup group, FYPEvaluation evaluation) {
        if (group != null && evaluation != null) {
            group.addEvaluation(evaluation);
        }
    }

    public void provideFYPFeedback(FYPEvaluation evaluation, String feedback) {
        if (evaluation != null && feedback != null) {
            evaluation.addFeedback(feedback);
        }
    }

    @Override
    public void evaluate() {
        for (FYPGroup group : supervisedGroups) {
            if (group != null) {
                FYPEvaluation evaluation = new FYPEvaluation("EVAL-" + System.currentTimeMillis(), LocalDate.now(), 100.0, "Evaluated by " + getName());
                evaluateFYPIdea(group, evaluation);
            }
        }
    }

    @Override
    public String getRole() {
        return "Permanent Instructor";
    }
}