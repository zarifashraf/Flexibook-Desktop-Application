package ca.mcgill.ecse.flexibook.view;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.sql.Time;
import java.util.List;

import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;

import ca.mcgill.ecse.flexibook.application.FlexiBookApplication;
import ca.mcgill.ecse.flexibook.controller.FlexibookController;
import ca.mcgill.ecse.flexibook.controller.InvalidInputException;
//update, cancel or make an appointment
public class ManageBookingsPage extends JFrame{
	//pages dimensions
	private static final int  HEIGHT = 800;
	private static final int WIDTH = 800;

	


	//jBouttons
	private JButton makeApptBtn;
	private JButton updateOrdeleteApptBtn;

	//
	MakeAppointmentPage newPage1;
	UpdateAppointmentPage newPage2;
	
	
	public ManageBookingsPage() {
		initComponents();
		refreshData();
	}
	
	public void initComponents() {
		//make a new appointment
		makeApptBtn = new JButton();
		makeApptBtn.setText("Make a New Appointment");
		makeApptBtn.addActionListener(new ActionListener()
		
		{
			@Override
			public void actionPerformed(ActionEvent e) {
				makeAppointmentActionButtonPerformed(e);
			}
		});
		
		updateOrdeleteApptBtn = new JButton();
		updateOrdeleteApptBtn.setText("Manage my Appointments");
		updateOrdeleteApptBtn.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				updateAppointmentActionButtonPerformed(e);
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
						
						.addComponent(updateOrdeleteApptBtn)
						.addComponent(makeApptBtn))
						
				.addGroup(layout.createParallelGroup()
						.addGap(600,600,600))
				);		

		layout.setVerticalGroup(

				layout.createSequentialGroup()
				.addGap(300,300,300)
				.addComponent(updateOrdeleteApptBtn)
				.addComponent(makeApptBtn)

				.addGap(300,300,300)
				);
		pack();
		
		
		
	}
	//takes you to make appointment page
	private void makeAppointmentActionButtonPerformed(ActionEvent evt) {
	

	
			dispose();
			newPage1 = new MakeAppointmentPage();
			newPage1.setVisible(true);
			
		
		//refreshData();
	}
	////takes you to update or delete appointment page
private void updateAppointmentActionButtonPerformed(ActionEvent evt) {
	
	
			dispose();
			newPage2 = new UpdateAppointmentPage();
			newPage2.setVisible(true);
			
		
		//refreshData();
	}
	
	
	public void refreshData() {
		
	}
}