import java.io.*;
import java.text.*;
import java.util.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class Calculate extends HttpServlet{
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

			headerSetup(out, "Calculating");
			
			Statement stmt = con.createStatement();
			
			String confid = request.getParameter("cid");
			int numrevs = Integer.parseInt(request.getParameter("nrevs"));
			
			boolean ready = true;
			
			String check_req_assign = "select count(*) from roles where confID="+confid+" and roleName='reviewer' group by paperID";
			ResultSet rs = stmt.executeQuery(check_req_assign);
			
			while(rs.next())
			{
				int num_for_paper = rs.getInt(1);
				if(num_for_paper < numrevs);
				{
					ready = false;
					out.println("* One or more of the papers in this conference were not assigned the required number or reviewers!");
					break;
				}
					
			}
			
			rs.close();
			
			String check_submitted_revs ="select count(*) from roles where confID="+confid+" and roleName='reviewer' and rating is NULL";
			
			rs = stmt.executeQuery(check_submitted_revs);
			if(rs.next())
			{
				int num_not_sub = rs.getInt(1);
				if(num_not_sub>0)
				{
					ready = false;
					out.println("* One or more reviewers have not submitted their reviews yet!");
				}
			}
			
			rs.close();
			
			if(ready)
			{
				out.println("Calculating the reviews");	
				
				String calculate = "select paperID, avg(rating) from roles where confID="+confid+" and roleName='reviewer' group by paperID";
				rs = stmt.executeQuery(calculate);
				while(rs.next())
				{
					String pid = rs.getString(1);
					double result = rs.getDouble(2);
					
					Statement stmt2 = con.createStatement();
					
					String updatestat ="";
					if(result>4)
						updatestat="update papers set p_stat='Accept' where pID="+pid;
					else
						updatestat="update papers set p_stat='Reject' where pID="+pid;
					
					try {
						con.setAutoCommit(false);
						stmt.executeUpdate(updatestat);
						con.commit();
						con.setAutoCommit(true);
					} catch (SQLException ex) {
						out.println("There was an error updating the status of the paper with pID "+pid+" in the database<br>");
						System.err.println("SQLException: " + ex.getMessage());
						con.rollback();
						con.setAutoCommit(true);
					}
				}
				
				rs.close();
				
				String updateconf = "update conferences set iscalc=1 where cID="+confid;
				try {
					con.setAutoCommit(false);
					stmt.executeUpdate(updateconf);
					con.commit();
					con.setAutoCommit(true);
				} catch (SQLException ex) {
					out.println("There was an error updating the status of the conference to calculated in the database<br>");
					System.err.println("SQLException: " + ex.getMessage());
					con.rollback();
					con.setAutoCommit(true);
				}

			}
			else
			{
				out.println("This conference is not ready for final calculations due to above conditions.");
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
