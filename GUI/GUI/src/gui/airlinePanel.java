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
public class airlinePanel extends JPanel implements ActionListener {
    JTextField airlineNameField,airlineWebsiteField;
    JButton addButton;

    airlinePanel(){
        airlineNameField = new JTextField("Airline Name",25);
        airlineWebsiteField = new JTextField("Website",25);
        addButton = new JButton("Add");
        addButton.addActionListener(this);

        this.add(airlineNameField);
        this.add(airlineWebsiteField);
        this.add(addButton);
    }

     public void actionPerformed(ActionEvent e) {

     }
}
