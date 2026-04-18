<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"
	import="shifts.Shift"
	import="users.Employee"
	import="java.util.List"
	import="java.time.LocalDate"  
	import="java.time.LocalTime"  
%>
<%
	List<Shift>    shifts    = (List<Shift>)request.getAttribute("shifts");
	List<Employee> employees = (List<Employee>)request.getAttribute("employees");
	String 		   status    = (String)request.getAttribute("status");
	
    final String EMPLOYEE_NOT_ELIGIBLE = "Employee not eligible for this shift";
	final String EMPLOYEE_ASSIGNED = "Employee Assigned";
	final String EMPLOYEE_NOT_FOUND = "Employee not found";
	final String SHIFT_NOT_FOUND = "Shift not found";
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Assign Shift</title>
<link rel="stylesheet" type="text/css" href="<%= request.getContextPath() %>/css/styles.css">
</head>
<body>

<div class="dashboard-container form-section">

    <div class="top-bar">
        <div class="dashboard-header">
            <h1>Assign Shift</h1>
            <p class="dashboard-intro">
                Assign employees to available shifts.
            </p>
        </div>
	<!-- Status messages -->
	<%
	if (status != null) {
		if(EMPLOYEE_NOT_ELIGIBLE.equalsIgnoreCase(status)
		|| EMPLOYEE_NOT_FOUND.equalsIgnoreCase(status)	
		|| SHIFT_NOT_FOUND.equalsIgnoreCase(status)	
		){
	%>
			<p style="color:red"><%=status%></p>
	<%
		}
		if (EMPLOYEE_ASSIGNED.equalsIgnoreCase(status)) {
	%>
			<p style="color:green"><%=status%></p>
	<%
		}
	}
	%>
       
    </div>

    <div class="dashboard-section shift-form">
        <form action="#" method="post">

            <div class="form-grid">

                <!-- Employee -->
                <div class="form-group">
                    <label for="employee">Select Employee</label>
                    <select id="employee" name="employee">
                    	<%
                    	if (employees == null || employees.isEmpty()){
                    	%>
                    		<option value="">No employees available</option>
                    	<%	
                    	} else {
                    	%>
	                    	<option value="">Select employee</option>
	                    <%
	                    	for (Employee empl: employees){
	                    		int    userId  = empl.getUserId();
	                    		String name    = empl.getFullName();
	                    		String type    = empl.getType();
	                    %>	
	                        	<option value="<%=userId%>"><%=name%> (<%=type%>)</option>
                    	<%
	                    	}
                    	}
                    	%>
                        
                    </select>
                </div>
                <!--  <div class="form-group">
                    <label for="employee">Select Employee</label>
                    <select id="employee" name="employee">
                        <option value="">Select employee</option>
                        <option>Emily Carter</option>
                        <option>Michael Brown</option>
                        <option>Sophia Wilson</option>
                        <option>Daniel Lee</option>
                    </select>
                </div>-->

                <!-- Shift -->
                <div class="form-group">
                    <label for="shift">Select Shift</label>
                    <select id="shift" name="shift">
                    <%
                    	if (shifts == null || shifts.isEmpty()){
                    %>
                    		<option value="">No Shifts Scheduled</option>
                    <%
                    	} else {
                    %>
                    	<option value="">Select shift</option>
                    <%
                    		for (Shift shift: shifts) {
                    			int       shiftId = shift.getShiftId();
                    			LocalDate date    = shift.getStart().toLocalDate();
                    			LocalTime start   = shift.getStart().toLocalTime();
                    			LocalTime end     = shift.getEnd().toLocalTime();
                    %>
                    			<option value="<%=shiftId%>">
                    			<%=date%>: <%=start%> - <%=end%>
                    			</option>
                    <%
                    		}
                    	}
                    %>
                        
                    </select>
                </div>
                
                <!--  
                <div class="form-group">
                    <label for="shift">Select Shift</label>
                    <select id="shift" name="shift">
                        <option value="">Select shift</option>
                        <option>Morning Shift (08:00 - 16:00)</option>
                        <option>Evening Shift (16:00 - 00:00)</option>
                        <option>Night Shift (00:00 - 08:00)</option>
                    </select>
                </div>-->

            </div>

            <!-- Notes -->
            <div class="form-group full-width">
                <label for="notes">Notes (Optional)</label>
                <textarea id="notes" name="notes" rows="4" placeholder="Add any notes..."></textarea>
            </div>

            <div class="form-actions">
                <input type="submit" value="Assign Shift" class="btn">
                <a href="<%= request.getContextPath() %>/managerDash" class="btn small-btn">Cancel</a>
            </div>

        </form>
    </div>

</div>

</body>
</html>