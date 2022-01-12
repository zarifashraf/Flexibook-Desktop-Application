package ca.mcgill.ecse.flexibook.view;

import ca.mcgill.ecse.flexibook.controller.FlexibookController; 
import ca.mcgill.ecse.flexibook.controller.TOAppointment;

import java.awt.event.ActionEvent;  
import java.awt.event.ActionListener;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Properties;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField.AbstractFormatter;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import org.jdatepicker.impl.JDatePanelImpl;
import org.jdatepicker.impl.JDatePickerImpl;
import org.jdatepicker.impl.SqlDateModel;

import ca.mcgill.ecse.flexibook.application.FlexiBookApplication;
import ca.mcgill.ecse.flexibook.controller.FlexibookController;
import ca.mcgill.ecse.flexibook.controller.InvalidInputException;



/**
 * @author Abrar Fahad Rahman Anik & Saghar Sahebi
 */

public class CustomerHomePage extends JFrame{
	private JLabel flexiBook;
	private JLabel greeting; 

	//not sure 
	//private JLabel calendar;
	private JLabel datePickerLabel;
	private JLabel B1;    
	private JLabel B2;	 
	
    //private JButton flexiBookButton; 
	private JButton AccountSettingsButton;
	private JButton ManageBookingsButton;
	private JButton LogoutButton; 
	
	//datePicker
	
	private JScrollPane overviewScrollPane;
	private JTable overviewTable; 
	
	private JDatePickerImpl datePicker;
	
	//Table
	private DefaultTableModel overviewDtm;
	private String overviewColumnNames[] = {"Booking", "Start Date", "End Date", "Start Time", "End Time"};
	private static final int HEIGHT_OVERVIEW_TABLE = 200;
	
	
	
	private static final int  HEIGHT = 800;
	private static final int WIDTH = 800;
	
	private String error = "";
	
	//instance variables
	//we want to get the username of the current User 
	private String user = FlexiBookApplication.getCurrentUser().getUsername(); 
	AccountSettingsPage newPage;
	ManageBookingsPage newPage1; 
	FlexibookPage newPage2;	


	public CustomerHomePage() {
		initComponents();
		refreshData();
	}
	public void initComponents() {
		//flexibook label 
		flexiBook = new JLabel();
		setTitle("Customer Home Page");
		flexiBook.setText("FLEXIBOOK");
		
		//greeting label 
		greeting = new JLabel();
		greeting.setText("HELLO"+ user);
		
		B1 = new JLabel(); 
		B1.setText("B1(Time)");
		
		B2 = new JLabel(); 
		B2.setText("B2(Time)");
		
		//Buttons
		
		//Date Picker Implementation
		
		SqlDateModel model = new SqlDateModel();
		LocalDate now = LocalDate.now();
		model.setDate(now.getYear(), now.getMonthValue() - 1, now.getDayOfMonth());
		model.setSelected(true);
		Properties p = new Properties();
		p.put("text.today", "Today");
		p.put("text.month", "Month");
		p.put("text.year", "Year");
		JDatePanelImpl panel = new JDatePanelImpl(model, p);
		datePicker = new JDatePickerImpl(panel, new DateLabelFormatter());
		
		this.add(datePicker);
		this.pack();
		this.setVisible(true);
		
		datePickerLabel = new JLabel();
		datePickerLabel.setText("Date for Overview: ");
		
		//DatePicker ends
		
		//Overview Table starts
		
		overviewTable = new JTable() {
			@Override
			public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				if(!c.getBackground().equals(getSelectionBackground())) {
					Object obj = getModel().getValueAt(row, column);
					if (obj instanceof java.lang.String) {
						String str = (String)obj;
						c.setBackground(Color.WHITE);
					}
					else {
						c.setBackground(Color.WHITE);
					}
				}
				
				return c;
			}
		};
		
		overviewScrollPane = new JScrollPane(overviewTable);
		this.add(overviewScrollPane);
		Dimension d = overviewTable.getPreferredSize();
		overviewScrollPane.setPreferredSize(new Dimension(d.width, HEIGHT_OVERVIEW_TABLE));
		overviewScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		
		//listener for datePicker
		
		datePicker.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent evt) {
				refreshData();
			}
		});
		 
		
		
		/**we will set this up and in the action Listener
		part it will only redirect us to the appropriate page**/
		
		AccountSettingsButton = new JButton();
		AccountSettingsButton.setText("ACCOUNT SETTINGS");
		AccountSettingsButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
			newPage = new AccountSettingsPage();
			newPage.setVisible(true);
			}
			});	
		
		ManageBookingsButton = new JButton();
		ManageBookingsButton.setText("MANAGE BOOKINGS");
		ManageBookingsButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			newPage1 = new ManageBookingsPage();
			newPage1.setVisible(true);
			}
		    });
			
		LogoutButton = new JButton();
		LogoutButton.setText("LOGOUT");
		LogoutButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dispose();
				
				try {
					FlexibookController.logoutUser();
				}catch(InvalidInputException err) {
					error += err.getMessage();
				}
				if(error.length() == 0) {
					newPage2 = new FlexibookPage();
					newPage2.setVisible(true);
					
				}
				
			}
			});
		
		
		JSeparator horizontalLineTop =  new JSeparator();
		JSeparator horizontalLineMiddle = new JSeparator();
		JSeparator horizontalLineBottom = new JSeparator();
		
		
		Container contentPanel = this.getContentPane();
		GroupLayout layout = new GroupLayout(contentPanel);
		contentPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(
				layout.createParallelGroup()
				.addComponent(horizontalLineTop)
				.addComponent(horizontalLineMiddle)
				.addComponent(overviewScrollPane)
				
				.addGroup(layout.createSequentialGroup()
						.addGap(100)
						.addComponent(AccountSettingsButton)
						.addGap(100)
						.addComponent(ManageBookingsButton)
						.addGap(100)
						.addComponent(LogoutButton)
						.addGap(100)
						)
						
				.addGroup(layout.createSequentialGroup()
						.addComponent(datePickerLabel)
						.addComponent(datePicker))
						.addGap(100)
				
				
	
						);
						

		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(AccountSettingsButton)
						.addComponent(ManageBookingsButton)
						.addComponent(LogoutButton))
						
				.addGroup(layout.createParallelGroup()
						.addComponent(horizontalLineTop))
				
				.addGroup(layout.createParallelGroup()
						.addComponent(datePickerLabel)
						.addComponent(datePicker))
						
				.addGroup(layout.createParallelGroup()
						.addComponent(horizontalLineMiddle))
				
				.addComponent(overviewScrollPane)
						
				);

		pack();
	} 	

	public void refreshData() {
		overviewDtm = new DefaultTableModel(0,0);
		overviewDtm.setColumnIdentifiers(overviewColumnNames);
		overviewTable.setModel(overviewDtm);
		
		String nameOfUser = FlexiBookApplication.getCurrentUserName();
		
		if(datePicker.getModel().getValue() != null) {
			
			int index = 1;
			
			for (TOAppointment app : FlexibookController.getAppointmentsOfCurrentUser(nameOfUser)) {
				
				
				if (app.getStartDate().equals(datePicker.getModel().getValue())){
					String bookingText = "B"+index;
					String startDateText = app.getStartDate().toString();
					String endDateText = app.getEndDate().toString();
					String startTimeText = app.getStartTime().toString();
					String endTimeText = app.getEndTime().toString();
					
					Object[] obj = {bookingText, startDateText, endDateText, startTimeText, endTimeText};
					overviewDtm.addRow(obj);
					index++;
				}	
				
				else {
					String bookingText = "No bookings on this day";
					String startDateText = " ";
					String endDateText = " ";
					String startTimeText = " ";
					String endTimeText = " ";
					
					Object[] obj = {bookingText, startDateText, endDateText, startTimeText, endTimeText};
					overviewDtm.addRow(obj);
				}
			}
		}
		
		Dimension d = overviewTable.getPreferredSize();
		overviewScrollPane.setPreferredSize(new Dimension(d.width, HEIGHT_OVERVIEW_TABLE));	

	}

}