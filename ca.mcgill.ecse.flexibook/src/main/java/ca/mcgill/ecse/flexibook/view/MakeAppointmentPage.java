package ca.mcgill.ecse.flexibook.view;


import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import ca.mcgill.ecse.flexibook.application.FlexiBookApplication;
import ca.mcgill.ecse.flexibook.controller.FlexibookController;
import ca.mcgill.ecse.flexibook.controller.InvalidInputException;
import ca.mcgill.ecse.flexibook.controller.TOService;



public class MakeAppointmentPage extends JFrame{
	//pages dimensions
	private static final int  HEIGHT = 800;
	private static final int WIDTH = 800;
	
	//data
	private List<String> availableServices;
	String error;

	
	//list of services to add
	private JLabel serviceLabel;
	private JComboBox<String> servicesList;
	
	//time & date of appointment
	private JTextField timeTextField;
	private JTextField dateTextField;
	private JLabel timeLabel;
	private JLabel dateLabel;
	
	//button
	private JButton makeAppointmentBtn;
	
	private JLabel errorMessage;
	
	
	public MakeAppointmentPage() {
		initComponents();
		refreshData();
	}
	
	public void initComponents() {
		//list of availabe service
		serviceLabel = new JLabel();
		serviceLabel.setText("Select a Service");
		servicesList = new JComboBox<String>();
			
		
		
		
		//Time
		timeLabel = new JLabel();
		timeLabel.setText("Input Appointment Time in the following format HH:mm ");
		//Time text input
		timeTextField = new JTextField();
		
		//Date
		dateLabel = new JLabel();
		dateLabel.setText("Input Appointmen Date in the following format yyyy-MM-dd ");
		//date text input
		dateTextField = new JTextField();
		
	
			
		// make appointment
		//update an appointment
		makeAppointmentBtn = new JButton();
		makeAppointmentBtn.setText("Book the Appointment");
		makeAppointmentBtn.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						createAppointmentActionButtonPerformed(e);
					}

				});
		errorMessage = new JLabel();
		errorMessage.setForeground(Color.RED);
		


		
		
		
	

		Container contentPanel = this.getContentPane();
		GroupLayout layout = new GroupLayout(contentPanel);
		contentPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(
				layout.createParallelGroup()
				.addComponent(errorMessage)
				.addGroup(layout.createSequentialGroup()
						.addComponent(timeLabel)
						.addComponent(timeTextField, 200, 200, 400)
						.addGap(10,20,100)
						.addComponent(dateLabel)
						.addComponent(dateTextField, 200, 200, 400))		
				.addComponent(serviceLabel)
				.addComponent(servicesList)
				.addComponent(makeAppointmentBtn)
				
						
						
				)
		;

		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(errorMessage)
				.addGroup(layout.createParallelGroup()
						.addComponent(errorMessage)
						.addComponent(timeLabel)
						.addComponent(timeTextField)
						.addComponent(dateLabel)
						.addComponent(dateTextField))
				.addComponent(serviceLabel)
				.addComponent(servicesList)
				.addComponent(makeAppointmentBtn)
						
				);

		pack();
		
	}
	
	private void createAppointmentActionButtonPerformed(ActionEvent e) {
	
		error = "";
		String action="";
		List<String> optionalServices=null;
		
		//user
		
		String username = FlexiBookApplication.getCurrentUser().getUsername();
		
		
		int selectedIndex = servicesList.getSelectedIndex();
		
		
		//retrieve Date
		String aDate=dateTextField.getText();
		Date date=FlexiBookApplication.convertToDate(aDate);
		//retrieve time
		String startTimeString=timeTextField.getText();
		Time time=FlexiBookApplication.convertTotime(startTimeString);
		
		if(selectedIndex < 0) {
			error = "You need to pick a appointment";
		}
		

		if(error.length() == 0) {
			
			
			String nameOfService = servicesList.getItemAt(selectedIndex);
			
			try {
				FlexibookController.makeAppointment(username, nameOfService, date, time, optionalServices); ;		
			} catch (InvalidInputException i) {
				error = i.getMessage();
			}
		}
		//for updating UI
		refreshData();
		
	}
		
		
		
		
		
		
	

public void refreshData() {
	errorMessage.setText(error);
	


	

	
	
	if(error == null || error.length() == 0) {
	//populate the page with data
	timeTextField.setText("");
	dateTextField.setText("");
	
	
	availableServices = new ArrayList<>();
	servicesList.removeAllItems();
	for(TOService service : FlexibookController.getServices()) {
		availableServices.add(service.getName());
		servicesList.addItem(service.getName());
	}
	servicesList.setSelectedIndex(-1);}
	}
	
}
