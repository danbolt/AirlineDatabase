import java.util.ArrayList;
import java.util.Iterator;

import javax.swing.*;

public class AirlineDB
{
	public static void main(String args[])
	{
                DatabaseAccess db = new DatabaseAccess("test", "test", "jdbc:mysql://localhost/airlineDB");
                TestGUI gui = new TestGUI(db);
                
                SwingUtilities.invokeLater(gui);

		if(db.openConnection())
		{
			//


		}
	}

} // class AirlineDB


