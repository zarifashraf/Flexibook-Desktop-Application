package ca.mcgill.ecse.flexibook.view;

import javax.swing.JFrame;
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
import javax.swing.JSeparator;
import javax.swing.JTextField;

import ca.mcgill.ecse.flexibook.application.FlexiBookApplication;
import ca.mcgill.ecse.flexibook.controller.FlexibookController;
import ca.mcgill.ecse.flexibook.controller.InvalidInputException;
import ca.mcgill.ecse.flexibook.controller.TOAppointment;
import ca.mcgill.ecse.flexibook.controller.TOService;


public class UpdateAppointmentPage extends JFrame{
	//string Error
	String error;
	
	
	//data
	private List<String> userAppointments;
	private List<String> availableServices;
	private List<String> servicesBookedForAppt;
	
	private JLabel errorMessage;
	//pages dimensions
	private static final int  HEIGHT = 800;
	private static final int WIDTH = 800;
	
	//list of bookings of that user
	private JLabel appointmentsLabel;
	private JComboBox<String> bookedAppointmentList;
	
	//list of services to add
	private JLabel addServiceLabel;
	private JComboBox<String> servicesList;
	

	//list of services booked for that appointment
	private JLabel bookedServiceLabel;
	private JComboBox<String> servicesBookedList;
	
	
    // JButtons
	private JButton updateAppointmentTimeBtn;
	private JButton updateAppointmentAddServiceBtn;
	private JButton updateAppointmentDeleteServiceBtn;
	private JButton deleteApptBtn;
	
	//
	private JTextField timeTextField;
	private JTextField dateTextField;
	private JLabel timeLabel;
	private JLabel dateLabel;
	
	
	public UpdateAppointmentPage() {
		initComponents();
		refreshData();
	}
	
	public void initComponents() {
		
		//list of user booked appointments
		appointmentsLabel = new JLabel();
		appointmentsLabel.setText("Select a Booked Appointment to be Updated or Canceled");
		bookedAppointmentList = new JComboBox<String>();
		
		//list of availabe service
		addServiceLabel = new JLabel();
		addServiceLabel.setText("Select a Service to be Added to Booking Selected");
		servicesList = new JComboBox<String>();
		
	
		//list of booked services associated with appointment selected
		bookedServiceLabel = new JLabel();
		bookedServiceLabel.setText("Select a Booked Service to be Removed from the Appointment Selected");
		servicesBookedList = new JComboBox<String>();
		
		//Time
		timeLabel = new JLabel();
		timeLabel.setText("Input updated Appointment start Time in the following format HH:mm");
		//Time text input
		timeTextField = new JTextField();
		
		//Date
		dateLabel = new JLabel();
		dateLabel.setText("Input updated Appointmen Date in the following format yyyy-MM-dd ");
		//date text input
		dateTextField = new JTextField();
		
		
	
		//update an appointment time
		updateAppointmentTimeBtn = new JButton();
		updateAppointmentTimeBtn.setText("Update the time of the Appointment selected");
		updateAppointmentTimeBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				updateAppointmentTimeActionButtonPerformed(e);
			}
			
		});
		
		//update an appointment by adding a service
		updateAppointmentAddServiceBtn = new JButton();
		updateAppointmentAddServiceBtn.setText("Add a service to the Appointment selected");
		updateAppointmentAddServiceBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				addServiceAppointmentActionButtonPerformed(e);
			}


			
		});
		
		//update an appointment by removing a service
		updateAppointmentDeleteServiceBtn = new JButton();
		updateAppointmentDeleteServiceBtn.setText("Remove a service to the Appointment selected");
		updateAppointmentDeleteServiceBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				removeServiceAppointmentActionButtonPerformed(e);
			}


			
		});
		
		
		
		
		//delete an appointment
		deleteApptBtn = new JButton();
		deleteApptBtn.setText("Cancel Appointment selected");
		deleteApptBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteAppointmentActionButtonPerformed(e);
			}

			
		});
		
		
		errorMessage = new JLabel();
		errorMessage.setForeground(Color.RED);
		


		
		
		
	

		//labyout

		Container contentPanel = this.getContentPane();
		GroupLayout layout = new GroupLayout(contentPanel);
		contentPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(
				layout.createParallelGroup()
				
				.addComponent(errorMessage)
				.addGroup(layout.createSequentialGroup()
						.addComponent(appointmentsLabel)
						.addComponent(bookedAppointmentList)
						.addComponent(timeLabel)
						.addComponent(timeTextField, 200, 200, 400)
						.addGap(10,20,100)
						.addComponent(dateLabel)
						.addComponent(dateTextField, 200, 200, 400)
						)		
				.addComponent(addServiceLabel)
				.addComponent(servicesList)
				.addComponent(bookedServiceLabel)
				.addComponent(servicesBookedList)
				.addComponent(updateAppointmentAddServiceBtn)
				.addComponent(deleteApptBtn)
				.addComponent(updateAppointmentDeleteServiceBtn)
				.addComponent(updateAppointmentTimeBtn)
				
				
						
						
				)
		;

		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(errorMessage)
				.addGroup(layout.createParallelGroup()
						.addComponent(errorMessage)
						.addComponent(appointmentsLabel)
						.addComponent(bookedAppointmentList)
						.addComponent(timeLabel)
						.addComponent(timeTextField)
						.addComponent(dateLabel)
						.addComponent(dateTextField))
				.addComponent(addServiceLabel)
				.addComponent(servicesList)
				.addComponent(bookedServiceLabel)
				.addComponent(servicesBookedList)
				.addComponent(updateAppointmentAddServiceBtn)
				.addComponent(deleteApptBtn)
				.addComponent(updateAppointmentDeleteServiceBtn)
				.addComponent(updateAppointmentTimeBtn)
						
				);

		pack();
		
		
		
		
		
	}
	
	
	
	private void updateAppointmentTimeActionButtonPerformed(ActionEvent e) {
		error = "";
		String action="";
		String comboItem="";
		//user
		
		String username = FlexiBookApplication.getCurrentUser().getUsername();
		int selectedIndex = bookedAppointmentList.getSelectedIndex();
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
			
			
			
					
			try {
				String nameOfService = FlexibookController.getCustomersAppointment(FlexiBookApplication.getCurrentUser(),selectedIndex);
				FlexibookController.updateAppointment(username, username,  nameOfService,  action, comboItem, date, time) ;		
			} catch (InvalidInputException i) {
				error = i.getMessage();
			}
		}
		//for updating UI
		refreshData();
		
	}
	
	
	
	
	private void addServiceAppointmentActionButtonPerformed(ActionEvent e) {
		error = "";
		String action="add";
		String comboItem="";
		//user

		String username = FlexiBookApplication.getCurrentUser().getUsername();
		
		//
		int selectedIndexappointment = bookedAppointmentList.getSelectedIndex();
		
		
		int selectedIndexAddedService = servicesList.getSelectedIndex();

		//retrieve Date
		Date date=null;
		//retrieve time
        Time time=null;
        
        
		
		if(selectedIndexappointment < 0) {
			error = "You need to pick a appointment";
		}
		if(selectedIndexAddedService < 0) {
			error = "You need to pick a service";
		}
		

		if(error.length() == 0) {

			
			//time of appointment
					
			
			//serviceadded
			comboItem=servicesList.getItemAt(selectedIndexAddedService);
			try {
				date=FlexibookController.getappointmentDate(FlexiBookApplication.getCurrentUser(),selectedIndexappointment);
				time=FlexibookController.getappointmentTime(FlexiBookApplication.getCurrentUser(),selectedIndexappointment);
				
				
				String nameOfService = FlexibookController.getCustomersAppointment(FlexiBookApplication.getCurrentUser(),selectedIndexappointment);
				FlexibookController.updateAppointment(username, username,  nameOfService,  action, comboItem, date, time) ;		
			} catch (InvalidInputException i) {
				error = i.getMessage();
			}
		}
		//for updating UI
		refreshData();
		
	}
	
	
	
	
	
	
	private void removeServiceAppointmentActionButtonPerformed(ActionEvent e) {
		error = "";
		String action="remove";
		String comboItem="";
		//user

		String username = FlexiBookApplication.getCurrentUser().getUsername();
		
		//
		int selectedIndexappointment = bookedAppointmentList.getSelectedIndex();
		
		
		int selectedIndexServiceToRemove = servicesBookedList.getSelectedIndex();

		//retrieve Date
		Date date=null;
		//retrieve time
        Time time=null;
        
        
		
		if(selectedIndexappointment < 0) {
			error = "You need to pick a appointment";
		}
		if(selectedIndexServiceToRemove < 0) {
			error = "You need to pick a Book Service";
		}
		

		if(error.length() == 0) {
			
			
			//serviceadded
			comboItem=servicesBookedList.getItemAt(selectedIndexServiceToRemove);
			try {
				date=FlexibookController.getappointmentDate(FlexiBookApplication.getCurrentUser(),selectedIndexappointment);
				time=FlexibookController.getappointmentTime(FlexiBookApplication.getCurrentUser(),selectedIndexappointment);
				
				
				String nameOfService = FlexibookController.getCustomersAppointment(FlexiBookApplication.getCurrentUser(),selectedIndexappointment);
				
				FlexibookController.updateAppointment(username, username,  nameOfService,  action, comboItem, date, time) ;		
			} catch (InvalidInputException i) {
				error = i.getMessage();
			}
		}
		//for updating UI
		refreshData();
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void deleteAppointmentActionButtonPerformed(ActionEvent e) {
		error = "";
		
		String username = FlexiBookApplication.getCurrentUser().getUsername();
		int selectedIndex = bookedAppointmentList.getSelectedIndex();
		//retrieve Date
		Date date=null;
		//retrieve time
        Time time=null;
		
		if(selectedIndex < 0) {
			error = "You need to pick a appointment";
		}
		
		
		
		if(error.length() == 0) {
			//call the controller

			try {
				date=FlexibookController.getappointmentDate(FlexiBookApplication.getCurrentUser(),selectedIndex);
				time=FlexibookController.getappointmentTime(FlexiBookApplication.getCurrentUser(),selectedIndex);
				
				
				String nameOfService = FlexibookController.getCustomersAppointment(FlexiBookApplication.getCurrentUser(),selectedIndex);
				

				FlexibookController.cancelAppointment(username,username, nameOfService,date,time);		
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
	
		//clear data
		
	//view users appointments	
		userAppointments = new ArrayList<>();
		bookedAppointmentList.removeAllItems();
		try {
			for(String appointment : FlexibookController.getAppointments(FlexiBookApplication.getCurrentUser())) {
				bookedAppointmentList.addItem(appointment);
			}
		} catch (InvalidInputException i) {
			// TODO Auto-generated catch block
			error = i.getMessage();
		}
		bookedAppointmentList.setSelectedIndex(-1);
		
		//

		
		
		//get all the services in the system
		availableServices = new ArrayList<>();
		servicesList.removeAllItems();
		for(TOService service : FlexibookController.getServices()) {
			availableServices.add(service.getName());
			servicesList.addItem(service.getName());
		}
		servicesList.setSelectedIndex(-1);
	
	
	

	servicesBookedList.removeAllItems();
	try {
	 for(String serviceItem :FlexibookController.getServiceOfAppointment(FlexiBookApplication.getCurrentUser(),bookedAppointmentList.getSelectedIndex() )) {
	 
		
		servicesBookedList.addItem(serviceItem);
	}}
	catch (InvalidInputException i) {
		// TODO Auto-generated catch block
		error = i.getMessage();
	}
	servicesList.setSelectedIndex(-1);
	}
	
}
}
