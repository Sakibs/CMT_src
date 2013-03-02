import java.io.*;
import java.text.*;
import java.util.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class LoginManager extends HttpServlet{
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
			HttpSession session = request.getSession(true);

			String firstname = "";
			String middlename = "";
			String lastname = "";
			String email = "";
			String affil = "";
			int isChair=0;

			// String pageTracker =
			// session.getAttribute("pageTracker").toString();
			// this should be the first page of the session

			String submitVal = request.getParameter("submitVal");

			if (submitVal.equals("Sign in")) {
				headerSetup(out, "Signing in");

				Statement stmt = con.createStatement();

				String username = "";
				String password = "";
				username = request.getParameter("username");
				password = request.getParameter("password");

				boolean readyForQuery = true;
				if(!fieldEmptyError(out, username, "username") || !fieldEmptyError(out, password, "password"))
					readyForQuery = false;
				
				if (readyForQuery) {
					//out.println("going to search db for "+username+" and "+password);
					
					String chkUsr = "select * from users where email = '"
							+ username + "' and last_n = '" + password + "'";
					ResultSet rs = stmt.executeQuery(chkUsr);

					// rs.next();
					// if we got a resultset from the query, the user was in
					// system
					if (rs.next()) {
						email = rs.getString(2);
						firstname = rs.getString(3);
						middlename = rs.getString(4);
						lastname = rs.getString(5);
						affil = rs.getString(6);
						isChair = rs.getInt(7);
						session.setAttribute("firstn", firstname);
						session.setAttribute("middlen", middlename);
						session.setAttribute("lastn", lastname);
						session.setAttribute("email", email);
						session.setAttribute("affil", affil);
						session.setAttribute("ischair", isChair);
						
						if(isChair == 1)
						{
							out.println("<h3>Signed in successfully! Welcome Chair user!<br></h3>"
									+ "<a href=../servlet/ChairManager>Continue to profile page</a>");
						}
						else
						{
							out.println("<h3>Signed in successfully!<br></h3>"
									+ "<a href=../servlet/User>Continue to profile page</a>");
						}
					}
					// otherwise the user was not in system, user not found
					else {
						out.println("<h3>Error signing in, username and/or password was not found in the system<br>"
								+ "Please <a href=../index.html>try signing in again, or if you are new register</a>.");
					}
					
				} else {
					out.println("Encountered the above errors when trying to sign you in, please enter the required fields and try again");
				}

				footerSetup(out);
			} else if (submitVal.equals("Register")) {
				headerSetup(out, "Registering");
				
				Statement stmt = con.createStatement();

				firstname = request.getParameter("r_first");
				middlename = request.getParameter("r_middle");
				lastname = request.getParameter("r_last");
				email = request.getParameter("r_email");
				affil = request.getParameter("r_affil");

				boolean readyForQuery = true;
				if(!fieldEmptyError(out, firstname, "first name"))
					readyForQuery = false;
				if(!fieldEmptyError(out, lastname, "last name"))
					readyForQuery = false;
				if(!fieldEmptyError(out, email, "email"))
					readyForQuery = false;
				if(!fieldEmptyError(out, affil, "affiliation"))
					readyForQuery = false;
				

				if (readyForQuery) {
					//out.println("going to enter in db "+firstname+", "+middlename+", "+lastname+", "+email+", "+affil);
					
					String insertUsr = "insert into users (email, first_n, middle_n, last_n, affil) "
							+ "values ('"
							+ email
							+ "', '"
							+ firstname
							+ "', '"
							+ middlename
							+ "', '"
							+ lastname
							+ "', '"
							+ affil + "')";
					//out.println(insertUsr);
					try {
						con.setAutoCommit(false);
						stmt.executeUpdate(insertUsr);
						con.commit();
						session.setAttribute("firstn", firstname);
						session.setAttribute("middlen", middlename);
						session.setAttribute("lastn", lastname);
						session.setAttribute("email", email);
						session.setAttribute("affil", affil);
						session.setAttribute("ischair", 0);

						out.println("Registered successfully!<br>"
								+ "You are signed in and can <a href=../servlet/User>continue to your profile page</a>");

						con.setAutoCommit(true);
					} catch (SQLException ex) {
						out.println("There was an error entering your information into the database<br>"
								+ "please check your fields and try again. You might already be registered.");
						System.err.println("SQLException: " + ex.getMessage());
						con.rollback();
						con.setAutoCommit(true);

					}
				} else {
					out.println("Encountered the above errors when trying to sign you in, please enter the required fields and try again");
				}
				
				footerSetup(out);
			}

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
