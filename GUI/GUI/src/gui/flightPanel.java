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
public class flightPanel extends JPanel implements ActionListener {
    JTextField locationFromField,locationToField,planeModelField;
    JButton addButton;

    flightPanel(){
        locationFromField = new JTextField("Location From ID #",25);
        locationToField = new JTextField("Location To ID #",25);
        planeModelField = new JTextField("Plane Model ID #",25);
        addButton = new JButton("Add");
        addButton.addActionListener(this);

        this.add(locationFromField);
        this.add(locationToField);
        this.add(planeModelField);
        this.add(addButton);
    }

     public void actionPerformed(ActionEvent e) {

     }
}
