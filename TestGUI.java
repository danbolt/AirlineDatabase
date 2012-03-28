import javax.swing.*;
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