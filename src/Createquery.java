import java.io.*;
import java.text.*;
import java.util.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class Createquery extends HttpServlet {
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
				+ "<td><a href=../servlet/Signout>Sign Out</a></td>" + "</tr>"
				+ "</table>");

		out.println("<div id=line></div>" + "<br>");
	}

	private void footerSetup(PrintWriter out) {
		out.println("</body>");
		out.println("</html>");
	}

	private boolean fieldEmptyError(PrintWriter out, String toCheck,
			String fieldname) {
		if (toCheck.equals("")) {
			out.println("* No " + fieldname + " entered!<br>");
			return false;
		}
		return true;
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

			/*
			 * SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.SSS");
java.util.Date parsedDate = dateFormat.parse("2006-05-22 14:04:59:612");
java.sql.Timestamp timestamp = new java.sql.Timestamp(parsedDate.getTime());

			 */
			
			String submitval = request.getParameter("createconf");
			if (submitval.equals("Create")) {
				String cname = request.getParameter("confn");
				String confstart = request.getParameter("confstart");
				String confend = request.getParameter("confend");
				String reqrevs = request.getParameter("reqnumrevs");
				String maxrevs = request.getParameter("maxnumrevs");

				boolean ready = true;
				if (!fieldEmptyError(out, cname, "conference name"))
					ready = false;
				if (!fieldEmptyError(out, confstart, "start time"))
					ready = false;
				if (!fieldEmptyError(out, confend, "end time"))
					ready = false;
				if (!fieldEmptyError(out, reqrevs,
						"required number of reviewers"))
					ready = false;
				if (!fieldEmptyError(out, maxrevs, "max number of reviewers"))
					ready = false;

				try
				{
				SimpleDateFormat startdate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				java.util.Date parsedStartDate = startdate.parse(confstart);
				java.sql.Timestamp stimestamp = new java.sql.Timestamp(parsedStartDate.getTime());
				
				SimpleDateFormat enddate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
				java.util.Date parsedEndDate = enddate.parse(confend);
				java.sql.Timestamp etimestamp = new java.sql.Timestamp(parsedEndDate.getTime());
				
				if(stimestamp.after(etimestamp))
				{
					out.println("* The start value entered is after the end value");
					ready = false;
				}
				
				
				} catch(java.text.ParseException ex)
				{
					out.println("There was an error parsing your dates. Please check your fields and try again!<br>");
					System.err.println("SQLException: " + ex.getMessage());
				}
				
				if (ready) {
					String query = "insert into conferences (cname, substart, subend, num_revs, max_revs) "
							+ "values ('"
							+ cname
							+ "', '"
							+ confstart
							+ "', '"
							+ confend + "', " + reqrevs + ", " + maxrevs + ")";

					out.println(query);

					Statement stmt = con.createStatement();

					try {
						con.setAutoCommit(false);
						stmt.executeUpdate(query);
						con.commit();

						out.println("Created conference successfully!<br>");

						con.setAutoCommit(true);
					} catch (SQLException ex) {
						out.println("There was an error entering the conference into the database<br>"
								+ "please check your fields and try again.");
						System.err.println("SQLException: " + ex.getMessage());
						con.rollback();
						con.setAutoCommit(true);

					}

				} else {
					out.println("Encountered above errors, please enter the required fields and try again");
				}
			} else if (submitval.equals("Edit")) {
				String cname = request.getParameter("confn");
				String confstart = request.getParameter("confstart");
				String confend = request.getParameter("confend");
				String reqrevs = request.getParameter("reqnumrevs");
				String maxrevs = request.getParameter("maxnumrevs");

				boolean ready = true;
				if (!fieldEmptyError(out, cname, "conference name"))
					ready = false;
				if (!fieldEmptyError(out, confstart, "start time"))
					ready = false;
				if (!fieldEmptyError(out, confend, "end time"))
					ready = false;
				if (!fieldEmptyError(out, reqrevs,
						"required number of reviewers"))
					ready = false;
				if (!fieldEmptyError(out, maxrevs, "max number of reviewers"))
					ready = false;

				if (ready) {
					String cid = request.getParameter("cid");

					String query = "update conferences set cname='" + cname
							+ "', substart='" + confstart + "', subend='"
							+ confend + "', num_revs=" + reqrevs
							+ ", max_revs=" + maxrevs + " where cid=" + cid;
					//out.println(query);

					Statement stmt = con.createStatement();
					try {
						con.setAutoCommit(false);
						stmt.executeUpdate(query);
						con.commit();
						out.println("Updated conference successfully!<br>");

						con.setAutoCommit(true);
					} catch (SQLException ex) {
						out.println("There was an error updating the conference in the database<br>");
						System.err.println("SQLException: " + ex.getMessage());
						con.rollback();
						con.setAutoCommit(true);

					}

				} else {
					out.println("Encountered the above errors. Please enter valid fields and try again");
				}
			}

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
