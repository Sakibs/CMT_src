import java.io.*;
import java.text.*;
import java.util.*;
import java.sql.*;
import javax.servlet.*;
import javax.servlet.http.*;
import java.sql.Timestamp;
import java.util.Date;

public class User extends HttpServlet 
{
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
			Statement stmt4 = con.createStatement();
			Statement stmt5 = con.createStatement();
			//// /////////// ////////////////// ///////////////////////////            ////////////////////   ///////////////
			Statement stmt6 = con.createStatement();
			Statement stmt7 = con.createStatement();
			
			headerSetup(out, "User");
			
		    //try 
			//{
				ResultSet rsu = null;
				ResultSet rsc = null;
				ResultSet rsr = null;
				ResultSet rsp = null;
				ResultSet rss = null;
				ResultSet rsd = null;
				//// /////////// ////////////////// ///////////////////////////            ////////////////////   ///////////////
				ResultSet rspa = null;
				ResultSet rspr = null;
				//out.println("gonna find the id<br>");
				
                //con.setAutoCommit(false) ;
				//get user id
	            String findID = "select uID from users where email = '" +email+"'";
                rsu = stmt.executeQuery(findID);
				//out.println("executed query for the id<br>");
				int ID = 0;
				
				if(rsu.next())
					ID = rsu.getInt(1);
				
				//out.println("got id from result<br>");
				
				//get role
				String findRole = "select roleName from roles where userID="+ID;
				rsr = stmt2.executeQuery(findRole);
				String role = "";
			
				//out.println("ran findrole query<br>");
				while(rsr.next())
				{
					role = rsr.getString(1);
				}
				//out.println("got role from result<br>");
				int pidA, pid, pidR, cid;
                //con.commit();
				
				out.println("Welcome User<br>");
				//New paper submit
				out.println("<a href=\"../servlet/SubmitPaper?uid="+ID+"\">Submit New Paper Here!</a>");
				
				String findPapers = "select paperID, confID from roles where userID = "+ID+" and roleName='"+role+"'";
				//// /////////// ////////////////// ///////////////////////////            ////////////////////   ///////////////
				String findAuthored = "select paperID, confID from roles where userID = "+ID+" and roleName='author'";
				String findReviewed = "select paperID, confID from roles where userID = "+ID+" and roleName='reviewer'";
				String roleA = "author";
				String roleR = "reviewer";
				String roley = "";
				//out.println(findAuthored);
				//out.println("<br>"+findReviewed);
				
				rspa = stmt6.executeQuery(findAuthored);
				rspr = stmt7.executeQuery(findReviewed);
				
				// AUTHOR TALK
						out.println("<table border=\"1\"><tr><td><font size=\"4\">Papers Submitted</font></td>");
						out.println("<td>Status</td>");
						out.println("<td>View/Edit Paper</td>");
						out.println("<td>Conference</td></tr>");
					while(rspa.next())
					{
						//if no papers are writen by this, quit!
						if(rspa.wasNull())
						{
							out.println("No Papers Written!<br>");
							break;
						}
						
						//show paper ID
						pidA = rspa.getInt(1);
						
						String findStatusA = "";
						String findDateA = "";
						String statusA = "";
						String titleA = "";
						int confIDA = 0;
						
						//TimeStamp crap
						java.util.Date currentDateA = new java.util.Date();
						Timestamp currA = new Timestamp(currentDateA.getTime());
						Timestamp tempA;
					
						//get status
						findStatusA = "select p_stat, title from papers where pID="+pidA;
						rss = stmt4.executeQuery(findStatusA);
						while(rss.next())
						{
							statusA = rss.getString(1);
							titleA = rss.getString(2);
						}
						
						out.println("<tr><td>");
						out.println(pidA+". "+titleA+"</td>");

						//show status
						out.println("<td>");
						out.println(statusA+" </td>");
						
						//get conference ID
						confIDA = rspa.getInt(2);
						
						//get Submission end date
						findDateA = "select subEnd from conferences where cID = "+confIDA;
						rsd = stmt5.executeQuery(findDateA);
						
						/////////////////////////////////CHEAATT
						rsd.next();
						tempA = rsd.getTimestamp(1);
						
						//show edit/view buttons
						out.println("<td>");
						
						//out.println("userid: "+ID+" - - PaperID: "+pidA+" - - Role: "+roleA+"<br>");
						out.println("<form method=\"link\" action=\"../servlet/Viewpaper\"><fieldset><input type=\"hidden\" name=\"confid\" value="+confIDA+"><input type=\"hidden\" name=\"userid\" value="+ID+"><input type=\"hidden\" name=\"time\" value=\"0\"><input type=\"hidden\" name=\"role\" value="+roleA+"><input type=\"hidden\" name=\"paper\" value="+pidA+"><input type=\"submit\" border=0 value=\"View\" name=\"viewPaper\"></fieldset></form>");
	
							//if(statusA.equals("ir"))
							//							TESSTING 
							if(currA.before(tempA))
							{
								//out.println("userid: "+ID+" - - PaperID: "+pidA+" - - Role: "+roleA+"<br>");
								out.println("<form method=\"link\" action=\"../servlet/Viewpaper\"><fieldset><input type=\"hidden\" name=\"confid\" value="+confIDA+"><input type=\"hidden\" name=\"userid\" value="+ID+"><input type=\"hidden\" name=\"time\" value=\"1\"><input type=\"hidden\" name=\"role\" value="+roleA+"><input type=\"hidden\" name=\"paper\" value="+pidA+"><input type=\"submit\" border=0 value=\"Edit\" name=\"viewPaper\"></fieldset></form>");						
								out.println("</td>");
							}
							out.println("</td>");
							
						//show conference id
						out.println("<td>");
						out.println(confIDA+"</td></tr>");
						
					}
					out.println("</table><br>");
					
				// REVIEW TALK
						out.println("<table border=\"1\"><tr><td><font size=\"4\">Papers to Review</font></td>");
						out.println("<td>Status</td>");
						out.println("<td>Submit/Change Rating</td>");
						out.println("<td>Conference</td></tr>");
					while(rspr.next())
					{
						//if no papers to review by this, quit!
						if(rspr.wasNull())
						{
							out.println("No Papers to Review!<br>");
							break;
						}
						

						//show paper ID
						pidR = rspr.getInt(1);
						
						String findStatusR = "";
						String findDateR = "";
						String statusR = "";
						String titleR = "";
						int confIDR = 0;
						
						//TimeStamp crap
						java.util.Date currentDateR = new java.util.Date();
						Timestamp currR = new Timestamp(currentDateR.getTime());
						Timestamp tempR;
					
						//get status
						findStatusR = "select p_stat, title from papers where pID="+pidR;
						rss = stmt4.executeQuery(findStatusR);
						while(rss.next())
						{
							statusR = rss.getString(1);
							titleR = rss.getString(2);
						}
						
						out.println("<tr><td>");
						out.println(pidR+". "+titleR+"</td>");

						//show status
						out.println("<td>");
						out.println(statusR+" </td>");
						
						//get conference ID
						confIDR = rspr.getInt(2);
						
						//get Submission end date
						findDateR = "select subEnd from conferences where cID = "+confIDR;
						rsd = stmt5.executeQuery(findDateR);
						
						/////////////////////////////////CHEAATT
						rsd.next();
						tempR = rsd.getTimestamp(1);
						
						//show edit/view buttons
						out.println("<td>");
						
						//out.println("userid: "+ID+" - - PaperID: "+pidR+" - - Role: "+roleR+"<br>");
						out.println("<form method=\"link\" action=\"../servlet/Viewpaper\"><fieldset><input type=\"hidden\" name=\"confid\" value="+confIDR+"><input type=\"hidden\" name=\"userid\" value="+ID+"><input type=\"hidden\" name=\"time\" value=\"0\"><input type=\"hidden\" name=\"role\" value="+roleR+"><input type=\"hidden\" name=\"paper\" value="+pidR+"><input type=\"submit\" border=0 value=\"View Paper\" name=\"viewPaper\"></fieldset></form>");
								
							//if(statusR.equals("ir"))
							//							TESSTING 
							if(currR.before(tempR))
							{
								//out.println("userid: "+ID+" - - PaperID: "+pidR+" - - Role: "+roleR+"<br>");
								out.println("<form method=\"link\" action=\"../servlet/Viewpaper\"><fieldset><input type=\"hidden\" name=\"confid\" value="+confIDR+"><input type=\"hidden\" name=\"userid\" value="+ID+"><input type=\"hidden\" name=\"time\" value=\"1\"><input type=\"hidden\" name=\"role\" value="+roleR+"><input type=\"hidden\" name=\"paper\" value="+pidR+"><input type=\"submit\" border=0 value=\"Edit\" name=\"editRating\"></fieldset></form>");						
								//out.println("</td>");
							}
						out.println("</td>");
							
						//show conference id
						out.println("<td>");
						out.println(confIDR+"</td></tr>");
						
					}
						out.println("</table>");
			//Back button
			out.println("<FORM><INPUT TYPE=\"button\" VALUE=\"Back\" onClick=\"history.go(-2);return true;\"></FORM>");
				
			/*
				rsp = stmt3.executeQuery(findPapers);
				out.println("ran query to find papers<br>");

				String paperType = "";
				
				//author view
				if(role.equals("author"))
					paperType = "Papers Submitted";
				else
					paperType = "Papers for Review";
				
					out.println("<table border=\"1\"><tr><td><font size=\"4\">"+paperType+"</font></td>");
					out.println("<td>Status</td>");
					out.println("<td>Edit/View Paper</td>");
					out.println("<td>Conference</td></tr>");

					String findStatus = "";
					String findDate = "";
					String status = "";
					String title = "";
					int confID = 0;
					
					//TimeStamp crap
					java.util.Date currentDate = new java.util.Date();
					Timestamp curr = new Timestamp(currentDate.getTime());
					Timestamp temp;
					
					while(rsp.next())
					{
						//show paper ID
						pid = rsp.getInt(1);
						
						//get status
						findStatus = "select p_stat, title from papers where pID="+pid;
						rss = stmt4.executeQuery(findStatus);
						while(rss.next())
						{
							status = rss.getString(1);
							title = rss.getString(2);
						}
						
						out.println("<tr><td>");
						out.println(pid+". "+title+"</td>");

						//show status
						out.println("<td>");
						out.println(status+" </td>");
						
						//get conference ID
						confID = rsp.getInt(2);
						
						//get Submission end date
						findDate = "select subEnd from conferences where cID = "+confID;
						rsd = stmt5.executeQuery(findDate);
						
						/////////////////////////////////CHEAATT
						rsd.next();
						temp = rsd.getTimestamp(1);
						
						//show edit/view buttons
						out.println("<td>");
						if(role.equals("author"))
						{
							if(status.equals("ir"))
							//							TESSTING 
							//if(curr.before(temp))
							{
								out.println("userid: "+ID+" - - PaperID: "+pid+" - - Role: "+role+"<br>");
								out.println("<form method=\"link\" action=\"../servlet/Viewpaper\"><fieldset><input type=\"hidden\" name=\"userid\" value="+ID+"><input type=\"hidden\" name=\"time\" value=\"1\"><input type=\"hidden\" name=\"role\" value="+role+"><input type=\"hidden\" name=\"paper\" value="+pid+"><input type=\"submit\" border=0 value=\"View/Edit\" name=\"viewPaper\"></fieldset></form>");						
								out.println("</td>");
							}
							else
							{
								out.println("userid: "+ID+" - - PaperID: "+pid+" - - Role: "+role+"<br>");
								out.println("<form method=\"link\" action=\"../servlet/Viewpaper\"><fieldset><input type=\"hidden\" name=\"userid\" value="+ID+"><input type=\"hidden\" name=\"time\" value=\"0\"><input type=\"hidden\" name=\"role\" value="+role+"><input type=\"hidden\" name=\"paper\" value="+pid+"><input type=\"submit\" border=0 value=\"View\" name=\"viewPaper\"></fieldset></form>");
								out.println("</td>");
							}
						}
						else
						{
							if(status.equals("ir"))
							//							TESSTING 
							//if(curr.before(temp))
							{
								out.println("userid: "+ID+" - - PaperID: "+pid+" - - Role: "+role+"<br>");
								out.println("<form method=\"link\" action=\"../servlet/Viewpaper\"><fieldset><input type=\"hidden\" name=\"userid\" value="+ID+"><input type=\"hidden\" name=\"time\" value=\"1\"><input type=\"hidden\" name=\"role\" value="+role+"><input type=\"hidden\" name=\"paper\" value="+pid+"><input type=\"submit\" border=0 value=\"View/Edit\" name=\"viewPaper\"></fieldset></form>");						
								out.println("</td>");
							}
							else
							{
								out.println("userid: "+ID+" - - PaperID: "+pid+" - - Role: "+role+"<br>");
								out.println("<form method=\"link\" action=\"../servlet/Viewpaper\"><fieldset><input type=\"hidden\" name=\"userid\" value="+ID+"><input type=\"hidden\" name=\"time\" value=\"0\"><input type=\"hidden\" name=\"role\" value="+role+"><input type=\"hidden\" name=\"paper\" value="+pid+"><input type=\"submit\" border=0 value=\"View/Edit\" name=\"viewPaper\"></fieldset></form>");						
								out.println("</td>");
							}
						}
						
						//show conference id
						out.println("<td>");
						out.println(confID+"</td></tr></table>");
						
					}
*/
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
	}
}