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

public class LoginPage extends JFrame{
	private JButton loginButton;
	private JTextField usernameTextField;
	private JTextField passwordTextField;
	private JLabel usernameLabel;
	private JLabel passwordLabel;
	private JLabel errorMessage;

	private static final int  HEIGHT = 800;
	private static final int WIDTH = 800;

	//instance variables
	private String error = "";
	CustomerHomePage newPage;
	OwnerHomePage newPage1;
	

	//SAGHAR PAGE
	public LoginPage() {
		initComponents();
		refreshData();
	}

	public void initComponents() {
		//create components
		setBounds(100, 100, 1500, 1000);
		setTitle("REGISTRATION FORM");

		//error message
		errorMessage = new JLabel();
		errorMessage.setForeground(Color.RED);

		//username label
		usernameLabel = new JLabel();
		usernameLabel.setText("Username");

		//username text input
		usernameTextField = new JTextField();

		//password label 
		passwordLabel = new JLabel();
		passwordLabel.setText("Password");

		//password text input
		passwordTextField = new JTextField();

		//register button
		loginButton = new JButton();
		loginButton.setText("Login");
		loginButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				loginActionButtonPerformed(e);
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
						.addComponent(usernameTextField)
						.addComponent(passwordLabel)
						.addComponent(passwordTextField)
						.addComponent(loginButton))
				.addGroup(layout.createParallelGroup()
						.addGap(600,600,600))
				);		

		layout.setVerticalGroup(

				layout.createSequentialGroup()
				.addGap(300,300,300)
				.addComponent(errorMessage)
				.addComponent(usernameLabel)
				.addComponent(usernameTextField)
				.addComponent(passwordLabel)
				.addComponent(passwordTextField)
				.addComponent(loginButton)
				.addGap(300,300,300)
				);
		pack();

	}

	//ACTION TO REGISTER NEW CUSTOMER AND OPEN UP NEW FRAME WHEN SUCCESSFUL
	private void loginActionButtonPerformed(ActionEvent evt) {
		error = "";
		String username = usernameTextField.getText();
		String password = passwordTextField.getText();
		try {
			FlexibookController.loginUser(username, password);
		}catch(InvalidInputException e) {
			error += e.getMessage();
		}	
		if(error.length() == 0) {
			//check if the current user is an instance of Customer or Owner
			if(username.toLowerCase().equals("owner")) {
				dispose();
				newPage1 = new OwnerHomePage();
				newPage1.setVisible(true);
			}else {
			dispose();
			newPage = new CustomerHomePage();
			newPage.setVisible(true);
			}
		}
		refreshData();
	}

	private void refreshData() {
		errorMessage.setText(error);
		if(error.length() == 0 || error == null) {
			//reset input fields
			usernameTextField.setText("");
			passwordTextField.setText("");
		}
	}
}
