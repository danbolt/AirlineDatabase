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
        
        /*
         * Given an airline name, prints a list of flight numbers that airline operates
         */
        public ArrayList<Integer> printAirlineFlights(String airlineName)
	{
            
                int airlineID;
                ArrayList<Integer> flightNo = new ArrayList<Integer>();
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
					if(s.equalsIgnoreCase(airlineName)) {
                                            airlineID = rset.getInt("airline_ID");
                                            String queryString = "SELECT * FROM OperatesFlights WHERE airline_ID = " + airlineID;
                                            Statement queryStatement = connect.createStatement();
                                            ResultSet results = queryStatement.executeQuery(queryString);
                                            while(results.next()){
                                                flightNo.add(results.getInt("flightNo"));
                                            }
                                            break;
                                        }
				}

				testStatement.close();
			}
			catch (Exception e)
			{
				System.out.println("Error with creating a statement.");
				e.printStackTrace();
			}
		}
                return flightNo;
	} 

        /*
         * Given a location, print all flight numbers to or from that location
         */
        public ArrayList<Integer> printFlightsToFrom(String locationName)
	{
            
                int locationID;
                ArrayList<Integer> flightNo = new ArrayList<Integer>();
		if (connect != null)
		{
			try
			{
				String statementString = "SELECT * FROM location";

				Statement testStatement = connect.createStatement();
				ResultSet rset = testStatement.executeQuery(statementString);
				
				while (rset.next())
				{
					String s = rset.getString("name");
					if(s.equalsIgnoreCase(locationName)) {
                                            locationID = rset.getInt("location_ID");
                                            String queryString = "SELECT * FROM flight WHERE locationTo = " + locationID + " OR locationFrom = "+ locationID;
                                            Statement queryStatement = connect.createStatement();
                                            ResultSet results = queryStatement.executeQuery(queryString);
                                            while(results.next()){
                                                flightNo.add(results.getInt("flightNo"));
                                            }
                                            queryStatement.close();
                                            break;
                                        }
				}

				testStatement.close();
			}
			catch (Exception e)
			{
				System.out.println("Error with creating a statement.");
				e.printStackTrace();
			}
		}
                return flightNo;
	}
        
        /*
         * Given a string of time (HH:MM:SS) find flights around that time
         */
        public ArrayList<String[]> printFlightsTime(String aroundTime)
	{
                
                java.sql.Time sqlTime = java.sql.Time.valueOf(aroundTime);
                java.sql.Time retrievedTime;
                String flight,status;
                ArrayList<String[]> output = new ArrayList<String[]>();
		if (connect != null)
		{
			try
			{
				String statementString = "SELECT * FROM arrivals";

				Statement testStatement = connect.createStatement();
				ResultSet rset = testStatement.executeQuery(statementString);
				
				while (rset.next())
				{
                                        retrievedTime = rset.getTime("arrivalDate");
                                        if((retrievedTime.getTime() > sqlTime.getTime() &&
                                                retrievedTime.getTime() < sqlTime.getTime() + 1800000) || 
                                                retrievedTime.getTime() < sqlTime.getTime() &&
                                                retrievedTime.getTime() > sqlTime.getTime() - 1800000){
                                            
                                            String[] row = new String[2];
                                            flight = rset.getString("incomingPlane");
                                            status = rset.getString("arrivalStatus");
                                            row[0] = flight;
                                            row[1] = status;
                                            output.add(row);
                                        }
                                                
				}

				testStatement.close();
                                String secondString = "SELECT * FROM departures";

				Statement secondStatement = connect.createStatement();
				ResultSet results = secondStatement.executeQuery(secondString);
				
				while (results.next())
				{
                                        retrievedTime = results.getTime("arrivalDate");
                                        if((retrievedTime.getTime() > sqlTime.getTime() &&
                                                retrievedTime.getTime() < sqlTime.getTime() + 1800000) || 
                                                retrievedTime.getTime() < sqlTime.getTime() &&
                                                retrievedTime.getTime() > sqlTime.getTime() - 1800000){
                                            
                                            String[] row = new String[2];
                                            flight = results.getString("incomingPlane");
                                            status = results.getString("arrivalStatus");
                                            row[0] = flight;
                                            row[1] = status;
                                            output.add(row);
                                        }
                                                
				}
                                secondStatement.close();
			}
			catch (Exception e)
			{
				System.out.println("Error with creating a statement.");
				e.printStackTrace();
			}
		}
                return output;
	}
        
        /*
         * Given a flight number, prints information about all passengers on it.
         */
        public ArrayList<String[]> printArrivalPassengers(int flightNo)
	{
                ArrayList<String[]> output = new ArrayList<String[]>();
                
		if (connect != null)
		{
			try
			{
				String statementString = "SELECT * FROM passengers WHERE passportNumber IN"
                                        + "(SELECT passengerPassport FROM FliesOn WHERE Flight_ID = "
                                        + flightNo + ")";

				Statement testStatement = connect.createStatement();
				ResultSet rset = testStatement.executeQuery(statementString);
				
				while (rset.next())
				{
                                        String row[] = new String[5];
                                        row[0] = rset.getString("passportNumber");
                                        row[1] = rset.getString("name");
                                        row[2] = rset.getString("dateOfBirth");
                                        row[3] = rset.getString("placeOfBirth");
                                        row[4] = rset.getString("citizenship");
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
	}
        
        public int printBaggage(int passenger)
	{               
                int baggage = -1;
		if (connect != null)
		{
			try
			{
				String statementString = "SELECT baggage FROM FliesOn WHERE passengerPassport = "
                                        + passenger;

				Statement testStatement = connect.createStatement();
				ResultSet rset = testStatement.executeQuery(statementString);
				
				while (rset.next())
				{
                                    baggage = rset.getInt("baggage");
				}

				testStatement.close();                            
			}
			catch (Exception e)
			{
				System.out.println("Error with creating a statement.");
				e.printStackTrace();
			}
		}
                return baggage;
	}
        
        public ArrayList<String[]> returnQuery(String tableName)
	{
                ArrayList<String[]> output = new ArrayList<String[]>();
                int colCount;
		if (connect != null)
		{
			try
			{
				// proper flight SQL query
				// % SELECT a.flightNo, b.loc, c.loc, d.pname FROM flight a, (SELECT name as loc, location_ID as id from location) b, (SELECT name as loc, location_ID as id from location) c, (SELECT model as pname, plane_ID as id from planeModel) d WHERE a.locationFrom = b.id AND a.locationTo = c.id AND a.planeModel = d.id;
				if (tableName.equals("flight"))
				{
					String statementString = "SELECT a.flightNo, b.loc, c.loc, d.pname FROM flight a, (SELECT name as loc, location_ID as id from location) b, (SELECT name as loc, location_ID as id from location) c, (SELECT model as pname, plane_ID as id from planeModel) d WHERE a.locationFrom = b.id AND a.locationTo = c.id AND a.planeModel = d.id;";

					Statement testStatement = connect.createStatement();
					ResultSet rset = testStatement.executeQuery(statementString);
	                                
	                                ResultSetMetaData rsetMetaData = rset.getMetaData();
	                                colCount = rsetMetaData.getColumnCount();
					while (rset.next())
					{
	                                        String[] row = new String[colCount];
	                                        for(int i=1;i<=colCount;i++){
	                                            row[i-1] = rset.getString(i);
	                                        }
	                                        output.add(row);
					}
	
					testStatement.close();
					
					return output;
				}


				String statementString = "SELECT * FROM " + tableName;

				Statement testStatement = connect.createStatement();
				ResultSet rset = testStatement.executeQuery(statementString);
                                
                                ResultSetMetaData rsetMetaData = rset.getMetaData();
                                colCount = rsetMetaData.getColumnCount();
				while (rset.next())
				{
                                        String[] row = new String[colCount];
                                        for(int i=1;i<=colCount;i++){
                                            row[i-1] = rset.getString(i);
                                        }
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
                
	}

} // class DatabaseAccess
