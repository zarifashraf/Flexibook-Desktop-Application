package ca.mcgill.ecse.flexibook.view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FlexibookPage extends JFrame{
	private static final long serialVersionUID = 1L;

	//components
	private JButton register;
	private JButton login;

	//dimension of screen
	private static final int  HEIGHT = 800;
	private static final int WIDTH = 800;

	//NAVIGATION SCREENS
	private RegistrationPage registrationPage;
	private LoginPage loginPage;

	public FlexibookPage() {
		initComponents();

	}

	public void initComponents() {
		setBounds(100, 100, 1500, 1000);
		setTitle("FLEXIBOOK APPLICATION");
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		register = new JButton("REGISTER");
		login = new JButton("LOGIN");
		
		register.setBounds(725, 50, 95, 30);
		login.setBounds(725, 80, 95, 30);
		
		//layout
		Container contentPanel = this.getContentPane();
		GroupLayout groupLayout = new GroupLayout(contentPanel);
		contentPanel.setLayout(groupLayout);
		groupLayout.setAutoCreateGaps(true);
		groupLayout.setAutoCreateContainerGaps(true);


		register.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				registerButtonActionPerformed(e);
			}
		});
		
		login.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				loginButtonActionPerformed(e);
			}

		});
		
		addAllComponentsToFrame();
	}


	//EVENT HANDLER FOR REGISTRATION BUTTON
	private void registerButtonActionPerformed(ActionEvent e) {
		dispose();
		//if you want to change the page that opens up after clicking register button, change this 
		registrationPage = new RegistrationPage();
		registrationPage.setVisible(true);

	}

	//EVENT HANDLER FOR LOGIN BUTTON
	private void loginButtonActionPerformed(ActionEvent e) {
		dispose();
		loginPage = new LoginPage();
		loginPage.setVisible(true);
		// TODO Auto-generated method stub
		
	}

	public void addAllComponentsToFrame() {
		this.add(register);
		this.add(login);
	}
}
