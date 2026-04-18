package shiftEligibility;

import java.time.Duration;
import java.time.LocalDate;

import dao.ShiftDAO;
import services.ShiftService;
import shifts.Shift;
import users.Employee;
import users.EmployeeType;

public class PartTimeEligibility implements ShiftEligibilityStrat {

	private static final Duration     minDur    = Duration.ofHours(3);
	private static final Duration 	  maxDur    = Duration.ofHours(8);
	private static final Duration 	  maxHours  = Duration.ofHours(EmployeeType.PARTTIME.getMaxHours()) ;
	private static final ShiftService shiftServ = new ShiftDAO();
	
	public PartTimeEligibility() {
	}

	@Override
	public Boolean canSelectShift(Employee empl, Shift shift) {
		// Can be assigned shifts of no less than 3 hours,
		Duration shiftLength = shift.getDuration();
		if (shiftLength.compareTo(minDur) < 0
		||  shiftLength.compareTo(maxDur) > 0) {
			return false;
		}
		
		LocalDate shiftDate  = shift.getStart().toLocalDate();
		int       userId     = empl.getUserId();
		Duration  timeWorked = shiftServ.getScheduledTimeByWeek(shiftDate, userId);
		Duration  newTime    = timeWorked.plus(shiftLength);
		// No more than 40 hours per week.
		if (newTime.compareTo(maxHours) > 0) {
			return false;
		}
		
		return true;
	}

}
