package ca.mcgill.ecse.flexibook.view;

import ca.mcgill.ecse.flexibook.controller.FlexibookController; 
import ca.mcgill.ecse.flexibook.controller.TOAppointment;
import ca.mcgill.ecse.flexibook.controller.TOOwner;

import java.awt.event.ActionEvent;  
import java.awt.event.ActionListener;
import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.util.List;

import javax.swing.ComboBoxModel;
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

public class OwnerHomePage extends JFrame{
	private JLabel flexiBook;
	private JLabel greeting; 

	//not sure 
	//private JLabel calendar;
	private JLabel datePickerLabel;	 
	
    //private JButton flexiBookButton; 
	private JButton accountSettingsButton;
	private JButton ManageServiceButton;
	private JButton LogoutButton; 
	private JButton BusinessInfoButton;
	
	//datePicker
	
	private JScrollPane overviewScrollPane;
	private JTable overviewTable; 
	
	private JDatePickerImpl datePicker;
	
	//Table
	private DefaultTableModel overviewDtm;
	private String overviewColumnNames[] = {"Appointment", "Start Time", "End Time"};
	private static final int HEIGHT_OVERVIEW_TABLE = 200;
	
	//combo box
	private JLabel appointmentListLabel;
	private JComboBox<String> appointmentList;
	private List<String> appointmentsOfTheDay;
	
	//Buttons for appointment list
	
	private JButton startAppButton;
	private JButton endAppButton;
	private JButton noShowButton;
	
	
	
	
	private static final int  HEIGHT = 800;
	private static final int WIDTH = 800;
	
	private String error = "";
	private TOOwner owner;
	
	//instance variables
	//we want to get the username of the current User 
	private String user = FlexiBookApplication.getCurrentUser().getUsername(); 
	AccountSettingsPage newPage;
	ManageServicesPage newPage1; 
	FlexibookPage newPage2;
	BusinessInfoPage newPage3;	


	public OwnerHomePage() {
		initComponents();
		refreshData();
	}
	public void initComponents() {
		//flexibook label 
		flexiBook = new JLabel();
		setTitle("Owner Home Page");
		flexiBook.setText("FLEXIBOOK");
		
		//greeting label 
		greeting = new JLabel();
		greeting.setText("HELLO"+ user);
		
		
		//the combo box to see the appointments on a specific day
		appointmentListLabel = new JLabel();
		appointmentListLabel.setText("Select Appointment");
		appointmentList = new JComboBox<String>(); 
		
		
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
		//Buttons
		accountSettingsButton = new JButton();
		accountSettingsButton.setText("ACCOUNT SETTINGS");
		accountSettingsButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
			newPage = new AccountSettingsPage();
			newPage.setVisible(true);
			}
			});	
		
		ManageServiceButton = new JButton();
		ManageServiceButton.setText("MANAGE SERVICE");
		ManageServiceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
			newPage1 = new ManageServicesPage();
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
		
		BusinessInfoButton = new JButton();
		BusinessInfoButton.setText("BUSINESS INFO & HOURS");
		BusinessInfoButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				newPage3 = new BusinessInfoPage();
				newPage3.setVisible(true);
			}
		});
		
		//Button actions for selected appointment
		
		startAppButton = new JButton();
		startAppButton.setText("START APPOINTMENT");
		startAppButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				startAppActionPerformed(e);
			}
		});
		
		endAppButton = new JButton();
		endAppButton.setText("END APPOINTMENT");
		endAppButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				endAppActionPerformed(e);
			}
		});
		
		noShowButton = new JButton();
		noShowButton.setText("NO SHOW");
		noShowButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				noShowActionPerformed(e);
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
						.addComponent(accountSettingsButton)
						.addGap(100)
						.addComponent(ManageServiceButton)
						.addGap(100)
						.addComponent(LogoutButton)
						.addGap(100)
						.addComponent(BusinessInfoButton)
						)
						
				.addGroup(layout.createSequentialGroup()
						.addComponent(datePickerLabel)
						.addComponent(datePicker)
						.addComponent(appointmentListLabel)
						.addComponent(appointmentList)
						.addGap(40)
						.addComponent(startAppButton)
						.addComponent(endAppButton)
						.addComponent(noShowButton))
						);
						

		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addComponent(accountSettingsButton)
						.addComponent(ManageServiceButton)
						.addComponent(LogoutButton)
						.addComponent(BusinessInfoButton))
						
				.addGroup(layout.createParallelGroup()
						.addComponent(horizontalLineTop))
				
				.addGroup(layout.createParallelGroup()
						.addComponent(datePickerLabel)
						.addComponent(datePicker)
						.addComponent(appointmentListLabel)
						.addComponent(appointmentList)
						.addComponent(startAppButton)
						.addComponent(endAppButton)
						.addComponent(noShowButton))
						
				.addGroup(layout.createParallelGroup()
						.addComponent(horizontalLineMiddle))
				
				.addComponent(overviewScrollPane)
						
				);

		pack();
	} 	
	
	private void startAppActionPerformed(ActionEvent evt) {
		int selectedIndex = appointmentList.getSelectedIndex();
		//Date date = (Date) datePicker.getModel().getValue();
		//DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		//String dateText = df.format(date);
		
		if(selectedIndex < 0) {
			error = "You need to select an appointment!";
		}
		if(error.length() == 0) {
			String selectedTime = appointmentsOfTheDay.get(selectedIndex);
			
			for(TOAppointment app : FlexibookController.viewDayCalendar(datePicker.getJFormattedTextField().getText())) {
				if (app.getStartTime().toString().equals(selectedTime)) {
					FlexibookController.startAppointment(datePicker.getJFormattedTextField().getText());			
				}
			}							
		}
		refreshData();
	}
	
	private void endAppActionPerformed(ActionEvent evt) {
		int selectedIndex = appointmentList.getSelectedIndex();
		//Date date = (Date) datePicker.getModel().getValue();
		//DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		//String dateText = df.format(date);
		
		if(selectedIndex < 0) {
			error = "You need to select an appointment!";
		}
		if(error.length() == 0) {
			String selectedTime = appointmentsOfTheDay.get(selectedIndex);
			
			for(TOAppointment app : FlexibookController.viewDayCalendar(datePicker.getJFormattedTextField().getText())) {
				if (app.getStartTime().toString().equals(selectedTime)) {
					FlexibookController.endAppointment(datePicker.getJFormattedTextField().getText());			
				}
			}							
		}
		refreshData();
	}
	
	private void noShowActionPerformed(ActionEvent evt) {
		int selectedIndex = appointmentList.getSelectedIndex();
		//Date date = (Date) datePicker.getModel().getValue();
		//DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		//String dateText = df.format(date);
		
		if(selectedIndex < 0) {
			error = "You need to select an appointment!";
		}
		if(error.length() == 0) {
			String selectedTime = appointmentsOfTheDay.get(selectedIndex);
			
			for(TOAppointment app : FlexibookController.viewDayCalendar(datePicker.getJFormattedTextField().getText())) {
				if (app.getStartTime().toString().equals(selectedTime)) {
					FlexibookController.registerNoShow(datePicker.getJFormattedTextField().getText());			
				}
			}							
		}
		refreshData();
	}


	public void refreshData() {
		overviewDtm = new DefaultTableModel(0,0);
		overviewDtm.setColumnIdentifiers(overviewColumnNames);
		overviewTable.setModel(overviewDtm);
		
		//Date date = (Date) datePicker.getModel().getValue();
		//DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		//String dateText = df.format(date);
		
		if(datePicker.getModel().getValue() != null) {
			
			for (TOAppointment app : FlexibookController.viewDayCalendar(datePicker.getJFormattedTextField().getText())) {
				int index = 1;
				String appointmentText = "A"+index;
				String startTimeText = app.getStartTime().toString();
				String endTimeText = app.getEndTime().toString();
				 
				Object[] obj = {appointmentText, startTimeText, endTimeText};
				overviewDtm.addRow(obj);
				index++;
			}
		}
		
		Dimension d = overviewTable.getPreferredSize();
		overviewScrollPane.setPreferredSize(new Dimension(d.width, HEIGHT_OVERVIEW_TABLE));	
		
		appointmentsOfTheDay = new ArrayList<>();
		
		appointmentList.removeAllItems();
		for(TOAppointment app : FlexibookController.viewDayCalendar(datePicker.getJFormattedTextField().getText())) {
			appointmentsOfTheDay.add(app.getStartTime().toString());
			appointmentList.addItem(app.getStartTime().toString());
		}
		appointmentList.setSelectedIndex(-1);
		
		//datePicker.getModel().setValue(null);
		
		
	}
}