package shiftEligibility;

import schedule.WorkWeek;
import shifts.Shift;
import users.Employee;

public interface ShiftEligibilityStrat {
	
	public Boolean canSelectShift(Employee empl, Shift shift);
	
}
