package ca.mcgill.ecse.flexibook.view;

import java.awt.Color;
import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

import ca.mcgill.ecse.flexibook.controller.FlexibookController;
import ca.mcgill.ecse.flexibook.controller.InvalidInputException;

public class AccountSettingsPage extends JFrame{
	private JLabel errorMessage;
	private JLabel usernameLabel;
	private JLabel passwordLabel;
	private JLabel updateinformation;
	private JLabel deletAccount;


	private JTextField usernameTxtF;
	private JTextField passwordTxtF;

	private JButton updateButton;
	private JButton deleteButton;

	private static final int  HEIGHT = 800;
	private static final int WIDTH = 800;

	//instance variables
	private String error = "";


	public AccountSettingsPage() {
		initComponents();
		refreshData();

	}
	public void initComponents() {
		//create components
		//setBounds(100, 100, 1500 ,1000);

		//error message 
		errorMessage = new JLabel();
		errorMessage.setForeground(Color.RED);

		//username label 
		usernameLabel = new JLabel();
		usernameLabel.setText("Username");

		//password label 
		passwordLabel = new JLabel();
		passwordLabel.setText("Password");

		//username text input
		usernameTxtF = new JTextField();

		//password text input
		passwordTxtF = new JTextField();

		//update button 
		updateButton = new JButton();
		updateButton.setText("Update Information");
		updateButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				updateActionButtonPerformed(e);
			}
		});

		//delete button 
		deleteButton = new JButton();
		deleteButton.setText("Delete account");
		deleteButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				deleteActionButtonPerformed(e);
			}
		});

		Container contentPanel = this.getContentPane();
		GroupLayout layout = new GroupLayout(contentPanel);
		contentPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);


		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup()
						.addGap(600,600,600))
				.addGroup(
						layout.createParallelGroup()
						.addComponent(errorMessage)
						.addComponent(usernameLabel)
						.addComponent(usernameTxtF)
						.addComponent(passwordLabel)
						.addComponent(passwordTxtF)
						.addComponent(updateButton)
						.addComponent(deleteButton))
				.addGroup(layout.createParallelGroup()
						.addGap(600,600,600))
				);		

		layout.setVerticalGroup(

				layout.createSequentialGroup()
				.addGap(300,300,300)
				.addComponent(errorMessage)
				.addComponent(usernameLabel)
				.addComponent(usernameTxtF)
				.addComponent(passwordLabel)
				.addComponent(passwordTxtF)
				.addComponent(updateButton)
				.addComponent(deleteButton)
				.addGap(300,300,300)
				);
		pack();
	}
	private void updateActionButtonPerformed(ActionEvent evt) {
		error ="";
		String newUsername = usernameTxtF.getText();
		String newPassword = passwordTxtF.getText();

		//call controller
		try {
			FlexibookController.UpdateUser(newUsername, newPassword);
		}catch(InvalidInputException e) {
			error += e.getMessage();
		}	
		if(error.length() == 0) {
			dispose();	
		}
		refreshData();
	}

	private void deleteActionButtonPerformed(ActionEvent evt) {
		error ="";
		String User = usernameTxtF.getText();

		try {
			FlexibookController.deleteUser(User);
		}catch(InvalidInputException e) {
			error += e.getMessage();
		}	
		if(error.length() == 0) {
			dispose();	
		}
		refreshData();

	}


	private void refreshData() {
		errorMessage.setText(error);
		if(error.length() == 0 || error == null) {
			//reset input fields
			usernameTxtF.setText("");
			passwordTxtF.setText("");
		}
	}

}
