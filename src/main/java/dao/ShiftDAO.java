package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

import services.ShiftService;
import shifts.Shift;
import shifts.ShiftStatusFactory;
import users.Employee;
import users.Manager;
import users.UserFactory;

/**
 * Mediates database interactions for Shift objects.
 * Implemented for MySQL 8.0
 * 
 * @author Sam Joyce
 */
public class ShiftDAO implements ShiftService {

	public static final String shiftsTable = "shifts";
	public static final String usersTable = "users";
	private static DayOfWeek FIRST_DAY = DayOfWeek.MONDAY;
	
	public ShiftDAO() {
	}

	/**
	 * Generate a new, open shift, insert into the database,
	 * add generated ID, then return the Shift object.
	 * 
	 * @param	Date and Time the shift begins.
	 * @param	Data and Time the shift is complete.
	 */
	@Override
	public Shift createShift(	LocalDateTime start, 
								LocalDateTime end, 
								Manager 	  manager) 
	{
		Shift shift = new Shift.Builder()
							   .setStart(start)
							   .setEnd(end)
							   .build();
		String newShift = "INSERT INTO " + shiftsTable + " ("
						+ "start, end, status, managerId ) "
						+ "VALUES (?,?,?,?)";
		Connection 		  connection    = null;
		PreparedStatement statement     = null;
		ResultSet		  generatedKeys = null;
		
		try {
			connection = DBConnection.getInstance().getConnection();
			statement = connection.prepareStatement(newShift,  Statement.RETURN_GENERATED_KEYS);
			
			statement.setTimestamp(1, Timestamp.valueOf(start));
			statement.setTimestamp(2, Timestamp.valueOf(end));
			statement.setString   (3, shift.getStatus().toString());
			statement.setInt	  (4, manager.getUserId());
			
			statement.executeUpdate();
			generatedKeys = statement.getGeneratedKeys();
			if(generatedKeys.next()) {
				int generatedShiftID = generatedKeys.getInt(1);
				shift.setShiftId(generatedShiftID);
			}
			// Return the new User object
			return shift;
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			try {
				if (generatedKeys != null) {
					generatedKeys.close();
				}
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}

	@Override
	public Shift createShift(	LocalDateTime start, 
								LocalDateTime end,
								Manager  manager, 
								Employee employee) 
	{
		Shift shift = new Shift.Builder()
				   			   .setStart(start)
				   			   .setEnd(end)
				   			   .setManager(manager)
				   			   .assignEmployee(employee)
				   			   .build();
		String newShift = "INSERT INTO " + shiftsTable + " ("
						+ "start, end, status, userId, managerId ) "
						+ "VALUES (?,?,?,?,?)";
		
		Connection 		  connection    = null;
		PreparedStatement statement     = null;
		ResultSet		  generatedKeys = null;
		
		try {
			connection = DBConnection.getInstance().getConnection();
			statement = connection.prepareStatement(newShift,  Statement.RETURN_GENERATED_KEYS);
			
			statement.setTimestamp(1, Timestamp.valueOf(start));
			statement.setTimestamp(2, Timestamp.valueOf(end));
			statement.setString   (3, shift.getStatus().toString());
			statement.setInt	  (4, employee.getUserId());
			statement.setInt	  (5, manager.getUserId());
			
			statement.executeUpdate();
			generatedKeys = statement.getGeneratedKeys();
			
			if(generatedKeys.next()) {
				int generatedShiftID = generatedKeys.getInt(1);
				shift.setShiftId(generatedShiftID);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			try {
				if (generatedKeys != null) {
					generatedKeys.close();
				}
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return shift;
	}

	@Override
	public void deleteShift(int shiftId) {
	    String deleteShift = "DELETE FROM " + shiftsTable + " WHERE shiftId = ?";

	    try (Connection connection = DBConnection.getInstance().getConnection();
	         PreparedStatement statement = connection.prepareStatement(deleteShift)) {

	        statement.setInt(1, shiftId);
	        statement.executeUpdate();

	    } catch (SQLException e) {
	        e.printStackTrace();
	    }

	}

	@Override
	public Shift getShift(int shiftId) {
		Shift shift = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resSet = null;
		String getShift = "SELECT "
						+ "	s.start as start,"
						+ "	s.end as end,"
						+ " s.status as status,"
						+ " e.userId as employeeId,"
						+ "	e.firstName as employeeFirstName,"
						+ " e.lastName as employeeLastName,"
						+ " e.email as employeeEmail,"
						+ " e.type as employeeType,"
						+ " e.seniority as employeeSeniority,"
						+ " e.passHash as employeePassHash,"
						+ " m.userId as managerId,"
						+ "	m.firstName as managerFirstName,"
						+ " m.lastName as managerLastName,"
						+ " m.email as managerEmail,"
						+ " m.type as managerType,"
						+ " m.seniority as managerSeniority,"
						+ " m.passHash as managerPassHash"
						+ " FROM " + shiftsTable + " as s "
						+ " LEFT JOIN " + usersTable + " as e "
						+ " ON s.employeeId = e.userId"
						+ " LEFT JOIN " + usersTable + " as m "
						+ " ON s.managerId = m.userId"
						+ " WHERE shiftId = ? ";
		
		try {
			connection = DBConnection.getInstance().getConnection();
			statement  = connection.prepareStatement(getShift);
			statement.setInt(1, shiftId);
			resSet = statement.executeQuery();
			if (resSet.next()) {
				shift = new Shift.Builder()
						.setShiftId(shiftId)
						.setStart(resSet.getTimestamp("start").toLocalDateTime())
						.setEnd  (resSet.getTimestamp("end").toLocalDateTime())
						.setStatus(ShiftStatusFactory.get(resSet.getString("status")))
						.build();
				if (resSet.getInt("managerId")!= 0) {
					shift.setManager(UserFactory.get(
										resSet.getInt   ("managerId"), 
										resSet.getString("managerFirstName"), 
										resSet.getString("managerLastName"), 
										resSet.getString("managerEmail"), 
										resSet.getString("managerType"), 
										resSet.getInt   ("managerseniority"), 
										resSet.getString("managerpassHash")));
				}
				if (resSet.getInt("employeeId") != 0) {
					shift.assignEmployee (UserFactory.get(
										resSet.getInt   ("employeeId"), 
										resSet.getString("employeefirstName"), 
										resSet.getString("employeelastName"), 
										resSet.getString("employeeemail"), 
										resSet.getString("employeetype"), 
										resSet.getInt   ("employeeseniority"), 
										resSet.getString("employeepassHash")));
				}
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resSet!=null) {
					resSet.close();
				}
				if (statement!=null) {
					statement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return shift;
	}

	public List<Shift> getShiftByManagerId(int managerId) {
		Shift shift = null;
		List<Shift> shifts = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resSet = null;
		String getShift = "SELECT "
						+ " s.shiftId as shiftId,"
						+ "	s.start as start,"
						+ "	s.end as end,"
						+ " s.status as status, "
						+ " e.userId as employeeId, "
						+ "	e.firstName as employeeFirstName,"
						+ " e.lastName as employeeLastName,"
						+ " e.email as employeeEmail,"
						+ " e.type as employeeType,"
						+ " e.seniority as employeeSeniority,"
						+ " e.passHash as employeePassHash,"
						+ " m.userId as managerId, "
						+ "	m.firstName as managerFirstName,"
						+ " m.lastName as managerLastName,"
						+ " m.email as managerEmail,"
						+ " m.type as managerType,"
						+ " m.seniority as managerSeniority,"
						+ " m.passHash as managerPassHash"
						+ " FROM " + shiftsTable + " as s "
						+ " LEFT JOIN " + usersTable + " as e "
						+ " ON s.employeeId = e.userId"
						+ " LEFT JOIN " + usersTable + " as m "
						+ " ON s.managerId = m.userId"
						+ " WHERE s.managerId = ?"
						+ " ORDER BY start ASC";
		
		try {
			connection = DBConnection.getInstance().getConnection();
			statement  = connection.prepareStatement(getShift);
			statement.setInt(1, managerId);
			resSet = statement.executeQuery();
			shifts = new ArrayList<>();
			// Get all shifts with this managerId attached
			while (resSet.next()) {
				shift = new Shift.Builder()
						.setShiftId(resSet.getInt("shiftId"))
						.setStart(resSet.getTimestamp("start").toLocalDateTime())
						.setEnd  (resSet.getTimestamp("end").toLocalDateTime())
						.setStatus(ShiftStatusFactory.get(resSet.getString("status")))
						.build();
				if (resSet.getInt("managerId")!= 0) {
					shift.setManager(UserFactory.get(
										resSet.getInt   ("managerId"), 
										resSet.getString("managerFirstName"), 
										resSet.getString("managerLastName"), 
										resSet.getString("managerEmail"), 
										resSet.getString("managerType"), 
										resSet.getInt   ("managerseniority"), 
										resSet.getString("managerpassHash")));
				}
				if (resSet.getInt("employeeId") != 0) {
					shift.assignEmployee (UserFactory.get(
										resSet.getInt   ("employeeId"), 
										resSet.getString("employeefirstName"), 
										resSet.getString("employeelastName"), 
										resSet.getString("employeeemail"), 
										resSet.getString("employeetype"), 
										resSet.getInt   ("employeeseniority"), 
										resSet.getString("employeepassHash")));
				}
				shifts.add(shift);
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resSet!=null) {
					resSet.close();
				}
				if (statement!=null) {
					statement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return shifts;
	}
	
	@Override
	public List<Shift> getOpenShifts(int managerId){
		Shift shift = null;
		List<Shift> shifts = null;
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resSet = null;
		String getShift = "SELECT "
						+ " s.shiftId as shiftId,"
						+ "	s.start as start,"
						+ "	s.end as end,"
						+ " s.status as status, "
						+ " e.userId as employeeId, "
						+ "	e.firstName as employeeFirstName,"
						+ " e.lastName as employeeLastName,"
						+ " e.email as employeeEmail,"
						+ " e.type as employeeType,"
						+ " e.seniority as employeeSeniority,"
						+ " e.passHash as employeePassHash,"
						+ " m.userId as managerId, "
						+ "	m.firstName as managerFirstName,"
						+ " m.lastName as managerLastName,"
						+ " m.email as managerEmail,"
						+ " m.type as managerType,"
						+ " m.seniority as managerSeniority,"
						+ " m.passHash as managerPassHash"
						+ " FROM " + shiftsTable + " as s "
						+ " LEFT JOIN " + usersTable + " as e "
						+ " ON s.employeeId = e.userId"
						+ " LEFT JOIN " + usersTable + " as m "
						+ " ON s.managerId = m.userId"
						+ " WHERE s.managerId = ?"
						+ " AND lower(status) = lower('open')"
						+ " ORDER BY start ASC";
		
		try {
			connection = DBConnection.getInstance().getConnection();
			statement  = connection.prepareStatement(getShift);
			statement.setInt(1, managerId);
			resSet = statement.executeQuery();
			shifts = new ArrayList<>();
			// Get all shifts with this managerId attached
			while (resSet.next()) {
				shift = new Shift.Builder()
						.setShiftId(resSet.getInt("shiftId"))
						.setStart(resSet.getTimestamp("start").toLocalDateTime())
						.setEnd  (resSet.getTimestamp("end").toLocalDateTime())
						.setStatus(ShiftStatusFactory.get(resSet.getString("status")))
						.build();
				if (resSet.getInt("managerId")!= 0) {
					shift.setManager(UserFactory.get(
										resSet.getInt   ("managerId"), 
										resSet.getString("managerFirstName"), 
										resSet.getString("managerLastName"), 
										resSet.getString("managerEmail"), 
										resSet.getString("managerType"), 
										resSet.getInt   ("managerseniority"), 
										resSet.getString("managerpassHash")));
				}
				if (resSet.getInt("employeeId") != 0) {
					shift.assignEmployee (UserFactory.get(
										resSet.getInt   ("employeeId"), 
										resSet.getString("employeefirstName"), 
										resSet.getString("employeelastName"), 
										resSet.getString("employeeemail"), 
										resSet.getString("employeetype"), 
										resSet.getInt   ("employeeseniority"), 
										resSet.getString("employeepassHash")));
				}
				shifts.add(shift);
			}
			
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resSet!=null) {
					resSet.close();
				}
				if (statement!=null) {
					statement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return shifts;
	}
	
	@Override
	public List<Shift> getShiftsByDay(LocalDate date) {
		List<Shift> shifts = new ArrayList<>();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resSet = null;
		String getShifts = "SELECT "
						+ "	s.start as start,"
						+ "	s.end as end,"
						+ " s.status as status,"
						+ " e.userId as employeeId,"
						+ "	e.firstName as employeeFirstName,"
						+ " e.lastName as employeeLastName,"
						+ " e.email as employeeEmail,"
						+ " e.type as employeeType,"
						+ " e.seniority as employeeSeniority,"
						+ " e.passHash as employeePassHash,"
						+ " m.userId as managerId,"
						+ "	m.firstName as managerFirstName,"
						+ " m.lastName as managerLastName,"
						+ " m.email as managerEmail,"
						+ " m.type as managerType,"
						+ " m.seniority as managerSeniority,"
						+ " m.passHash as managerPassHash"
						+ " FROM " + shiftsTable + " as s"
						+ " AND start < ? "
						+ " LEFT JOIN " + usersTable + " as e "
						+ " ON s.employeeId = e.userId"
						+ " LEFT JOIN " + usersTable + " as m "
						+ " ON s.managerId = m.userId"
						+ " WHERE start >= ? ";
		
		try {
			connection = DBConnection.getInstance().getConnection();
			statement  = connection.prepareStatement(getShifts);
			
	        Timestamp startOfDay = Timestamp.valueOf(date.atStartOfDay());
	        Timestamp startOfNextDay = Timestamp.valueOf(date.plusDays(1).atStartOfDay());
	        Shift shift = null;
			
	        statement.setTimestamp(1, startOfDay);
	        statement.setTimestamp(2, startOfNextDay);
			resSet = statement.executeQuery();
			
			while(resSet.next()) {
				shift = new Shift.Builder()
								 .setShiftId(resSet.getInt("shiftId"))
								 .setStart(resSet.getTimestamp("start").toLocalDateTime())
								 .setEnd(resSet.getTimestamp("end").toLocalDateTime())
								 .setStatus(ShiftStatusFactory.get(resSet.getString("status")))
								 .build();
				if (resSet.getInt("managerId")!= 0) {
					shift.setManager(UserFactory.get(
										resSet.getInt   ("managerId"), 
										resSet.getString("managerFirstName"), 
										resSet.getString("managerLastName"), 
										resSet.getString("managerEmail"), 
										resSet.getString("managerType"), 
										resSet.getInt   ("managerseniority"), 
										resSet.getString("managerpassHash")));
				}
				if (resSet.getInt("employeeId") != 0) {
					shift.assignEmployee (UserFactory.get(
										resSet.getInt   ("employeeId"), 
										resSet.getString("employeefirstName"), 
										resSet.getString("employeelastName"), 
										resSet.getString("employeeemail"), 
										resSet.getString("employeetype"), 
										resSet.getInt   ("employeeseniority"), 
										resSet.getString("employeepassHash")));
				}
				shifts.add(shift);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resSet!=null) {
					resSet.close();
				}
				if (statement!=null) {
					statement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return shifts;
	}
	
	@Override
	public List<Shift> getShiftsByDay(LocalDate date, Employee employee){
		List<Shift> shifts = new ArrayList<>();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resSet = null;
		String getShifts = "SELECT "
				+ "	s.start as start,"
				+ "	s.end as end,"
				+ " s.status as status,"
				+ " e.userId as employeeId,"
				+ "	e.firstName as employeeFirstName,"
				+ " e.lastName as employeeLastName,"
				+ " e.email as employeeEmail,"
				+ " e.type as employeeType,"
				+ " e.seniority as employeeSeniority,"
				+ " e.passHash as employeePassHash,"
				+ " m.userId as managerId,"
				+ "	m.firstName as managerFirstName,"
				+ " m.lastName as managerLastName,"
				+ " m.email as managerEmail,"
				+ " m.type as managerType,"
				+ " m.seniority as managerSeniority,"
				+ " m.passHash as managerPassHash"
				+ " FROM " + shiftsTable + " as s"
				+ " AND start < ? "
				+ " AND employeeId = ?"
				+ " LEFT JOIN " + usersTable + " as e "
				+ " ON s.employeeId = e.userId"
				+ " LEFT JOIN " + usersTable + " as m "
				+ " ON s.managerId = m.userId"
				+ " WHERE start >= ? ";

		try {
			connection = DBConnection.getInstance().getConnection();
			statement  = connection.prepareStatement(getShifts);
			
		    Timestamp startOfDay = Timestamp.valueOf(date.atStartOfDay());
		    Timestamp startOfNextDay = Timestamp.valueOf(date.plusDays(1).atStartOfDay());
		    Shift shift = null;
			
		    statement.setTimestamp(1, startOfDay);
		    statement.setTimestamp(2, startOfNextDay);
		    statement.setInt	  (3, employee.getUserId());
			resSet = statement.executeQuery();
			
			while(resSet.next()) {
				shift = new Shift.Builder()
								 .setShiftId(resSet.getInt("shiftId"))
								 .setStart(resSet.getTimestamp("start").toLocalDateTime())
								 .setEnd(resSet.getTimestamp("end").toLocalDateTime())
								 .setStatus(ShiftStatusFactory.get(resSet.getString("status")))
								 .build();
				if (resSet.getInt("managerId")!= 0) {
					shift.setManager(UserFactory.get(
										resSet.getInt   ("managerId"), 
										resSet.getString("managerFirstName"), 
										resSet.getString("managerLastName"), 
										resSet.getString("managerEmail"), 
										resSet.getString("managerType"), 
										resSet.getInt   ("managerseniority"), 
										resSet.getString("managerpassHash")));
				}
				if (resSet.getInt("employeeId") != 0) {
					shift.assignEmployee (UserFactory.get(
										resSet.getInt   ("employeeId"), 
										resSet.getString("employeefirstName"), 
										resSet.getString("employeelastName"), 
										resSet.getString("employeeemail"), 
										resSet.getString("employeetype"), 
										resSet.getInt   ("employeeseniority"), 
										resSet.getString("employeepassHash")));
				}
				shifts.add(shift);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resSet!=null) {
					resSet.close();
				}
				if (statement!=null) {
					statement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return shifts;
	}
	
	@Override
	public List<Shift> getShiftsByWeek(LocalDate weekOf) {
		List<Shift> shifts = new ArrayList<>();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resSet = null;
		String getShifts = "SELECT "
						+ "	s.start as start,"
						+ "	s.end as end,"
						+ " s.status as status,"
						+ " e.userId as employeeId,"
						+ "	e.firstName as employeeFirstName,"
						+ " e.lastName as employeeLastName,"
						+ " e.email as employeeEmail,"
						+ " e.type as employeeType,"
						+ " e.seniority as employeeSeniority,"
						+ " e.passHash as employeePassHash,"
						+ " m.userId as managerId,"
						+ "	m.firstName as managerFirstName,"
						+ " m.lastName as managerLastName,"
						+ " m.email as managerEmail,"
						+ " m.type as managerType,"
						+ " m.seniority as managerSeniority,"
						+ " m.passHash as managerPassHash"
						+ " FROM " + shiftsTable + " "
						+ " AND start < ? "
						+ " LEFT JOIN " + usersTable + " as e "
						+ " ON s.employeeId = e.userId"
						+ " LEFT JOIN " + usersTable + " as m "
						+ " ON s.managerId = m.userId"
						+ " WHERE start >= ? ";
		
		try {
			connection = DBConnection.getInstance().getConnection();
			statement  = connection.prepareStatement(getShifts);
			// rollback the day
			LocalDate firstDay = weekOf.with(
					 TemporalAdjusters.previousOrSame(FIRST_DAY) );
	        Timestamp startOfWeek = Timestamp.valueOf(firstDay.atStartOfDay());
	        Timestamp startOfNextWeek = Timestamp.valueOf(firstDay.plusDays(7).atStartOfDay());
	        Shift shift = null;
			
	        statement.setTimestamp(1, startOfWeek);
	        statement.setTimestamp(2, startOfNextWeek);
			resSet = statement.executeQuery();
			
			while(resSet.next()) {
				shift = new Shift.Builder()
								 .setShiftId(resSet.getInt("shiftId"))
								 .setStart(resSet.getTimestamp("start").toLocalDateTime())
								 .setEnd(resSet.getTimestamp("end").toLocalDateTime())
								 .setStatus(ShiftStatusFactory.get(resSet.getString("status")))
								 .build();
				if (resSet.getInt("userId") != 0) {
					if (resSet.getInt("managerId")!= 0) {
						shift.setManager(UserFactory.get(
											resSet.getInt   ("managerId"), 
											resSet.getString("managerFirstName"), 
											resSet.getString("managerLastName"), 
											resSet.getString("managerEmail"), 
											resSet.getString("managerType"), 
											resSet.getInt   ("managerseniority"), 
											resSet.getString("managerpassHash")));
					}
					if (resSet.getInt("employeeId") != 0) {
						shift.assignEmployee (UserFactory.get(
											resSet.getInt   ("employeeId"), 
											resSet.getString("employeefirstName"), 
											resSet.getString("employeelastName"), 
											resSet.getString("employeeemail"), 
											resSet.getString("employeetype"), 
											resSet.getInt   ("employeeseniority"), 
											resSet.getString("employeepassHash")));
					}
				}
				shifts.add(shift);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resSet!=null) {
					resSet.close();
				}
				if (statement!=null) {
					statement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return shifts;
	}
	
	@Override
	public List<Shift> getShiftsByWeek(LocalDate weekOf, Employee employee) {
		List<Shift> shifts = new ArrayList<>();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resSet = null;
		String getShifts = "SELECT "
						+ "	s.start as start,"
						+ "	s.end as end,"
						+ " s.status as status"
						+ " e.userId as employeeId"
						+ "	e.firstName as employeeFirstName,"
						+ " e.lastName as employeeLastName,"
						+ " e.email as employeeEmail,"
						+ " e.type as employeeType,"
						+ " e.seniority as employeeSeniority,"
						+ " e.passHash as employeePassHash,"
						+ " m.userId as managerId,"
						+ "	m.firstName as managerFirstName,"
						+ " m.lastName as managerLastName,"
						+ " m.email as managerEmail,"
						+ " m.type as managerType,"
						+ " m.seniority as managerSeniority,"
						+ " m.passHash as managerPassHash"
						+ " FROM " + shiftsTable + " "
						+ " LEFT JOIN " + usersTable + " as e "
						+ " ON s.employeeId = e.userId"
						+ " LEFT JOIN " + usersTable + " as m "
						+ " ON s.managerId = m.userId"
						+ " WHERE start >= ? "
						+ " AND start < ? "
						+ " AND employeeId = ?";
		
		try {
			connection = DBConnection.getInstance().getConnection();
			statement  = connection.prepareStatement(getShifts);
			// rollback the day
			LocalDate firstDay = weekOf.with(
					 TemporalAdjusters.previousOrSame(FIRST_DAY) );
	        Timestamp startOfWeek = Timestamp.valueOf(firstDay.atStartOfDay());
	        Timestamp startOfNextWeek = Timestamp.valueOf(firstDay.plusDays(7).atStartOfDay());
	        Shift shift = null;
			
	        statement.setTimestamp(1, startOfWeek);
	        statement.setTimestamp(2, startOfNextWeek);
	        statement.setInt	  (3, employee.getUserId());
			resSet = statement.executeQuery();
			
			while(resSet.next()) {
				shift = new Shift.Builder()
								 .setShiftId(resSet.getInt("shiftId"))
								 .setStart(resSet.getTimestamp("start").toLocalDateTime())
								 .setEnd(resSet.getTimestamp("end").toLocalDateTime())
								 .setStatus(ShiftStatusFactory.get(resSet.getString("status")))
								 .assignEmployee(employee)
								 .build();
				if (resSet.getInt("managerId")!= 0) {
					shift.setManager(UserFactory.get(
										resSet.getInt   ("managerId"), 
										resSet.getString("managerFirstName"), 
										resSet.getString("managerLastName"), 
										resSet.getString("managerEmail"), 
										resSet.getString("managerType"), 
										resSet.getInt   ("managerseniority"), 
										resSet.getString("managerpassHash")));
				}
				if (resSet.getInt("employeeId") != 0) {
					shift.assignEmployee (UserFactory.get(
										resSet.getInt   ("employeeId"), 
										resSet.getString("employeefirstName"), 
										resSet.getString("employeelastName"), 
										resSet.getString("employeeemail"), 
										resSet.getString("employeetype"), 
										resSet.getInt   ("employeeseniority"), 
										resSet.getString("employeepassHash")));
				}
				shifts.add(shift);
			}
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resSet!=null) {
					resSet.close();
				}
				if (statement!=null) {
					statement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return shifts;
	}

	@Override
	public void updateShift(Shift shift) {
		String newShift = "UPDATE " + shiftsTable + " SET "
						+ "start = ?,"
						+ "end = ?, "
						+ "status = ?, "
						+ "employeeId = ?, "
						+ "managerId = ? "
						+ "WHERE shiftId = ?";
		Connection 		  connection    = null;
		PreparedStatement statement     = null;
		
		try {
			connection = DBConnection.getInstance().getConnection();
			statement = connection.prepareStatement(newShift,  Statement.RETURN_GENERATED_KEYS);
			
			statement.setTimestamp(1, Timestamp.valueOf(shift.getStart()));
			statement.setTimestamp(2, Timestamp.valueOf(shift.getEnd()));
			statement.setString   (3, shift.getStatus().toString());
			statement.setInt	  (4, shift.getEmployee() != null ? shift.getEmployee().getUserId() : 0);
			statement.setInt	  (5, shift.getManager().getUserId());
			statement.setInt	  (6, shift.getShiftId());
			
			statement.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			try {
				if (statement != null) {
					statement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Override
	public Duration getScheduledTimeByWeek(LocalDate weekOf, int userId) {
		List<Shift> shifts = new ArrayList<>();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resSet = null;
		Duration scheduledTime = Duration.ZERO;
		String getShifts = "SELECT "
						+ "	start,"
						+ "	end"
						+ " FROM " + shiftsTable
						+ " WHERE start >= ? "
						+ " AND start < ? "
						+ " AND employeeId = ?";
		
		try {
			connection = DBConnection.getInstance().getConnection();
			statement  = connection.prepareStatement(getShifts);
			// rollback the day
			LocalDate firstDay = weekOf.with(
					 TemporalAdjusters.previousOrSame(FIRST_DAY) );
	        Timestamp startOfWeek = Timestamp.valueOf(firstDay.atStartOfDay());
	        Timestamp startOfNextWeek = Timestamp.valueOf(firstDay.plusDays(7).atStartOfDay());
	        Shift shift;
			
	        statement.setTimestamp(1, startOfWeek);
	        statement.setTimestamp(2, startOfNextWeek);
	        statement.setInt	  (3, userId);
			resSet = statement.executeQuery();
			
			while(resSet.next()) {
				shift = new Shift.Builder()
								 .setStart(resSet.getTimestamp("start").toLocalDateTime())
								 .setEnd(resSet.getTimestamp("end").toLocalDateTime())
								 .build();
				shifts.add(shift);
			}
			scheduledTime = calcScheduledTime(shifts);
			
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resSet!=null) {
					resSet.close();
				}
				if (statement!=null) {
					statement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return scheduledTime;
	}
	
	@Override
	public Duration getScheduledTimeByDay(LocalDate date, int userId) {
		List<Shift> shifts = new ArrayList<>();
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resSet = null;
		Duration scheduledTime = Duration.ZERO;
		String getShifts = "SELECT "
						 + "	start,"
						 + "	end"
						 + " FROM " + shiftsTable
						 + " WHERE start >= ? "
						 + " AND start < ? "
						 + " AND employeeId = ?";
		
		try {
			connection = DBConnection.getInstance().getConnection();
			statement  = connection.prepareStatement(getShifts);
		    Timestamp startOfDay = Timestamp.valueOf(date.atStartOfDay());
		    Timestamp startOfNextDay = Timestamp.valueOf(date.plusDays(1).atStartOfDay());
		    Shift shift = null;
			
		    statement.setTimestamp(1, startOfDay);
		    statement.setTimestamp(2, startOfNextDay);
		    statement.setInt	  (3, userId);
			resSet = statement.executeQuery();
			
			while(resSet.next()) {
				shift = new Shift.Builder()
								 .setStart(resSet.getTimestamp("start").toLocalDateTime())
								 .setEnd(resSet.getTimestamp("end").toLocalDateTime())
								 .build();
				shifts.add(shift);
			}
			scheduledTime = calcScheduledTime(shifts);
			
		} catch(SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (resSet!=null) {
					resSet.close();
				}
				if (statement!=null) {
					statement.close();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return scheduledTime;
	}
	
	private Duration calcScheduledTime(List<Shift> shifts) {
		Duration timeWorked = Duration.ZERO;
		for (Shift shift: shifts) {
			timeWorked = timeWorked.plus(shift.getDuration());
		}
		return timeWorked;
	}
	

}
