import java.io.*;
import java.text.*;
import java.util.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;


public class Assignrevs extends HttpServlet{
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
			HttpSession session = request.getSession(false);

			headerSetup(out, "Assign Reviewers");
			
			String cid = request.getParameter("cid");
			
			//out.println("cid: "+cid);
			
			if(session.getAttribute("confid") == null)
			{
				//out.println(" confid is null<br>");
				session.setAttribute("confid", cid);
			}
			
			
			boolean firstinstance = false;
			
			if(session.getAttribute("firstvisitrev") == null)
			{
				//out.println("first visit to page");
				session.setAttribute("firstvisitrev", "no");
				firstinstance = true;
			}
			if(firstinstance == false)
			{
				String revid = request.getParameter("revtoadd");
				String confid = session.getAttribute("confid").toString();
				if(!(revid == null))
				{
					//out.println("Going to enter reviewer "+revid+" conference "+confid);
					
					String insertr = "insert into conf_reviewer values("+confid+", "+revid+")";
					Statement inr = con.createStatement();
					
					try {
						con.setAutoCommit(false);
						inr.executeUpdate(insertr);
						con.commit();

						out.println("Entered user successfully!<br>");
						out.println("<br>If you want to enter another reviewer<br>");

						con.setAutoCommit(true);
					} catch (SQLException ex) {
						out.println("There was an error entering the reviewer into the database<br>");
						System.err.println("SQLException: " + ex.getMessage());
						con.rollback();
						con.setAutoCommit(true);
					}
				}				
			}

			
			out.println("<h3>Select a reviewer to add to this conference</h3>");
						
			String confid = session.getAttribute("confid").toString();
			
			String query = "select * from users where uID not in (select userID from conf_reviewer where confID = "+confid+")";
			out.println("Users currently not in conference<br>");
			
			Statement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery(query);
			
			out.println("<form method=get action=../servlet/Assignrevs><select name=revtoadd>");
			while(rs.next())
			{
				String uid = rs.getString(1);
				if(uid.equals("1"))
					continue;
				String fname = rs.getString(3);
				String mname = rs.getString(4);
				String lname = rs.getString(5);
				out.println("<option value="+uid+">"+fname+" "+mname+" "+lname+"<br>");
			}
			out.println("</select><input type=submit name=add value=\"Add Reviewers\"></form>");
			
						
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
