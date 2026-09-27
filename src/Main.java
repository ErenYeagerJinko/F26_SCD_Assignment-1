import java.util.Scanner;
import model.*;
import exceptions.*;
import java.util.*;

public class Main {
    private static AcademicOfficeAdmin admin;
    private static List<NormalStudent> students = new ArrayList<>();
    private static List<PermanentInstructor> instructors = new ArrayList<>();
    private static List<Course> courses = new ArrayList<>();
    private static List<Section> sections = new ArrayList<>();

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        try {
            admin = new AcademicOfficeAdmin("A01", "Admin Name", "admin@uni.edu", "1234567890");
        } catch (Exception e) {
            System.out.println("Failed to initialize admin: " + e.getMessage());
            return;
        }

        boolean running = true;
        while (running) {
            System.out.println("\n=== University Management System ===");
            System.out.println("1. Academic Office Admin Menu");
            System.out.println("2. Student Menu");
            System.out.println("3. Instructor Menu");
            System.out.println("4. Exit");
            System.out.print("Select an option: ");
            
            String choice = scanner.nextLine();
            
            switch (choice) {
                case "1":
                    adminMenu(scanner);
                    break;
                case "2":
                    studentMenu(scanner);
                    break;
                case "3":
                    instructorMenu(scanner);
                    break;
                case "4":
                    running = false;
                    System.out.println("Exiting system. Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
        scanner.close();
    }

    private static void adminMenu(Scanner scanner) {
        boolean inAdminMenu = true;
        while (inAdminMenu) {
            System.out.println("\n--- Academic Office Admin Menu ---");
            System.out.println("1. Create Course");
            System.out.println("2. Create Student");
            System.out.println("3. Create Instructor");
            System.out.println("4. Back to Main Menu");
            System.out.print("Select an option: ");
            
            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1":
                        System.out.print("Enter Course Code: ");
                        String code = scanner.nextLine();
                        System.out.print("Enter Course Title: ");
                        String title = scanner.nextLine();
                        System.out.print("Enter Credit Hours: ");
                        int credits = Integer.parseInt(scanner.nextLine());
                        Course newCourse = new Course(code, title, credits);
                        admin.createCourse(newCourse);
                        courses.add(newCourse);
                        System.out.println("Course created successfully.");
                        break;
                    case "2":
                        System.out.print("Enter Student ID: ");
                        String sId = scanner.nextLine();
                        System.out.print("Enter Name: ");
                        String sName = scanner.nextLine();
                        System.out.print("Enter Email: ");
                        String sEmail = scanner.nextLine();
                        System.out.print("Enter Phone: ");
                        String sPhone = scanner.nextLine();
                        NormalStudent newStudent = new NormalStudent(sId, sName, sEmail, sPhone);
                        students.add(newStudent);
                        System.out.println("Student created successfully.");
                        break;
                    case "3":
                        System.out.print("Enter Instructor ID: ");
                        String iId = scanner.nextLine();
                        System.out.print("Enter Name: ");
                        String iName = scanner.nextLine();
                        System.out.print("Enter Email: ");
                        String iEmail = scanner.nextLine();
                        System.out.print("Enter Phone: ");
                        String iPhone = scanner.nextLine();
                        PermanentInstructor newInstructor = new PermanentInstructor(iId, iName, iEmail, iPhone);
                        instructors.add(newInstructor);
                        System.out.println("Instructor created successfully.");
                        break;
                    case "4":
                        inAdminMenu = false;
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void studentMenu(Scanner scanner) {
        if (students.isEmpty()) {
            System.out.println("No students available. Please create one from the Admin Menu.");
            return;
        }
        
        System.out.println("\nSelect a Student:");
        for (int i = 0; i < students.size(); i++) {
            System.out.println((i + 1) + ". " + students.get(i).getName() + " (" + students.get(i).getStudentId() + ")");
        }
        System.out.print("Enter student number: ");
        int studentIdx = Integer.parseInt(scanner.nextLine()) - 1;
        if (studentIdx < 0 || studentIdx >= students.size()) {
            System.out.println("Invalid student selection.");
            return;
        }
        NormalStudent student = students.get(studentIdx);

        boolean inStudentMenu = true;
        while (inStudentMenu) {
            System.out.println("\n--- Student Menu (" + student.getName() + ") ---");
            System.out.println("1. View Timetable");
            System.out.println("2. Back to Main Menu");
            System.out.print("Select an option: ");
            
            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1":
                        System.out.println("Timetable: " + student.viewTimetable());
                        break;
                    case "2":
                        inStudentMenu = false;
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private static void instructorMenu(Scanner scanner) {
        if (instructors.isEmpty()) {
            System.out.println("No instructors available. Please create one from the Admin Menu.");
            return;
        }
        
        System.out.println("\nSelect an Instructor:");
        for (int i = 0; i < instructors.size(); i++) {
            System.out.println((i + 1) + ". " + instructors.get(i).getName() + " (" + instructors.get(i).getTeacherId() + ")");
        }
        System.out.print("Enter instructor number: ");
        int instructorIdx = Integer.parseInt(scanner.nextLine()) - 1;
        if (instructorIdx < 0 || instructorIdx >= instructors.size()) {
            System.out.println("Invalid instructor selection.");
            return;
        }
        PermanentInstructor instructor = instructors.get(instructorIdx);

        boolean inInstructorMenu = true;
        while (inInstructorMenu) {
            System.out.println("\n--- Instructor Menu (" + instructor.getName() + ") ---");
            System.out.println("1. View Assigned Sections");
            System.out.println("2. Back to Main Menu");
            System.out.print("Select an option: ");
            
            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1":
                        System.out.println("Assigned Sections: " + instructor.getAssignedSections());
                        break;
                    case "2":
                        inInstructorMenu = false;
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
}