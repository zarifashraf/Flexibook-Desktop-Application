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

import ca.mcgill.ecse.flexibook.application.FlexiBookApplication;
import ca.mcgill.ecse.flexibook.controller.FlexibookController;
import ca.mcgill.ecse.flexibook.controller.InvalidInputException;
import ca.mcgill.ecse.flexibook.controller.TOBusinessHour;

/**
 * @author zarifashraf
 */

public class BusinessInfoPage extends JFrame{
	
	private static final long serialVersionUID = 1L;

	private JLabel errorMessageLabel;
	private JLabel successMessageLabel;
	private JLabel businessNameLabel;
	private JLabel businessAddressLabel;
	private JLabel businessEmailLabel;
	private JLabel businessPhoneLabel;
	private JLabel oldDayLabel;
	private JLabel newDayLabel;
	private JLabel oldStartTimeLabel;
	private JLabel newStartTimeLabel;
	private JLabel newEndTimeLabel;
	
	private JTextField nameText;
	private JTextField emailText;
	private JTextField addressText;
	private JTextField phoneText;
	private JTextField newStartTimeText;
	private JTextField newEndTimeText;
	
	
	private JButton setUpInfoButton;
	private JButton updateInfoButton;
	private JButton addHourButton;
	private JButton updateHourButton;
	private JButton deleteHourButton;
	
	private JComboBox<String> oldDaysList;
	private JComboBox<String> oldStartTimeList;
	private JComboBox<String> daysList;
	
	private String error = "";
	private String success = "";
	private String username;
	
	private String businessName;
	private String businessAddress;
	private String businessEmail;
	private String businessPhone;
	private String oldDay;
	private String newDay;
	private String oldStartHour;
	private String newStartHour;
	private String newEndHour;

	
	private List<String> days;
	private List<String> daysEnlisted;
	private List<String> startTimesEnlisted;
	
	
	/**
	 * @author zarifashraf
	 */
	public BusinessInfoPage() {
		initComponents();
		refreshData();
	}
	
	/**
	 * @author zarifashraf
	 */
	public void initComponents() {
		setBounds(100, 100, 1500, 1000);
		setTitle("BUSINESS INFO PAGE");
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		errorMessageLabel = new JLabel();
		errorMessageLabel.setForeground(Color.RED);
		
		successMessageLabel = new JLabel();
		successMessageLabel.setForeground(Color.MAGENTA);
		
		businessNameLabel = new JLabel();
		businessNameLabel.setText("BusinessName");
		nameText = new JTextField();
		
		businessAddressLabel = new JLabel();
		businessAddressLabel.setText("BusinessAddress");
		addressText = new JTextField();
		
		businessEmailLabel = new JLabel();
		businessEmailLabel.setText("BusinessEmail");
		emailText = new JTextField();
		
		businessPhoneLabel = new JLabel();
		businessPhoneLabel.setText("BusinessPhone");
		phoneText = new JTextField();
		
		oldDayLabel = new JLabel();
		oldDayLabel.setForeground(Color.RED);
		oldDayLabel.setText("Select DayOfWeek to be updated/deleted");
		oldDaysList = new JComboBox<String>();
		
		oldStartTimeLabel = new JLabel();
		oldStartTimeLabel.setForeground(Color.RED);
		oldStartTimeLabel.setText("Select Opening Hour to be updated/deleted (HH:mm:ss)");
		oldStartTimeList = new JComboBox<String>();
		
		newStartTimeLabel = new JLabel();
		newStartTimeLabel.setText(" New OpeningHour (HH:mm)");
		newStartTimeText = new JTextField();
		
		newEndTimeLabel = new JLabel();
		newEndTimeLabel.setText("New ClosingHour (HH:mm)");
		newEndTimeText = new JTextField();
		
		daysList = new JComboBox<String>();
		newDayLabel = new JLabel();
		newDayLabel.setText("Select DayOfWeek");
		
		addHourButton = new JButton();
		addHourButton.setText("Add Hour");
		addHourButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				addBusinessHourActionPerformed(e);
			}
		});
		
		updateHourButton = new JButton();
		updateHourButton.setText("Update Hour");
		updateHourButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateBusinessHourActionPerformed(e);
			}
		});
		
		deleteHourButton = new JButton();
		deleteHourButton.setText("Delete Hour");
		deleteHourButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteBusinessHourActionPerformed(e);
			}
		});
		
		setUpInfoButton = new JButton();
		setUpInfoButton.setText("SetUpInfo");
		setUpInfoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setUpBusinessInfoActionPerformed(e);
			}
		});
	
		updateInfoButton = new JButton();
		updateInfoButton.setText("UpdateInfo");
		updateInfoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateBusinessInfoActionPerformed(e);
			}
		});
	
		// horizontal line elements
		JSeparator horizontalLineMiddle = new JSeparator();
		
		//layout
		Container contentPanel = this.getContentPane();
		GroupLayout layout = new GroupLayout(contentPanel);
		contentPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);

		layout.setHorizontalGroup(
				layout.createParallelGroup()
				.addComponent(horizontalLineMiddle)
				.addComponent(errorMessageLabel)
				.addGap(10)
				.addComponent(successMessageLabel)
				.addGap(20)
				.addGroup(layout.createSequentialGroup()
						.addComponent(businessNameLabel)
						.addComponent(nameText, 200, 200, 400)
						.addComponent(businessAddressLabel)
						.addComponent(addressText, 200, 200, 400)
						.addComponent(businessPhoneLabel)
						.addComponent(phoneText, 200, 200, 400)
						.addComponent(businessEmailLabel)
						.addComponent(emailText, 200, 200, 400)
						.addComponent(setUpInfoButton)
						.addComponent(updateInfoButton))
				.addGroup(layout.createSequentialGroup()
						.addComponent(newDayLabel)
						.addComponent(daysList)
						.addComponent(newStartTimeLabel)
						.addComponent(newStartTimeText, 200, 200, 400)
						.addComponent(newEndTimeLabel)
						.addComponent(newEndTimeText, 200, 200, 400)
						.addComponent(addHourButton))
				.addGroup(layout.createSequentialGroup()
						.addComponent(oldDayLabel)
						.addComponent(oldDaysList, 200, 200, 400)
						.addComponent(oldStartTimeLabel)
						.addComponent(oldStartTimeList, 200, 200, 400))
				.addGroup(layout.createSequentialGroup()
						.addComponent(updateHourButton)
						.addComponent(deleteHourButton))
						);

		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addComponent(errorMessageLabel)
				.addComponent(successMessageLabel)
				.addGroup(layout.createParallelGroup()
						.addComponent(errorMessageLabel)
						.addComponent(successMessageLabel)
						.addComponent(businessNameLabel)
						.addComponent(nameText)
						.addComponent(businessAddressLabel)
						.addComponent(addressText)
						.addComponent(businessPhoneLabel)
						.addComponent(phoneText)
						.addComponent(businessEmailLabel)
						.addComponent(emailText)
						.addComponent(setUpInfoButton)
						.addComponent(updateInfoButton))
				.addGroup(layout.createParallelGroup()
						.addComponent(horizontalLineMiddle)
						.addGap(20))
				.addGroup(layout.createParallelGroup()
						.addComponent(newDayLabel)
						.addComponent(daysList)
						.addComponent(newStartTimeLabel)
						.addComponent(newStartTimeText)
						.addComponent(newEndTimeLabel)
						.addComponent(newEndTimeText)
						.addComponent(addHourButton))
				.addGroup(layout.createParallelGroup()
						.addComponent(oldDayLabel)
						.addComponent(oldDaysList)
						.addComponent(oldStartTimeLabel)
						.addComponent(oldStartTimeList))
				.addGroup(layout.createParallelGroup()
						.addGap(100,200,200)
						.addComponent(updateHourButton)
						.addComponent(deleteHourButton))
						
				
				);

		pack();
	}	//init method ends
	
	/**
	 * @author zarifashraf
	 * @param event
	 */
	private void addBusinessHourActionPerformed(ActionEvent event) {
		error = "";
		success = "";
		
		username = FlexiBookApplication.getCurrentUserName();
		newStartHour = newStartTimeText.getText();
		newEndHour = newEndTimeText.getText();
		
		int selectedIndex3 = daysList.getSelectedIndex();
		
		if (selectedIndex3 < 0) {
			error += "You need to pick a new DayOfWeek!";
		}
		else if ((newStartHour.length() != 5) || (!(newStartHour.matches("([01][0-9]|2[0-3]):[0-5][0-9]")))) {
			error += "Please input an opening hour of the correct format. Opening hours can range from 00:00 to 23:59";
		}
		else if ((newEndHour.length() != 5) || (!(newEndHour.matches("([01][0-9]|2[0-3]):[0-5][0-9]")))) {
			error += "Please input a closing hour of the correct format. Closing hours can range from 00:00 to 23:59";
		}
		
		if(error.length() == 0) {
			newDay = days.get(selectedIndex3);
			try {
				FlexibookController.addBusinessHours(username, newDay, newStartHour, newEndHour); 
			}catch(InvalidInputException e1) {
				error += e1.getMessage();
			}
		}
		if (error.length() == 0) {
			success = "Business Hour has been successfully added.";
		}
		refreshData();
	}
	
	/**
	 * @author zarifashraf
	 * @param event
	 */
	private void updateBusinessHourActionPerformed(ActionEvent event) {
		error = "";
		success = "";
		username = FlexiBookApplication.getCurrentUserName();
		
		
		int selectedIndex = oldDaysList.getSelectedIndex();
		int selectedIndex2 = oldStartTimeList.getSelectedIndex();
		int selectedIndex3 = daysList.getSelectedIndex();
		newStartHour = newStartTimeText.getText();
		newEndHour = newEndTimeText.getText();
		
		if(selectedIndex < 0) {
			error += "You need to pick an enlisted DayOfWeek to update!";
		}
		else if(selectedIndex2 < 0) {
			error += "You need to pick an enlisted opening hour to update!";
		}
		else if(selectedIndex != selectedIndex2) {
			error += "You need to pick corresponding DayOfWeek and opening hour to update successfully!";
		}
		else if (selectedIndex3 < 0) {
			error += "You need to pick a new DayOfWeek!";
		}
		else if ((newStartHour.length() != 5) || (!(newStartHour.matches("([01][0-9]|2[0-3]):[0-5][0-9]")))) {
			error += "Please input an opening hour of the correct format. Opening hours can range from 00:00 to 23:59";
		}
		else if ((newEndHour.length() != 5) || (!(newEndHour.matches("([01][0-9]|2[0-3]):[0-5][0-9]")))) {
			error += "Please input a closing hour of the correct format. Closing hours can range from 00:00 to 23:59";
		}


		if(error.length() == 0) {
			oldDay = daysEnlisted.get(selectedIndex);
			oldStartHour = startTimesEnlisted.get(selectedIndex2);
			newDay = days.get(selectedIndex3);
			try {
				FlexibookController.updateBusinessHours(username, oldDay, newDay, oldStartHour, newStartHour, newEndHour);
			}catch(InvalidInputException e) {
				error += e.getMessage();
			}
		}
		if (error.length() == 0) {
			success = "Business Hour has been successfully updated.";
		}
	refreshData();
	}
	
	/**
	 * @author zarifashraf
	 * @param event
	 */
	private void deleteBusinessHourActionPerformed(ActionEvent event) {
		error = "";
		success = "";
		username = FlexiBookApplication.getCurrentUserName();
		
		
		int selectedIndex = oldDaysList.getSelectedIndex();
		if(selectedIndex < 0) {
			error += "You need to pick an enlisted DayOfWeek to delete!";
		}
		int selectedIndex2 = oldStartTimeList.getSelectedIndex();
		if(selectedIndex2 < 0) {
			error += "You need to pick an enlisted opening hour to delete!";
		}
		else if(selectedIndex != selectedIndex2) {
			error += "You need to pick corresponding DayOfWeek and opening hour to delete successfully!";
		}

		if(error.length() == 0) {
			oldDay = daysEnlisted.get(selectedIndex);
			oldStartHour = startTimesEnlisted.get(selectedIndex2);
		try {
			FlexibookController.deleteBusinessHours(username, oldDay, oldStartHour);
		}
		catch(InvalidInputException e) {
			error += e.getMessage();
		}
	}
		if (error.length() == 0) {
			success = "Business Hour has been successfully deleted.";
		}
	
	refreshData();
	}
	
	/**
	 * @author zarifashraf
	 * @param event
	 */
	private void setUpBusinessInfoActionPerformed(ActionEvent event) {
			error = "";
			success = "";
			username = FlexiBookApplication.getCurrentUserName();
			
			if (nameText.getText().length() == 0) {
				error += "Business name cannot be empty.";
			}
			
			else if (addressText.getText().length() == 0) {
				error += "Business address cannot be empty.";
			}
			
			else if (emailText.getText().length() == 0) {
				error += "Business email cannot be empty.";
			}
			
			else if (phoneText.getText().length() == 0) {
				error += "Business phone cannot be empty.";
			}
			
			else if (!(phoneText.getText().matches("^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$"))) {
				error += "Business phone should have 10 digits formatted: (xxx)xxx-xxxx";
			}
			
			if(error.length() == 0) {
				businessName = nameText.getText();
				businessAddress = addressText.getText();
				businessEmail = emailText.getText();
				businessPhone = phoneText.getText();
				try {
					FlexibookController.setUpBusinessInfo(username, businessName, businessAddress, businessPhone, businessEmail);;		//change owner 
				}catch(InvalidInputException e1) {
					error += e1.getMessage();
				}
			}
			if(error.length() == 0) {
				success = "BusinessInfo has been successfully set up.";
			}
			
			refreshData();
		}
		
	/**
	 * @author zarifashraf
	 * @param event
	 */
		private void updateBusinessInfoActionPerformed(ActionEvent event) {
			error = "";
			success = "";
			username = FlexiBookApplication.getCurrentUserName();
			
			if (nameText.getText().length() == 0) {
				error += "Business name cannot be empty.";
			}
			
			else if (addressText.getText().length() == 0) {
				error += "Business address cannot be empty.";
			}
			
			else if (emailText.getText().length() == 0) {
				error += "Business email cannot be empty.";
			}
			
			else if (phoneText.getText().length() == 0) {
				error += "Business phone cannot be empty.";
			}
			
			else if (!(phoneText.getText().matches("^\\(?(\\d{3})\\)?[- ]?(\\d{3})[- ]?(\\d{4})$"))) {
				error += "Business phone should have 10 digits formatted: (xxx)xxx-xxxx";
			}
			
			if(error.length() == 0) {
				businessName = nameText.getText();
				businessAddress = addressText.getText();
				businessEmail = emailText.getText();
				businessPhone = phoneText.getText();
				try {
					FlexibookController.updateBusinessInfo(username, businessName, businessAddress , businessPhone, businessEmail);
				}
				catch(InvalidInputException e) {
					error += e.getMessage();
				}
			}
			if (error.length() == 0) {
				success = "BusinessInfo has been successfully updated.";
			}
			refreshData();
		}
		
		/**
		 * @author zarifashraf
		 */
		public void refreshData() {
	
			errorMessageLabel.setText(error);
			successMessageLabel.setText(success);
			if(error == null || error.length() == 0) {
				
				nameText.setText("");
				emailText.setText("");
				addressText.setText("");
				phoneText.setText("");
				newStartTimeText.setText("");
				newEndTimeText.setText("");
				
				// old Days List and old StartTime List refresh and update
				daysEnlisted = new ArrayList<String>();
				startTimesEnlisted = new ArrayList<String>();
				oldDaysList.removeAllItems();
				oldStartTimeList.removeAllItems();
				
				for (TOBusinessHour businesshour : FlexibookController.getBusinessHourList()) {
					daysEnlisted.add(businesshour.getDay());
					oldDaysList.addItem(businesshour.getDay());
					startTimesEnlisted.add(businesshour.getStartTime().toString());
					oldStartTimeList.addItem(businesshour.getStartTime().toString());
				};
				

				oldDaysList.setSelectedIndex(-1);
				oldStartTimeList.setSelectedIndex(-1);
				
				// new Days List values 
				days = new ArrayList<String>();
				daysList.removeAllItems();
				for (String day : FlexibookController.getDayOfWeekValues()) {
				days.add(day);
				daysList.addItem(day);
			};
		
		} // if ends
		
	} // refresh Data ends

}	//BusinessInfoPage ends
	

