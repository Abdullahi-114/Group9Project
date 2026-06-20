-- Create database
CREATE DATABASE ShiftManager;

USE ShiftManager;

-- Users
CREATE TABLE IF NOT EXISTS users (
	userId		int			NOT NULL UNIQUE AUTO_INCREMENT,
	firstName	varchar(50)	NOT NULL,
    lastName	varchar(50)	NOT NULL,
	email		varchar(50) NOT NULL,
	type		varchar(20)	NOT NULL,
	seniority	int,
	passHash	varchar(255),
	CONSTRAINT employees_PK PRIMARY KEY (userId)
);

-- Shifts Table
CREATE TABLE IF NOT EXISTS shifts (
	shiftId		int			NOT NULL UNIQUE AUTO_INCREMENT,
	start		timestamp	NOT NULL,
	end			timestamp	NOT NULL,
	status		varchar(12) NOT NULL,
	employeeId	int			NULL,
	managerId	int			NOT NULL,
	CONSTRAINT shifts_PK PRIMARY KEY (shiftId),
	CONSTRAINT employee_FK FOREIGN KEY (employeeId) REFERENCES users (userId),
	CONSTRAINT manager_FK FOREIGN KEY (managerId) REFERENCES users (userId)
);

