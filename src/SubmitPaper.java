import java.io.*;
import java.text.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Timestamp;
import java.util.Date;

public class SubmitPaper extends HttpServlet {
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
			headerSetup(out, "Create Paper");
			
			
			
			//Retrieve user id
			String iden = request.getParameter("uid");
			int ID = Integer.parseInt(iden);
			
				//TimeStamp crap
				java.util.Date currentDateA = new java.util.Date();
				Timestamp currA = new Timestamp(currentDateA.getTime());
				String date = currA.toString();
				
				String findConferences = "select cID, cName from conferences where subEnd > '"+date+"'";
				String confName = "";
				
			ResultSet rsc = null;
			
				rsc = stmt2.executeQuery(findConferences);
				
				int cid = 0;
				
		    //try 
			//{
               //con.setAutoCommit(false);
				//get paper details
	        
						out.println("<table border=\"1\"><tr>");
						out.println("<form name=\"input\" action=\"../servlet/SendsPaper\" method=\"get\">");
						out.println("Title: <input type=\"text\" name=\"title\"></tr><br>");
						//<input type=\"submit\" value=\"Submit"></form>
						
						//<textarea rows="4" cols="50"> dasds </textarea>
						out.println("<tr>");
						out.println("Abstract: <br><textarea name=\"abstract\" rows=\"4\" cols=\"50\"></textarea><br></tr>");
						
						out.println("<tr>");
						out.println("Content: <br><textarea name=\"content\" rows=\"4\" cols=\"50\"></textarea><br></tr>");
	
						//pick a valid conference
							out.println("<tr>Select Conference: <select name=\"conf\">");
							while(rsc.next())
							{
								cid = rsc.getInt(1);
								confName = rsc.getString(2);
								out.println("<option value="+cid+">"+confName+"</option>");
							}

						out.println("</select><input type=\"hidden\" name = \"uid\" value="+ID+"><input type=\"submit\" value=\"Submit Paper\"></form>");		
					
            //con.commit();
				
			//Back button
			out.println("<FORM><INPUT TYPE=\"button\" VALUE=\"Back\" onClick=\"history.go(-2);return true;\"></FORM>");
				
			//con.setAutoCommit(false);
			out.println("</body>");
			out.println("</html>");

			//stmt.close();
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