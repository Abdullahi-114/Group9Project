package users;

import shiftEligibility.ShiftEligibilityStrat;
import shifts.Shift;

/**
 * Employee forms the base class for the different
 * employee types: Full Time, Part Time, and Casual
 * 
 * @author Sam Joyce
 */ 
public class Employee extends User {

	// Fields
	protected EmployeeType type;
	/**
	 * Stored as minutes for precision
	 */
	protected ShiftEligibilityStrat eligibility;
	
	// Constructors
	protected Employee() {
	}

	// Methods
	public String getType() {
		return type.toString();
	}

	public void setType(EmployeeType type) {
		this.type = type;
	}

	public Boolean isFullTime() {
		return type.isFullTime();
	}
	
	public Boolean isPartTime() {
		return type.isPartTime();
	}
	
	public Boolean isCasual() {
		return type.isCasual();
	}
	
	public Boolean isType(EmployeeType type) {
		return this.type != null && this.type.isType(type);
	}
	
	public Boolean isManager() {
		return false;
	}
	
	public Boolean canSelectShift(Shift shift) {
		return eligibility.canSelectShift(this, shift);
	}
	
	// Builder
	public static class Builder extends User.Builder<Builder> {
		protected EmployeeType type;
		protected ShiftEligibilityStrat eligibility;
		
		public Builder setType(EmployeeType type) {
			this.type = type;
			return self();
		}
		public Builder setEligibility(ShiftEligibilityStrat eligibility) {
			this.eligibility = eligibility;
			return self();
		}
		
		@Override
		public Employee build() {
			Employee e = new Employee();
			e.userId	  = userId;
			e.firstName	  = firstName;
			e.lastName    = lastName;
			e.email 	  = email;
			e.type        = type;
			e.seniority   = seniority;
			e.eligibility = eligibility;
			e.passHash	  = passHash;
			return e;
		}
		
		@Override
		protected Builder self() {
			return this;
		}
		
	}
		

}
