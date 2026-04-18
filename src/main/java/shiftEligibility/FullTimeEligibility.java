package shiftEligibility;

import java.time.Duration;
import java.time.LocalDate;

import dao.ShiftDAO;
import services.ShiftService;
import shifts.Shift;
import users.Employee;
import users.EmployeeType;

public class FullTimeEligibility implements ShiftEligibilityStrat {

	private static final Duration minDur = Duration.ofHours(8);
	private static final Duration maxHours = Duration.ofHours(EmployeeType.FULLTIME.getMaxHours()) ;
	private static final ShiftService shiftServ = new ShiftDAO();
	
	public FullTimeEligibility() {
	}

	@Override
	public Boolean canSelectShift(Employee empl, Shift shift) {
		// Must work 40 hours per week
		// 8 hour shifts
		Duration shiftLength = shift.getDuration();
		if (shiftLength != minDur) {
			return false;
		}
		// Cannot go over 40hrs/week
		LocalDate shiftDate = shift.getStart().toLocalDate();
		int       userId    = empl.getUserId();
		Duration timeWorked = shiftServ.getScheduledTimeByWeek(shiftDate, userId);
		Duration newTime    = timeWorked.plus(shiftLength);
		if (newTime.compareTo(maxHours) > 0) {
			return false;
		}
		
		return true;
	}

}
