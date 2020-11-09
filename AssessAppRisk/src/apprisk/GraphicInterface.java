package apprisk;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.*;
import javax.swing.event.*;

@SuppressWarnings("serial")
public class GraphicInterface extends JPanel {
	JTextArea output;
	JList<String> list;
	JTable table;
	ListSelectionModel listSelectionModel;
	AppRisk appRisk;

	/**
	 * Create the panel.
	 */
	public GraphicInterface() {
		super(new BorderLayout());
		
		appRisk = new AppRisk();
		
		list = new JList<String>(appRisk.getListData());
		
		listSelectionModel = list.getSelectionModel();
        listSelectionModel.addListSelectionListener(new ListSelectionHandler());
        JScrollPane listPane = new JScrollPane(list);
        listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        //Build output area.
        output = new JTextArea(1, 10);
        output.setEditable(false);
        JScrollPane outputPane = new JScrollPane(output,
                         ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                         ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        //Do the layout.
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        add(splitPane, BorderLayout.CENTER);

        JPanel topHalf = new JPanel();
        topHalf.setLayout(new BoxLayout(topHalf, BoxLayout.LINE_AXIS));
        JPanel listContainer = new JPanel(new GridLayout(1,1));
        listContainer.setBorder(BorderFactory.createTitledBorder("Installed Applications"));
        listContainer.add(listPane);
        
        topHalf.setBorder(BorderFactory.createEmptyBorder(5,5,0,5));
        topHalf.add(listContainer);
        //topHalf.add(tableContainer);

        topHalf.setMinimumSize(new Dimension(100, 100));
        topHalf.setPreferredSize(new Dimension(100, 300));
        splitPane.add(topHalf);

        JPanel bottomHalf = new JPanel(new BorderLayout());
        bottomHalf.add(outputPane, BorderLayout.CENTER);
        //XXX: next line needed if bottomHalf is a scroll pane:
        bottomHalf.setMinimumSize(new Dimension(400, 100));
        bottomHalf.setPreferredSize(new Dimension(450, 300));
        splitPane.add(bottomHalf);
	}
	
	    /**
	     * Create the GUI and show it.  For thread safety,
	     * this method should be invoked from the
	     * event-dispatching thread.
	     */
    	private static void createAndShowGUI() {
        //Create and set up the window.
        JFrame frame = new JFrame("Your Risk Assessment");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        GraphicInterface gui = new GraphicInterface();
        gui.setOpaque(true);
        frame.setContentPane(gui);

        //Display the window.
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

	public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
	
    class ListSelectionHandler implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) { 
        	String appName = (list.getSelectedValue().toString().split(" \\("))[0];
        	String[] data = appRisk.calculateRisk(appName);

        	output.setText("Risk = " + data[0] + "\n\n"
        			+ "First installed: " + data[1] + "\n"
        			+ "Last modified: " + data[2] + "\n\n"
        			+ "Permissions:\n" + data[3]);
        	output.setCaretPosition(0);
        }
    }
}
