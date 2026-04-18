package services;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import shifts.Shift;
import users.Employee;
import users.Manager;

/**
 * Interface defining the available interactions with
 * a database for Shifts.
 * 
 * @author Sam Joyce
 */
public interface ShiftService {
	
	/**
	 * Create a shift with no employee assigned,
	 * and insert into the DB.
	 * 
	 * @param start	time shift beings
	 * @param end	time shift ends
	 * @return		a new shift object
	 */
	Shift createShift(LocalDateTime start,
					  LocalDateTime end,
					  Manager 		manager);
	
	/**
	 * Create a shift with an employee assigned.
	 * 
	 * @param start		time shift begins
	 * @param end		time shift ends
	 * @param employee	the person assigned to this shift
	 * @return			a new shift object
	 */
	Shift createShift(LocalDateTime start,
			 		  LocalDateTime end,
			 		  Manager 		manager,
			 		  Employee      employee);
	
	/**
	 * @param shiftId of the shift to be deleted
	 */
	void deleteShift(int shiftId);
	
	/**
	 * @param shiftId	the id of the Shift to be retrieved.
	 * @return			the corresponding shift.
	 */
	Shift getShift(int shiftId); 
	
	/**
	 * @param managerId
	 * @return			All shifts with this managerId
	 */
	List<Shift> getShiftByManagerId(int managerId);
	
	/**
	 * @param managerId
	 * @return			All open shifts with this managerId
	 */
	List<Shift> getOpenShifts(int managerId);
	
	/**
	 * @param date	the desired date.
	 * @return		a list of all shifts on the chosen day.
	 */
	List<Shift> getShiftsByDay(LocalDate date);
	
	/**
	 * @param date		the date of the shifts to be retrieved
	 * @param employee	the employee whose shifts are to be retrieved
	 * @return			a list of the given employee's shifts
	 * 					on the chosen day.
	 */
	List<Shift> getShiftsByDay(LocalDate date, Employee employee);
	
	/**
	 * @param weekOf	a date within the desired workWeek
	 * @return			a list of all shifts schedule for the
	 * 					chosen week.
	 */
	List<Shift> getShiftsByWeek(LocalDate weekOf);
	
	/**
	 * @param weekOf	a date within the desired work week
	 * @param employee	the employee whose shifts are to be retrieved
	 * @return			all of th given employee's shifts for the
	 * 					selected work week.
	 */
	List<Shift> getShiftsByWeek(LocalDate weekOf, Employee employee);
	
	/**
	 * @param shift object containing new shift information
	 */
	void updateShift(Shift shift);
	
	/**
	 * @param userId
	 * @return			Amount of time the user has been schedule to work that week
	 */
	Duration getScheduledTimeByWeek(LocalDate weekOf, int userId);
	
	/**
	 * @param userId
	 * @return			Amount of time the user has been schedule to work that day
	 */
	Duration getScheduledTimeByDay(LocalDate date, int userId);
	
}
