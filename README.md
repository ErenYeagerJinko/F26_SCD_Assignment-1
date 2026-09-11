# Campus Management System - SE3005 Assignment 1

Welcome to the **Campus Management System**! This console-based Java application models a university's day-to-day academic operations as part of the SE3005 assignment.

## What It Does

This application provides a complete campus management experience with the following core features:

### 📚 Course & Section Management
- Create and manage courses with credit hours and prerequisites
- Set up sections with capacity limits, rooms, and schedules
- Assign instructors to sections
- Enroll students in courses (with clash detection and capacity management)

### 👥 Role-Based System
- **Students**: Register/drop courses, view timetables, submit assignments, check attendance
- **Teaching Assistants**: Create assignments, view submissions, evaluate work, give feedback
- **Visiting Instructors**: Mark attendance, view courses/sections (cannot assign TAs or supervise FYP)
- **Permanent Instructors**: Everything visiting instructors can do, plus assign TAs and supervise FYP groups
- **Administrators**: Create/update courses and sections, manage requests, set capacities

### 📝 Assignments & Submissions
- TAs create assignments with deadlines and total marks
- Students submit assignments before/after deadlines
- Mark submissions as evaluated with marks and feedback
- Detect and flag late submissions

### 🔔 Academic Requests
- Students submit course clash requests, grading issues, or other complaints
- Administrators view, approve, or reject requests
- Track request status (PENDING, APPROVED, REJECTED)

### 📿 Final Year Project (FYP) Supervision
- Permanent instructors supervise FYP groups
- Schedule FYP meetings with agendas and notes
- Evaluate FYP ideas with scores and feedback
- Manage group members and meetings

### 📊 Enumerations & Comparisons
- **Enumerations**: Day, RequestStatus, RequestCategory, EnrollmentStatus, SubmissionStatus, AttendanceStatus
- **Comparators**: StudentNameComparator, RequestPriorityComparator, RequestDateComparator, AssignmentDeadlineComparator, FYPMeetingDateComparator

### 🗄️ Data Persistence
- All data persists to `data/` folder as `.txt` files
- Consistent delimited format for all entities
- Auto-load on startup, auto-save on changes

### 📝 Logging System
- All events logged to `logs/app.log` on every run
- Registrations, drops, course operations, TA assignments
- Assignment submissions/evaluations, attendance marking
- Request submit/approve/reject cycles, FYP meetings/evaluations
- Every caught exception is logged

## How It Works

1. **Startup**: Application loads all data from `data/*.txt` files
2. **Menu System**: Role-based main menu dispatches to appropriate operations
3. **Operations**: Each menu option triggers the relevant business logic
4. **Exception Handling**: Custom exceptions thrown and caught at use case boundaries
5. **Logging**: Every operation logged via the Logger utility
6. **Shutdown**: Data saved back to `data/*.txt` files

## Project Structure

```
src/
  model/          → Person, Student, Instructor, Course, Section, etc.
  enums/          → RequestStatus, RequestCategory, EnrollmentStatus, etc.
  comparators/    → 5 comparators for sorting
  exceptions/     → CampusException hierarchy
  util/           → Logger class
  Main.java       → Entry point and menu system

data/             → Persisted .txt files
logs/             → app.log (generated on each run)
```

## Getting Started

1. Ensure Java is installed
2. Run `Main.java` as the entry point
3. Follow the role-based menu to explore features
4. Data is automatically persisted to `data/` folder
5. Check `logs/app.log` for operation history

## Important Notes

- ⚠️ **No hardcoded/in-memory-only data**: All data persists to files
- ⚠️ **Role restrictions enforced**: Visiting Instructors cannot assign TAs or supervise FYP
- ⚠️ **Custom exceptions thrown**: Specific exceptions for specific scenarios
- ⚠️ **Logging always active**: Meaningful entries on every run
- ⚠️ **All 5 comparators used**: For sorting lists before display

## Team

Made with ❤️ by **Team Yeagerists++**