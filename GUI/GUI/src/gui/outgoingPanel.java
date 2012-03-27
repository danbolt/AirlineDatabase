package gui;

import javax.swing.JTextField;
import javax.swing.JButton;
import javax.swing.JPanel;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 *
 * @author doughnut
 */
public class outgoingPanel extends JPanel implements ActionListener {
    JTextField flightNoField,dateField;
    JButton addButton;

    outgoingPanel(){
        flightNoField = new JTextField("Flight ID #",25);
        dateField = new JTextField("Planned departure Date/Time",25);
        addButton = new JButton("Add");
        addButton.addActionListener(this);

        this.add(flightNoField);
        this.add(dateField);
        this.add(addButton);
    }

     public void actionPerformed(ActionEvent e) {

     }
}
