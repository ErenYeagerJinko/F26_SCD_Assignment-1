import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import comparators.*;
import enums.*;
import exceptions.*;
import model.*;
import util.*;

/**
 * Main interactive console entry point for the Campus Management System ("Life at FAST").
 * Connects all 29 use cases across Academic Office Admin, Normal Student,
 * Teaching Assistant, and Instructor roles backed by SystemContext and 5 Comparators.
 */
public class Main {
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Phase 2 Startup: load all 15 repositories and establish object graph
        try {
            SystemContext.loadAll();
            System.out.println("System initialized successfully.");
        } catch (Exception e) {
            Logger.severe("Fatal error during system initialization: " + e.getMessage());
            System.out.println("Initialization Error: " + e.getMessage());
            return;
        }

        boolean running = true;
        while (running) {
            System.out.println("\n=========================================");
            System.out.println("        CAMPUS MANAGEMENT SYSTEM         ");
            System.out.println("             (Life at FAST)              ");
            System.out.println("=========================================");
            System.out.println("1. Academic Office Admin Portal");
            System.out.println("2. Student Portal (Normal Student)");
            System.out.println("3. Teaching Assistant Portal");
            System.out.println("4. Instructor Portal (Visiting & Permanent)");
            System.out.println("5. Save & Exit");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();
            if (choice.isEmpty()) continue;
            switch (choice) {
                case "1":
                    adminPortal(scanner);
                    break;
                case "2":
                    studentPortal(scanner);
                    break;
                case "3":
                    taPortal(scanner);
                    break;
                case "4":
                    instructorPortal(scanner);
                    break;
                case "5":
                    running = false;
                    try {
                        SystemContext.saveAll();
                        System.out.println("All system data saved successfully to disk.");
                    } catch (Exception e) {
                        Logger.error("Failed to save data on exit: " + e.getMessage());
                        System.out.println("Error saving system data: " + e.getMessage());
                    }
                    System.out.println("Thank you for using FAST Campus Management System. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please enter 1-5.");
            }
        }
        scanner.close();
    }

    // =========================================================================
    // 1. ACADEMIC OFFICE ADMIN PORTAL (All 11 Admin Use Cases + User Creation)
    // =========================================================================

    private static void adminPortal(Scanner scanner) {
        AcademicOfficeAdmin admin = SystemContext.getPrimaryAdmin();
        if (admin == null) {
            System.out.println("No administrator registered in the system.");
            return;
        }

        boolean inAdminPortal = true;
        while (inAdminPortal) {
            System.out.println("\n--- Academic Office Admin Portal (" + admin.getName() + " [" + admin.getAdminId() + "]) ---");
            System.out.println("1.  Create Course");
            System.out.println("2.  Update Course");
            System.out.println("3.  Search Course by Code");
            System.out.println("4.  View All Courses");
            System.out.println("5.  Create Section");
            System.out.println("6.  Update Section");
            System.out.println("7.  Set Section Capacity");
            System.out.println("8.  Assign Room & Schedule");
            System.out.println("9.  Assign Instructor to Section");
            System.out.println("10. View Academic Requests (Comparator: Priority/Date)");
            System.out.println("11. Approve Request");
            System.out.println("12. Reject Request");
            System.out.println("13. Create User (Student / Instructor / Admin)");
            System.out.println("14. Back to Main Menu");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();
            if (choice.isEmpty()) continue;
            try {
                switch (choice) {
                    case "1": // Create Course
                        createCourseFlow(scanner, admin);
                        break;
                    case "2": // Update Course
                        updateCourseFlow(scanner, admin);
                        break;
                    case "3": // Search Course
                        searchCourseFlow(scanner, admin);
                        break;
                    case "4": // View All Courses
                        viewAllCoursesFlow();
                        break;
                    case "5": // Create Section
                        createSectionFlow(scanner, admin);
                        break;
                    case "6": // Update Section
                        updateSectionFlow(scanner, admin);
                        break;
                    case "7": // Set Section Capacity
                        setSectionCapacityFlow(scanner, admin);
                        break;
                    case "8": // Assign Room & Schedule
                        assignScheduleFlow(scanner, admin);
                        break;
                    case "9": // Assign Instructor
                        assignInstructorFlow(scanner, admin);
                        break;
                    case "10": // View Requests (Comparators used!)
                        viewRequestsFlow(scanner, admin);
                        break;
                    case "11": // Approve Request
                        approveRequestFlow(scanner, admin);
                        break;
                    case "12": // Reject Request
                        rejectRequestFlow(scanner, admin);
                        break;
                    case "13": // Create User
                        createUserFlow(scanner);
                        break;
                    case "14":
                        inAdminPortal = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please enter 1-14.");
                }
            } catch (CampusException e) {
                Logger.error("Admin Portal Action Failed: " + e.getMessage());
                System.out.println("[REJECTED]: " + e.getMessage());
            } catch (Exception e) {
                Logger.error("Unexpected error in Admin Portal: " + e.getMessage());
                System.out.println("[ERROR]: " + e.getMessage());
            }
        }
    }

    private static void createCourseFlow(Scanner scanner, AcademicOfficeAdmin admin) throws InvalidUserDataException {
        System.out.print("Enter Course Code (e.g. CS3001): ");
        String code = scanner.nextLine().trim().toUpperCase();
        if (SystemContext.findCourse(code) != null) {
            System.out.println("Course with code " + code + " already exists.");
            return;
        }
        System.out.print("Enter Course Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Enter Credit Hours (positive integer): ");
        int credits = Integer.parseInt(scanner.nextLine().trim());

        Course newCourse = new Course(code, title, credits);
        System.out.print("Enter Prerequisite Course Codes (comma-separated, or '-' for none): ");
        String prereqs = scanner.nextLine().trim();
        if (!prereqs.equals("-") && !prereqs.isEmpty()) {
            for (String pCode : prereqs.split(",")) {
                Course prereq = SystemContext.findCourse(pCode.trim());
                if (prereq != null) {
                    newCourse.addPrerequisite(prereq);
                } else {
                    System.out.println("Warning: Prerequisite " + pCode.trim() + " not found, skipping.");
                }
            }
        }

        admin.createCourse(newCourse);
        SystemContext.addCourse(newCourse);
        System.out.println("Course " + code + " (" + title + ") created successfully.");
    }

    private static void updateCourseFlow(Scanner scanner, AcademicOfficeAdmin admin) throws InvalidUserDataException {
        System.out.print("Enter Course Code to update: ");
        String code = scanner.nextLine().trim().toUpperCase();
        Course course = SystemContext.findCourse(code);
        if (course == null) {
            System.out.println("Course " + code + " not found.");
            return;
        }

        System.out.println("Current Title: " + course.getTitle() + ", Credits: " + course.getCreditHours());
        System.out.print("Enter New Title (leave blank to keep current): ");
        String newTitle = scanner.nextLine().trim();
        if (!newTitle.isEmpty()) {
            course.setTitle(newTitle);
        }

        System.out.print("Enter New Credit Hours (leave blank to keep current): ");
        String creditInput = scanner.nextLine().trim();
        if (!creditInput.isEmpty()) {
            int newCredits = Integer.parseInt(creditInput);
            course.setCreditHours(newCredits);
        }

        admin.updateCourse(course);
        System.out.println("Course " + code + " updated successfully.");
    }

    private static void searchCourseFlow(Scanner scanner, AcademicOfficeAdmin admin) throws InvalidUserDataException {
        System.out.print("Enter Course Code to search: ");
        String code = scanner.nextLine().trim();
        Course course = admin.searchCourse(code);
        if (course == null) {
            course = SystemContext.findCourse(code);
        }
        if (course == null) {
            System.out.println("No course found with code " + code);
            return;
        }

        System.out.println("\n=== Course Details ===");
        System.out.println("Code:         " + course.getCourseCode());
        System.out.println("Title:        " + course.getTitle());
        System.out.println("Credit Hours: " + course.getCreditHours());
        System.out.print("Prerequisites: ");
        if (course.getPrerequisites().isEmpty()) {
            System.out.println("None");
        } else {
            List<String> pList = new ArrayList<>();
            for (Course p : course.getPrerequisites()) pList.add(p.getCourseCode());
            System.out.println(String.join(", ", pList));
        }
        System.out.println("Active Sections: " + course.getSections().size());
        for (Section s : course.getSections()) {
            System.out.println("  • " + s.getSectionId() + " (Capacity: " + s.getCapacity() + ", Enrolled: " + s.getEnrolledStudents().size() + ")");
        }
    }

    private static void viewAllCoursesFlow() {
        if (SystemContext.courses.isEmpty()) {
            System.out.println("No courses currently in system.");
            return;
        }
        System.out.println("\n---------------------------------------------------------------------------------");
        System.out.printf("%-10s | %-35s | %-7s | %-15s | %-8s%n", "Code", "Title", "Credits", "Prerequisites", "Sections");
        System.out.println("---------------------------------------------------------------------------------");
        for (Course c : SystemContext.courses.values()) {
            String prereqs = "-";
            if (!c.getPrerequisites().isEmpty()) {
                List<String> codes = new ArrayList<>();
                for (Course p : c.getPrerequisites()) codes.add(p.getCourseCode());
                prereqs = String.join(",", codes);
            }
            System.out.printf("%-10s | %-35s | %-7d | %-15s | %-8d%n",
                    c.getCourseCode(),
                    c.getTitle().length() > 35 ? c.getTitle().substring(0, 32) + "..." : c.getTitle(),
                    c.getCreditHours(),
                    prereqs,
                    c.getSections().size());
        }
        System.out.println("---------------------------------------------------------------------------------");
    }

    private static void createSectionFlow(Scanner scanner, AcademicOfficeAdmin admin) throws InvalidUserDataException {
        System.out.print("Enter Section ID (e.g. CS101-C): ");
        String sectionId = scanner.nextLine().trim().toUpperCase();
        if (SystemContext.findSection(sectionId) != null) {
            System.out.println("Section " + sectionId + " already exists.");
            return;
        }

        System.out.print("Enter Course Code for this Section: ");
        String code = scanner.nextLine().trim().toUpperCase();
        Course course = SystemContext.findCourse(code);
        if (course == null) {
            System.out.println("Course " + code + " not found. Please create the course first.");
            return;
        }

        System.out.print("Enter Section Capacity: ");
        int capacity = Integer.parseInt(scanner.nextLine().trim());

        Section section = new Section(sectionId, capacity, course);
        admin.createSection(section);
        SystemContext.addSection(section);
        System.out.println("Section " + sectionId + " for course " + code + " created successfully.");
    }

    private static void updateSectionFlow(Scanner scanner, AcademicOfficeAdmin admin) throws InvalidUserDataException {
        System.out.print("Enter Section ID to update: ");
        String sectionId = scanner.nextLine().trim().toUpperCase();
        Section section = SystemContext.findSection(sectionId);
        if (section == null) {
            System.out.println("Section " + sectionId + " not found.");
            return;
        }

        System.out.print("Enter New Capacity (current: " + section.getCapacity() + ", leave blank to keep): ");
        String capInput = scanner.nextLine().trim();
        if (!capInput.isEmpty()) {
            admin.setCapacity(section, Integer.parseInt(capInput));
        }

        admin.updateSection(section);
        System.out.println("Section " + sectionId + " updated successfully.");
    }

    private static void setSectionCapacityFlow(Scanner scanner, AcademicOfficeAdmin admin) throws InvalidUserDataException {
        System.out.print("Enter Section ID: ");
        String sectionId = scanner.nextLine().trim().toUpperCase();
        Section section = SystemContext.findSection(sectionId);
        if (section == null) {
            System.out.println("Section " + sectionId + " not found.");
            return;
        }

        System.out.println("Section " + sectionId + " currently has " + section.getEnrolledStudents().size() + " enrolled students.");
        System.out.print("Enter New Capacity: ");
        int newCap = Integer.parseInt(scanner.nextLine().trim());
        admin.setCapacity(section, newCap);
        System.out.println("Capacity for section " + sectionId + " updated to " + newCap);
    }

    private static void assignScheduleFlow(Scanner scanner, AcademicOfficeAdmin admin) throws InvalidUserDataException {
        System.out.print("Enter Section ID: ");
        String sectionId = scanner.nextLine().trim().toUpperCase();
        Section section = SystemContext.findSection(sectionId);
        if (section == null) {
            System.out.println("Section " + sectionId + " not found.");
            return;
        }

        System.out.print("Enter Day of Week (e.g. MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY): ");
        Day day = Day.valueOf(scanner.nextLine().trim().toUpperCase());
        System.out.print("Enter Start Time (HH:mm, e.g. 09:00): ");
        LocalTime start = LocalTime.parse(scanner.nextLine().trim(), TIME_FORMAT);
        System.out.print("Enter End Time (HH:mm, e.g. 10:30): ");
        LocalTime end = LocalTime.parse(scanner.nextLine().trim(), TIME_FORMAT);
        System.out.print("Enter Room (e.g. C-401): ");
        String room = scanner.nextLine().trim();

        Schedule schedule = new Schedule(day, start, end, room);
        admin.assignRoom(section, schedule);
        System.out.println("Schedule " + schedule.getScheduleInfo() + " assigned to section " + sectionId);
    }

    private static void assignInstructorFlow(Scanner scanner, AcademicOfficeAdmin admin) throws InvalidUserDataException {
        System.out.print("Enter Section ID: ");
        String sectionId = scanner.nextLine().trim().toUpperCase();
        Section section = SystemContext.findSection(sectionId);
        if (section == null) {
            System.out.println("Section " + sectionId + " not found.");
            return;
        }

        System.out.println("\nAvailable Instructors:");
        for (Instructor inst : SystemContext.getAllInstructors().values()) {
            System.out.println("  • " + inst.getTeacherId() + ": " + inst.getName() + " (" + inst.getRole() + ")");
        }
        System.out.print("Enter Teacher ID to assign: ");
        String teacherId = scanner.nextLine().trim();
        Instructor instructor = SystemContext.findInstructor(teacherId);
        if (instructor == null) {
            System.out.println("Instructor " + teacherId + " not found.");
            return;
        }

        admin.assignInstructor(section, instructor);
        System.out.println("Instructor " + instructor.getName() + " assigned to section " + sectionId);
    }

    /**
     * Demonstrates RequestPriorityComparator and RequestDateComparator integration.
     */
    private static void viewRequestsFlow(Scanner scanner, AcademicOfficeAdmin admin) {
        List<Request> requestList = new ArrayList<>(SystemContext.requests);
        if (requestList.isEmpty()) {
            System.out.println("No academic requests recorded in system.");
            return;
        }

        System.out.println("\nSelect Sorting Option:");
        System.out.println("1. Sort by Priority (Highest First - RequestPriorityComparator)");
        System.out.println("2. Sort by Submission Date (Oldest First - RequestDateComparator)");
        System.out.print("Choice [1/2]: ");
        String sortChoice = scanner.nextLine().trim();

        if ("1".equals(sortChoice)) {
            Collections.sort(requestList, new RequestPriorityComparator());
            System.out.println("\n[Sorted by Priority (5 -> 1)]");
        } else {
            Collections.sort(requestList, new RequestDateComparator());
            System.out.println("\n[Sorted by Submission Date]");
        }

        System.out.println("------------------------------------------------------------------------------------------------------");
        System.out.printf("%-10s | %-8s | %-10s | %-8s | %-10s | %-40s%n", "Req ID", "Type", "Date", "Priority", "Status", "Details");
        System.out.println("------------------------------------------------------------------------------------------------------");
        for (Request req : requestList) {
            String type = (req instanceof CourseClashRequest) ? "CLASH" : "GENERIC";
            String details = req.getDescription();
            if (req instanceof CourseClashRequest) {
                CourseClashRequest c = (CourseClashRequest) req;
                details = c.getConflictingSection().getSectionId() + " <-> " + c.getRequestedSection().getSectionId() + ": " + c.getDescription();
            }
            if (details.length() > 40) details = details.substring(0, 37) + "...";

            System.out.printf("%-10s | %-8s | %-10s | %-8d | %-10s | %-40s%n",
                    req.getRequestId(), type, req.getRequestDate().format(DATE_FORMAT), req.getPriority(), req.getStatus(), details);
        }
        System.out.println("------------------------------------------------------------------------------------------------------");
    }

    private static void approveRequestFlow(Scanner scanner, AcademicOfficeAdmin admin) throws InvalidRequestException {
        System.out.print("Enter Request ID to approve: ");
        String reqId = scanner.nextLine().trim();
        Request request = SystemContext.findRequest(reqId);
        if (request == null) {
            System.out.println("Request " + reqId + " not found.");
            return;
        }

        admin.approveRequest(request);
        System.out.println("Request " + reqId + " marked as APPROVED.");
    }

    private static void rejectRequestFlow(Scanner scanner, AcademicOfficeAdmin admin) throws InvalidRequestException {
        System.out.print("Enter Request ID to reject: ");
        String reqId = scanner.nextLine().trim();
        Request request = SystemContext.findRequest(reqId);
        if (request == null) {
            System.out.println("Request " + reqId + " not found.");
            return;
        }

        admin.rejectRequest(request);
        System.out.println("Request " + reqId + " marked as REJECTED.");
    }

    private static void createUserFlow(Scanner scanner) throws InvalidUserDataException {
        System.out.println("\nSelect User Role to Create:");
        System.out.println("1. Normal Student");
        System.out.println("2. Permanent Instructor");
        System.out.println("3. Visiting Instructor");
        System.out.println("4. Academic Office Admin");
        System.out.print("Choice: ");
        String rChoice = scanner.nextLine().trim();

        System.out.print("Enter ID: ");
        String id = scanner.nextLine().trim();
        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Email: ");
        String email = scanner.nextLine().trim();
        System.out.print("Enter Phone: ");
        String phone = scanner.nextLine().trim();

        switch (rChoice) {
            case "1":
                NormalStudent student = new NormalStudent(id, name, email, phone);
                SystemContext.addStudent(student);
                System.out.println("Normal Student created: " + id);
                break;
            case "2":
                PermanentInstructor pInst = new PermanentInstructor(id, name, email, phone);
                SystemContext.addPermanentInstructor(pInst);
                System.out.println("Permanent Instructor created: " + id);
                break;
            case "3":
                VisitingInstructor vInst = new VisitingInstructor(id, name, email, phone);
                SystemContext.addVisitingInstructor(vInst);
                System.out.println("Visiting Instructor created: " + id);
                break;
            case "4":
                AcademicOfficeAdmin newAdmin = new AcademicOfficeAdmin(id, name, email, phone);
                SystemContext.admins.put(newAdmin.getAdminId(), newAdmin);
                System.out.println("Academic Office Admin created: " + id);
                break;
            default:
                System.out.println("Invalid selection.");
        }
    }

    // =========================================================================
    // 2. STUDENT PORTAL (All 10 Normal Student Use Cases)
    // =========================================================================

    private static void studentPortal(Scanner scanner) {
        if (SystemContext.students.isEmpty()) {
            System.out.println("No students registered in system. Please add one via Admin Portal.");
            return;
        }

        System.out.println("\nSelect Active Student Profile:");
        List<NormalStudent> studentList = new ArrayList<>(SystemContext.students.values());
        for (int i = 0; i < studentList.size(); i++) {
            NormalStudent s = studentList.get(i);
            System.out.println((i + 1) + ". " + s.getName() + " (" + s.getStudentId() + ") - " + s.getTotalCreditHours() + " Credits");
        }
        System.out.print("Enter student number: ");
        int sIdx;
        try {
            sIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }
        if (sIdx < 0 || sIdx >= studentList.size()) {
            System.out.println("Invalid student selection.");
            return;
        }
        NormalStudent student = studentList.get(sIdx);

        boolean inStudentPortal = true;
        while (inStudentPortal) {
            System.out.println("\n--- Student Portal: " + student.getName() + " (" + student.getStudentId()
                    + ") | Registered Credits: " + student.getTotalCreditHours() + " ---");
            System.out.println("1.  Browse Available Courses");
            System.out.println("2.  Register for Section (Capacity & Clash Checked)");
            System.out.println("3.  Drop Section");
            System.out.println("4.  View Registered Courses & Weekly Timetable");
            System.out.println("5.  View Attendance Records & Percentage");
            System.out.println("6.  View Assignments (Comparator: Earliest Deadline)");
            System.out.println("7.  Submit Assignment");
            System.out.println("8.  Submit Course Clash Request");
            System.out.println("9.  View My Submitted Requests");
            System.out.println("10. Switch Student / Back to Main Menu");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();
            if (choice.isEmpty()) continue;
            try {
                switch (choice) {
                    case "1":
                        viewAllCoursesFlow();
                        break;
                    case "2":
                        studentRegisterSectionFlow(scanner, student);
                        break;
                    case "3":
                        studentDropSectionFlow(scanner, student);
                        break;
                    case "4":
                        studentTimetableFlow(student);
                        break;
                    case "5":
                        studentAttendanceFlow(scanner, student);
                        break;
                    case "6":
                        studentViewAssignmentsFlow(student);
                        break;
                    case "7":
                        studentSubmitAssignmentFlow(scanner, student);
                        break;
                    case "8":
                        studentSubmitClashRequestFlow(scanner, student);
                        break;
                    case "9":
                        studentViewMyRequestsFlow(student);
                        break;
                    case "10":
                        inStudentPortal = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please enter 1-10.");
                }
            } catch (CourseFullException e) {
                Logger.warning("Registration blocked (Full): " + e.getMessage());
                System.out.println("[REGISTRATION FAILED - SECTION FULL]: " + e.getMessage());
            } catch (CourseClashException e) {
                Logger.warning("Registration blocked (Clash): " + e.getMessage());
                System.out.println("[REGISTRATION FAILED - TIMETABLE CLASH]: " + e.getMessage());
            } catch (SubmissionDeadlineException e) {
                Logger.warning("Submission blocked (Late): " + e.getMessage());
                System.out.println("[SUBMISSION REJECTED - DEADLINE PASSED]: " + e.getMessage());
            } catch (CampusException e) {
                Logger.error("Student action failed: " + e.getMessage());
                System.out.println("[ERROR]: " + e.getMessage());
            } catch (Exception e) {
                Logger.error("Unexpected error in Student Portal: " + e.getMessage());
                System.out.println("[ERROR]: " + e.getMessage());
            }
        }
    }

    private static void studentRegisterSectionFlow(Scanner scanner, NormalStudent student)
            throws CourseFullException, CourseClashException, InvalidUserDataException {
        System.out.println("\nAvailable Sections Offering Courses:");
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.printf("%-10s | %-10s | %-7s | %-9s | %-20s | %-10s%n", "Section", "Course", "Credits", "Seats", "Schedule", "Room");
        System.out.println("------------------------------------------------------------------------------------------");
        for (Section sec : SystemContext.sections.values()) {
            String sch = (sec.getSchedule() != null) ? (sec.getSchedule().getDay() + " " + sec.getSchedule().getStartTime() + "-" + sec.getSchedule().getEndTime()) : "TBA";
            String room = (sec.getSchedule() != null) ? sec.getSchedule().getRoom() : "TBA";
            System.out.printf("%-10s | %-10s | %-7d | %-2d/%-5d | %-20s | %-10s%n",
                    sec.getSectionId(),
                    sec.getCourse().getCourseCode(),
                    sec.getCourse().getCreditHours(),
                    sec.getEnrolledStudents().size(),
                    sec.getCapacity(),
                    sch,
                    room);
        }
        System.out.println("------------------------------------------------------------------------------------------");

        System.out.print("Enter Section ID to register: ");
        String sectionId = scanner.nextLine().trim().toUpperCase();
        Section section = SystemContext.findSection(sectionId);
        if (section == null) {
            System.out.println("Section " + sectionId + " not found.");
            return;
        }

        student.register(section);
        System.out.println("Successfully registered for section " + sectionId + "!");
        System.out.println("Your total registered credit hours: " + student.getTotalCreditHours());
    }

    private static void studentDropSectionFlow(Scanner scanner, NormalStudent student) throws InvalidUserDataException {
        List<Enrollment> active = new ArrayList<>();
        for (Enrollment e : student.getEnrollments()) {
            if (e != null && e.getStatus() == EnrollmentStatus.ACTIVE) {
                active.add(e);
            }
        }

        if (active.isEmpty()) {
            System.out.println("You are not currently enrolled in any section.");
            return;
        }

        System.out.println("\nYour Active Sections:");
        for (Enrollment e : active) {
            System.out.println("  • " + e.getSection().getSectionId() + " (" + e.getSection().getCourse().getTitle() + ")");
        }
        System.out.print("Enter Section ID to drop: ");
        String secId = scanner.nextLine().trim().toUpperCase();
        Section target = SystemContext.findSection(secId);
        if (target == null) {
            System.out.println("Section " + secId + " not found.");
            return;
        }

        student.drop(target);
        System.out.println("Section " + secId + " dropped successfully.");
        System.out.println("Updated credit hours: " + student.getTotalCreditHours());
    }

    private static void studentTimetableFlow(NormalStudent student) {
        System.out.println("\n=== Enrolled Courses & Weekly Schedule ===");
        List<Course> courses = student.viewCourses();
        if (courses.isEmpty()) {
            System.out.println("No enrolled courses.");
            return;
        }

        System.out.println("Registered Courses (" + student.getTotalCreditHours() + " Total Credits):");
        for (Course c : courses) {
            System.out.println("  • " + c.getCourseCode() + " - " + c.getTitle() + " (" + c.getCreditHours() + " Cr. Hrs)");
        }

        System.out.println("\nWeekly Timetable:");
        List<Schedule> timetable = student.viewTimetable();
        if (timetable.isEmpty()) {
            System.out.println("No schedule set for enrolled sections.");
        } else {
            for (Schedule s : timetable) {
                System.out.println("  • " + s.getScheduleInfo());
            }
        }
    }

    private static void studentAttendanceFlow(Scanner scanner, NormalStudent student) {
        List<Course> courses = student.viewCourses();
        if (courses.isEmpty()) {
            System.out.println("No active courses to view attendance.");
            return;
        }

        System.out.print("Enter Section ID to check attendance: ");
        String secId = scanner.nextLine().trim().toUpperCase();
        Section section = SystemContext.findSection(secId);
        if (section == null) {
            System.out.println("Section " + secId + " not found.");
            return;
        }

        List<Attendance> records = student.viewAttendance(section);
        double pct = student.viewAttendancePercentage(section);

        System.out.println("\n=== Attendance for Section " + secId + " ===");
        if (records.isEmpty()) {
            System.out.println("No attendance sessions marked yet for this section.");
        } else {
            for (Attendance a : records) {
                System.out.println("  Date: " + a.getDate().format(DATE_FORMAT) + " | Status: " + a.getStatus());
            }
        }
        System.out.printf("Overall Attendance: %.2f%%%n", pct);
    }

    /**
     * Demonstrates AssignmentDeadlineComparator integration.
     */
    private static void studentViewAssignmentsFlow(NormalStudent student) {
        List<Assignment> asgs = student.viewAssignments();
        if (asgs.isEmpty()) {
            System.out.println("No assignments posted for your enrolled sections.");
            return;
        }

        // Sort assignments by earliest deadline first using AssignmentDeadlineComparator
        Collections.sort(asgs, new AssignmentDeadlineComparator());

        System.out.println("\n=== Assignments for Enrolled Sections (Sorted by Earliest Deadline) ===");
        System.out.println("---------------------------------------------------------------------------------------");
        System.out.printf("%-10s | %-10s | %-25s | %-12s | %-8s | %-10s%n", "Asg ID", "Section", "Title", "Deadline", "Marks", "Status");
        System.out.println("---------------------------------------------------------------------------------------");
        for (Assignment a : asgs) {
            boolean submitted = false;
            for (Submission s : a.getSubmissions()) {
                if (s.getStudent().getStudentId().equals(student.getStudentId())) {
                    submitted = true;
                    break;
                }
            }
            String status = submitted ? "SUBMITTED" : (a.isDeadlinePassed() ? "OVERDUE" : "PENDING");
            System.out.printf("%-10s | %-10s | %-25s | %-12s | %-8.1f | %-10s%n",
                    a.getId(),
                    a.getSection().getSectionId(),
                    a.getTitle().length() > 25 ? a.getTitle().substring(0, 22) + "..." : a.getTitle(),
                    a.getDeadline().format(DATE_FORMAT),
                    a.getTotalMarks(),
                    status);
        }
        System.out.println("---------------------------------------------------------------------------------------");
    }

    private static void studentSubmitAssignmentFlow(Scanner scanner, NormalStudent student)
            throws SubmissionDeadlineException, InvalidUserDataException {
        System.out.print("Enter Assignment ID to submit: ");
        String asgId = scanner.nextLine().trim();
        Assignment assignment = SystemContext.findAssignment(asgId);
        if (assignment == null) {
            System.out.println("Assignment " + asgId + " not found.");
            return;
        }

        System.out.println("Assignment: " + assignment.getTitle() + " | Deadline: " + assignment.getDeadline().format(DATE_FORMAT));
        System.out.print("Enter your submission content / solution text: ");
        String content = scanner.nextLine().trim();

        Submission submission = student.submitAssignment(assignment, content);
        SystemContext.addSubmission(submission);
        System.out.println("Assignment submitted successfully! Submission ID: " + submission.getSubmissionId());
    }

    private static void studentSubmitClashRequestFlow(Scanner scanner, NormalStudent student) throws InvalidRequestException {
        System.out.print("Enter Conflicting Section ID (e.g. CS101-A): ");
        String cSecId = scanner.nextLine().trim().toUpperCase();
        Section conflicting = SystemContext.findSection(cSecId);

        System.out.print("Enter Requested Section ID (e.g. CS201-A): ");
        String rSecId = scanner.nextLine().trim().toUpperCase();
        Section requested = SystemContext.findSection(rSecId);

        if (conflicting == null || requested == null) {
            System.out.println("One or both sections could not be found.");
            return;
        }

        System.out.print("Enter Reason / Description: ");
        String desc = scanner.nextLine().trim();
        System.out.print("Enter Priority (1-5, with 5 being urgent): ");
        int priority = Integer.parseInt(scanner.nextLine().trim());

        String reqId = "REQ-C-" + (SystemContext.requests.size() + 1);
        CourseClashRequest clashRequest = new CourseClashRequest(reqId, LocalDate.now(), desc, priority, conflicting, requested);

        student.submitCourseClashRequest(clashRequest);
        SystemContext.addRequest(clashRequest);
        System.out.println("Course clash request " + reqId + " submitted to Academic Office Admin.");
    }

    private static void studentViewMyRequestsFlow(NormalStudent student) {
        List<Request> myReqs = student.viewRequests();
        if (myReqs.isEmpty()) {
            System.out.println("You have not submitted any requests.");
            return;
        }

        System.out.println("\n=== My Submitted Requests ===");
        for (Request r : myReqs) {
            System.out.println("  • [" + r.getRequestId() + "] Date: " + r.getRequestDate()
                    + " | Priority: " + r.getPriority()
                    + " | Status: " + r.getStatus()
                    + " | " + r.getDescription());
        }
    }

    // =========================================================================
    // 3. TEACHING ASSISTANT PORTAL (All 6 TA Use Cases)
    // =========================================================================

    private static void taPortal(Scanner scanner) {
        if (SystemContext.assistants.isEmpty()) {
            System.out.println("No Teaching Assistants appointed in the system.");
            System.out.println("(A Permanent Instructor can appoint a student as TA from the Instructor Portal).");
            return;
        }

        System.out.println("\nSelect Active Teaching Assistant Profile:");
        List<TeachingAssistant> taList = new ArrayList<>(SystemContext.assistants.values());
        for (int i = 0; i < taList.size(); i++) {
            TeachingAssistant ta = taList.get(i);
            String secInfo = (ta.getAssignedSection() != null) ? ta.getAssignedSection().getSectionId() : "Unassigned";
            System.out.println((i + 1) + ". " + ta.getName() + " (" + ta.getStudentId() + ") - Section: " + secInfo);
        }
        System.out.print("Enter TA number: ");
        int taIdx;
        try {
            taIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }
        if (taIdx < 0 || taIdx >= taList.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        TeachingAssistant ta = taList.get(taIdx);

        boolean inTaPortal = true;
        while (inTaPortal) {
            String secName = (ta.getAssignedSection() != null) ? ta.getAssignedSection().getSectionId() : "None";
            System.out.println("\n--- Teaching Assistant Portal: " + ta.getName() + " (" + ta.getStudentId()
                    + ") | Assigned Section: " + secName + " ---");
            System.out.println("1. View Assigned Section Details");
            System.out.println("2. View Enrolled Students (Comparator: StudentNameComparator)");
            System.out.println("3. Create Assignment (Sets Total Marks & Deadline)");
            System.out.println("4. View Submissions for Assignment");
            System.out.println("5. Check Late Submissions");
            System.out.println("6. Evaluate Submission (Assign Marks & Feedback Atomically)");
            System.out.println("7. Switch TA / Back to Main Menu");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();
            if (choice.isEmpty()) continue;
            try {
                switch (choice) {
                    case "1":
                        taViewAssignedSectionFlow(ta);
                        break;
                    case "2":
                        taViewStudentsFlow(ta);
                        break;
                    case "3":
                        taCreateAssignmentFlow(scanner, ta);
                        break;
                    case "4":
                        taViewSubmissionsFlow(scanner, ta);
                        break;
                    case "5":
                        taCheckLateFlow(scanner, ta);
                        break;
                    case "6":
                        taEvaluateSubmissionFlow(scanner, ta);
                        break;
                    case "7":
                        inTaPortal = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please enter 1-7.");
                }
            } catch (CampusException e) {
                Logger.error("TA Action Failed: " + e.getMessage());
                System.out.println("[ERROR]: " + e.getMessage());
            } catch (Exception e) {
                Logger.error("Unexpected error in TA Portal: " + e.getMessage());
                System.out.println("[ERROR]: " + e.getMessage());
            }
        }
    }

    private static void taViewAssignedSectionFlow(TeachingAssistant ta) {
        Section sec = ta.getAssignedSection();
        if (sec == null) {
            System.out.println("You are not currently assigned to any section.");
            return;
        }
        System.out.println("\n=== Assigned Section Details ===");
        System.out.println("Section ID:   " + sec.getSectionId());
        System.out.println("Course:       " + sec.getCourse().getCourseCode() + " - " + sec.getCourse().getTitle());
        System.out.println("Capacity:     " + sec.getEnrolledStudents().size() + " / " + sec.getCapacity());
        System.out.println("Instructor:   " + (sec.getInstructor() != null ? sec.getInstructor().getName() : "Unassigned"));
        System.out.println("Schedule:     " + (sec.getSchedule() != null ? sec.getSchedule().getScheduleInfo() : "TBA"));
        System.out.println("Assignments:  " + sec.getAssignments().size());
    }

    /**
     * Demonstrates StudentNameComparator integration in TA Portal.
     */
    private static void taViewStudentsFlow(TeachingAssistant ta) throws UnauthorizedActionException {
        List<Student> students = ta.viewEnrolledStudents();
        if (students.isEmpty()) {
            System.out.println("No students enrolled in your assigned section.");
            return;
        }

        // Sort students alphabetically by name using StudentNameComparator
        Collections.sort(students, new StudentNameComparator());

        System.out.println("\n=== Enrolled Students (Alphabetically Sorted via StudentNameComparator) ===");
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            System.out.println((i + 1) + ". " + s.getName() + " | ID: " + s.getStudentId() + " | Email: " + s.getEmail());
        }
    }

    private static void taCreateAssignmentFlow(Scanner scanner, TeachingAssistant ta) throws InvalidUserDataException {
        if (ta.getAssignedSection() == null) {
            System.out.println("Cannot create assignment: you are not assigned to any section.");
            return;
        }

        System.out.print("Enter Assignment ID (e.g. ASG-201): ");
        String asgId = scanner.nextLine().trim();
        System.out.print("Enter Assignment Title: ");
        String title = scanner.nextLine().trim();
        System.out.print("Enter Description: ");
        String desc = scanner.nextLine().trim();
        System.out.print("Enter Deadline (yyyy-MM-dd): ");
        LocalDate deadline = LocalDate.parse(scanner.nextLine().trim(), DATE_FORMAT);
        System.out.print("Enter Total Marks: ");
        double marks = Double.parseDouble(scanner.nextLine().trim());

        Assignment asg = new Assignment(asgId, title, desc, deadline, marks, ta.getAssignedSection(), ta);
        SystemContext.addAssignment(asg);
        System.out.println("Assignment " + asgId + " (" + title + ") created for section " + ta.getAssignedSection().getSectionId());
    }

    private static void taViewSubmissionsFlow(Scanner scanner, TeachingAssistant ta)
            throws AssessmentException, InvalidUserDataException {
        if (ta.getAssignedSection() == null) {
            System.out.println("No assigned section.");
            return;
        }

        System.out.print("Enter Assignment ID to view submissions: ");
        String asgId = scanner.nextLine().trim();
        Assignment asg = SystemContext.findAssignment(asgId);
        if (asg == null) {
            System.out.println("Assignment " + asgId + " not found.");
            return;
        }

        List<Submission> subs = ta.viewSubmissions(asg);
        if (subs.isEmpty()) {
            System.out.println("No submissions received for assignment " + asgId);
            return;
        }

        System.out.println("\n=== Submissions for " + asg.getTitle() + " ===");
        System.out.println("---------------------------------------------------------------------------------------");
        System.out.printf("%-10s | %-12s | %-20s | %-12s | %-8s | %-10s%n", "Sub ID", "Student ID", "Student Name", "Date", "Marks", "Status");
        System.out.println("---------------------------------------------------------------------------------------");
        for (Submission s : subs) {
            System.out.printf("%-10s | %-12s | %-20s | %-12s | %-8.1f | %-10s%n",
                    s.getSubmissionId(),
                    s.getStudent().getStudentId(),
                    s.getStudent().getName(),
                    s.getSubmissionDate().format(DATE_FORMAT),
                    s.getMarks(),
                    s.getStatus());
        }
        System.out.println("---------------------------------------------------------------------------------------");
    }

    private static void taCheckLateFlow(Scanner scanner, TeachingAssistant ta)
            throws AssessmentException, InvalidUserDataException {
        System.out.print("Enter Assignment ID to check late submissions: ");
        String asgId = scanner.nextLine().trim();
        Assignment asg = SystemContext.findAssignment(asgId);
        if (asg == null) {
            System.out.println("Assignment " + asgId + " not found.");
            return;
        }

        List<Submission> late = ta.checkLateSubmissions(asg);
        if (late.isEmpty()) {
            System.out.println("No late submissions found for assignment " + asgId);
            return;
        }

        System.out.println("\n=== Late Submissions for " + asg.getTitle() + " ===");
        for (Submission s : late) {
            System.out.println("  • " + s.getSubmissionId() + " by " + s.getStudent().getName()
                    + " (" + s.getStudent().getStudentId() + ") on " + s.getSubmissionDate());
        }
    }

    private static void taEvaluateSubmissionFlow(Scanner scanner, TeachingAssistant ta) throws AssessmentException, InvalidUserDataException {
        System.out.print("Enter Submission ID to evaluate: ");
        String subId = scanner.nextLine().trim();
        Submission sub = SystemContext.findSubmission(subId);
        if (sub == null) {
            System.out.println("Submission " + subId + " not found.");
            return;
        }

        System.out.println("Evaluating submission for: " + sub.getAssignment().getTitle()
                + " by " + sub.getStudent().getName());
        System.out.println("Content Preview: " + sub.getContent());
        System.out.print("Enter Marks (Max " + sub.getAssignment().getTotalMarks() + "): ");
        double marks = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Enter Feedback Comments: ");
        String comments = scanner.nextLine().trim();

        ta.evaluateSubmission(sub, marks, comments);
        if (sub.getFeedback() != null) {
            SystemContext.addFeedback(sub.getFeedback());
        }
        System.out.println("Submission evaluated successfully: " + marks + " marks assigned with feedback.");
    }

    // =========================================================================
    // 4. INSTRUCTOR PORTAL (Visiting & Permanent Roles, All 10 Instructor Use Cases)
    // =========================================================================

    private static void instructorPortal(Scanner scanner) {
        Map<String, Instructor> allInst = SystemContext.getAllInstructors();
        if (allInst.isEmpty()) {
            System.out.println("No instructors registered in the system.");
            return;
        }

        System.out.println("\nSelect Active Instructor Profile:");
        List<Instructor> instList = new ArrayList<>(allInst.values());
        for (int i = 0; i < instList.size(); i++) {
            Instructor inst = instList.get(i);
            System.out.println((i + 1) + ". " + inst.getName() + " (" + inst.getTeacherId() + ") - [" + inst.getRole() + "]");
        }
        System.out.print("Enter instructor number: ");
        int idx;
        try {
            idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        } catch (NumberFormatException e) {
            System.out.println("Invalid input.");
            return;
        }
        if (idx < 0 || idx >= instList.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        Instructor instructor = instList.get(idx);
        boolean isPermanent = (instructor instanceof PermanentInstructor);

        boolean inInstPortal = true;
        while (inInstPortal) {
            System.out.println("\n--- Instructor Portal: " + instructor.getName() + " (" + instructor.getTeacherId()
                    + ") | Role: " + instructor.getRole() + " ---");
            System.out.println("1.  View Assigned Courses");
            System.out.println("2.  View Assigned Sections");
            System.out.println("3.  View Enrolled Students in Section (Comparator: StudentNameComparator)");
            System.out.println("4.  Mark Attendance (Present / Absent / Late)");
            System.out.println("5.  Update Attendance Record");
            System.out.println("6.  Calculate Attendance Percentage for Student");
            System.out.println("--- Permanent Instructor Only Privileges ---");
            System.out.println("7.  [Permanent] Assign Teaching Assistant to Section");
            System.out.println("8.  [Permanent] View Supervised FYP Groups");
            System.out.println("9.  [Permanent] Schedule FYP Meeting (Comparator: FYPMeetingDateComparator)");
            System.out.println("10. [Permanent] Evaluate FYP Idea (Score & Feedback)");
            System.out.println("11. Switch Instructor / Back to Main Menu");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();
            if (choice.isEmpty()) continue;
            try {
                switch (choice) {
                    case "1":
                        instViewCoursesFlow(instructor);
                        break;
                    case "2":
                        instViewSectionsFlow(instructor);
                        break;
                    case "3":
                        instViewEnrolledStudentsFlow(scanner, instructor);
                        break;
                    case "4":
                        instMarkAttendanceFlow(scanner, instructor);
                        break;
                    case "5":
                        instUpdateAttendanceFlow(scanner, instructor);
                        break;
                    case "6":
                        instCalculateAttendancePctFlow(scanner, instructor);
                        break;
                    case "7":
                        // Role Enforcement
                        if (!isPermanent) {
                            throw new UnauthorizedActionException("Visiting Instructors are NOT authorized to assign Teaching Assistants.");
                        }
                        instAssignTAFlow(scanner, (PermanentInstructor) instructor);
                        break;
                    case "8":
                        if (!isPermanent) {
                            throw new UnauthorizedActionException("Visiting Instructors are NOT authorized to supervise FYP Groups.");
                        }
                        instViewFYPGroupsFlow((PermanentInstructor) instructor);
                        break;
                    case "9":
                        if (!isPermanent) {
                            throw new UnauthorizedActionException("Visiting Instructors are NOT authorized to schedule FYP Meetings.");
                        }
                        instScheduleFYPMeetingFlow(scanner, (PermanentInstructor) instructor);
                        break;
                    case "10":
                        if (!isPermanent) {
                            throw new UnauthorizedActionException("Visiting Instructors are NOT authorized to evaluate FYPs.");
                        }
                        instEvaluateFYPFlow(scanner, (PermanentInstructor) instructor);
                        break;
                    case "11":
                        inInstPortal = false;
                        break;
                    default:
                        System.out.println("Invalid option. Please enter 1-11.");
                }
            } catch (UnauthorizedActionException e) {
                Logger.warning("Security check blocked instructor " + instructor.getTeacherId() + ": " + e.getMessage());
                System.out.println("[ACCESS DENIED - UNAUTHORIZED]: " + e.getMessage());
            } catch (InvalidFYPEvaluationException | InvalidFYPGroupException e) {
                Logger.warning("FYP operation failed: " + e.getMessage());
                System.out.println("[FYP ERROR]: " + e.getMessage());
            } catch (CampusException e) {
                Logger.error("Instructor action failed: " + e.getMessage());
                System.out.println("[ERROR]: " + e.getMessage());
            } catch (Exception e) {
                Logger.error("Unexpected error in Instructor Portal: " + e.getMessage());
                System.out.println("[ERROR]: " + e.getMessage());
            }
        }
    }

    private static void instViewCoursesFlow(Instructor instructor) {
        List<Course> courses = instructor.viewCourses();
        if (courses.isEmpty()) {
            System.out.println("No courses assigned to instructor " + instructor.getName());
            return;
        }
        System.out.println("\n=== Assigned Courses ===");
        for (Course c : courses) {
            System.out.println("  • " + c.getCourseCode() + " - " + c.getTitle() + " (" + c.getCreditHours() + " Credits)");
        }
    }

    private static void instViewSectionsFlow(Instructor instructor) {
        List<Section> sections = instructor.getAssignedSections();
        if (sections.isEmpty()) {
            System.out.println("No sections assigned to instructor " + instructor.getName());
            return;
        }
        System.out.println("\n=== Assigned Sections ===");
        for (Section s : sections) {
            String sch = (s.getSchedule() != null) ? s.getSchedule().getScheduleInfo() : "No schedule set";
            String taInfo = (s.getTeachingAssistant() != null) ? s.getTeachingAssistant().getName() : "None";
            System.out.println("  • " + s.getSectionId() + " (" + s.getCourse().getCourseCode()
                    + ") | Enrolled: " + s.getEnrolledStudents().size() + "/" + s.getCapacity()
                    + " | Schedule: " + sch + " | TA: " + taInfo);
        }
    }

    /**
     * Demonstrates StudentNameComparator integration in Instructor Portal.
     */
    private static void instViewEnrolledStudentsFlow(Scanner scanner, Instructor instructor) throws InvalidUserDataException {
        System.out.print("Enter Section ID: ");
        String secId = scanner.nextLine().trim().toUpperCase();
        Section sec = SystemContext.findSection(secId);
        if (sec == null) {
            System.out.println("Section " + secId + " not found.");
            return;
        }

        List<Student> students = instructor.viewEnrolledStudents(sec);
        if (students.isEmpty()) {
            System.out.println("No students enrolled in section " + secId);
            return;
        }

        // Sort students alphabetically by name using StudentNameComparator
        Collections.sort(students, new StudentNameComparator());

        System.out.println("\n=== Enrolled Students for Section " + secId + " (Alphabetically Sorted via StudentNameComparator) ===");
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            System.out.println((i + 1) + ". " + s.getName() + " | ID: " + s.getStudentId() + " | Email: " + s.getEmail());
        }
    }

    private static void instMarkAttendanceFlow(Scanner scanner, Instructor instructor) throws InvalidUserDataException {
        System.out.print("Enter Section ID: ");
        String secId = scanner.nextLine().trim().toUpperCase();
        Section sec = SystemContext.findSection(secId);
        if (sec == null) {
            System.out.println("Section " + secId + " not found.");
            return;
        }

        List<Student> students = sec.getEnrolledStudents();
        if (students.isEmpty()) {
            System.out.println("No students enrolled in section " + secId);
            return;
        }

        System.out.println("\nEnrolled Students in " + secId + ":");
        for (Student s : students) {
            System.out.println("  • " + s.getStudentId() + ": " + s.getName());
        }
        System.out.print("Enter Student ID to mark attendance: ");
        String sId = scanner.nextLine().trim();
        Student student = SystemContext.findStudent(sId);
        if (student == null || !students.contains(student)) {
            System.out.println("Student " + sId + " is not enrolled in section " + secId);
            return;
        }

        System.out.println("Select Attendance Status: 1. PRESENT, 2. ABSENT, 3. LATE");
        System.out.print("Choice: ");
        String statChoice = scanner.nextLine().trim();
        AttendanceStatus status;
        switch (statChoice) {
            case "1": status = AttendanceStatus.PRESENT; break;
            case "2": status = AttendanceStatus.ABSENT; break;
            case "3": status = AttendanceStatus.LATE; break;
            default:
                System.out.println("Invalid choice. Marking as PRESENT by default.");
                status = AttendanceStatus.PRESENT;
        }

        instructor.markAttendance(student, sec, status);
        System.out.println("Attendance marked as " + status + " for student " + student.getName() + " on " + LocalDate.now());
    }

    private static void instUpdateAttendanceFlow(Scanner scanner, Instructor instructor) throws InvalidUserDataException {
        System.out.print("Enter Section ID: ");
        String secId = scanner.nextLine().trim().toUpperCase();
        Section sec = SystemContext.findSection(secId);
        if (sec == null) {
            System.out.println("Section " + secId + " not found.");
            return;
        }

        List<Attendance> records = sec.getAttendanceRecords();
        if (records.isEmpty()) {
            System.out.println("No attendance records found for section " + secId);
            return;
        }

        System.out.println("\nAttendance Records for Section " + secId + ":");
        for (int i = 0; i < records.size(); i++) {
            Attendance a = records.get(i);
            System.out.println((i + 1) + ". Date: " + a.getDate() + " | Student: " + a.getStudent().getName()
                    + " (" + a.getStudent().getStudentId() + ") | Status: " + a.getStatus());
        }
        System.out.print("Enter record number to update: ");
        int rIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        if (rIdx < 0 || rIdx >= records.size()) {
            System.out.println("Invalid record index.");
            return;
        }

        System.out.println("Select New Status: 1. PRESENT, 2. ABSENT, 3. LATE");
        System.out.print("Choice: ");
        String statChoice = scanner.nextLine().trim();
        AttendanceStatus newStatus = ("2".equals(statChoice)) ? AttendanceStatus.ABSENT
                : ("3".equals(statChoice) ? AttendanceStatus.LATE : AttendanceStatus.PRESENT);

        instructor.updateAttendance(records.get(rIdx), newStatus);
        System.out.println("Attendance status updated to " + newStatus);
    }

    private static void instCalculateAttendancePctFlow(Scanner scanner, Instructor instructor) throws InvalidUserDataException {
        System.out.print("Enter Section ID: ");
        String secId = scanner.nextLine().trim().toUpperCase();
        Section sec = SystemContext.findSection(secId);
        if (sec == null) {
            System.out.println("Section " + secId + " not found.");
            return;
        }

        System.out.print("Enter Student ID: ");
        String sId = scanner.nextLine().trim();
        Student student = SystemContext.findStudent(sId);
        if (student == null) {
            System.out.println("Student " + sId + " not found.");
            return;
        }

        double pct = instructor.calculateAttendancePercentage(student, sec);
        System.out.printf("Attendance Percentage for %s in %s: %.2f%%%n", student.getName(), secId, pct);
    }

    private static void instAssignTAFlow(Scanner scanner, PermanentInstructor instructor) throws InvalidUserDataException {
        System.out.print("Enter Section ID: ");
        String secId = scanner.nextLine().trim().toUpperCase();
        Section sec = SystemContext.findSection(secId);
        if (sec == null) {
            System.out.println("Section " + secId + " not found.");
            return;
        }

        System.out.println("\nEligible Normal Students:");
        for (NormalStudent s : SystemContext.students.values()) {
            System.out.println("  • " + s.getStudentId() + ": " + s.getName());
        }
        System.out.print("Enter Student ID to appoint as TA: ");
        String sId = scanner.nextLine().trim();
        NormalStudent candidate = SystemContext.students.get(sId);
        if (candidate == null) {
            System.out.println("Normal Student " + sId + " not found.");
            return;
        }

        instructor.assignTA(candidate, sec);
        if (sec.getTeachingAssistant() != null) {
            SystemContext.addTeachingAssistant(sec.getTeachingAssistant());
        }
        System.out.println("Student " + candidate.getName() + " appointed as Teaching Assistant for section " + secId);
    }

    private static void instViewFYPGroupsFlow(PermanentInstructor instructor) {
        List<FYPGroup> groups = instructor.viewFYPGroups();
        if (groups.isEmpty()) {
            System.out.println("No FYP groups currently assigned to " + instructor.getName());
            return;
        }

        System.out.println("\n=== Supervised FYP Groups ===");
        for (FYPGroup g : groups) {
            System.out.println(g.getDetails());
        }
    }

    /**
     * Demonstrates FYPMeetingDateComparator integration in Instructor Portal.
     */
    private static void instScheduleFYPMeetingFlow(Scanner scanner, PermanentInstructor instructor)
            throws InvalidFYPGroupException, InvalidUserDataException {
        List<FYPGroup> groups = instructor.viewFYPGroups();
        if (groups.isEmpty()) {
            // If no group is supervised yet, allow supervisor to assign/create one
            System.out.println("No supervised groups found. Creating or selecting group from repository...");
            if (SystemContext.fypGroups.isEmpty()) {
                System.out.print("Enter new FYP Group ID (e.g. FYP-26-01): ");
                String gid = scanner.nextLine().trim();
                System.out.print("Enter Project Title: ");
                String gTitle = scanner.nextLine().trim();
                System.out.print("Enter Description: ");
                String gDesc = scanner.nextLine().trim();
                FYPGroup group = new FYPGroup(gid, gTitle, gDesc);
                group.setSupervisor(instructor);
                SystemContext.addFYPGroup(group);
                groups.add(group);
                System.out.println("Created and assigned FYP group " + gid);
            } else {
                for (FYPGroup g : SystemContext.fypGroups.values()) {
                    if (g.getSupervisor() == null) {
                        g.setSupervisor(instructor);
                        groups.add(g);
                    }
                }
            }
        }

        System.out.println("\nSelect FYP Group:");
        for (int i = 0; i < groups.size(); i++) {
            System.out.println((i + 1) + ". " + groups.get(i).getGroupId() + " - " + groups.get(i).getTitle());
        }
        System.out.print("Enter group number: ");
        int gIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        if (gIdx < 0 || gIdx >= groups.size()) {
            System.out.println("Invalid group selection.");
            return;
        }
        FYPGroup group = groups.get(gIdx);

        System.out.print("Enter Meeting ID (e.g. MEET-101): ");
        String mId = scanner.nextLine().trim();
        System.out.print("Enter Meeting Date (yyyy-MM-dd): ");
        LocalDate date = LocalDate.parse(scanner.nextLine().trim(), DATE_FORMAT);
        System.out.print("Enter Agenda: ");
        String agenda = scanner.nextLine().trim();
        System.out.print("Enter Notes: ");
        String notes = scanner.nextLine().trim();

        FYPMeeting meeting = new FYPMeeting(mId, date, agenda, notes);
        instructor.scheduleFYPMeeting(group, meeting);
        SystemContext.addFYPMeeting(meeting);

        // Sort all scheduled meetings for this group chronologically using FYPMeetingDateComparator
        List<FYPMeeting> meetingList = new ArrayList<>(group.getMeetings());
        Collections.sort(meetingList, new FYPMeetingDateComparator());

        System.out.println("\nMeeting successfully scheduled! Current meetings for " + group.getGroupId() + " (Chronological via FYPMeetingDateComparator):");
        for (FYPMeeting m : meetingList) {
            System.out.println("  • [" + m.getMeetingId() + "] Date: " + m.getMeetingDate().format(DATE_FORMAT)
                    + " | Agenda: " + m.getAgenda() + " | Notes: " + m.getNotes());
        }
    }

    private static void instEvaluateFYPFlow(Scanner scanner, PermanentInstructor instructor)
            throws InvalidFYPGroupException, InvalidFYPEvaluationException, InvalidUserDataException {
        List<FYPGroup> groups = instructor.viewFYPGroups();
        if (groups.isEmpty()) {
            System.out.println("No supervised FYP groups available to evaluate.");
            return;
        }

        System.out.println("\nSelect FYP Group to Evaluate:");
        for (int i = 0; i < groups.size(); i++) {
            System.out.println((i + 1) + ". " + groups.get(i).getGroupId() + " - " + groups.get(i).getTitle());
        }
        System.out.print("Enter group number: ");
        int gIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        if (gIdx < 0 || gIdx >= groups.size()) {
            System.out.println("Invalid selection.");
            return;
        }
        FYPGroup group = groups.get(gIdx);

        System.out.print("Enter Evaluation ID (e.g. EVAL-01): ");
        String evalId = scanner.nextLine().trim();
        System.out.print("Enter Evaluation Score (0.0 to 100.0): ");
        double score = Double.parseDouble(scanner.nextLine().trim());
        System.out.print("Enter Detailed Feedback: ");
        String feedback = scanner.nextLine().trim();

        instructor.evaluateFYPIdea(group, evalId, score, feedback);
        FYPEvaluation evaluation = new FYPEvaluation(evalId, LocalDate.now(), score, feedback);
        SystemContext.addFYPEvaluation(evaluation);
        System.out.println("FYP Evaluation completed successfully for group " + group.getGroupId() + " with score " + score);
    }
}