import java.io.*;
import java.text.*;
import java.util.*;
import java.sql.*;

import javax.servlet.*;
import javax.servlet.http.*;


public class Assignpapers extends HttpServlet{
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

			headerSetup(out, "Assign papers");
			
			String rid = request.getParameter("rid");
			String cid = request.getParameter("cid");
			String maxct = request.getParameter("maxct");
			
			//out.println("cid: "+cid);
			
			if(session.getAttribute("revid") == null)
			{
				session.setAttribute("revid", rid);
			}
			if(session.getAttribute("confid") == null)
			{
				//out.println(" confid is null<br>");
				session.setAttribute("confid", cid);
			}
			if(session.getAttribute("maxcount") == null)
			{
				session.setAttribute("maxcount", maxct);
			}
			
			
			boolean firstinstance = false;
			
			if(session.getAttribute("firstvisitpaper") == null)
			{
				//out.println("first visit to page");
				session.setAttribute("firstvisitpaper", "no");
				firstinstance = true;
			}
			if(firstinstance == false)
			{
				String paperid = request.getParameter("papertoadd");
				String confid = session.getAttribute("confid").toString();
				String revid = session.getAttribute("revid").toString();
				
				if(!(paperid == null)&& !(confid == null)&& !(revid == null))
				{
					//out.println("Going to enter paper "+paperid+" conference "+confid+" reviewer "+revid);
					
					String insertp = "insert into roles (confID, userID, paperID, roleName) values ("+confid+", "+revid+", "+paperid+", 'reviewer')";
					Statement inp = con.createStatement();
					
					try {
						con.setAutoCommit(false);
						inp.executeUpdate(insertp);
						con.commit();

						out.println("Assigned paper successfully!<br>");
						out.println("<br>If you want to assign another paper<br>");

						con.setAutoCommit(true);
					} catch (SQLException ex) {
						out.println("There was an error entering the paper into the database<br>");
						System.err.println("SQLException: " + ex.getMessage());
						con.rollback();
						con.setAutoCommit(true);
					}
					
				}				
			}
			
			String curconf = session.getAttribute("confid").toString();
			String currev = session.getAttribute("revid").toString();
		
			String maxpossible = session.getAttribute("maxcount").toString();
			out.println("Max number of papers this reviewer can be assigned in this conference: "+maxpossible+"<br>");
			
			String check = "select count(paperID) from roles where confID= "+curconf+" and userID= "+currev+" and roleName='reviewer'";
			
			Statement s = con.createStatement();
			ResultSet r = s.executeQuery(check);
			
			boolean reachedlimit = false;
			if(r.next())
			{
				int curnum = r.getInt(1);
				out.println("Number of papers this reviewer is already assigned: "+curnum+"<br>");
				int maxnum = Integer.parseInt(maxpossible);
				
				if(curnum == maxnum)
					reachedlimit = true;
			}
			
			
			if(!reachedlimit)
			{
			out.println("<h3>Select a paper to assign to this reviewer</h3>");
			
			//query to get papers not assigned to this reviewer
			//select * from papers where pID not in (select paperID from roles where confID=1 and userID=10 and roleName='reviewer');
			
			//out.println("confid: "+curconf+" reviewer: "+currev+"<br>");			
			//String query = "select * from papers where pID not in (select paperID from roles where confID="+curconf+" and userID="+currev+" and roleName='reviewer')";
			String query = "select * from papers where pID in (select paperID from roles where confID="+curconf+" and roleName='author' and paperID not in (select paperID from roles where confID="+curconf+" and userID="+currev+" and roleName='reviewer'))";
			
			Statement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery(query);
			
			out.println("<form method=get action=../servlet/Assignpapers><select name=papertoadd>");
			while(rs.next())
			{
				String pid = rs.getString(1);
				String title= rs.getString(2);
				out.println("Title: "+title+"<br>");
				out.println("<option value="+pid+">"+title+"<br>");
			}
			out.println("</select><input type=submit name=add value=\"Add Papers\"></form>");
			}
			else
			{
				out.println("This reviewer has the maximum number of papers that he can be assigned");
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
