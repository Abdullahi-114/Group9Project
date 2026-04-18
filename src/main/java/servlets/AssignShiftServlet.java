package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import services.ShiftService;
import services.UserService;
import shifts.Shift;
import users.Employee;
import users.User;

import java.io.IOException;
import java.util.List;

import dao.ShiftDAO;
import dao.UserDAO;

@WebServlet("/assignShift")
public class AssignShiftServlet extends HttpServlet {
    private static final String EMPLOYEE_NOT_ELIGIBLE = "Employee not eligible for this shift";
	private static final String EMPLOYEE_ASSIGNED = "Employee Assigned";
	private static final String EMPLOYEE_NOT_FOUND = "Employee not found";
	private static final String SHIFT_NOT_FOUND = "Shift not found";
	private static final long serialVersionUID = 1L;
    private static final String ASSIGN_SHIFT_JSP = "/WEB-INF/views/assignShift.jsp";
    private static final ShiftService shiftServ = new ShiftDAO();
    private static final UserService userServ   = new UserDAO();
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

    	HttpSession s = request.getSession(false);
    	Integer userId = (Integer) s.getAttribute("userId");
    	if (userId == null) {
			bounce(request, response, "loginError");
		}
    	User u = userServ.getUser(userId);
    	if (!u.isManager()) {
			bounce(request, response, "InsufficientRights");
		}
    	List<Employee> employees = userServ.getAllEmployees();
    	List<Shift> shifts = shiftServ.getOpenShifts(userId);
    	request.setAttribute("employees", employees);
    	request.setAttribute("shifts", shifts);
        request.getRequestDispatcher(ASSIGN_SHIFT_JSP)
               .forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	int employeeId = Integer.valueOf(request.getParameter("employee"));
    	int shiftId    = Integer.valueOf(request.getParameter("shift"));
    	Shift shift    = shiftServ.getShift(shiftId);
    	User  user     = userServ.getUser(employeeId);
    	HttpSession s = request.getSession(false);
    	Integer managerId = (Integer) s.getAttribute("userId");
    	String status = null;
    	if (managerId == null) {
			bounce(request, response, "loginError");
			return;
		}
    	User u = userServ.getUser(managerId);
    	if (!u.isManager()) {
			bounce(request, response, "InsufficientRights");
			return;
		}
    	Employee empl = (Employee)user;
    	// Check shift exists
    	if (shift == null) {
    		status = SHIFT_NOT_FOUND;
    	}
    	// Check employee exists
    	else if (user == null || user.isManager()) {
    		status = EMPLOYEE_NOT_FOUND;
    	}
    	// Check shift eligibility
    	else if (!empl.canSelectShift(shift)) {
    		status = EMPLOYEE_NOT_ELIGIBLE;
    	} else {
    		// Assign the shift
	    	shift.assignEmployee(empl);
	    	shiftServ.updateShift(shift);
	    	status = EMPLOYEE_ASSIGNED;
    	}
    	
    	List<Employee> employees = userServ.getAllEmployees();
    	List<Shift> shifts = shiftServ.getOpenShifts(managerId);
    	request.setAttribute("employees", employees);
    	request.setAttribute("shifts", shifts);
    	
    	request.setAttribute("status", status);
    	request.getRequestDispatcher(ASSIGN_SHIFT_JSP)
		.forward(request, response);
    }
    
    
	private void bounce(HttpServletRequest request,
			 HttpServletResponse response,
			 String status)
			 throws ServletException, IOException {

		request.setAttribute("status", status);
		request.getRequestDispatcher("/login")
		.forward(request, response);
	}
}