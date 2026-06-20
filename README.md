# Shift Scheduling Management System

Java web application for managing employee shift scheduling, user accounts, login sessions, and shift status workflows.

## Highlights

- Servlet and JSP based web application packaged as a WAR file.
- MySQL persistence through JDBC and DAO classes.
- User and shift service layers for business logic.
- Manager and employee dashboard flows.
- Session-based authentication filter.
- Argon2 password hashing for new user accounts.
- JUnit test structure for schedule domain classes.

## Tech Stack

- Java 17
- Jakarta Servlets and JSP
- JDBC
- MySQL
- Maven
- Tomcat-compatible WAR packaging
- JUnit 5
- HTML and CSS

## Project Structure

- `src/main/java/dao` - database connection and DAO classes.
- `src/main/java/services` - user and shift service logic.
- `src/main/java/servlets` - request handlers for login, dashboards, logout, and account creation.
- `src/main/java/security` - authentication filter and password hashing utility.
- `src/main/java/shifts` - shift model and status pattern classes.
- `src/main/webapp/WEB-INF/views` - JSP views.
- `src/main/java/ddl/ShiftManager.sql` - database setup script.

## Run Locally

1. Install Java 17, Maven, MySQL, and Tomcat.
2. Create a local MySQL user or update `DBConnection.java` for your local credentials.
3. Run the SQL script in `src/main/java/ddl/ShiftManager.sql`, or let the app create tables on startup.
4. Build the WAR:

```powershell
mvn clean package
```

5. Deploy the generated WAR from `target/` to Tomcat.

## Portfolio Notes

This academic team project demonstrates Java web application architecture, database access, authentication, and scheduling business logic. My contribution can be described around Java/JSP workflow development, database-backed features, debugging, and Git collaboration.
