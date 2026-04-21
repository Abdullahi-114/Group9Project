package servlets;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import services.UserService;
import users.User;

import java.io.IOException;

import dao.UserDAO;

/**
 * Servlet implementation class ManagerDashServlet
 */
@WebServlet("/managerDash")
public class ManagerDashServlet extends HttpServlet {
	private static final String MAN_DASH_JSP = "/WEB-INF/views/ManagerDashboard.jsp";
	private static final long serialVersionUID = 1L;

	private static final UserService userServ = new UserDAO();

	public ManagerDashServlet() {
		super();
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Check that the user has a valid session
		HttpSession s = request.getSession(false);

		if (s == null || s.getAttribute("userId") == null) {
			bounce(request, response, "loginError");
			return;
		}

		Integer userId = (Integer) s.getAttribute("userId");
		User u = userServ.getUser(userId);

		// Check that the user exists and is a manager
		if (u == null || !u.isManager()) {
			bounce(request, response, "InsufficientRights");
			return;
		}

		// Pass manager object to JSP
		request.setAttribute("manager", u);
		request.getRequestDispatcher(MAN_DASH_JSP).forward(request, response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

	private void bounce(HttpServletRequest request,
						HttpServletResponse response,
						String status)
			throws ServletException, IOException {

		request.setAttribute("status", status);
		request.getRequestDispatcher("/login").forward(request, response);
	}
}
