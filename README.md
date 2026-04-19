# ShiftManager (Group 9 Project)

## Project Overview

ShiftManager is a Java web application we are building for our Software Development Project course. The goal of the system is to help manage employee work shifts.

Managers can create and manage shifts, and employees can log in to view their schedules. The project is built using Java Servlets and JSP, with a MySQL database.

This README describes the final version of our course project.

---

## Features Implemented

In the final version of the project, we implemented the following:

- User registration (create new account)
- Login system with session handling
- Role-based access (Manager vs Employee)
- Manager and Employee dashboards
- Manager features for creating shifts, assigning shifts, and viewing schedules
- Employee listing and related manager pages
- Basic shift structure and scheduling classes (Shift, WorkDay, WorkWeek)
- Shift status logic (Open, Assigned, Complete, Cancelled)
- Database connection and CRUD operations (users and shifts)
- Authentication filter to protect routes

We also added unit tests for important logic like shift states, users, and scheduling classes.

---

## Technologies Used

- Java (Servlets, JSP)
- Apache Tomcat 10
- MySQL
- Maven
- HTML, CSS
- GitHub (for version control)

---

## Project Structure

The project still follows a layered structure, but now it has more packages and pages than before:

- `servlets/` → Handles the main web requests like login, logout, registration, dashboards, creating shifts, assigning shifts, and viewing schedules/employees
- `dao/` → Handles database operations for users and shifts, and includes the database connection setup
- `services/` → Contains service interfaces for user and shift operations
- `users/` → User-related classes like `User`, `Employee`, `Manager`, `EmployeeType`, and `UserFactory`
- `shifts/` → Shift-related classes and status logic such as open, assigned, complete, and cancelled
- `shiftEligibility/` → Contains the eligibility strategy classes used to check if an employee can take a shift
- `schedule/` → `WorkDay` and `WorkWeek` logic for time tracking and scheduling
- `security/` → Authentication filter and password hashing
- `ddl/` → SQL file used for the database setup
- `webapp/WEB-INF/views/` → JSP pages for login, registration, manager dashboard, employee dashboard, adding employees, creating shifts, assigning shifts, and viewing schedules/employees
- `webapp/css/` → CSS styling for the web pages
- `test/` → JUnit test classes for important parts like shifts, users, and scheduling

We tried to keep each package responsible for one part of the system so the code stays easier to understand and manage.

---

## Design Approach

We followed some basic design patterns we learned in class:

- Singleton → used in DBConnection
- Factory → used to create Users and Shift statuses
- Strategy → used for shift eligibility rules
- State → used for shift status (Open, Assigned, etc.)

We used these patterns to keep the code more organized and easier to extend.

---

## How to Run the Project

1. Clone the repo:
git clone https://github.com/SamDJoyce/Group9Project.git
2. Open in VS Code or IntelliJ

3. Set up MySQL:
- Default user is `root`
- Update password in `DBConnection.java` if needed

4. Build the project:

mvn clean package
5. Deploy the `.war` file to Tomcat (`webapps` folder)

6. Start Tomcat and go to:

http://localhost:8080/schedule-management-system-0.0.1-SNAPSHOT/login


7. Create a user using `/newUser`

---

## Version Control

We are using GitHub to manage our code:
https://github.com/SamDJoyce/Group9Project

- We worked using branches
- Each member commits their changes
- Commit messages describe what was added or fixed

---

## Agile Development

We are following an Agile approach:

- Work is divided into sprints
- Tasks are tracked using Trello
- Features are developed step by step
- We test as we build instead of waiting until the end

---

## Challenges & Solutions

**Challenge:** Setting up Tomcat and MySQL for everyone  
**Solution:** We shared setup steps and helped each other configure environments  

**Challenge:** Understanding how to separate layers (DAO, Servlets, Models)  
**Solution:** We followed examples and gradually refactored code  

**Challenge:** Role-based access control  
**Solution:** Implemented an AuthFilter to restrict pages  

---

## Possible Future Improvements

If this project was extended further, we would improve these areas:

- Let employees directly select or request shifts from their dashboard
- Improve UI and usability
- Add more validation and error handling
- Add more servlet and integration testing
- Add notifications for shift changes

---

## Contributors

- Sam Joyce – Main development and repo setup  
- Neslihan - UI Development  
- Yashbir (Yash) – Documentation and support  
- Mohamed Abdullahi - Dev work Manager Dashboard
- Shimaa Abouzeid - Unit tests

---

## Notes

This project was completed as part of CST8319. It represents our final course submission for the ShiftManager system.
