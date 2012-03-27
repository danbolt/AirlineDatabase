/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package gui;

import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import java.awt.event.*;
import java.awt.Container;
import java.awt.BorderLayout;

/**
 *
 * @author doughnut
 */
public class AirlineDBFrame extends JFrame {

    JMenuBar jmb;
    JMenu addMenu, deleteMenu, queryMenu;
    JMenuItem itemAirlines;
    locationPanel location;
    flightPanel flight;

    AirlineDBFrame(){

        super("Airline Database");

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        flight = new flightPanel();
        cp.add(flight);

        jmb = new JMenuBar();
        addMenu = new JMenu("Add to Database", true);
        deleteMenu = new JMenu("Delete from Database", true);
        queryMenu = new JMenu("Query Database", true);

        jmb.add(addMenu);
        jmb.add(deleteMenu);
        jmb.add(queryMenu);

        itemAirlines = new JMenuItem("Airlines");
        addMenu.add(itemAirlines);

        this.setJMenuBar(jmb);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(400, 320);

        this.setVisible(true);
        this.setResizable(false);
    }
}
