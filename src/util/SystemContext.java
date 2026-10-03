package util;

import java.util.*;
import model.*;

/**
 * Central orchestrator and memory context for the University Management System.
 * Manages all persistent collections, multi-repository startup loading,
 * shutdown persistence, cross-entity relationships, and global lookups.
 */
public class SystemContext {
    // Master collections
    public static Map<String, AcademicOfficeAdmin> admins = new LinkedHashMap<>();
    public static Map<String, NormalStudent> students = new LinkedHashMap<>();
    public static Map<String, TeachingAssistant> assistants = new LinkedHashMap<>();
    public static Map<String, PermanentInstructor> permanentInstructors = new LinkedHashMap<>();
    public static Map<String, VisitingInstructor> visitingInstructors = new LinkedHashMap<>();
    public static Map<String, Course> courses = new LinkedHashMap<>();
    public static Map<String, Section> sections = new LinkedHashMap<>();
    public static List<Enrollment> enrollments = new ArrayList<>();
    public static Map<String, Assignment> assignments = new LinkedHashMap<>();
    public static Map<String, Feedback> feedback = new LinkedHashMap<>();
    public static Map<String, Submission> submissions = new LinkedHashMap<>();
    public static List<Request> requests = new ArrayList<>();
    public static Map<String, FYPMeeting> fypMeetings = new LinkedHashMap<>();
    public static Map<String, FYPEvaluation> fypEvaluations = new LinkedHashMap<>();
    public static Map<String, FYPGroup> fypGroups = new LinkedHashMap<>();

    /**
     * Sequentially loads all 15 repositories in strict dependency order,
     * seeds initial demonstration profiles if user collections are empty,
     * and establishes bidirectional object associations.
     */
    public static void loadAll() {
        Logger.info("SystemContext: Starting complete system initialization and data load...");

        // 1. Admins
        try {
            admins = AcademicOfficeAdminRepository.load();
            if (admins.isEmpty()) {
                AcademicOfficeAdmin defaultAdmin = new AcademicOfficeAdmin("A01", "Admin Office", "admin@fast.nu.edu.pk", "03001234567");
                admins.put(defaultAdmin.getAdminId(), defaultAdmin);
                AcademicOfficeAdminRepository.save(admins.values());
                Logger.info("SystemContext: Seeded default admin A01");
            }
        } catch (Exception e) {
            Logger.error("SystemContext: Error loading admins: " + e.getMessage());
        }

        // 2. Normal Students
        try {
            students = NormalStudentRepository.load();
            if (students.isEmpty()) {
                NormalStudent s1 = new NormalStudent("ST-001", "Hamza Bilal", "hamza@student.fast.nu.edu.pk", "03111111111");
                NormalStudent s2 = new NormalStudent("ST-002", "Ayesha Noor", "ayesha@student.fast.nu.edu.pk", "03222222222");
                NormalStudent s3 = new NormalStudent("ST-003", "Zain Malik", "zain@student.fast.nu.edu.pk", "03333333333");
                students.put(s1.getStudentId(), s1);
                students.put(s2.getStudentId(), s2);
                students.put(s3.getStudentId(), s3);
                NormalStudentRepository.save(students.values());
                Logger.info("SystemContext: Seeded default students ST-001, ST-002, ST-003");
            }
        } catch (Exception e) {
            Logger.error("SystemContext: Error loading normal students: " + e.getMessage());
        }

        // 3. Permanent Instructors
        try {
            permanentInstructors = PermanentInstructorRepository.load();
            if (permanentInstructors.isEmpty()) {
                PermanentInstructor p1 = new PermanentInstructor("INS-01", "Dr. Ali Khan", "ali.khan@fast.nu.edu.pk", "03001112233");
                PermanentInstructor p2 = new PermanentInstructor("INS-02", "Dr. Sara Ahmed", "sara.ahmed@fast.nu.edu.pk", "03004445566");
                permanentInstructors.put(p1.getTeacherId(), p1);
                permanentInstructors.put(p2.getTeacherId(), p2);
                PermanentInstructorRepository.save(permanentInstructors.values());
                Logger.info("SystemContext: Seeded default permanent instructors INS-01, INS-02");
            }
        } catch (Exception e) {
            Logger.error("SystemContext: Error loading permanent instructors: " + e.getMessage());
        }

        // 4. Visiting Instructors
        try {
            visitingInstructors = VisitingInstructorRepository.load();
            if (visitingInstructors.isEmpty()) {
                VisitingInstructor v1 = new VisitingInstructor("VIS-01", "Mr. Usman Tariq", "usman.tariq@fast.nu.edu.pk", "03007778899");
                visitingInstructors.put(v1.getTeacherId(), v1);
                VisitingInstructorRepository.save(visitingInstructors.values());
                Logger.info("SystemContext: Seeded default visiting instructor VIS-01");
            }
        } catch (Exception e) {
            Logger.error("SystemContext: Error loading visiting instructors: " + e.getMessage());
        }

        // 5. Teaching Assistants
        try {
            assistants = TeachingAssistantRepository.load();
            if (assistants.isEmpty()) {
                TeachingAssistant ta1 = new TeachingAssistant("TA-001", "Bilal Tariq", "bilal.ta@fast.nu.edu.pk", "03444444444");
                assistants.put(ta1.getStudentId(), ta1);
                TeachingAssistantRepository.save(assistants.values());
                Logger.info("SystemContext: Seeded default teaching assistant TA-001");
            }
        } catch (Exception e) {
            Logger.error("SystemContext: Error loading teaching assistants: " + e.getMessage());
        }

        // Merged lookups for downstream loaders
        Map<String, Instructor> allInstructors = getAllInstructors();
        Map<String, Student> allStudents = getAllStudents();
        Map<String, Evaluator> allEvaluators = getAllEvaluators();

        // 6. Courses (with prerequisites)
        try {
            courses = CourseRepository.load();
        } catch (Exception e) {
            Logger.error("SystemContext: Error loading courses: " + e.getMessage());
        }

        // 7. Sections (with Course, Instructor, TA, Schedule)
        try {
            sections = SectionRepository.load(courses, allInstructors, assistants);
        } catch (Exception e) {
            Logger.error("SystemContext: Error loading sections: " + e.getMessage());
        }

        // Synchronize Admin managed courses and sections
        for (AcademicOfficeAdmin a : admins.values()) {
            for (Course c : courses.values()) {
                a.addManagedCourse(c);
            }
            for (Section s : sections.values()) {
                a.addManagedSection(s);
            }
        }

        // 8. Enrollments (links Students and Sections)
        try {
            enrollments = EnrollmentRepository.load(allStudents, sections);
            // Synchronize each student's total credit hours based on restored enrollments
            for (Student s : allStudents.values()) {
                s.calculateTotalCreditHours();
            }
        } catch (Exception e) {
            Logger.error("SystemContext: Error loading enrollments: " + e.getMessage());
        }

        // 9. Assignments (links Sections and TAs)
        try {
            assignments = AssignmentRepository.load(sections, assistants);
        } catch (Exception e) {
            Logger.error("SystemContext: Error loading assignments: " + e.getMessage());
        }

        // 10. Feedback (links Evaluators)
        try {
            feedback = FeedbackRepository.load(allEvaluators);
        } catch (Exception e) {
            Logger.error("SystemContext: Error loading feedback: " + e.getMessage());
        }

        // 11. Submissions (links Assignments, Students, Feedback)
        try {
            submissions = SubmissionRepository.load(assignments, allStudents, feedback);
            // Bidirectional link: ensure each assignment contains its loaded submissions
            for (Submission sub : submissions.values()) {
                if (sub != null && sub.getAssignment() != null) {
                    if (!sub.getAssignment().getSubmissions().contains(sub)) {
                        sub.getAssignment().addSubmission(sub);
                    }
                }
            }
        } catch (Exception e) {
            Logger.error("SystemContext: Error loading submissions: " + e.getMessage());
        }

        // 12. Requests (Generic and CourseClash)
        try {
            requests = RequestRepository.load(sections);
            // Synchronize with Admin managed requests
            for (AcademicOfficeAdmin a : admins.values()) {
                for (Request r : requests) {
                    a.addManagedRequest(r);
                }
            }
        } catch (Exception e) {
            Logger.error("SystemContext: Error loading requests: " + e.getMessage());
        }

        // 13. FYP Meetings
        try {
            fypMeetings = FYPMeetingRepository.load();
        } catch (Exception e) {
            Logger.error("SystemContext: Error loading FYP meetings: " + e.getMessage());
        }

        // 14. FYP Evaluations
        try {
            fypEvaluations = FYPEvaluationRepository.load();
        } catch (Exception e) {
            Logger.error("SystemContext: Error loading FYP evaluations: " + e.getMessage());
        }

        // 15. FYP Groups (links Students, Supervisors, Meetings, Evaluations)
        try {
            fypGroups = FYPGroupRepository.load(allStudents, permanentInstructors, fypMeetings, fypEvaluations);
            // Bidirectional link: ensure supervisor registers the group
            for (FYPGroup g : fypGroups.values()) {
                if (g != null && g.getSupervisor() != null) {
                    if (!g.getSupervisor().getSupervisedGroups().contains(g)) {
                        g.getSupervisor().addSupervisedGroup(g);
                    }
                }
            }
        } catch (Exception e) {
            Logger.error("SystemContext: Error loading FYP groups: " + e.getMessage());
        }

        Logger.info("SystemContext: System load completed successfully.");
    }

    /**
     * Persists all 15 entity collections back to data/*.txt.
     */
    public static void saveAll() {
        Logger.info("SystemContext: Starting complete system persistence...");

        try {
            AcademicOfficeAdminRepository.save(admins.values());
        } catch (Exception e) {
            Logger.error("SystemContext: Failed to save admins: " + e.getMessage());
        }

        try {
            NormalStudentRepository.save(students.values());
        } catch (Exception e) {
            Logger.error("SystemContext: Failed to save students: " + e.getMessage());
        }

        try {
            PermanentInstructorRepository.save(permanentInstructors.values());
        } catch (Exception e) {
            Logger.error("SystemContext: Failed to save permanent instructors: " + e.getMessage());
        }

        try {
            VisitingInstructorRepository.save(visitingInstructors.values());
        } catch (Exception e) {
            Logger.error("SystemContext: Failed to save visiting instructors: " + e.getMessage());
        }

        try {
            TeachingAssistantRepository.save(assistants.values());
        } catch (Exception e) {
            Logger.error("SystemContext: Failed to save teaching assistants: " + e.getMessage());
        }

        try {
            CourseRepository.save(courses.values());
        } catch (Exception e) {
            Logger.error("SystemContext: Failed to save courses: " + e.getMessage());
        }

        try {
            SectionRepository.save(sections.values());
        } catch (Exception e) {
            Logger.error("SystemContext: Failed to save sections: " + e.getMessage());
        }

        try {
            // Collect all enrollments from sections to ensure newly enrolled students are included
            EnrollmentRepository.saveFromSections(sections.values());
        } catch (Exception e) {
            Logger.error("SystemContext: Failed to save enrollments: " + e.getMessage());
        }

        try {
            AssignmentRepository.save(assignments.values());
        } catch (Exception e) {
            Logger.error("SystemContext: Failed to save assignments: " + e.getMessage());
        }

        try {
            FeedbackRepository.save(feedback.values());
        } catch (Exception e) {
            Logger.error("SystemContext: Failed to save feedback: " + e.getMessage());
        }

        try {
            SubmissionRepository.save(submissions.values());
        } catch (Exception e) {
            Logger.error("SystemContext: Failed to save submissions: " + e.getMessage());
        }

        try {
            RequestRepository.save(requests);
        } catch (Exception e) {
            Logger.error("SystemContext: Failed to save requests: " + e.getMessage());
        }

        try {
            FYPMeetingRepository.save(fypMeetings.values());
        } catch (Exception e) {
            Logger.error("SystemContext: Failed to save FYP meetings: " + e.getMessage());
        }

        try {
            FYPEvaluationRepository.save(fypEvaluations.values());
        } catch (Exception e) {
            Logger.error("SystemContext: Failed to save FYP evaluations: " + e.getMessage());
        }

        try {
            FYPGroupRepository.save(fypGroups.values());
        } catch (Exception e) {
            Logger.error("SystemContext: Failed to save FYP groups: " + e.getMessage());
        }

        Logger.info("SystemContext: Complete system persistence completed.");
    }

    // ==========================================
    // Aggregated Lookup & Utility Methods
    // ==========================================

    public static Map<String, Instructor> getAllInstructors() {
        Map<String, Instructor> map = new LinkedHashMap<>();
        map.putAll(permanentInstructors);
        map.putAll(visitingInstructors);
        return map;
    }

    public static Map<String, Student> getAllStudents() {
        Map<String, Student> map = new LinkedHashMap<>();
        map.putAll(students);
        map.putAll(assistants);
        return map;
    }

    public static Map<String, Evaluator> getAllEvaluators() {
        Map<String, Evaluator> map = new LinkedHashMap<>();
        map.putAll(permanentInstructors);
        map.putAll(assistants);
        return map;
    }

    public static AcademicOfficeAdmin getPrimaryAdmin() {
        if (!admins.isEmpty()) {
            return admins.values().iterator().next();
        }
        return null;
    }

    public static Student findStudent(String id) {
        if (id == null) return null;
        String clean = id.trim();
        if (students.containsKey(clean)) return students.get(clean);
        if (assistants.containsKey(clean)) return assistants.get(clean);
        return null;
    }

    public static Instructor findInstructor(String id) {
        if (id == null) return null;
        String clean = id.trim();
        if (permanentInstructors.containsKey(clean)) return permanentInstructors.get(clean);
        if (visitingInstructors.containsKey(clean)) return visitingInstructors.get(clean);
        return null;
    }

    public static Course findCourse(String code) {
        if (code == null) return null;
        return courses.get(code.trim().toUpperCase());
    }

    public static Section findSection(String id) {
        if (id == null) return null;
        return sections.get(id.trim().toUpperCase());
    }

    public static Assignment findAssignment(String id) {
        if (id == null) return null;
        return assignments.get(id.trim());
    }

    public static Submission findSubmission(String id) {
        if (id == null) return null;
        return submissions.get(id.trim());
    }

    public static FYPGroup findFYPGroup(String id) {
        if (id == null) return null;
        return fypGroups.get(id.trim());
    }

    public static Request findRequest(String id) {
        if (id == null) return null;
        String clean = id.trim();
        for (Request r : requests) {
            if (r != null && r.getRequestId().equalsIgnoreCase(clean)) {
                return r;
            }
        }
        return null;
    }

    // ==========================================
    // Entity Registration Helpers
    // ==========================================

    public static void addCourse(Course course) {
        if (course == null) return;
        courses.put(course.getCourseCode().toUpperCase(), course);
        for (AcademicOfficeAdmin admin : admins.values()) {
            admin.addManagedCourse(course);
        }
    }

    public static void addSection(Section section) {
        if (section == null) return;
        sections.put(section.getSectionId().toUpperCase(), section);
        for (AcademicOfficeAdmin admin : admins.values()) {
            admin.addManagedSection(section);
        }
    }

    public static void addStudent(NormalStudent student) {
        if (student == null) return;
        students.put(student.getStudentId(), student);
    }

    public static void addTeachingAssistant(TeachingAssistant ta) {
        if (ta == null) return;
        assistants.put(ta.getStudentId(), ta);
    }

    public static void addPermanentInstructor(PermanentInstructor instructor) {
        if (instructor == null) return;
        permanentInstructors.put(instructor.getTeacherId(), instructor);
    }

    public static void addVisitingInstructor(VisitingInstructor instructor) {
        if (instructor == null) return;
        visitingInstructors.put(instructor.getTeacherId(), instructor);
    }

    public static void addAssignment(Assignment assignment) {
        if (assignment == null) return;
        assignments.put(assignment.getId(), assignment);
        if (assignment.getSection() != null) {
            assignment.getSection().addAssignment(assignment);
        }
    }

    public static void addSubmission(Submission submission) {
        if (submission == null) return;
        submissions.put(submission.getSubmissionId(), submission);
        if (submission.getAssignment() != null) {
            submission.getAssignment().addSubmission(submission);
        }
    }

    public static void addFeedback(Feedback fb) {
        if (fb == null) return;
        feedback.put(fb.getFeedbackId(), fb);
    }

    public static void addRequest(Request req) {
        if (req == null) return;
        if (!requests.contains(req)) {
            requests.add(req);
        }
        for (AcademicOfficeAdmin admin : admins.values()) {
            admin.addManagedRequest(req);
        }
    }

    public static void addFYPGroup(FYPGroup group) {
        if (group == null) return;
        fypGroups.put(group.getGroupId(), group);
        if (group.getSupervisor() != null) {
            group.getSupervisor().addSupervisedGroup(group);
        }
    }

    public static void addFYPMeeting(FYPMeeting meeting) {
        if (meeting == null) return;
        fypMeetings.put(meeting.getMeetingId(), meeting);
    }

    public static void addFYPEvaluation(FYPEvaluation evaluation) {
        if (evaluation == null) return;
        fypEvaluations.put(evaluation.getEvaluationId(), evaluation);
    }
}
