import java.io.*;
import java.text.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Timestamp;
import java.util.Date;

public class Viewpaper extends HttpServlet {
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
			Statement stmt3 = con.createStatement();
			
			headerSetup(out, "Paper View");
			
			//get query info
			String p = request.getParameter("paper");
			int pID = Integer.parseInt(p); //paper ID looking at
			String t = request.getParameter("time");
			int time = Integer.parseInt(t); //before submission or after? before = 1, after = 0
			String role = request.getParameter("role"); //role of user
			String u = request.getParameter("userid");
			int userID = Integer.parseInt(u); //user ID
			String conn = request.getParameter("confid");
			int confID = Integer.parseInt(conn);
			
		    //try 
			//{
				ResultSet rsi = null;
				ResultSet rsr = null;
				ResultSet rsco = null;
				
                //con.setAutoCommit(false);
				//get paper details
	            String info = "select * from papers where pID = "+pID;
				String getRating = "select rating, comments from roles where userID="+userID+" and paperID = "+pID+" and roleName = '"+role+"'";
				String getCos = "select userID from roles where paperID = "+pID+" and roleName = 'author' and userID <> "+userID;
				/*select roles.userID, users.last_n, from roles INNER JOIN users ON Persons.P_Id=Orders.P_Id
				*/
                rsi = stmt.executeQuery(info);
				rsr = stmt2.executeQuery(getRating);
				rsco = stmt3.executeQuery(getCos);
				
				String title = "";
				String abs = "";
				String content = "";
				String status = "";
				String comm = "";
				int rev = 0; //if 0, submit review. 1, change review.
				
				while(rsi.next())
				{
					title = rsi.getString(2);
					abs = rsi.getString(3);
					content = rsi.getString(4);
					status = rsi.getString(6);
				}
                //con.commit();
				int coID = 0;
				
				if(role.equals("author"))
				{
					/*//get potential coauthors
					while(rsco.next())
					{
						coID = rsr.getInt(2);
					}*/
					
					//After submission time
					if(time==0)
					{
						out.println("<table border=\"1\"><tr>Title: ");
						out.println(title+"</tr><br>");

						out.println("<tr>Status: "+status+"</tr><br>");
						
						out.println("<tr>");
						out.println("<div style=\"overflow: auto; width:500px; height:150px;\">Abstract: <br>"+abs+"</div><br></tr>");
							
						out.println("<tr>");
						out.println("<div style=\"overflow: auto; width:500px; height:150px;\">"+content+"</div><br></tr>");
					}
					else
					{
						out.println("<table border=\"1\"><tr>");
						out.println("<form name=\"input\" action=\"../servlet/ChangesPaper\" method=\"get\">");
						out.println("Title: <input type=\"textarea\" name=\"title\" value = "+title+"></tr><br>");
						//<input type=\"submit\" value=\"Submit"></form>

						out.println("<tr>Status: "+status+"</tr><br>");
						
						//<textarea rows="4" cols="50"> dasds </textarea>
						out.println("<tr>");
						out.println("Abstract: <br><textarea name=\"abstract\" rows=\"4\" cols=\"50\">"+abs+"</textarea><br></tr>");
						
						out.println("<tr>");
						out.println("Content: <br><textarea name=\"content\" rows=\"4\" cols=\"50\">"+content+"</textarea><br></tr>");

						//Add co-authors
						out.println("<tr>");
						out.println("Enter Co-Author email: <input type=\"text\" name=\"coA\"><br></tr>");
						
						out.println("<input type=\"submit\" value=\"Submit\"><input type=\"hidden\" name = \"confid\" value="+confID+"><input type=\"hidden\" name = \"paperid\" value="+pID+"></form>");		
					}
						//Back button
						//out.println("<FORM><INPUT TYPE=\"button\" VALUE=\"Back\" onClick=\"history.go(-1);return true;\"></FORM>");
				}
				//Reviewer View of Paper
				else
				{
					//rating variable
					int r = 0;
					//get potential rating by reviewer
					while(rsr.next())
					{
						comm = rsr.getString(2);
						r = rsr.getInt(1);
					}
					
					/////////////////////////////////CANT CHANGE NO MORE!
					if(time == 0)
					{
						out.println("<table border=\"1\"><tr>Title: ");
						out.println(title+"</tr><br>");

						out.println("<tr>Status: "+status+"</tr><br>");
						
						if(rsr.wasNull())
						{
							out.println("<tr>Your Rating: Not submitted</tr>");
						}
						else
						{
							out.println("<tr>Your Rating: "+r);
							out.println("Comment: "+comm+"</tr>");
						}
						out.println("<tr>");
						out.println("<div style=\"overflow: auto; width:500px; height:150px;\">Abstract: <br>"+abs+"</div><br></tr>");
							
						out.println("<tr>");
						out.println("<div style=\"overflow: auto; width:500px; height:150px;\">"+content+"</div><br></tr>");				
						
						//Back button
						//out.println("<FORM><INPUT TYPE=\"button\" VALUE=\"Back\" onClick=\"history.go(-1);return true;\"></FORM>");
					}
					//If revierwe has time to edit stuff
					/////////////////////////////////CAN STILL CHANGE Rating!
					else
					{
						out.println("<table border=\"1\"><tr>Title: ");
						out.println(title+"</tr><br>");

						out.println("<tr>Status: "+status+"</tr><br>");
						
						//if null, u can rate it
						if(rsr.wasNull())
						{
							out.println("<form name=\"input\" action=\"../servlet/ChangesRating\" method=\"get\">");
							out.println("<tr>Your Rating: <select name=\"rate\">"+
							"<option value=\"1\">Strong Reject</option>"+
							"<option value=\"2\">Reject</option>"+
							"<option value=\"3\">Weak Reject</option>"+
							"<option value=\"4\">Neutral</option>"+
							"<option value=\"5\">Weak Accept</option>"+
							"<option value=\"6\">Accept</option>"+
							"<option value=\"7\">Strong Accept</option></select>");
							out.println("Comment: <input type=\"textarea\" name=\"comment\">");
							out.println("</tr>");
						}
						//you can change ur rating
						else
						{
							out.println("<form name=\"input\" action=\"../servlet/ChangesRating\" method=\"post\">");
							out.println("<tr>Your Rating: <select name=\"rate\">"+
							"selected value ="+r+
							"<option value=\"1\">Strong Reject</option>"+
							"<option value=\"2\">Reject</option>"+
							"<option value=\"3\">Weak Reject</option>"+
							"<option value=\"4\">Neutral</option>"+
							"<option value=\"5\">Weak Accept</option>"+
							"<option value=\"6\">Accept</option>"+
							"<option value=\"7\">Strong Accept</option></select>");
							out.println("Comment: <input type=\"textarea\" name=\"comment\"><value = '"+comm+"'");
							out.println("</tr>");
						}
						out.println("<tr>");
						out.println("<div style=\"overflow: auto; width:500px; height:150px;\">Abstract: <br>"+abs+"</div><br></tr>");
							
						out.println("<tr>");
						out.println("<div style=\"overflow: auto; width:500px; height:150px;\">"+content+"</div><br></tr>");				
						
						out.println("<input type=\"submit\" value=\"Submit Score\"><input type=\"hidden\" name = \"paperid\" value="+pID+"><input type=\"hidden\" name = \"role\" value="+role+"><input type=\"hidden\" name = \"userid\" value="+userID+"></form>");	
					}
				}
				//Back button
				out.println("<FORM><INPUT TYPE=\"button\" VALUE=\"Back\" onClick=\"history.go(-1);return true;\"></FORM>");
				
				out.println("</table>");

				//con.setAutoCommit(false);
			out.println("</body>");
			out.println("</html>");

			stmt.close();
			stmt2.close();
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