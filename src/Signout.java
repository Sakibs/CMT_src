
import java.io.*;
import java.text.*;
import java.util.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class Signout extends HttpServlet{
	private String url = "jdbc:mysql://localhost/cs143s36";
	private String userName = "cs143s36";
	private String password = "sssmsql143";

	private void headerSetup(PrintWriter out, String title) {
		out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01//EN\" \"http://www.w3.org/TR/html4/strict.dtd\">");
		out.println("<html>");
		out.println("<head>");
		out.println("<title>" + title + "</title>");
		out.println("<link rel=stylesheet type=text/css href=../style.css />");
		out.println("</head>");
		out.println("<body>");
		out.println("<h1>" + title + "</h1>");
		
		out.println("<div id=line></div>" + "<br>");
	}

	private void footerSetup(PrintWriter out) {
		out.println("</body>");
		out.println("</html>");
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException, ServletException {
		// Register MySQL JDBC driver
		try {
			// register the MySQL JDBC driver with DriverManager
			Class.forName("com.mysql.jdbc.Driver");
		} catch (Exception e) {
			e.printStackTrace();
		}

		// get the output stream for result page
		response.setContentType("text/html");
		PrintWriter out = response.getWriter();
		try {
			Connection con = null;
			con = DriverManager.getConnection(url, userName, password);

			// initiate the session and set up variables needed for signin
			HttpSession session = request.getSession(true);

			String firstname = "";
			String middlename = "";
			String lastname = "";
			String email = "";
			String affil = "";
			int isChair = 0;

			firstname = session.getAttribute("firstn").toString();
			middlename = session.getAttribute("middlen").toString();
			lastname = session.getAttribute("lastn").toString();
			email = session.getAttribute("email").toString();
			affil = session.getAttribute("affil").toString();
			
			headerSetup(out, "Signout");
			
			session.setAttribute("firstn", "");
			session.setAttribute("middlen", "");
			session.setAttribute("lastn", "");
			session.setAttribute("email", "");
			session.setAttribute("affil", "");
			
			out.println("You have been successfully signed out!!");
			
			footerSetup(out);
			
			con.close();
		} catch (SQLException ex) {
			out.println("SQLException caught<br>");
			out.println("---<br>");
			while (ex != null) {
				out.println("Message   : " + ex.getMessage() + "<br>");
				out.println("SQLState  : " + ex.getSQLState() + "<br>");
				out.println("ErrorCode : " + ex.getErrorCode() + "<br>");
				out.println("---<br>");
				ex = ex.getNextException();
			}
		}
	}
}
