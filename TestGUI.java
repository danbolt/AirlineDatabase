import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class TestGUI extends JFrame implements Runnable, ActionListener
{
	JPanel rootPanel;

	JTable table;
	JScrollPane tableScrollPane;

	private void fillTables()
	{
		System.out.println("Here!");
		
		String titles[] = {"Airline", "Website"};
		String data[][] = {{"Spike", "www.spike.com"},
  		       		   {"Jet", "www.jet.com"},
  		       		   {"Faye", "www.faye.com"},
  		       		   {"Ed", "www.ed.com"},
  		       		   {"Ein", "www.ein.com"},
  		       		   {"Julia", "www.julia.com"},
   		       		   {"Vicious", "www.vicious.com"}};

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

	public TestGUI ()
	{
		super("Airline Database");
		
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
	}
	
	@Override
	public void run()
	{
		// Sets the behaviour for when the window is closed
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		rootPanel.setPreferredSize(new Dimension(500,500));

		add(rootPanel);
		this.setResizable(false);
		
		rootPanel.setLayout(new GridLayout(0, 2, 20, 20));
		rootPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

		// add a label and a button
		rootPanel.add(tableScrollPane);
		JButton jb = new JButton("Fill Tables");
		jb.setActionCommand("test");
		jb.addActionListener(this);
		rootPanel.add(jb);
		rootPanel.add(new JButton("hello"));

		// arrange the components inside the window
		this.pack();
		//By default, the window is not visible. Make it visible.
		this.setVisible(true);
    }

    public static void main(String[] args)
    {
        TestGUI se = new TestGUI();

        // Schedules the application to be run at the correct time in the event queue.
        SwingUtilities.invokeLater(se);
    }
}