package apprisk;

import java.awt.*;
import java.util.Map;

import javax.swing.*;
import javax.swing.event.*;

@SuppressWarnings("serial")
public class GraphicInterface extends JPanel {
	JTextArea listOutput;
	JTextArea assessmentOutput;
	JTextArea descriptionOutput;
	JList<String> list;
	ListSelectionModel listSelectionModel;
	AppRisk appRisk;
	Map<String, Permissions> permWeightMap;
	Map<String, String> permDescMap;

	/**
	 * Create the panel.
	 */
	public GraphicInterface() {
		super(new BorderLayout());
		
		appRisk = new AppRisk();
		permWeightMap = appRisk.getPermWeightMap();
		permDescMap = appRisk.getPermissionDescriptions();
		list = new JList<>(appRisk.getListData());
		
		listSelectionModel = list.getSelectionModel();
        listSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listSelectionModel.addListSelectionListener(new ListSelectionHandler());
        JScrollPane listPane = new JScrollPane(list);

        //Build output area.
        listOutput = new JTextArea(1, 10);
        listOutput.setEditable(false);
        listOutput.setMargin(new Insets(2, 5, 2, 5));
        JScrollPane listOutputPane = new JScrollPane(listOutput,
                         ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                         ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        assessmentOutput = new JTextArea(1, 10);
        assessmentOutput.setEditable(false);
        assessmentOutput.setLineWrap(true);
        assessmentOutput.setWrapStyleWord(true);
        assessmentOutput.setMargin(new Insets(2, 5, 2, 5));
        assessmentOutput.setText("Click on an item to view its details.");
        JScrollPane assessmentPane = new JScrollPane(assessmentOutput,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        descriptionOutput = new JTextArea(1, 10);
        descriptionOutput.setEditable(false);
        descriptionOutput.setLineWrap(true);
        descriptionOutput.setWrapStyleWord(true);
        descriptionOutput.setMargin(new Insets(2, 5, 2, 5));
        JScrollPane descriptionPane = new JScrollPane(descriptionOutput,
                ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        //Do the layout.
        JSplitPane splitPaneLeft = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        add(splitPaneLeft, BorderLayout.WEST);

        JPanel topLeft = new JPanel();
        topLeft.setLayout(new BoxLayout(topLeft, BoxLayout.LINE_AXIS));
        JPanel listContainer = new JPanel(new GridLayout(1, 1));
        listContainer.setBorder(BorderFactory.createTitledBorder("Installed Applications"));
        listContainer.add(listPane);
        
        topLeft.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        topLeft.add(listContainer);

        topLeft.setMinimumSize(new Dimension(100, 100));
        topLeft.setPreferredSize(new Dimension(100, 300));
        splitPaneLeft.add(topLeft);

        JPanel bottomLeft = new JPanel(new BorderLayout());
        bottomLeft.add(listOutputPane, BorderLayout.CENTER);
        bottomLeft.setMinimumSize(new Dimension(400, 100));
        bottomLeft.setPreferredSize(new Dimension(450, 300));
        splitPaneLeft.add(bottomLeft);
        
        JSplitPane splitPaneRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        
        JPanel topRight = new JPanel();
        topRight.setLayout(new BoxLayout(topRight, BoxLayout.LINE_AXIS));
        JPanel assessmentContainer = new JPanel(new GridLayout(1, 1));
        assessmentContainer.setBorder(BorderFactory.createTitledBorder("Assessment"));
        assessmentContainer.add(assessmentPane);
        
        topRight.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        topRight.add(assessmentContainer);
        
        topRight.setMinimumSize(new Dimension(100, 100));
        topRight.setPreferredSize(new Dimension(400, 300));
        splitPaneRight.add(topRight);
        
        add(splitPaneRight, BorderLayout.CENTER);
        
        JPanel bottomRight = new JPanel();
        bottomRight.setLayout(new BoxLayout(bottomRight, BoxLayout.LINE_AXIS));
        JPanel descriptionContainer = new JPanel(new GridLayout(1, 1));
        descriptionContainer.setBorder(BorderFactory.createTitledBorder("Permission Details"));
        descriptionContainer.add(descriptionPane);
        
        bottomRight.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        bottomRight.add(descriptionContainer);
        bottomRight.setMinimumSize(new Dimension(100, 100));
        bottomRight.setPreferredSize(new Dimension(500, 500));
        splitPaneRight.add(bottomRight);
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
        javax.swing.SwingUtilities.invokeLater(GraphicInterface::createAndShowGUI);
    }
	// Listen for change in list selection
    class ListSelectionHandler implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            boolean isAdjusting = e.getValueIsAdjusting();
            if (!isAdjusting) {
            	getPermissionDetails(getListOutput());
            }
        }
    }
    // Changes display in top right and bottom left panels according to list selection in first panel
    String getListOutput() {
        Map<String, String> whitelist = appRisk.getWhiteList();

        String appName = (list.getSelectedValue().split(" \\("))[0];
        String[] data = appRisk.calculateRisk(appName);
        double riskValue = Double.parseDouble(data[0]);
        String justPerms = data[4].replace("dangerous ", "").replace("removed ", "")
                .replace("signature ", "");

        listOutput.setText("Risk = " + data[0] + "\n\n"
        		+ "First installed: " + data[2] + "\n"
        		+ "Last modified: " + data[3] + "\n\n"
                + "Permissions:\n");
        if (justPerms.equals("")) {
            listOutput.append("No suspicious permissions");
        } else {
            listOutput.append(justPerms);
        }
        listOutput.setCaretPosition(0);

        if (whitelist.containsKey(appName) && whitelist.get(appName).equals(data[1])) {
        	assessmentOutput.setText("This app has been whitelisted.\n\nPopular, well-known apps still have the"
        			+ " potential to be dangerous. For example, if an attacker knows your device PIN or"
        			+ " account password, they can access your private data collected by the app.");
        } else if (riskValue == 0.0) {
        	assessmentOutput.setText("This app is very unlikely to be spyware.\n\nThis app does not use any permissions"
        			+ " associated with spyware.\n\nBe mindful that a future update could introduce permissions"
        			+ " that could be dangerous in the wrong hands.");
        } else if (riskValue > 0 && riskValue < 20) {
        	assessmentOutput.setText("This app is unlikely to be spyware.\n\nHowever, the risk value could have been"
        			+ " skewed downward if the total number of permissions used by this app is small.");
        } else if (riskValue >= 20 && riskValue < 50) {
        	assessmentOutput.setText("This app is slightly likely to be spyware.\n\nMany popular apps fall within"
        			+ " this range, in part because they use location services. Check the name and installation"
        			+ " date to see if you recognize installing this app yourself, and familiarize yourself with"
        			+ " its capabilities through its permissions.");
        } else if (riskValue >= 50 && riskValue < 70) {
        	assessmentOutput.setText("This app is likely to be spyware.\n\nThe more suspicious permissions an app"
        			+ " uses, the more capabilities the app has to invade your privacy.");
        } else {
        	assessmentOutput.setText("This app is very likely to be spyware.\n\nThe maximum risk value is 129.05."
        			+ " The closer an app's risk value is to 129.05, the more likely it is to be spyware"
        			+ " due to the number of dangerous and spyware-linked permissions it uses.");
        }
        return data[4];
    }
    // Changes display in bottom right panel according to list selection in first panel
    void getPermissionDetails(String str) {
    	String[] result1 = str.split("\n");
    	
    	descriptionOutput.setText("");
    	
    	if (!(result1[0].equals(""))) {
        	for (String s : result1) {
                String[] result2 = s.split(" ");
                descriptionOutput.append(result2[1] + "\n" + result2[0].toUpperCase() + "\n"
                        + permDescMap.get(result2[1]) + "\n\n");
        	}
    	}
    	descriptionOutput.setCaretPosition(0);
    }
}
