/* This is a test class for accessing the database. We could evolve it later into something
 * cooler and much nicer. Either that or start over.
 */
 
// create the test user with the following sql command:
//   CREATE USER 'test'@'localhost' IDENTIFIED BY 'test';
//   GRANT ALL PRIVILEGES ON airlineDB.* TO 'test'@'localhost';
 
// inital code based off of ideas found from:
// http://www.kitebird.com/articles/jdbc.html

import java.sql.*;
import java.util.ArrayList;

public class DatabaseAccess
{
	String username = "";
	String password = "";
	String dbURL = "";
	
	Connection connect = null;

	public DatabaseAccess(String newUsername, String newPassword, String url)
	{
		username = newUsername;
		password = newPassword;
		dbURL = url;
	} // DatabaseAccess()
	
	public boolean openConnection()
	{
		try
		{
			Class.forName ("com.mysql.jdbc.Driver").newInstance();
			connect = DriverManager.getConnection(dbURL, username, password);
			System.out.println("Connection to database successful!");
			return true;
		}
		catch (Exception e)
		{
			System.err.println("Cannot connect to the database");
			e.printStackTrace();
			return false;
		}
	} // bool openConnection();
	
	public void closeConnection()
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
	} //void closeConnection
	
	public void printAirlines()
	{
		if (connect != null)
		{
			try
			{
				String statementString = "SELECT * FROM airline";

				Statement testStatement = connect.createStatement();
				ResultSet rset = testStatement.executeQuery(statementString);
				
				while (rset.next())
				{
					String s = rset.getString("name");
					System.out.println(s);
				}

				testStatement.close();
			}
			catch (Exception e)
			{
				System.out.println("Error with creating a statement.");
				e.printStackTrace();
			}
		}
	} // void printAirlines
        
        public ArrayList<String[]> returnQuery()
	{
                ArrayList<String[]> output = new ArrayList<String[]>();
                String name;
                String website;
		if (connect != null)
		{
			try
			{
				String statementString = "SELECT * FROM airline";

				Statement testStatement = connect.createStatement();
				ResultSet rset = testStatement.executeQuery(statementString);
				while (rset.next())
				{
                                        String[] row = new String[2];
					name = rset.getString("name");
                                        website = rset.getString("website");
                                        row[0] = name;
                                        row[1] = website;
                                        output.add(row);
				}

				testStatement.close();
			}
			catch (Exception e)
			{
				System.out.println("Error with creating a statement.");
				e.printStackTrace();
			}
		}
               
                return output;
	} // void printAirlines
	
	public void addToDatabase(String table, String[] columns, String[] values)
	{
		if (connect != null)
		{
			try
			{
				String statementString = "INSERT INTO "+ table + " (";
                                for(int i=0;i<columns.length-1;i++){
                                    statementString = statementString + columns[i] + ",";
                                }
                                statementString = statementString + columns[columns.length-1] + ") values (";
                                
                                for(int i=0;i<values.length-1;i++){
                                    statementString = statementString + "\"" + values[i] + "\",";
                                }
                                statementString = statementString + "\"" + values[values.length-1] + "\")";
                                System.out.println(statementString);
				Statement insertStatement = connect.createStatement();
                                insertStatement.executeUpdate(statementString);
				

				insertStatement.close();
			}
			catch (Exception e)
			{
				System.out.println("Error with creating a statement.");
				e.printStackTrace();
			}
		}
	} // bool addAirline

} // class DatabaseAccess
