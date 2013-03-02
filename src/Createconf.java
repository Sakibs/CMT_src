import java.io.*;
import java.text.*;
import java.util.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class Createconf extends HttpServlet {
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
		out.println("<table class=nav-List cellspacing=0 cellpadding=0>"
				+ "<tr>"
				+ "<td><a href=../servlet/Createconf>Create Conference</a></td>"
				+ "<td><a href=../servlet/ChairManager>View Conference</a></td>"
				+ "<td><a href=../servlet/Signout>Sign Out</a></td>" + "</tr>" + "</table>");

		out.println("<div id=line></div>" + "<br>");
	}

	private void footerSetup(PrintWriter out) {
		out.println("</body>");
		out.println("</html>");
	}

	private void printform(PrintWriter out) {
		out.println("<form method=get action=../servlet/Createquery>"
				+ "<table>"
				+ "<tr><td><b>Conference Name:</b></td>    <td><input type=text name=confn maxlength=100 size=40></td></tr>"
				+ "<tr><td><b>Submission Start Time:</b><br>in format YYYY-MM-DD HH:MM:SS</td>    <td><input type=text name=confstart maxlength=100 size=40></td></tr>"
				+ "<tr><td><b>Submission End Time:</b><br>in format YYYY-MM-DD HH:MM:SS</td>    <td><input type=text name=confend maxlength=100 size=40></td></tr>"
				+ "<tr><td><b>Required Number of Reviewers per Paper:</b></td>    <td><input type=text name=reqnumrevs maxlength=100 size=40></td></tr>"
				+ "<tr><td><b>Maximum Number of papers to review:</b></td>    <td><input type=text name=maxnumrevs maxlength=100 size=40></td></tr>"
				+ "</table>"
				+ "<input type=submit name=createconf value=Create>" + "</form>");
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
			HttpSession session = request.getSession(false);
			
			headerSetup(out, "Create Conference");
			printform(out);
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
