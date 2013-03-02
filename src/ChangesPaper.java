import java.io.*;
import java.text.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Timestamp;
import java.util.Date;

public class ChangesPaper extends HttpServlet {
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
			
			headerSetup(out, "Paper Update");
			
			//get query info
			String p = request.getParameter("paperid");
			int pID = Integer.parseInt(p); //paper ID looking at
			String t = request.getParameter("title");
			//int time = Integer.parseInt(t); //before submission or after? before = 1, after = 0
			String c = request.getParameter("content"); //role of user
			String a = request.getParameter("abstract");
			String coAuthor = request.getParameter("coA");
			String conn = request.getParameter("confid");
			int confID = Integer.parseInt(conn); 
			//int userID = Integer.parseInt(u); //user ID
			
		    //try 
			//{
				ResultSet rsi = null;
				ResultSet rsc = null;

                con.setAutoCommit(false);
				//get paper details
	            String change = "update papers set title='"+t+"', abstract='"+a+"', content='"+c+"' where pID="+p;
				String findCo = "select uID from users where email='"+coAuthor+"'";
				
				//look up coauthor email
				rsc = stmt2.executeQuery(findCo);
				int coAID = 0;
				
				while(rsc.next())
				{
					//not found?
					if(rsc.wasNull())
					{
						out.println("Email in-valid!<br>");
						out.println("<form name=\"input\" action=\"../servlet/ChangesPaper\" method=\"get\">");
						out.println("Not found! Please re-enter email: <input type=\"hidden\" name = \"paperid\" value="+pID+"><input type=\"hidden\" name = \"title\" value="+t+"><input type=\"hidden\" name = \"abstract\" value="+a+"><input type=\"hidden\" name = \"content\" value="+c+"><input type=\"text\" name=\"coA\"></form>");
						break;
					}
					//coauthor exists!
					else
					{
						coAID = rsc.getInt(1);
						try
						{
							con.setAutoCommit(false);
							String intoRole = "insert into roles (confID, userID, paperID, roleName) values ("+confID+", "+coAID+", "+pID+", 'author')";
							stmt3.executeUpdate(intoRole);
							con.commit();
							out.println("NEW ROLE ADDED!! ");
							con.setAutoCommit(true);
						}
						catch(SQLException ex)
						{
							out.println("There was an error entering the coAuthor...it already exists!<br>");
							System.err.println("SQLException: " + ex.getMessage());
							con.rollback();
							con.setAutoCommit(true);
						}
					}
				}
                stmt.executeUpdate(change);
				//out.println("title: "+t+"\nabstract: "+a+"\ncontent: "+c+"\n");
				out.println("PAPED UPDATED !!!<br>");
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