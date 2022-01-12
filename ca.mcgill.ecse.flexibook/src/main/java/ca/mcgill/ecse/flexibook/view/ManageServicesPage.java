package ca.mcgill.ecse.flexibook.view;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import ca.mcgill.ecse.flexibook.controller.FlexibookController;
import ca.mcgill.ecse.flexibook.controller.InvalidInputException;
import ca.mcgill.ecse.flexibook.controller.TOOwner;
import ca.mcgill.ecse.flexibook.controller.TOService;

public class ManageServicesPage extends JFrame {

	private JLabel errorMessage;
	private JLabel nameLabel;
	private JLabel durationLabel;
	private JLabel downtimeDurationLabel;
	private JLabel downtimeStartLabel;

	private JTextField nameTx;
	private JTextField durationTx;
	private JTextField downtimeDurationTx;
	private JTextField downtimeStartTx;

	private JButton createBtn;
	private JButton updateBtn;
	private JButton deleteBtn;

	private JLabel serviceLabel;
	private JComboBox<String> serviceList;

	//data
	private String error = "";
	private TOOwner owner;
	private List<String> availableServices;

	//instance variables 
	private int duration;
	private int downtimeStart;
	private int downtimeDuration;
	private String nameOfService;

	public ManageServicesPage(){
		initComponents();
		refreshData();
	}

	public void initComponents() {
		setBounds(100, 100, 1500, 1000);
		setTitle("ManageServicesPage");
		//error
		errorMessage = new JLabel();
		errorMessage.setForeground(Color.RED);

		//name of service
		nameLabel = new JLabel();
		nameLabel.setText("Name");
		nameTx = new JTextField();

		//duration of service
		durationLabel = new JLabel();
		durationLabel.setText("Duration");
		durationTx = new JTextField();

		//downtime duration
		downtimeDurationLabel = new JLabel();
		downtimeDurationLabel.setText("DowntimeDuration");
		downtimeDurationTx = new JTextField();

		//downtime start
		downtimeStartLabel = new JLabel();
		downtimeStartLabel.setText("DowntimeStart");
		downtimeStartTx = new JTextField();

		//create button
		createBtn = new JButton();
		createBtn.setText("Create");
		createBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				createServiceActionPerformed(e);
			}
		});

		//update button
		updateBtn = new JButton();
		updateBtn.setText("Update");
		updateBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateServiceActionPerformed(e);
			}
		});

		//delete button
		deleteBtn = new JButton();
		deleteBtn.setText("Delete");
		deleteBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteServiceActionPerformed(e);
			}
		});


		//list of services
		serviceLabel = new JLabel();
		serviceLabel.setText("Select a service");
		serviceList = new JComboBox<String>();	//selects first service by default

		// horizontal line elements
		JSeparator horizontalLineTop = new JSeparator();
		JSeparator horizontalLineMiddle = new JSeparator();
		JSeparator horizontalLineBottom = new JSeparator();

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
						.addComponent(nameLabel)
						.addComponent(nameTx, 200, 200, 400)
						.addGap(10,20,100)
						.addComponent(durationLabel)
						.addComponent(durationTx, 200, 200, 400)
						.addComponent(downtimeDurationLabel)
						.addComponent(downtimeDurationTx, 200, 200, 400)
						.addComponent(downtimeStartLabel)
						.addComponent(downtimeStartTx, 200, 200, 400)
						.addComponent(createBtn))
				.addComponent(serviceLabel)
				.addComponent(serviceList)
				.addGroup(layout.createSequentialGroup()
						.addComponent(updateBtn)
						.addComponent(deleteBtn))
				)
		;

		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(errorMessage)
				.addGroup(layout.createParallelGroup()
						.addComponent(errorMessage)
						.addComponent(nameLabel)
						.addComponent(nameTx)
						.addComponent(durationLabel)
						.addComponent(durationTx)
						.addComponent(downtimeDurationLabel)
						.addComponent(downtimeDurationTx)
						.addComponent(downtimeStartLabel)
						.addComponent(downtimeStartTx)
						.addComponent(createBtn))
				.addComponent(serviceLabel)
				.addComponent(serviceList)
				.addGroup(layout.createParallelGroup()
						.addGap(100,200,200)
						.addComponent(updateBtn)
						.addComponent(deleteBtn))
				);

		pack();
	}

	//action event for creating a service
	private void createServiceActionPerformed(ActionEvent evt) {
		// clear error message
		error = "";
		owner = FlexibookController.getOwner();
		String ownerUsername = owner.getUsername();
		setErrorMessageForIncorrectInput();

		if(error.length() == 0) {
			//call controller
			try {
				FlexibookController.createService(ownerUsername, nameOfService, duration, downtimeDuration, downtimeStart);		//change owner 
			}catch(InvalidInputException e1) {
				error = e1.getMessage();
			}
		}
		//for updating UI
		refreshData();
	}

	//action event for updating a service
	private void updateServiceActionPerformed(ActionEvent evt) {
		error = "";
		setErrorMessageForIncorrectInput();
		owner = FlexibookController.getOwner();
		String ownerUsername = owner.getUsername();
		int selectedIndex = serviceList.getSelectedIndex();
		if(selectedIndex < 0) {
			error = "You need to pick a service!";
		}
		if(error.length() == 0) {
			String oldName = availableServices.get(selectedIndex);
			try {
				FlexibookController.updateService(ownerUsername, oldName, nameOfService, duration, downtimeDuration, downtimeStart);
			}catch(InvalidInputException e) {
				error = e.getMessage();
			}
		}

		//for updating UI
		refreshData();
	}

	//action event for deleting a service
	private void deleteServiceActionPerformed(ActionEvent evt) {
		error = "";
		owner = FlexibookController.getOwner();
		String ownerUsername = owner.getUsername();
		int selectedIndex = serviceList.getSelectedIndex();
		if(selectedIndex < 0) {
			error = "You need to pick a service!";
		}

		if(error.length() == 0) {
			//call the controller
			String nameOfService = availableServices.get(selectedIndex);
			try {
				FlexibookController.deleteService(ownerUsername, nameOfService);			//change owner
			} catch (InvalidInputException e) {
				error = e.getMessage();
			}
		}
		//for updating UI
		refreshData();
	}

	public void setErrorMessageForIncorrectInput() {
		nameOfService = nameTx.getText();
		duration = 0; 
		downtimeDuration = 0;
		downtimeStart = 0;
		try {
			duration = Integer.parseInt(durationTx.getText());
		}catch(NumberFormatException e) {
			error = "Duration needs to be a number";
		}

		try {
			downtimeDuration = Integer.parseInt(downtimeDurationTx.getText());
		}catch(NumberFormatException e) {
			error = "Downtime duration needs to be a number";
		}
		try {
			downtimeStart = Integer.parseInt(downtimeStartTx.getText());
		}catch(NumberFormatException e) {
			error = "Downtime start needs to be a number";
		}
	}

	public void refreshData() {
		errorMessage.setText(error);
		if(error == null || error.length() == 0) {
			//populate the page with data
			durationTx.setText("");
			downtimeDurationTx.setText("");
			downtimeStartTx.setText("");
			nameTx.setText("");
			//clear data
			//get all the services in the system
			availableServices = new ArrayList<>();
			serviceList.removeAllItems();
			for(TOService service : FlexibookController.getServices()) {
				availableServices.add(service.getName());
				serviceList.addItem(service.getName());
			}
			serviceList.setSelectedIndex(-1);
		}
	}
}
