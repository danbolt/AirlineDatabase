import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import javax.swing.text.NumberFormatter;

class WindowEventHandler extends WindowAdapter
{
	public void windowClosing(WindowEvent evt)
	{
		TestGUI.database.closeConnection();
		System.exit(0);
	}
}

public class TestGUI extends JFrame implements Runnable, ActionListener
{
	// enum that sets what table is currently being shown
	// camelCode states
	private enum TableState
	{
		AIRLINE, FLIGHT, INCOMING, OUTGOING, ARRIVALS, DEPARTURES, PASSENGERS, CLASS, LOCATION, PLANEMODEL, FLIESON, OPERATESFLIGHTS, printAirlineFlights, printFlightsToFrom, printFlightsTime, printArrivalPassengers, printBaggage
	}
	
	// this class is used for binding ID's with string values
	private class KeyNamePair
	{
		public KeyNamePair(int newKey, String newName)
		{
			key = newKey;
			name = newName;
		}

		public int key = 1;
		public String name = "";
		
		public String toString()
		{
			return name;
		}
	}
	
	private class PopupListener extends MouseAdapter
	{
		public void mousePressed (MouseEvent e)
		{
			maybeShowPopup(e);
		}
		
		public void mouseReleased (MouseEvent e)
		{
			maybeShowPopup(e);
		}

		private void maybeShowPopup (MouseEvent e)
		{
			if (e.isPopupTrigger())
			{
				deletePopup.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}

	public static DatabaseAccess database;

	TableState currentState = TableState.AIRLINE;

	JPanel rootPanel;
	JPanel fieldsPanel;

	JTable table;
	JScrollPane tableScrollPane;
	JPopupMenu deletePopup;
	
	JFormattedTextField airlineNameField;
	
	JComboBox<String> stateSelector;
	String possibleStates[] = {"Airline", "Flight", "Incoming", "Outgoing", "Arrivals", "Departures", "Passengers", "Class", "Location", "Plane Model", "Flies On", "Operates Flights"};

	String arrivalStatuses[] = {"Arrived", "Delayed", "Cancelled", "On Time"};
	String departureStatuses[] = {"Departed", "Delayed", "Cancelled"};
	String classStatuses[] ={"FIRST CLASS", "ECONOMY", "HANDICAPPED"};

	ArrayList<JComponent> textFieldList;

	public static boolean open = true;

	private void addEntry()
	{
		ArrayList<String> entryValues = new ArrayList<String>();

		for (int i = 0; i < textFieldList.size(); i++)
                {
			if (textFieldList.get(i).getName().equals("field"))
			{
				// if a field is empty, don't bother adding the content (for now)
				if (((JFormattedTextField)textFieldList.get(i)).getText().length() < 1)
				{
					return;
				}
	
				entryValues.add(((JFormattedTextField)textFieldList.get(i)).getText());
			}
			else if (textFieldList.get(i).getName().equals("combo"))
			{
				entryValues.add( String.valueOf(((KeyNamePair)(((JComboBox)textFieldList.get(i)).getSelectedItem())).key) );
			}
			else if (textFieldList.get(i).getName().equals("statusCombo"))
			{
				entryValues.add( (String)((JComboBox)textFieldList.get(i)).getSelectedItem() );
			}
			else if (textFieldList.get(i).getName().equals("date"))
			{
				Date eventDate = (Date)((JSpinner)textFieldList.get(i)).getValue();
				
				SimpleDateFormat dbDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

				entryValues.add(dbDateFormat.format(eventDate));
			}
		}
		
		String[] entryValuesString = new String[entryValues.size()];
		entryValuesString = entryValues.toArray(entryValuesString);

		switch (currentState)
		{
			case AIRLINE:
			String[] airlineCols = {"name", "website"};
			database.addToDatabase("airline", airlineCols, entryValuesString);
			break;
			case LOCATION:
                        String[] locationCols = {"name"};
                        database.addToDatabase("location", locationCols, entryValuesString);
			default:
			break;
			case FLIGHT:
                        String[] flightCols = {"locationFrom", "locationTo", "planeModel"};
                        database.addToDatabase("flight", flightCols, entryValuesString);
			break;
			case PLANEMODEL:
                        String[] modelCols = {"model", "capacity"};
                        database.addToDatabase("planeModel", modelCols, entryValuesString);
			break;
			case INCOMING:
                        String[] incCols = {"flightNo", "plannedArrivalTime"};
                        database.addToDatabase("incoming", incCols, entryValuesString);
			break;
			case OUTGOING:
                        String[] outCols = {"flightNo", "plannedDepartureTime"};
                        database.addToDatabase("outgoing", outCols, entryValuesString);
			break;
			case ARRIVALS:
                        String[] arrivCols = {"incomingPlane", "gate", "arrivalDate", "arrivalStatus"};
                        database.addToDatabase("arrivals", arrivCols, entryValuesString);
			break;
			case DEPARTURES:
                        String[] departCols = {"outgoingPlane", "gate", "departureDate", "departureStatus"};
                        database.addToDatabase("departures", departCols, entryValuesString);
			break;
			case PASSENGERS:
                        String[] passengerCols = {"passportNumber","name", "dateOfBirth", "placeOfBirth", "citizenship"};
                        database.addToDatabase("passengers", passengerCols, entryValuesString);
			break;
			case CLASS:
                        String[] classCols = {"classType"};
                        database.addToDatabase("class", classCols, entryValuesString);
			break;
			case FLIESON:
                        String[] fliesOnCols = {"passengerPassport", "baggage", "class_ID", "flight_ID"};
                        database.addToDatabase("FliesOn", fliesOnCols, entryValuesString);
			break;
			case OPERATESFLIGHTS:
                        String[] operatesCols = {"airline_ID", "flightNo"};
                        database.addToDatabase("OperatesFlights", operatesCols, entryValuesString);
			break;
		}

		reloadEntries();
		fillTables();
	}

	private void removeEntry()
	{
		DefaultTableModel tModel = (DefaultTableModel)table.getModel();
		
		for (int i : table.getSelectedRows())
		{
			switch (currentState)
			{
				case LOCATION:
				database.removeFromDatabase("location", "location_ID", (String)tModel.getValueAt(i, 0));
				break;
				case AIRLINE:
				database.removeFromDatabase("airline", "airline_ID", (String)tModel.getValueAt(i, 0));
				break;
				case PLANEMODEL:
				database.removeFromDatabase("planeModel", "plane_ID", (String)tModel.getValueAt(i, 0));
				break;
				case FLIGHT:
				database.removeFromDatabase("flight", "flightNo", (String)tModel.getValueAt(i, 0));
				break;
				case INCOMING:
				database.removeFromDatabase("incoming", "flightNo", (String)tModel.getValueAt(i, 0));
				break;
				case OUTGOING:
				database.removeFromDatabase("outgoing", "flightNo", (String)tModel.getValueAt(i, 0));
				break;
				case ARRIVALS:
				database.removeFromDatabase("arrivals", "arrival_ID", (String)tModel.getValueAt(i, 0));
				break;
				case DEPARTURES:
				database.removeFromDatabase("departures", "departure_ID", (String)tModel.getValueAt(i, 0));
				break;
				case PASSENGERS:
				database.removeFromDatabase("passengers", "passportNumber", (String)tModel.getValueAt(i, 0));
				break;
    				case CLASS:
				database.removeFromDatabase("class", "class_ID", (String)tModel.getValueAt(i, 0));
				break;
    				case FLIESON:
				database.removeFromDatabase("FliesOn", "flight_ID", (String)tModel.getValueAt(i, 0));
				break;
    				case OPERATESFLIGHTS:
				database.removeFromDatabase("OperatesFlights", "airline_ID", (String)tModel.getValueAt(i, 0));
				break;
			}
		}

		reloadEntries();
		fillTables();
	}

	private void reloadEntries()
	{
		fieldsPanel.removeAll();

		for (int i = 0; i < textFieldList.size(); i++)
                {
			JPanel pan = new JPanel();

			// determine the labels first by placement, then by state
			switch (i)
			{
				case 0:
				if (currentState == TableState.AIRLINE)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Airline"));
					((JFormattedTextField)textFieldList.get(i)).setText("");
				}
				if (currentState == TableState.LOCATION)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Location Name"));
				}
				if (currentState == TableState.FLIGHT)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Location From"));
				}
				if (currentState == TableState.PLANEMODEL)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Model Name"));
				}
				if (currentState == TableState.INCOMING)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Flight"));
				}
				if (currentState == TableState.OUTGOING)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Flight"));
				}
				if (currentState == TableState.ARRIVALS)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Flight Number"));
				}
				if (currentState == TableState.DEPARTURES)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Flight Number"));
				}
				if (currentState == TableState.PASSENGERS)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Passport Number"));
				}
				if (currentState == TableState.CLASS)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Class Type"));
				}
				if (currentState == TableState.FLIESON)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Passenger"));
				}
				if (currentState == TableState.OPERATESFLIGHTS)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Airline"));
				}
				break;
				case 1:
				if (currentState == TableState.AIRLINE)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Website"));
					((JFormattedTextField)textFieldList.get(i)).setText("");
				}
				if (currentState == TableState.FLIGHT)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Location To"));
				}
				if (currentState == TableState.PLANEMODEL)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Capacity"));
				}
				if (currentState == TableState.INCOMING)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Time"));
				}
				if (currentState == TableState.OUTGOING)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Time"));
				}
				if (currentState == TableState.ARRIVALS)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Gate"));
				}
				if (currentState == TableState.DEPARTURES)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Gate"));
				}
				if (currentState == TableState.PASSENGERS)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Name"));
				}
				if (currentState == TableState.FLIESON)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Baggage"));
				}
				if (currentState == TableState.OPERATESFLIGHTS)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Flight Number"));
				}
				break;
				case 2:
				if (currentState == TableState.FLIGHT)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Plane Model"));
				}
				if (currentState == TableState.ARRIVALS)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Arrival Date"));
				}
				if (currentState == TableState.DEPARTURES)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Departure Date"));
				}
				if (currentState == TableState.PASSENGERS)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Date of Birth"));
				}
				if (currentState == TableState.FLIESON)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Class"));
				}
				break;
				case 3:
				if (currentState == TableState.ARRIVALS)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Status"));
				}
				if (currentState == TableState.DEPARTURES)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Status"));
				}
				if (currentState == TableState.PASSENGERS)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Place of Birth"));
				}
				if (currentState == TableState.FLIESON)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Flight"));
				}
				break;
				case 4:
				if (currentState == TableState.PASSENGERS)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Citizenship"));
				}
				break;
			}
			pan.add(textFieldList.get(i));
			fieldsPanel.add(pan);
		}

		fieldsPanel.getRootPane().revalidate();
	}
	
	private void changeState(TableState newState)
	{
		String data[][];
		KeyNamePair modelArray[] = new KeyNamePair[0];
		KeyNamePair locationArray[] = new KeyNamePair[0];
		KeyNamePair flightArray[] = new KeyNamePair[0];

		switch (newState)
		{
			case AIRLINE:
			textFieldList.clear();
			for (int i = 0; i < 2; i++)
			{
				JFormattedTextField fld = new JFormattedTextField();
				fld.setName("field");
				fld.setText("TEST" + i);
				fld.setColumns(10);
				textFieldList.add(fld);
			}
			break;
			case LOCATION:
			textFieldList.clear();
			for (int i = 0; i < 1; i++)
			{
				JFormattedTextField fld = new JFormattedTextField();
				fld.setName("field");
				fld.setText("TEST" + i);
				fld.setColumns(10);
				textFieldList.add(fld);
			}
			break;
			case PLANEMODEL:
			textFieldList.clear();
			for (int i = 0; i < 2; i++)
			{
				JFormattedTextField fld = new JFormattedTextField();
				fld.setName("field");
				fld.setText("TEST" + i);
				fld.setColumns(10);
				textFieldList.add(fld);
			}
			break;
			case INCOMING:
			textFieldList.clear();
			if (database.returnQuery("flight").size() > 0)
			{
				//get dropbox of Locations
	                        data = new String[database.returnQuery("flight_str").size()][database.returnQuery("flight_str").get(0).length];
				data = database.returnQuery("flight_str").toArray(data);
				ArrayList<KeyNamePair> flightList = new ArrayList<KeyNamePair>();
				for (int i = 0; i < data.length; i++)
				{
					flightList.add(new KeyNamePair(Integer.parseInt(data[i][0]), data[i][1] + "-" + data[i][2]));
				}
				flightArray = new KeyNamePair[flightList.size()];
				flightArray = flightList.toArray(flightArray);
			}
			textFieldList.clear();
			for (int i = 0; i < 2; i++)
			{
				if (i == 0)
				{
					JComboBox<KeyNamePair> combo = new JComboBox<KeyNamePair>(flightArray);
					combo.setName("combo");
					textFieldList.add(combo);
				}
				else
				{
					SpinnerDateModel dateModel = new SpinnerDateModel();
					JSpinner dateSpin = new JSpinner(dateModel);
					dateSpin.setName("date");
					textFieldList.add(dateSpin);
				}
			}
			break;
			case OUTGOING:
			textFieldList.clear();
			if (database.returnQuery("flight").size() > 0)
			{
				//get dropbox of Locations
	                        data = new String[database.returnQuery("flight_str").size()][database.returnQuery("flight_str").get(0).length];
				data = database.returnQuery("flight_str").toArray(data);
				ArrayList<KeyNamePair> flightList = new ArrayList<KeyNamePair>();
				for (int i = 0; i < data.length; i++)
				{
					flightList.add(new KeyNamePair(Integer.parseInt(data[i][0]), data[i][1] + "-" + data[i][2]));
				}
				flightArray = new KeyNamePair[flightList.size()];
				flightArray = flightList.toArray(flightArray);
			}
			textFieldList.clear();
			for (int i = 0; i < 2; i++)
			{
				if (i == 0)
				{
					JComboBox<KeyNamePair> combo = new JComboBox<KeyNamePair>(flightArray);
					combo.setName("combo");
					textFieldList.add(combo);
				}
				else
				{
					SpinnerDateModel dateModel = new SpinnerDateModel();
					JSpinner dateSpin = new JSpinner(dateModel);
					dateSpin.setName("date");
					textFieldList.add(dateSpin);
				}
			}
			break;
			case FLIGHT:
			if (database.returnQuery("location").size() > 0)
			{
				//get dropbox of Locations
	                        data = new String[database.returnQuery("location").size()][database.returnQuery("location").get(0).length];
				data = database.returnQuery("location").toArray(data);
				ArrayList<KeyNamePair> locationList = new ArrayList<KeyNamePair>();
				for (int i = 0; i < data.length; i++)
				{
					locationList.add(new KeyNamePair(Integer.parseInt(data[i][0]), data[i][1]));
				}
				locationArray = new KeyNamePair[locationList.size()];
				locationArray = locationList.toArray(locationArray);
			}
			
			if (database.returnQuery("planeModel").size() > 0)
			{
				//get dropbox of flights
	                        data = new String[database.returnQuery("planeModel").size()][database.returnQuery("planeModel").get(0).length];
				data = database.returnQuery("planeModel").toArray(data);
				ArrayList<KeyNamePair> modelList = new ArrayList<KeyNamePair>();
				for (int i = 0; i < data.length; i++)
				{
					modelList.add(new KeyNamePair(Integer.parseInt(data[i][0]), data[i][1]));
				}
				modelArray = new KeyNamePair[modelList.size()];
				modelArray = modelList.toArray(modelArray);
			}
			
			textFieldList.clear();
			for (int i = 0; i < 3; i++)
			{
				if (i == 0 || i == 1)
				{
					JComboBox<KeyNamePair> combo = new JComboBox<KeyNamePair>(locationArray);
					combo.setName("combo");
					textFieldList.add(combo);
				}
				else
				{
					JComboBox<KeyNamePair> combo = new JComboBox<KeyNamePair>(modelArray);
					combo.setName("combo");
					textFieldList.add(combo);
				}
			}
			break;
			case ARRIVALS:
			textFieldList.clear();
			if (database.returnQuery("incoming").size() > 0)
			{
				//get dropbox of Locations
	                        data = new String[database.returnQuery("incoming").size()][database.returnQuery("incoming").get(0).length];
				data = database.returnQuery("incoming").toArray(data);
				ArrayList<KeyNamePair> flightList = new ArrayList<KeyNamePair>();
				for (int i = 0; i < data.length; i++)
				{
					flightList.add(new KeyNamePair(Integer.parseInt(data[i][0]), data[i][0]));
				}
				flightArray = new KeyNamePair[flightList.size()];
				flightArray = flightList.toArray(flightArray);
			}
			{
				JComboBox<KeyNamePair> combo = new JComboBox<KeyNamePair>(flightArray);
				combo.setName("combo");
				textFieldList.add(combo);
	
	                        JFormattedTextField fld = new JFormattedTextField();
				fld.setName("field");
				fld.setText("");
				fld.setColumns(10);
				textFieldList.add(fld);
				
	                        SpinnerDateModel dateModel = new SpinnerDateModel();
				JSpinner dateSpin = new JSpinner(dateModel);
				dateSpin.setName("date");
				textFieldList.add(dateSpin);
				
				JComboBox<String> combo2 = new JComboBox<String>(arrivalStatuses);
				combo2.setName("statusCombo");
				textFieldList.add(combo2);
			}
			break;

			case DEPARTURES:
			textFieldList.clear();
			if (database.returnQuery("outgoing").size() > 0)
			{
				//get dropbox of Locations
	                        data = new String[database.returnQuery("outgoing").size()][database.returnQuery("outgoing").get(0).length];
				data = database.returnQuery("outgoing").toArray(data);
				ArrayList<KeyNamePair> flightList = new ArrayList<KeyNamePair>();
				for (int i = 0; i < data.length; i++)
				{
					flightList.add(new KeyNamePair(Integer.parseInt(data[i][0]), data[i][0]));
				}
				flightArray = new KeyNamePair[flightList.size()];
				flightArray = flightList.toArray(flightArray);
			}
			{
				JComboBox<KeyNamePair> combo = new JComboBox<KeyNamePair>(flightArray);
				combo.setName("combo");
				textFieldList.add(combo);
	
	                        JFormattedTextField fld = new JFormattedTextField();
				fld.setName("field");
				fld.setText("");
				fld.setColumns(10);
				textFieldList.add(fld);
				
	                        SpinnerDateModel dateModel = new SpinnerDateModel();
				JSpinner dateSpin = new JSpinner(dateModel);
				dateSpin.setName("date");
				textFieldList.add(dateSpin);
				
				JComboBox<String> combo2 = new JComboBox<String>(departureStatuses);
				combo2.setName("statusCombo");
				textFieldList.add(combo2);
			}
			break;
			case PASSENGERS:
			textFieldList.clear();
			for (int i = 0; i < 5; i++)
			{
				if (i == 0)
				{
					JFormattedTextField fld = new JFormattedTextField();
					fld.setName("field");
					fld.setText("");
					fld.setColumns(10);
					textFieldList.add(fld);
				}
				else if (i == 2)
				{
		                        SpinnerDateModel dateModel = new SpinnerDateModel();
					JSpinner dateSpin = new JSpinner(dateModel);
					dateSpin.setName("date");
					textFieldList.add(dateSpin);
				}
				else
				{
					JFormattedTextField fld = new JFormattedTextField();
					fld.setName("field");
					fld.setText("");
					fld.setColumns(10);
					textFieldList.add(fld);
				}
			}
			break;
			
			case CLASS:
                        textFieldList.clear();
                        {
				JComboBox<String> combo2 = new JComboBox<String>(classStatuses);
				combo2.setName("statusCombo");
				textFieldList.add(combo2);
			}
			break;
			case FLIESON:
			textFieldList.clear();
			if (database.returnQuery("passengers").size() > 0)
			{
				//get dropbox of Locations
	                        data = new String[database.returnQuery("passengers").size()][database.returnQuery("passengers").get(0).length];
				data = database.returnQuery("passengers").toArray(data);
				ArrayList<KeyNamePair> flightList = new ArrayList<KeyNamePair>();
				for (int i = 0; i < data.length; i++)
				{
					flightList.add(new KeyNamePair(Integer.parseInt(data[i][0]), data[i][1]));
				}
				flightArray = new KeyNamePair[flightList.size()];
				flightArray = flightList.toArray(flightArray);
			}
			{
				JComboBox<KeyNamePair> combo = new JComboBox<KeyNamePair>(flightArray);
				combo.setName("combo");
				textFieldList.add(combo);
			}
			{
				JFormattedTextField fld = new JFormattedTextField();
				fld.setName("field");
				fld.setText("");
				fld.setColumns(10);
				textFieldList.add(fld);
			}
			if (database.returnQuery("class").size() > 0)
			{
				//get dropbox of Locations
	                        data = new String[database.returnQuery("class").size()][database.returnQuery("class").get(0).length];
				data = database.returnQuery("class").toArray(data);
				ArrayList<KeyNamePair> flightList = new ArrayList<KeyNamePair>();
				for (int i = 0; i < data.length; i++)
				{
					flightList.add(new KeyNamePair(Integer.parseInt(data[i][0]), data[i][1]));
				}
				flightArray = new KeyNamePair[flightList.size()];
				flightArray = flightList.toArray(flightArray);
			}
			{
				JComboBox<KeyNamePair> combo = new JComboBox<KeyNamePair>(flightArray);
				combo.setName("combo");
				textFieldList.add(combo);
			}
			if (database.returnQuery("flight").size() > 0)
			{
				//get dropbox of Locations
	                        data = new String[database.returnQuery("flight_str").size()][database.returnQuery("flight_str").get(0).length];
				data = database.returnQuery("flight_str").toArray(data);
				ArrayList<KeyNamePair> flightList = new ArrayList<KeyNamePair>();
				for (int i = 0; i < data.length; i++)
				{
					flightList.add(new KeyNamePair(Integer.parseInt(data[i][0]), data[i][0]));
				}
				flightArray = new KeyNamePair[flightList.size()];
				flightArray = flightList.toArray(flightArray);
			}
			{
				JComboBox<KeyNamePair> combo = new JComboBox<KeyNamePair>(flightArray);
				combo.setName("combo");
				textFieldList.add(combo);
			}
			break;
			case OPERATESFLIGHTS:
			textFieldList.clear();
			if (database.returnQuery("airline").size() > 0)
			{
				//get dropbox of Locations
	                        data = new String[database.returnQuery("airline").size()][database.returnQuery("airline").get(0).length];
				data = database.returnQuery("airline").toArray(data);
				ArrayList<KeyNamePair> flightList = new ArrayList<KeyNamePair>();
				for (int i = 0; i < data.length; i++)
				{
					flightList.add(new KeyNamePair(Integer.parseInt(data[i][0]), data[i][1]));
				}
				flightArray = new KeyNamePair[flightList.size()];
				flightArray = flightList.toArray(flightArray);
			}
			{
				JComboBox<KeyNamePair> combo = new JComboBox<KeyNamePair>(flightArray);
				combo.setName("combo");
				textFieldList.add(combo);
			}
			if (database.returnQuery("flight").size() > 0)
			{
				//get dropbox of Locations
	                        data = new String[database.returnQuery("flight").size()][database.returnQuery("flight").get(0).length];
				data = database.returnQuery("flight").toArray(data);
				ArrayList<KeyNamePair> flightList = new ArrayList<KeyNamePair>();
				for (int i = 0; i < data.length; i++)
				{
					flightList.add(new KeyNamePair(Integer.parseInt(data[i][0]), data[i][0]));
				}
				flightArray = new KeyNamePair[flightList.size()];
				flightArray = flightList.toArray(flightArray);
			}
			{
				JComboBox<KeyNamePair> combo = new JComboBox<KeyNamePair>(flightArray);
				combo.setName("combo");
				textFieldList.add(combo);
			}
			break;
		}
		
		currentState = newState;
	}

	private void fillTables()
	{
		String data[][] = new String[0][3];

		switch (currentState)
		{
			default:
			case AIRLINE:
			if (database.returnQuery("airline").size() > 0)
			{
				data = new String[database.returnQuery("airline").size()][database.returnQuery("airline").get(0).length];
				data = database.returnQuery("airline").toArray(data);
			}
			break;
			case LOCATION:
			if (database.returnQuery("location").size() > 0)
			{
	                        data = new String[database.returnQuery("location").size()][database.returnQuery("location").get(0).length];
				data = database.returnQuery("location").toArray(data);
			}
			break;
			case FLIGHT:
			if (database.returnQuery("flight").size() > 0)
			{
				data = new String[database.returnQuery("flight").size()][database.returnQuery("flight").get(0).length];
				data = database.returnQuery("flight_str").toArray(data);
			}
			break;
			case PLANEMODEL:
			if (database.returnQuery("planeModel").size() > 0)
			{
				data = new String[database.returnQuery("planeModel").size()][database.returnQuery("planeModel").get(0).length];
				data = database.returnQuery("planeModel").toArray(data);
			}
			break;
			case INCOMING:
			if (database.returnQuery("incoming_str").size() > 0)
			{
				data = new String[database.returnQuery("incoming_str").size()][database.returnQuery("incoming_str").get(0).length];
				data = database.returnQuery("incoming_str").toArray(data);
			}
			break;
			case OUTGOING:
			if (database.returnQuery("outgoing_str").size() > 0)
			{
				data = new String[database.returnQuery("outgoing_str").size()][database.returnQuery("outgoing_str").get(0).length];
				data = database.returnQuery("outgoing_str").toArray(data);
			}
			break;
			case ARRIVALS:
			if (database.returnQuery("arrivals").size() > 0)
			{
				data = new String[database.returnQuery("arrivals").size()][database.returnQuery("arrivals").get(0).length];
				data = database.returnQuery("arrivals").toArray(data);
			}
			break;
			case DEPARTURES:
			if (database.returnQuery("departures").size() > 0)
			{
				data = new String[database.returnQuery("departures").size()][database.returnQuery("departures").get(0).length];
				data = database.returnQuery("departures").toArray(data);
			}
			break;
			case PASSENGERS:
			if (database.returnQuery("passengers").size() > 0)
			{
				data = new String[database.returnQuery("passengers").size()][database.returnQuery("passengers").get(0).length];
				data = database.returnQuery("passengers").toArray(data);
			}
			break;
			case CLASS:
			if (database.returnQuery("class").size() > 0)
			{
				data = new String[database.returnQuery("class").size()][database.returnQuery("class").get(0).length];
				data = database.returnQuery("class").toArray(data);
			}
			break;
			case FLIESON:
			if (database.returnQuery("FliesOn").size() > 0)
			{
				data = new String[database.returnQuery("FliesOn").size()][database.returnQuery("FliesOn").get(0).length];
				data = database.returnQuery("FliesOn").toArray(data);
			}
			break;
			case OPERATESFLIGHTS:
			if (database.returnQuery("OperatesFlights").size() > 0)
			{
				data = new String[database.returnQuery("OperatesFlights").size()][database.returnQuery("OperatesFlights").get(0).length];
				data = database.returnQuery("OperatesFlights").toArray(data);
			}
			break;
		}

		DefaultTableModel tm = (DefaultTableModel)table.getModel();

		switch (currentState)
		{
			default:
			case AIRLINE:
			tm.setColumnCount(3);
			String airlineTitles[] = {"Unique ID", "Airline", "Website"};
			tm.setColumnIdentifiers(airlineTitles);
			break;
			case LOCATION:
			tm.setColumnCount(2);
			String locationTitles[] = {"Unique ID", "Location Name"};
			tm.setColumnIdentifiers(locationTitles);
			break;
			case FLIGHT:
			tm.setColumnCount(4);
			String flightTitles[] = {"Flight Number", "Location From", "Location To", "Plane Model"};
			tm.setColumnIdentifiers(flightTitles);
			break;
			case PLANEMODEL:
			tm.setColumnCount(3);
			String modelTitles[] = {"Unique ID", "Model Name", "Capacity"};
			tm.setColumnIdentifiers(modelTitles);
			break;
			case INCOMING:
			tm.setColumnCount(4);
			String incomingTitles[] = {"Flight Number", "Date/Time", "From", "To"};
			tm.setColumnIdentifiers(incomingTitles);
			break;
			case OUTGOING:
			tm.setColumnCount(4);
			String outgoingTitles[] = {"Flight Number", "Date/Time", "From", "To"};
			tm.setColumnIdentifiers(outgoingTitles);
			break;
			case ARRIVALS:
			tm.setColumnCount(4);
			String arrivalTitles[] = {"Arrival Number", "Flight Number", "Gate", "Arrival Date"};
			tm.setColumnIdentifiers(arrivalTitles);
			break;
			case DEPARTURES:
			tm.setColumnCount(4);
			String departureTitles[] = {"Departure Number", "Flight Number", "Gate", "Departure Date"};
			tm.setColumnIdentifiers(departureTitles);
			break;
			case PASSENGERS:
			tm.setColumnCount(5);
			String passengerTitles[] = {"Passport Number", "Name", "Date of Birth", "Place of Birth", "Citizenship"};
			tm.setColumnIdentifiers(passengerTitles);
			break;
			case CLASS:
			tm.setColumnCount(2);
			String classTitles[] = {"Class ID", "Type"};
			tm.setColumnIdentifiers(classTitles);
			break;
			case FLIESON:
			tm.setColumnCount(4);
			String fliesOnTitles[] = {"Flight ID", "Passenger Passport", "Baggage", "Class"};
			tm.setColumnIdentifiers(fliesOnTitles);
			break;
			case OPERATESFLIGHTS:
			tm.setColumnCount(2);
			String operatesTitles[] = {"Airline", "Flight Number"};
			tm.setColumnIdentifiers(operatesTitles);
			break;
		}

		//empty the table's rows and refill them with the pulled DB data
		while (tm.getRowCount() > 0)
		{
			tm.removeRow(0);
		}
		for (int i = 0; i < data.length; i++)
		{
			tm.addRow(data[i]);
		}

		tm.fireTableDataChanged();
	}

	public TestGUI (DatabaseAccess newDB)
	{
		super("Airline Database");

		addWindowListener(new WindowEventHandler());
		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);   // Sets the behaviour for when the window is closed
		
		database = newDB;
		
		textFieldList = new ArrayList<JComponent>();
		
		changeState(TableState.LOCATION);

		rootPanel = new JPanel();

		table = new JTable(30, 5);
		table.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);

		tableScrollPane = new JScrollPane(table);
		tableScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		tableScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
	}
	
	@Override
	public void actionPerformed(ActionEvent e)
	{
		if ("test".equals(e.getActionCommand()))
		{
			fillTables();
		}
		else if ("add".equals(e.getActionCommand()))
		{
			addEntry();
		}
		else if ("flightsByLine".equals(e.getActionCommand()))
		{
			String airline = airlineNameField.getText();
			
			currentState = TableState.printAirlineFlights;

			textFieldList.clear();
			fieldsPanel.removeAll();
			fieldsPanel.repaint();
			
			String data[][] = new String[0][3];

			if (database.returnQuery("airline").size() > 0)
			{
				data = new String[database.returnQuery("airline").size()][database.returnQuery("airline").get(0).length];
				data = database.returnQuery("airline").toArray(data);
			}
			
			// FINITO
		}
		else if ("delete".equals(e.getActionCommand()))
		{
			removeEntry();
		}
		else
		{
			JComboBox cb = (JComboBox)e.getSource();
			String comboSelect = (String)cb.getSelectedItem();
			
			switch (comboSelect)
			{
				case "Airline":
				changeState(TableState.AIRLINE);
				break;
				case "Flight":
				changeState(TableState.FLIGHT);
				break;
				case "Incoming":
				changeState(TableState.INCOMING);
				break;
				case "Outgoing":
				changeState(TableState.OUTGOING);
				break;
				case "Arrivals":
				changeState(TableState.ARRIVALS);
				break;
				case "Departures":
				changeState(TableState.DEPARTURES);
				break;
				case "Passengers":
				changeState(TableState.PASSENGERS);
				break;
				case "Class":
				changeState(TableState.CLASS);
				break;
				case "Location":
				changeState(TableState.LOCATION);
				break;
				case "Plane Model":
				changeState(TableState.PLANEMODEL);
				break;
				case "Flies On":
				changeState(TableState.FLIESON);
				break;
				case "Operates Flights":
				changeState(TableState.OPERATESFLIGHTS);
				break;
				default:
				break;
			}

			reloadEntries();
			fieldsPanel.repaint();
			fillTables();
		}
	}
	
	@Override
	public void run()
	{
		rootPanel.setPreferredSize(new Dimension(640,640));

		add(rootPanel);
		this.setResizable(false);
		
		rootPanel.setLayout(new GridLayout(0, 2, 20, 20));
		rootPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		// add a label and a button
		rootPanel.add(tableScrollPane);

                JPanel addPanel = new JPanel();
                addPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
                rootPanel.add(addPanel);

		JButton jb = new JButton("Fill Tables");
		jb.setActionCommand("test");
		jb.addActionListener(this);
		addPanel.add(jb);

		jb = new JButton("Add A Row");
		jb.setActionCommand("add");
		jb.addActionListener(this);
                addPanel.add(jb);

                fieldsPanel = new JPanel();
                fieldsPanel.setPreferredSize(new Dimension(400,200));
                fieldsPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
                rootPanel.add(fieldsPanel);

                stateSelector = new JComboBox<String>(possibleStates);
                stateSelector.addActionListener(this);
                addPanel.add(stateSelector);
                
                JPanel airFlightsPanel = new JPanel();
                airFlightsPanel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));
                jb = new JButton("Display Flights by Airline");
                jb.setActionCommand("flightsByLine");
                jb.addActionListener(this);
                airlineNameField = new JFormattedTextField();
		airlineNameField.setText("");
		airlineNameField.setColumns(10);
                airFlightsPanel.add(airlineNameField);
                airFlightsPanel.add(jb);
                addPanel.add(airFlightsPanel);

                deletePopup = new JPopupMenu();
                JMenuItem menuItem = new JMenuItem("Delete Selected Rows");
                menuItem.addActionListener(this);
                menuItem.setActionCommand("delete");
                deletePopup.add(menuItem);

                MouseListener popupListener = new PopupListener();
                table.addMouseListener(popupListener);

                reloadEntries();

		// arrange the components inside the window
		this.pack();
		//By default, the window is not visible. Make it visible.
		this.setVisible(true);
    }
}