import java.io.*;
import java.text.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Timestamp;
import java.util.Date;

public class ChangesRating extends HttpServlet {
	private String url = "jdbc:mysql://localhost/cs143s36";
	private String userName = "cs143s36";
	private String password = "sssmsql143";

	private void headerSetup(PrintWriter out, String title) 
	{
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

	private void footerSetup(PrintWriter out) 
	{
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
		try
		{
			Connection con = null;
			con = DriverManager.getConnection(url, userName, password);

			// initiate the session and set up variables needed for signin
			HttpSession session = request.getSession(false);
			
			//Get email and name from session
			String email = session.getAttribute("email").toString();
			String name = session.getAttribute("firstn").toString();
			
			// Execute a SQL statement
			Statement stmt = con.createStatement();
			//Statement stmt2 = con.createStatement();
			//Statement stmt3 = con.createStatement();
			
			headerSetup(out, "Rating Update");
			
			//get query info
			String p = request.getParameter("paperid");
			int pID = Integer.parseInt(p); //paper ID looking at
			String r = request.getParameter("role");
			String u = request.getParameter("userid"); 
			int userID = Integer.parseInt(u);
			String rr = request.getParameter("rate");
			int rating = Integer.parseInt(rr);
			String comm = request.getParameter("comment");
			//int userID = Integer.parseInt(u); //user ID
			
		    //try 
			//{
				ResultSet rsi = null;
				//ResultSet rsr = null;

                con.setAutoCommit(false);
				//get paper details
	            String change = "update roles set rating="+rating+", comments='"+comm+"' where userID="+userID+" and paperID="+pID+" and roleName='"+r+"'";

                stmt.executeUpdate(change);
				out.println("pID: "+pID+"\nrole: "+r+"\nuseriD: "+userID+"\n");
				out.println("Rating UPDATED!!!");
                con.commit();
				
			//Back button
			out.println("<FORM><INPUT TYPE=\"button\" VALUE=\"Back\" onClick=\"history.go(-2);return true;\"></FORM>");
				
				//con.setAutoCommit(false);
			out.println("</body>");
			out.println("</html>");

			stmt.close();
			//stmt2.close();
			con.close();
		}
		catch (SQLException ex) 
		{
			out.println("SQLException caught<br>");
			out.println("---<br>");
			while (ex != null)
			{
				out.println("Message   : " + ex.getMessage() + "<br>");
				out.println("SQLState  : " + ex.getSQLState() + "<br>");
				out.println("ErrorCode : " + ex.getErrorCode() + "<br>");
				out.println("---<br>");
				ex = ex.getNextException();
			}
		}
		/*}
		catch (SQLException ex) 
		{
			out.println("SQLException caught<br>");
			out.println("---<br>");
			while (ex != null) 
			{
				out.println("Message   : " + ex.getMessage() + "<br>");
				out.println("SQLState  : " + ex.getSQLState() + "<br>");
				out.println("ErrorCode : " + ex.getErrorCode() + "<br>");
				out.println("---<br>");
				ex = ex.getNextException();
			}
		}*/
	}
}