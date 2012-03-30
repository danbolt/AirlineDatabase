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
		AIRLINE, FLIGHT, INCOMING, OUTGOING, ARRIVALS, DEPARTURES, PASSENGERS, CLASS, LOCATION
	}

	public static DatabaseAccess database;
	
	TableState currentState = TableState.AIRLINE;

	JPanel rootPanel;
	JPanel fieldsPanel;

	JTable table;
	JScrollPane tableScrollPane;
	
	ArrayList<JFormattedTextField> textFieldList;

	public static boolean open = true;

	private void addEntry()
	{
		ArrayList<String> entryValues = new ArrayList<String>();

		for (int i = 0; i < textFieldList.size(); i++)
                {
			// if a field is empty, don't bother adding the content (for now)
			if (textFieldList.get(i).getText().length() < 1)
			{
				return;
			}

			entryValues.add(textFieldList.get(i).getText());
		}
		
		String[] entryValuesString = new String[entryValues.size()];
		entryValuesString = entryValues.toArray(entryValuesString);
		
		switch (currentState)
		{
			case AIRLINE:
			String[] cols = {"name", "website"};
			database.addToDatabase("airline", cols, entryValuesString);
			break;
			default:
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
			pan.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(), "test" + i));
			textFieldList.get(i).setText("");
			pan.add(textFieldList.get(i));
			fieldsPanel.add(pan);
		}
		
		fieldsPanel.getRootPane().revalidate();
	}
	
	private void changeState(TableState newState)
	{
		switch (newState)
		{
			case AIRLINE:
			textFieldList.clear();
			for (int i = 0; i < 2; i++)
			{
				JFormattedTextField fld = new JFormattedTextField();
				fld.setText("TEST" + i);
				fld.setColumns(10);
				textFieldList.add(fld);
			}
			break;
		}
		
		currentState = newState;
	}

	private void fillTables()
	{
		String titles[] = {"Airline", "Website"};
		String data[][] = new String[database.returnQuery().get(0).length][database.returnQuery().size()];
		data = database.returnQuery().toArray(data);

		DefaultTableModel tm = (DefaultTableModel)table.getModel();
		tm.setColumnCount(2);
		tm.setColumnIdentifiers(titles);
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
		
		textFieldList = new ArrayList<JFormattedTextField>();
		
		changeState(TableState.AIRLINE);

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
                
                reloadEntries();

		// arrange the components inside the window
		this.pack();
		//By default, the window is not visible. Make it visible.
		this.setVisible(true);
    }
}