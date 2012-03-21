/* This is a test class for accessing the database. We could evolve it later into something
 * cooler and much nicer. Either that or start over.
 */
 
// create the test user with the following sql command:
//   CREATE USER 'test'@'localhost' IDENTIFIED BY 'test';
//   GRANT ALL PRIVILEGES ON airlineDB.* TO 'test'@'localhost';
 
// inital code based off of ideas found from:
// http://www.kitebird.com/articles/jdbc.html
 
import java.sql.*;

public class DatabaseAccess
{
	public DatabaseAccess(String username, String password)
	{
		Connection connect = null;
		
		try
		{
			String url = "jdbc:mysql://localhost/airlineDB";
			Class.forName ("com.mysql.jdbc.Driver").newInstance();
			connect = DriverManager.getConnection(url, username, password);
			System.out.println("Connection to database successful!");
		}
		catch (Exception e)
		{
			System.err.println("Cannot connect to the database");
			e.printStackTrace();
		}
		finally
		{
			if (connect != null)
			{
				try
				{
					connect.close();
					System.out.println("Database connection closed");
				}
				catch (Exception e)
				{
					// whatever
				}
			}
		}
	} // DatabaseAccess()

} // class DatabaseAccess
