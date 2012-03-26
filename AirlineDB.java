

public class AirlineDB
{
	public AirlineDB()
	{
		DatabaseAccess db = new DatabaseAccess("test", "test", "jdbc:mysql://localhost/airlineDB");
		if(db.openConnection())
		{
			db.printAirlines();
			db.closeConnection();
		}

	} // AirlineDB()

	public static void main(String args[])
	{
		new AirlineDB();
	}

} // class AirlineDB


