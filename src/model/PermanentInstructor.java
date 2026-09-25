package model;

import java.io.*;
import java.util.*;
import java.time.*;
import exceptions.*;
import util.*;

public class PermanentInstructor extends Instructor implements Evaluator {
    private List<FYPGroup> supervisedGroups;

    public PermanentInstructor(String teacherId, String name, String email, String phone) throws InvalidUserDataException {
        super(teacherId, name, email, phone);
        this.supervisedGroups = new ArrayList<>();
        Logger.info("PermanentInstructor initialized: Teacher ID=" + getTeacherId() + ", Name=" + getName());
    }

    public void assignTA(NormalStudent student, Section section) throws InvalidUserDataException {
        if (student == null) {
            Logger.error("PermanentInstructor " + getTeacherId() + " failed to assign TA: Student is null");
            throw new InvalidUserDataException("Student cannot be null for TA assignment.");
        }
        if (section == null) {
            Logger.error("PermanentInstructor " + getTeacherId() + " failed to assign TA: Section is null");
            throw new InvalidUserDataException("Section cannot be null for TA assignment.");
        }

        Logger.info("PermanentInstructor " + getTeacherId() + " assigning student " + student.getStudentId() + " as TA for section " + section.getSectionId());
        TeachingAssistant ta = new TeachingAssistant(
            student.getStudentId(),
            student.getName(),
            student.getEmail(),
            student.getPhone(),
            section
        );
        section.assignTA(ta);
        Logger.info("TeachingAssistant " + student.getStudentId() + " assigned successfully to section " + section.getSectionId());
    }

    public List<FYPGroup> viewFYPGroups() {
        Logger.info("PermanentInstructor " + getTeacherId() + " viewing supervised FYP groups");
        return supervisedGroups;
    }

    public String viewFYPGroupDetails(FYPGroup group) throws InvalidFYPGroupException {
        if (group == null) {
            Logger.error("PermanentInstructor " + getTeacherId() + " failed to view FYP group details: Group is null");
            throw new InvalidFYPGroupException("FYP group cannot be null.");
        }
        Logger.info("PermanentInstructor " + getTeacherId() + " viewing details for FYP group: " + group.getGroupId());
        return group.getDetails();
    }

    public void scheduleFYPMeeting(FYPGroup group, FYPMeeting meeting) throws InvalidFYPGroupException {
        if (group == null) {
            Logger.error("PermanentInstructor " + getTeacherId() + " failed to schedule meeting: Group is null");
            throw new InvalidFYPGroupException("FYP group cannot be null.");
        }
        if (meeting == null) {
            Logger.error("PermanentInstructor " + getTeacherId() + " failed to schedule meeting: Meeting is null");
            throw new InvalidFYPGroupException("FYP meeting cannot be null.");
        }

        Logger.info("PermanentInstructor " + getTeacherId() + " scheduling FYP meeting for group: " + group.getGroupId());
        group.addMeeting(meeting);
        Logger.info("FYP meeting successfully scheduled for group: " + group.getGroupId());
    }

    public void evaluateFYPIdea(FYPGroup group, FYPEvaluation evaluation) throws InvalidFYPGroupException, InvalidFYPEvaluationException {
        if (group == null) {
            Logger.error("PermanentInstructor " + getTeacherId() + " failed to evaluate idea: FYP Group is null");
            throw new InvalidFYPGroupException("FYP group cannot be null.");
        }
        if (evaluation == null) {
            Logger.error("PermanentInstructor " + getTeacherId() + " failed to evaluate idea: Evaluation object is null");
            throw new InvalidFYPEvaluationException("FYP evaluation cannot be null.");
        }

        Logger.info("PermanentInstructor " + getTeacherId() + " evaluating FYP idea for group: " + group.getGroupId());
        group.addEvaluation(evaluation);
        Logger.info("FYP idea successfully evaluated for group: " + group.getGroupId());
    }

    public void provideFYPFeedback(FYPEvaluation evaluation, String feedback) throws InvalidFYPEvaluationException, InvalidUserDataException {
        if (evaluation == null) {
            Logger.error("PermanentInstructor " + getTeacherId() + " failed to provide feedback: Evaluation is null");
            throw new InvalidFYPEvaluationException("Evaluation cannot be null.");
        }
        if (feedback == null || feedback.trim().isEmpty()) {
            Logger.error("PermanentInstructor " + getTeacherId() + " failed to provide feedback: Feedback text is empty");
            throw new InvalidUserDataException("Feedback text cannot be empty.");
        }

        Logger.info("PermanentInstructor " + getTeacherId() + " providing feedback for evaluation ID: " + evaluation.getEvaluationId());
        evaluation.addFeedback(feedback.trim());
        Logger.info("Feedback successfully added for evaluation ID: " + evaluation.getEvaluationId());
    }

    @Override
    public void evaluate() {
        Logger.info("PermanentInstructor " + getTeacherId() + " running evaluate() on supervised FYP groups");
        for (FYPGroup group : supervisedGroups) {
            if (group != null) {
                try {
                    FYPEvaluation evaluation = new FYPEvaluation("EVAL-" + System.currentTimeMillis(), LocalDate.now(), 100.0, "Evaluated by " + getName());
                    evaluateFYPIdea(group, evaluation);
                } catch (Exception e) {
                    Logger.error("Error during auto-evaluation for group " + group.getGroupId() + ": " + e.getMessage());
                }
            }
        }
    }

    @Override
    public String getRole() {
        return "Permanent Instructor";
    }
}