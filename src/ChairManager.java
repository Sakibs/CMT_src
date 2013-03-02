import java.io.*;
import java.text.*;
import java.util.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;

public class ChairManager extends HttpServlet {
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

			// initiate the session and set up variables needed for signin
			HttpSession session = request.getSession(false);
			headerSetup(out, "Chair Manager");
			
			String firstname = "";
			String middlename = "";
			String lastname = "";
			String email = "";
			String affil = "";
			int isChair = 0;

			//firstname = session.getAttribute("firstn").toString();
			//middlename = session.getAttribute("middlen").toString();
			//lastname = session.getAttribute("lastn").toString();
			
			if(session.isNew())
			{
				out.println("New session found");
			}
			else //email = session.getAttribute("email").toString();
			{
				email = session.getAttribute("email").toString();
				//out.println("Not new session");
			}
			
			if(!(session.getAttribute("firstvisitrev") == null))
				session.removeAttribute("firstvisitrev");
			if(!(session.getAttribute("confid") == null))
				session.removeAttribute("confid");
			if(!(session.getAttribute("revid") == null))
				session.removeAttribute("revid");
			if(!(session.getAttribute("maxcount") == null))
				session.removeAttribute("maxcount");


			
			//affil = session.getAttribute("affil").toString();
			
			Statement stmt1 = con.createStatement();
			Statement stmt2 = con.createStatement();

			//headerSetup(out, "View Conferences");
			
			//TimeStamp crap
			java.util.Date currentDateA = new java.util.Date();
			Timestamp currA = new Timestamp(currentDateA.getTime());
			String date = currA.toString();
						
			// get conferences
			String getconfs = "select * from conferences where subEnd > '"+date+"'";
			ResultSet rs = stmt1.executeQuery(getconfs);

			out.println("<h2>Ongoing Conferences</h2>");

			while (rs.next()) {
				int confid = rs.getInt(1);
				String confn = rs.getString(2);
				String confstart = rs.getString(3);
				String confend = rs.getString(4);
				String numrevs = rs.getString(5);
				String maxrevs = rs.getString(6);
				out.println("<table border=1 width=70%>");
				out.println("<tr><td colspan=2><b>Name: "+confn+"</b></td></tr>"
						+ "<tr><td><b>Conference Start Time: "+confstart+"</b></td><td><b>Conference End Time: "+confend+"</b></td></tr>"
						+ "<tr><td><b>Required reviewers per paper:</b> "+numrevs+"</td><td><b>Max papers a reviewer can review:</b> "+maxrevs+"</td></tr>"
						+ "<tr><td colspan=2><form method=get action=../servlet/Editconf><input type=hidden name=confid value="+confid
						+"><input type=submit name=confid value=\"Edit Conference\"></form></td></tr>");
				out.println("<tr><td colspan=2>"
						+ "<table width=100%>"
						+ "<tr><td><b>Reviewers</b></td><td><a href=../servlet/Assignrevs?cid="+confid+">Add Reviewers</a></td></tr>"
						+ "<tr><td>"
						+ "Click on Reviewer names to assign papers");
				
				String getconfrevs = "select * from users where uID in (select userID from conf_reviewer where confID = "+confid+")";
				ResultSet revs = stmt2.executeQuery(getconfrevs);
				out.println("<ul>");
				while (revs.next()) {
					//rid holds ids of reviewers in conference
					String rid = revs.getString(1);
					String rfirst = revs.getString(3);
					String rmid = revs.getString(4);
					String rlast = revs.getString(5);
					out.println("<li><a href=../servlet/Assignpapers?rid="+rid+"&cid="+confid+"&maxct="+maxrevs+">"+rfirst+" "+rmid+" "+rlast+"</a></li>");
				}
				revs.close();
				out.println("</ul>");
			
				out.println("</td></tr></table>");
				/*
				out.println("<table width=100%><tr><td><b>Authors</b></td></tr>"
						+ "Click on Author names to view papers they submitted");

				String getconfauths = "select * from conferences";
				ResultSet auths = stmt3.executeQuery(getconfauths);
				out.println("<ul>");
				while (auths.next()) {
					out.println("<li><a href=#>Auth name</a></li>");
				}
				out.println("</ul>");
				out.println("</td></tr></table>");
				
				out.println("</td></tr>");
				*/
				out.println("</table>");
			}
			rs.close();
			
			out.println("<br><hr>");
			
			String getoldconfs = "select * from conferences where subEnd < '"+date+"'";
			rs = stmt1.executeQuery(getoldconfs);

			out.println("<h2>Finished Conferences</h2>");

			while (rs.next()) {
				int confid = rs.getInt(1);
				String confn = rs.getString(2);
				String confstart = rs.getString(3);
				String confend = rs.getString(4);
				String numrevs = rs.getString(5);
				String maxrevs = rs.getString(6);
				int calc = rs.getInt(7);
				out.println("<table border=1 width=70%>");
				out.println("<tr><td colspan=2><b>Name: "+confn+"</b></td></tr>"
						+ "<tr><td><b>Conference Start Time: "+confstart+"</b></td><td><b>Conference End Time: "+confend+"</b></td></tr>"
						+ "<tr><td><b>Required reviewers per paper:</b> "+numrevs+"</td><td><b>Max papers a reviewer can review:</b> "+maxrevs+"</td></tr>");
				if(calc == 0)
				{
					out.println("<tr><td colspan=2><a href=../servlet/Calculate?cid="+confid+"&numrevs="+numrevs+">Calculate Final Results</a></td></tr>");
				}
				out.println("<tr><td colspan=2>"
						+ "<table width=100%>"
						+ "<tr><td><b>Reviewers</b></td><td><a href=../servlet/Assignrevs?cid="+confid+">Add Reviewers</a></td></tr>"
						+ "<tr><td>"
						+ "Click on Reviewer names to assign papers");
				
				String getconfrevs = "select * from users where uID in (select userID from conf_reviewer where confID = "+confid+")";
				ResultSet revs = stmt2.executeQuery(getconfrevs);
				out.println("<ul>");
				while (revs.next()) {
					//rid holds ids of reviewers in conference
					String rid = revs.getString(1);
					String rfirst = revs.getString(3);
					String rmid = revs.getString(4);
					String rlast = revs.getString(5);
					out.println("<li><a href=../servlet/Assignpapers?rid="+rid+"&cid="+confid+"&maxct="+maxrevs+">"+rfirst+" "+rmid+" "+rlast+"</a></li>");
				}
				revs.close();
				out.println("</ul>");
			
				out.println("</td></tr></table>");
				/*
				out.println("<table width=100%><tr><td><b>Authors</b></td></tr>"
						+ "Click on Author names to view papers they submitted");

				String getconfauths = "select * from conferences";
				ResultSet auths = stmt3.executeQuery(getconfauths);
				out.println("<ul>");
				while (auths.next()) {
					out.println("<li><a href=#>Auth name</a></li>");
				}
				out.println("</ul>");
				out.println("</td></tr></table>");
				
				out.println("</td></tr>");
				*/
				out.println("</table>");
			}
			rs.close();

			
			
			stmt1.close();
			stmt2.close();
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
