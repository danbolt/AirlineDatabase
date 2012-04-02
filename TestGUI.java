import java.util.ArrayList;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;

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
	private enum TableState
	{
		AIRLINE, FLIGHT, INCOMING, OUTGOING, ARRIVALS, DEPARTURES, PASSENGERS, CLASS, LOCATION, PLANEMODEL
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
			System.out.println("Mouse Event");
			
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
				break;
				case 2:
				if (currentState == TableState.FLIGHT)
				{
					pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "Plane Model"));
				}
				break;
				case 3:
				break;
				case 4:
				break;
			}
			//((JFormattedTextField)textFieldList.get(i)).setText("");
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
			case FLIGHT:

			if (database.returnQuery("location").size() > 0)
			{
				//get dropbox of Locations
	                        data = new String[database.returnQuery("location").get(0).length][database.returnQuery("location").size()];
				data = database.returnQuery("location").toArray(data);
				ArrayList<KeyNamePair> locationList = new ArrayList<KeyNamePair>();
				for (int i = 0; i < data.length; i++)
				{
					locationList.add(new KeyNamePair(Integer.parseInt(data[i][0]), data[i][1]));
				}
				locationArray = new KeyNamePair[locationList.size()];
				locationArray = locationList.toArray(locationArray);
			}
			
			if (database.returnQuery("location").size() > 0)
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
				data = new String[database.returnQuery("airline").get(0).length][database.returnQuery("airline").size()];
				data = database.returnQuery("airline").toArray(data);
			}
			break;
			case LOCATION:
			if (database.returnQuery("location").size() > 0)
			{
	                        data = new String[database.returnQuery("location").get(0).length][database.returnQuery("location").size()];
				data = database.returnQuery("location").toArray(data);
			}
			break;
			case FLIGHT:
			if (database.returnQuery("flight").size() > 0)
			{
				data = new String[database.returnQuery("flight").get(0).length][database.returnQuery("flight").size()];
				data = database.returnQuery("flight_str").toArray(data);
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
			String flightTitles[] = {"Unique ID", "Location From", "Location To", "Plane Model"};
			tm.setColumnIdentifiers(flightTitles);
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
		
		if ("add".equals(e.getActionCommand()))
		{
			addEntry();
		}
	}
	
	@Override
	public void run()
	{
		rootPanel.setPreferredSize(new Dimension(500,500));

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
                
                deletePopup = new JPopupMenu();
                JMenuItem menuItem = new JMenuItem("Delete Row");
                menuItem.addActionListener(this);
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