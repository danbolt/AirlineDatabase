import java.util.ArrayList;
import java.util.Iterator;

public class AirlineDB
{
	public static void main(String args[])
	{
                DatabaseAccess db = new DatabaseAccess("test", "test", "jdbc:mysql://localhost/airlineDB");
		if(db.openConnection())
		{
                        ArrayList<String[]> something = new ArrayList<String[]>();
                        String[] line;
			//db.printAirlines();
                        //String columns[] = {"name","website"};
                        //String values[] = {"bob","www.bob.com"};
                        //db.addToDatabase("airline",columns,values);
                        
                        something = db.returnQuery();
                        for(int i=0;i<something.size();i++) {
                            line = something.get(i);
                            for(int j = 0; j < line.length;j++){
                                System.out.print(line[j] + " ");
                            }
                            System.out.println(); 
                        }
			db.closeConnection();
		}
	}

} // class AirlineDB


