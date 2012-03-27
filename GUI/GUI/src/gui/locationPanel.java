/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

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
public class locationPanel extends JPanel implements ActionListener {
    JTextField locationField;
    JButton addButton;

    locationPanel(){
        locationField = new JTextField("name",20);
        addButton = new JButton("Add");
        addButton.addActionListener(this);

        this.add(locationField);
        this.add(addButton);
    }

     public void actionPerformed(ActionEvent e) {

     }
}
