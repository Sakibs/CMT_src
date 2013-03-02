import java.io.*;
import java.text.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Timestamp;
import java.util.Date;

public class SendsPaper extends HttpServlet {
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
			Statement stmt2 = con.createStatement();
			//Statement stmt3 = con.createStatement();
			
			headerSetup(out, "Paper Send");
			
			String t = request.getParameter("title");
			//int time = Integer.parseInt(t); //before submission or after? before = 1, after = 0
			String c = request.getParameter("content"); //role of user
			String a = request.getParameter("abstract");
			String conf = request.getParameter("conf");
			int confid = Integer.parseInt(conf);
			
			//Retrieve user id
			String iden = request.getParameter("uid");
			int ID = Integer.parseInt(iden);
			int pID = 0;
			
			
			//int userID = Integer.parseInt(u); //user ID
			
		    //try 
			//{
				ResultSet rsc = null;
				ResultSet rsp = null;
				//ResultSet rsr = null;

                con.setAutoCommit(false);
				//get paper details
				
	            String addPaper = "insert into papers (title, abstract, content, p_stat) values ('"+t+"', '"+a+"', '"+c+"', 'In Review')";
				

                stmt.executeUpdate(addPaper);
/*CREATE TABLE papers
(
pID int(8) AUTO_INCREMENT not null,
title varchar(255) not null,
abstract varchar(255),
content text,
numReviewed int(8) default 0, 
p_stat varchar(20),
primary key (pID)
) ENGINE=INNODB;*/
				String getPaperID = "select max(pID) from papers";
				rsp = stmt2.executeQuery(getPaperID);
				
				while(rsp.next())
				{	
					pID = rsp.getInt(1);
				}
				
				String addRole = "insert into roles (confID, userID, paperID, roleName) values ("+confid+", "+ID+", "+pID+", "+"'author')";
				out.println("title: "+t+"\nabstract: "+a+"\ncontent: "+c+"\npaperID: :"+pID);
				stmt.executeUpdate(addRole);
				
				out.println("PAPER Added!!!<br>");
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