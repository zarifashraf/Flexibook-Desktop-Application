package ca.mcgill.ecse.flexibook.controller;
import ca.mcgill.ecse.flexibook.application.FlexiBookApplication;
import ca.mcgill.ecse.flexibook.model.*;
import ca.mcgill.ecse.flexibook.model.BusinessHour.DayOfWeek;
import ca.mcgill.ecse.flexibook.persisitence.FlexiBookPersistence;

import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
public class FlexibookController {
	public static int numOfCustomers = 0;
	//should contain all controller methods
	/**
	 * @author saikouceesay
	 * Empty constructor of the FlexibookController
	 */
	public FlexibookController() {
	}
	/**
	 * @author saikouceesay
	 * @param ownerUsername
	 * @param name
	 * @param duration
	 * @param downtimeDuration
	 * @param downtimeStart
	 * @throws InvalidInputException
	 * adds a new service into the system
	 */
	public static void createService(String ownerUsername, String name, int duration, int downtimeDuration, int downtimeStart) throws InvalidInputException {
		String error = "";
		FlexiBook flexiBook = FlexiBookApplication.getFlexibook();
		if (!flexiBook.getOwner().getUsername().equals(ownerUsername)) {        //verify that the user = owner
			error = "You are not authorized to perform this operation";
			throw new InvalidInputException(error);
		}
		//flexiBook.addBookableService(service);
		if (duration <= 0) {
			error = "Duration must be positive";
			throw new InvalidInputException(error);
		} else if (downtimeStart > 0 && downtimeDuration == 0) {
			error = "Downtime duration must be positive";
			throw new InvalidInputException(error);
		} else if (downtimeStart == 0 && downtimeDuration < 0) {
			error = "Downtime duration must be 0";
			throw new InvalidInputException(error);
		} else if (downtimeStart == 0 && duration > 0 && downtimeDuration > 0) {
			error = "Downtime must not start at the beginning of the service";
			throw new InvalidInputException(error);
		} else if (downtimeStart > duration) {
			error = "Downtime must not start after the end of the service";
			throw new InvalidInputException(error);
		} else if (downtimeStart > duration - downtimeDuration) {
			error = "Downtime must not end after the service";
			throw new InvalidInputException(error);
		} else if (downtimeStart < 0) {
			error = "Downtime must not start before the beginning of the service";
			throw new InvalidInputException(error);
		}
		try {
			Service service = new Service(name, flexiBook, duration, downtimeDuration, downtimeStart);
			FlexiBookPersistence.saveFlexiBook(flexiBook);
		} catch (RuntimeException e) {
			if (e.getMessage().equals("Cannot create due to duplicate name. See http://manual.umple.org?RE003ViolationofUniqueness.html")) {
				error = "Service " + name + " already exists";
			}
			throw new InvalidInputException(error);
		}
	}
	/**
	 * @author saikouceesay
	 * @param username
	 * @param nameOfService
	 * @throws InvalidInputException
	 * deletes a service from the system
	 */
	public static void deleteService(String username, String nameOfService) throws InvalidInputException {
		String error = "";
		FlexiBook flexiBook = FlexiBookApplication.getFlexibook();
		if (!flexiBook.getOwner().getUsername().equals(username)) {
			error = "You are not authorized to perform this operation";
			throw new InvalidInputException(error);
		}
		for (BookableService bookableService : flexiBook.getBookableServices()) {
			if (bookableService instanceof Service && bookableService.getName().equals(nameOfService)) {
				if (bookableService.getAppointments().size() != 0) {
					for(Appointment appointment : bookableService.getAppointments()) {
						//make sure appointment starts after current time
						if(appointment.getTimeSlot().getStartDate().after(FlexiBookApplication.getDate())) {
							error = "The service contains future appointments";
							throw new InvalidInputException(error);
						}
					}
				}
			}
		}

		List<BookableService> toDelete = new ArrayList<BookableService>();
		for(BookableService b : flexiBook.getBookableServices()) {
			if (b instanceof ServiceCombo && ((ServiceCombo) b).getMainService().getService().getName().equals(nameOfService)) {
				toDelete.add(b);
			}
		}

		for(BookableService b : toDelete) {
			b.delete();
		}
		try {
			BookableService.getWithName(nameOfService).delete();
			FlexiBookPersistence.saveFlexiBook(flexiBook);
		}catch(RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}
	}
	/**
	 * @author saikouceesay
	 * @param username
	 * @param oldName
	 * @param newName
	 * @param duration
	 * @param downtimeDuration
	 * @param downtimeStart
	 * @throws InvalidInputException
	 * updates a service in the system
	 */
	public static void updateService(String username, String oldName, String newName, int duration, int downtimeDuration, int downtimeStart) throws InvalidInputException {
		FlexiBook flexiBook = FlexiBookApplication.getFlexibook();
		String error = "";
		if (!flexiBook.getOwner().getUsername().equals(username)) {
			error = "You are not authorized to perform this operation";
			throw new InvalidInputException(error);
		} else if (duration <= 0) {
			error = "Duration must be positive";
			throw new InvalidInputException(error);
		} else if (downtimeStart > 0 && downtimeDuration == 0) {
			error = "Downtime duration must be positive";
			throw new InvalidInputException(error);
		} else if (downtimeStart == 0 && downtimeDuration < 0) {
			error = "Downtime duration must be 0";
			throw new InvalidInputException(error);
		} else if (downtimeDuration > 0 && downtimeStart == 0) {
			error = "Downtime must not start at the beginning of the service";
			throw new InvalidInputException(error);
		} else if (downtimeStart > duration) {
			error = "Downtime must not start after the end of the service";
			throw new InvalidInputException(error);
		} else if (downtimeStart > duration - downtimeDuration) {
			error = "Downtime must not end after the service";
			throw new InvalidInputException(error);
		} else if (downtimeStart < 0 && downtimeDuration > 0) {
			error = "Downtime must not start before the beginning of the service";
			throw new InvalidInputException(error);
		}
		for (BookableService bookableService : flexiBook.getBookableServices()) {
			if (bookableService instanceof Service && bookableService.getName().contains(oldName)) {        //contains or equals
				Service updatedService = (Service) bookableService;
				if (!bookableService.setName(newName)) {
					error = "Service " + newName + " already exists";
					throw new InvalidInputException(error);
				}
				updatedService.setName(newName);
				updatedService.setDowntimeDuration(downtimeDuration);
				updatedService.setDowntimeStart(downtimeStart);
				updatedService.setDuration(duration);
				try {
					FlexiBookPersistence.saveFlexiBook(flexiBook);
				}catch(RuntimeException e) {
					throw new InvalidInputException(e.getMessage());
				}
			}
		}
	}
	/**
	 * @author Lakshmi Roy & zarifashraf
	 * @param username
	 * @param password
	 * @throws InvalidInputException
	 */
	//TODO
	public static void loginUser(String username, String password) throws InvalidInputException {
		String error = "Username/password not found";
		FlexiBook flexiBook = FlexiBookApplication.getFlexibook();
		if(username.length() == 0 || password.length() == 0) {
			throw new InvalidInputException(error);
		}
		for (Customer customer : flexiBook.getCustomers()) {
			if (customer.getUsername().equals(username)) {
				if (customer.getPassword().equals(password)) {
					FlexiBookApplication.setCurrentUser(customer);
					return;
				} else {
					throw new InvalidInputException(error);
				}
			}
		}


		//first time log in as the owner
		if(flexiBook.getOwner() == null && username.equals("owner") && password.equals("owner")) {
			Owner owner = new Owner(username, password, flexiBook);
			flexiBook.setOwner(owner);
			FlexiBookApplication.setCurrentUser(flexiBook.getOwner());
		}else if (flexiBook.getOwner() != null) {
			if((flexiBook.getOwner().getUsername().equals(username)) && (flexiBook.getOwner().getPassword().equals(password))) {
				FlexiBookApplication.setCurrentUser(flexiBook.getOwner());
			}else {
				throw new InvalidInputException(error);
			}
		}else {
			throw new InvalidInputException(error);
		}


	}

	/**
	 * @param startDay
	 * @return
	 * @Lakshmi Roy
	 */
	public static List<TimeSlot> viewWeekCalendar(String username, String startofWeekDate)throws InvalidInputException{
		User user = User.getWithUsername(username);
		FlexiBook flexiBook = FlexiBookApplication.getFlexibook();
		Date date = FlexiBookApplication.convertToDate(startofWeekDate);
		FlexiBookApplication.setCurrentUser(user);
		Date weekDate = FlexiBookApplication.addSevenDays(date);
		List<TimeSlot> slots = new ArrayList<TimeSlot>();
		List<Appointment> appointments = new ArrayList<Appointment>();

		for(Appointment appointment : flexiBook.getAppointments()) {
			Date startDate = appointment.getTimeSlot().getStartDate();
			if(startDate.equals(date) || startDate.equals(weekDate)  || (startDate.after(date) && startDate.before(weekDate))) {
				appointments.add(appointment);
				slots.add(appointment.getTimeSlot());
			}
		}
		return slots;

	}

	public static List<TimeSlot> viewDayCalendar(String username, String day)throws InvalidInputException{
		User user = User.getWithUsername(username);
		FlexiBook flexiBook = FlexiBookApplication.getFlexibook();
		FlexiBookApplication.setCurrentUser(user);
		Date date = FlexiBookApplication.convertToDate(day);
		int d = Integer.parseInt(day.substring(8));
		int m = Integer.parseInt(day.substring(5, 7));
		if(m > 12 || d > 31) {
			throw new InvalidInputException(day + " is not a valid date");
		}	
		List<TimeSlot> slots = new ArrayList<TimeSlot>();
		List<Appointment> appointments = new ArrayList<Appointment>();
		for(Appointment appointment : flexiBook.getAppointments()) {
			Date startDate = appointment.getTimeSlot().getStartDate();
			if(startDate.equals(date)) {
				appointments.add(appointment);
				slots.add(appointment.getTimeSlot());
			}
		}
		return slots;
	}
	
	public static List<TOAppointment> viewWeekCalendar(String date){
		List<TimeSlot> slots = new ArrayList<TimeSlot>();
		List<TOAppointment> appointments = new ArrayList<TOAppointment>();
		
		try {
			slots = viewWeekCalendar(FlexiBookApplication.getCurrentUser().getUsername(), date);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(TimeSlot s : slots) {
			Date startDate = s.getStartDate();
			Date endDate = s.getEndDate();
			Time startTime = s.getStartTime();
			Time endTime = s.getEndTime();
			TOAppointment toAppointment = new TOAppointment(startDate, endDate, startTime, endTime);
			appointments.add(toAppointment);
		}
		return appointments;	
	}
	
	public static List<TOAppointment> viewDayCalendar(String date){
		List<TimeSlot> slots = new ArrayList<TimeSlot>();
		List<TOAppointment> appointments = new ArrayList<TOAppointment>();
		
		try {
			slots = viewDayCalendar(FlexiBookApplication.getCurrentUser().getUsername(), date);
		} catch (InvalidInputException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		for(TimeSlot s : slots) {
			Date startDate = s.getStartDate();
			Date endDate = s.getEndDate();
			Time startTime = s.getStartTime();
			Time endTime = s.getEndTime();
			TOAppointment toAppointment = new TOAppointment(startDate, endDate, startTime, endTime);
			appointments.add(toAppointment);
		}
		return appointments;	
	}
	/**
	 * @throws InvalidInputException
	 * @author Lakshmi Roy
	 */
	public static void logoutUser() throws InvalidInputException {
		FlexiBook flexiBook = FlexiBookApplication.getFlexibook();
		User user = FlexiBookApplication.getCurrentUser();
		if (user == null) {
			throw new InvalidInputException("The user is already logged out");
		} else {
			FlexiBookApplication.setCurrentUser(null);
			try {
				FlexiBookPersistence.saveFlexiBook(flexiBook);
			}catch(RuntimeException e) {
				throw new InvalidInputException(e.getMessage());
			}
		}
	}
	/**
	 * @author Lakshmi Roy
	 * @throws InvalidInputException
	 */
	//Query methods begin
	/**
	 * @author saikouceesay
	 * get a list of all services in flexibook
	 */
	public static List<TOService> getServices() {
		ArrayList<TOService> services = new ArrayList<TOService>();
		for (BookableService bookableService : FlexiBookApplication.getFlexibook().getBookableServices()) {
			if (bookableService instanceof Service) {
				String nameOfService = bookableService.getName();
				int duration = ((Service) bookableService).getDuration();
				int downtimeDuration = ((Service) bookableService).getDowntimeDuration();
				int downtimeStart = ((Service) bookableService).getDowntimeStart();
				TOService toService = new TOService(nameOfService, duration, downtimeDuration, downtimeStart);
				services.add(toService);
			}
		}
		return services;
	}

	/**
	 * @author saikouceesay
	 */

	public static List<TOAppointment> getAppointments() {
		List<TOAppointment> appointments = new ArrayList<>();
		for(Appointment appointment : FlexiBookApplication.getFlexibook().getAppointments()) {
			Date startDate = appointment.getTimeSlot().getStartDate();
			Date endDate = appointment.getTimeSlot().getEndDate();
			Time startTime = appointment.getTimeSlot().getStartTime();
			Time endTime = appointment.getTimeSlot().getEndTime();
			TOAppointment toAppointment = new TOAppointment(startDate, endDate, startTime, endTime);
			appointments.add(toAppointment);
		}
		return appointments;
	}

	/**
	 * @author saikouceesay
	 */

	public static List<TOAppointment> getAppointmentsOfCurrentUser(String username){
		Customer customer = (Customer) User.getWithUsername(username);
		List<TOAppointment> appointments = new ArrayList<>();
		for(Appointment appointment : customer.getAppointments()) {
			Date startDate = appointment.getTimeSlot().getStartDate();
			Date endDate = appointment.getTimeSlot().getEndDate();
			Time startTime = appointment.getTimeSlot().getStartTime();
			Time endTime = appointment.getTimeSlot().getEndTime();
			TOAppointment toAppointment = new TOAppointment(startDate, endDate, startTime, endTime);
			appointments.add(toAppointment);
		}
		return appointments;
	}
	/**
	 * @return Owner of the flexibook
	 * @author saikouceesay
	 */
	public static TOOwner getOwner() {
		Owner owner = FlexiBookApplication.getFlexibook().getOwner();
		TOOwner toOwner = new TOOwner(owner.getUsername(), owner.getPassword());
		return toOwner;
	}
	/**
	 * @return all the customers in the system
	 * @author saikouceesay
	 */
	public static List<TOCustomer> getCustomers() {
		ArrayList<TOCustomer> customers = new ArrayList<TOCustomer>();
		for (Customer customer : FlexiBookApplication.getFlexibook().getCustomers()) {
			String username = customer.getUsername();
			String password = customer.getPassword();
			TOCustomer toCustomer = new TOCustomer(username, password);
			customers.add(toCustomer);
		}
		return customers;
	}
	/**
	 * @author EloyannRJ
	 */
	public static void makeAppointment(String customerName, String nameOfService, Date startDate, Time startTime, List<String> optionalServices) throws InvalidInputException {
		String error = "";
		FlexiBook flexiBook = FlexiBookApplication.getFlexibook();
		User user = User.getWithUsername(customerName);	
		//could be a customer or the owner
		if(user instanceof Owner) {
			error = "An owner cannot make an appointment";
			throw new InvalidInputException(error);
		}
		BookableService bookableService = BookableService.getWithName(nameOfService);		//could be service or service combo
		for(Appointment appointment: flexiBook.getAppointments()) {
			if(appointment.getTimeSlot().getStartTime().equals(startTime) && appointment.getTimeSlot().getStartDate().equals(startDate) ) {				//if existing appointment
				error = "There are no available slots for " + bookableService.getName() + " on " + startDate + " at " + startTime.getHours()+":"+startTime.getMinutes()+"0";
				throw new InvalidInputException(error);
			}
			else if(startDate.before(FlexiBookApplication.getDate())) {						//if start date of appoinment is in the past
				error = "There are no available slots for " + bookableService.getName() + " on " + startDate + " at " + startTime.getHours()+":"+startTime.getMinutes()+"0";
				throw new InvalidInputException(error);
			}
		}
		/*for(BusinessHour openedHours : flexiBook.getHours() ) {
			if (!(flexiBook.getHours().contains(openedHours.getStartTime().equals(startTime)))){
				error = "There are no available slots for " + bookableService.getName() + " on " + startDate + " at " + startTime.getHours()+":"+startTime.getMinutes()+"0";
				throw new InvalidInputException(error);
			}
			break;
		}*/
		List<Service> optionalServiceList = new ArrayList<>();
		if (optionalServices != null) {
		for(String name : optionalServices) {
			Service service = (Service) BookableService.getWithName(name);
			optionalServiceList.add(service);
		}
		}
		try {
			Time endTime = startTime;							//need to add the duration???
			TimeSlot timeSlot = new TimeSlot(startDate, startTime, startDate, endTime, flexiBook);	
			Appointment appointment;
			if(bookableService instanceof Service) {
				appointment = new Appointment((Customer) user, (Service) bookableService,timeSlot, flexiBook);
			}else {
				ServiceCombo serviceCombo = (ServiceCombo) bookableService;
				ComboItem comboItem;
				for(Service service : optionalServiceList) {
					comboItem = new ComboItem(false, service, serviceCombo);
					serviceCombo.addService(comboItem);
				}
				appointment = new Appointment((Customer) user, serviceCombo, timeSlot, flexiBook);
				FlexiBookPersistence.saveFlexiBook(flexiBook);
			}
		}catch(RuntimeException e) {
			error = e.getMessage();
			throw new InvalidInputException(error);
		}
	}
	/**
	 * @author Eloyann
	 * @return 
	 */
	public static  void updateAppointment(String user, String username, String nameOfService, String action, String comboItem, Date date, Time time)  throws InvalidInputException{
		String result = "unsuccessful";
		String error = "";
		FlexiBook flexiBook = FlexiBookApplication.getFlexibook();
		if(FlexiBookApplication.getCurrentUser() instanceof Owner) {
			error = "Error: An owner cannot update a customer's appointment";
			throw new InvalidInputException(error);
		}
		else if (!(user.equals(username))) {
			error = "Error: A customer can only update their own appointments";
			throw new InvalidInputException(error);
		}
		//cannot update a past appointment
		if(date.before(FlexiBookApplication.getDate())) {
			error = "Error: new time is in the past";
			throw new InvalidInputException(error);
		}
		if(action.equals("")) {		//updating the time a service
			Service bookableService = (Service) BookableService.getWithName(nameOfService);
			Customer customer = (Customer) User.getWithUsername(username);
			//if new time is even during business hours
			for(BusinessHour openedHours : flexiBook.getHours() ) {
				if (!(flexiBook.getHours().contains(openedHours.getStartTime().equals(time)))){
					error = "Error: new time is not even during business hours";
					throw new InvalidInputException(error);
				}
			}
			for(Appointment allappointments : flexiBook.getAppointments()) {
				//checking if the new timeslot exits
				if(allappointments.getTimeSlot().getStartTime().equals(time) && allappointments.getTimeSlot().getStartDate().equals(date) ) {
					error = "Error: new time slot is already is taken";
					throw new InvalidInputException(error);
				}
			}
			for(Appointment appointment : customer.getAppointments()) {
				ServiceCombo bookService = (ServiceCombo) appointment.getBookableService();
				if(bookService.equals(bookableService)) {
					//removing previous time slot associated with that appointment
					TimeSlot removedTimeSLOT=appointment.getTimeSlot();
					flexiBook.removeTimeSlot(removedTimeSLOT);
					//updating time slot of that appointment
					TimeSlot timeSlot = new TimeSlot(date, time, appointment.getTimeSlot().getEndDate(), appointment.getTimeSlot().getEndTime(), flexiBook);
					appointment.setTimeSlot(timeSlot);
					result = "successful";
				}
			}
		}
		if(action.equals("remove")) {
			ServiceCombo serviceCombo = (ServiceCombo) BookableService.getWithName(nameOfService);
			ComboItem mainService = serviceCombo.getMainService();
			if(mainService.getService().getName().equals(comboItem)) {
				error = "Error: Cannot delete a main service of a Combo";
				throw new InvalidInputException(error);
			}
			else {
				ComboItem c = null;
				for(ComboItem coItem : serviceCombo.getServices()) {
					if(coItem.getService().getName().equals(comboItem));
					c = coItem;
				}
				if(c != null) {
					serviceCombo.removeService(c);
					result="succesful";
					//should i add a break statement 
				}
				if(c==null) {
					error = "Error: invalide service name input";
					throw new InvalidInputException(error);
				}
			}
		}
		if(action.equals("add")) {
			BookableService bookableService =  BookableService.getWithName(nameOfService);
			BookableService aChosenItem= BookableService.getWithName(comboItem);
			Customer customer = (Customer) User.getWithUsername(username);
			for(Appointment appointment : customer.getAppointments()) {
				BookableService bookService =  appointment.getBookableService();
				if(bookService.equals(bookableService)) {
					int timeadded=0;
					if(aChosenItem instanceof Service) {
						timeadded= ((Service) aChosenItem).getDuration();
					}
					if(aChosenItem instanceof ServiceCombo) {
						ServiceCombo aChosenCombo=(ServiceCombo) aChosenItem;    
						for (ComboItem aItem: aChosenCombo.getServices()) {
							timeadded+= aItem.getService().getDuration();
						}
					}
					long newendTime=(appointment.getTimeSlot().getEndTime().getTime())+timeadded*6000;	
					//if we have a time duration that requires change
					if(newendTime!=0) {
						Time updatendTime = FlexiBookApplication.getCurrentTime();
						updatendTime.setTime(newendTime);
						//new variable to test if new end time is possible
						//checking if the new timeSlot is available
						for (Appointment aappointment: flexiBook.getAppointments()) {
							if( aappointment.getTimeSlot().getEndTime().equals(updatendTime) && aappointment.getTimeSlot().getStartDate().equals(date) ) {
								//already existing appointment in that slot
								error="the time slot isn't available";
								throw new InvalidInputException(error);
							}
						}
						//update appointment
						//updating the time slot
						appointment.getTimeSlot().setEndTime(updatendTime);
					}
					//creating the combo item
					if(aChosenItem instanceof ServiceCombo) {
						for(ComboItem addedChosenItem:((ServiceCombo) BookableService.getWithName(comboItem)).getServices()) {
							appointment.addChosenItem(addedChosenItem);
						}
					}
					else {
						//adding the service to the appointment
						aChosenItem.addAppointment(appointment);
					}
				}
			}
		}
		try {
			FlexiBookPersistence.saveFlexiBook(flexiBook);
		}catch(RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}

	}
	/** @author eloyann
	 * 
	 * @param actionUser
	 * @param username
	 * @param nameOfService
	 * @param date
	 * @param time
	 * @throws InvalidInputException
	 */
	public static void cancelAppointment(String actionUser, String username, String nameOfService, Date date, Time time ) throws InvalidInputException {
		FlexiBook flexiBook = FlexiBookApplication.getFlexibook();
		User user = User.getWithUsername(username);
		User user2 = User.getWithUsername(actionUser);
		String error=" ";
		Date curDate = FlexiBookApplication.getDate();
		if(user2 instanceof Owner) {
			error = "An owner cannot cancel an appointment";
			throw new InvalidInputException(error);
		}
		else if (!(user.equals(user2))) {
			error = "A customer can only cancel their own appointments";
			throw new InvalidInputException(error);
		}
		Customer customer = (Customer) user;
		BookableService bookableService = BookableService.getWithName(nameOfService);
		List<Appointment> appointments = customer.getAppointments();
		Appointment toDelete = null;
		for(Appointment appointment : appointments) {
			if(appointment.getTimeSlot().getStartDate().equals(date)&&appointment.getTimeSlot().getStartTime().equals(time)) {
				if(!appointment.getTimeSlot().getStartDate().before(date)) {
					error = "Cannot cancel an appointment on the appointment date";
					throw new InvalidInputException(error);
				}else {
					toDelete = appointment;
					if(toDelete != null) {
						toDelete.cancel(curDate, date);		//this method has been updated
					}
				}
			}
			try {
				FlexiBookPersistence.saveFlexiBook(flexiBook);
			}catch(RuntimeException e) {
				throw new InvalidInputException(e.getMessage());
			}
		}
	}
	/**
	 * @author saikouceesay
	 * updates the time of the appointment
	 */
	public static void updateAppointmentTime(String username, String todate, String totime, String tocurDate) {
		Date upDate = FlexiBookApplication.convertToDate(todate);
		Date curDate = FlexiBookApplication.convertToDate(tocurDate);
		Time time = FlexiBookApplication.convertTotime(totime);
		User user = User.getWithUsername(username);
		Customer customer = (Customer) user;
		List<Appointment> appointments = customer.getAppointments();
		for(Appointment appointment : appointments) {
			if(appointment.getTimeSlot().getStartDate().equals(curDate)) {
				//appointment.updateAppointmentTime(upDate, time);
			}
		}
	}
	/**
	 * @author saikouceesay
	 * starts the appointment if the current user is the owner
	 */
	public static void startAppointment(String tdate) {
		if(FlexiBookApplication.getCurrentUser() instanceof Owner) {
			Date curDate = FlexiBookApplication.convertToDate(tdate);
			FlexiBook flexiBook = FlexiBookApplication.getFlexibook();
			for (Appointment appointment : flexiBook.getAppointments()) {
				if(appointment.getTimeSlot().getStartDate().equals(curDate)) {
					//appointment.startAppointment(curDate, appointment.getTimeSlot().getStartTime());
					return;
				}
			} 
		}
	}
	/**@author saghar
	 *
	 * @param date
	 */
	public static void EndAppointment(String date) {
		if(FlexiBookApplication.getCurrentUser() instanceof Owner) {
			Date curDate = FlexiBookApplication.convertToDate(date);
			FlexiBook flexiBook = FlexiBookApplication.getFlexibook();
			for (Appointment appointment : flexiBook.getAppointments()) {
				if(appointment.getTimeSlot().getEndDate().equals(curDate)) {
					//appointment.endAppointment();
					return;
				}
			}
		}
	}
	/** 
	 * @author Abrar Fahad Rahman Anik, code starts
	 */
	public static void defineServiceCombo(String ownerUsername, String name, String mainService, String allServices, String mandatorySetting) throws InvalidInputException {
		String error = "";
		FlexiBook flexiBook = FlexiBookApplication.getFlexibook();
		if (!flexiBook.getOwner().getUsername().equals(ownerUsername)) {		// if the user is the owner
			error = "You are not authorized to perform this operation";
			throw new InvalidInputException(error);
		}
		String[] settings = mandatorySetting.split(",");        // parse mandatory settings string
		List<Boolean> boolSettings = new ArrayList<Boolean>();  // convert to list of booleans
		for (String x : settings) {
			boolSettings.add(Boolean.parseBoolean(x));
		}
		String [] myServices = allServices.split(",");         // parse allServices into an array
		List<Service> listOfServices = new ArrayList<Service>();
		int numOfServices = myServices.length;
		if (numOfServices < 2) {							   // check for minimum no. of services
			error = "A service Combo must contain at least 2 services";
			throw new InvalidInputException(error);
		}
		boolean mainServiceFound = false;
		boolean mainServiceExists = false;
		for (int i = 0; i < numOfServices; i++) {              // check for main service included and
			if (myServices[i].equals(mainService)) {		   // check for main service is mandatory
				mainServiceFound = true;
				if (boolSettings.get(i).equals(false)) {
					error = "Main service must be mandatory";
					throw new InvalidInputException(error);
				}
			}
		}
		if (mainServiceFound != true) {
			error = "Main service must be included in the services";
			throw new InvalidInputException(error);
		}
		for (BookableService bookableService : flexiBook.getBookableServices()) {
			if (bookableService instanceof Service && bookableService.getName().equals(mainService)) {
				mainServiceExists = true;
			}
			if (bookableService instanceof ServiceCombo && bookableService.getName().contains(name)) {
				error = "Service combo "+name+" already exists";   //check for serviceCombo name exists
				throw new InvalidInputException(error); 
			}
		}
		if (mainServiceExists != true) {
			error = "Service " + mainService + " does not exist";  //check for main service exists
			throw new InvalidInputException(error);
		}
		boolean serviceExists = false;
		for (int i = 0; i < numOfServices; i++) {
			serviceExists = false;
			for (BookableService bookableService : flexiBook.getBookableServices()) {
				if (bookableService instanceof Service && bookableService.getName().equals(myServices[i])) {
					serviceExists = true;
					Service service = (Service) bookableService;
					listOfServices.add(service);      // fetching the services and adding them to list
				}
			}
			if (serviceExists == false) {
				error = "Service " + myServices[i] + " does not exist";  //check for service exists
			}
		}
		try {
			ServiceCombo myCombo = new ServiceCombo(name, flexiBook);
			int j = 0;
			for (Service s : listOfServices) {
				ComboItem c = new ComboItem(boolSettings.get(j), s, myCombo);
				//myCombo.addServiceAt(c, j);
				if (s.getName().equals(mainService)){
					myCombo.setMainService(c);
				}
				j++;
			}
			FlexiBookPersistence.saveFlexiBook(flexiBook);
		}catch (RuntimeException e) {			
			throw new InvalidInputException(error);			
		}
	}
	/** 
	 * @author Abrar Fahad Rahman Anik
	 */
	public static void updateServiceCombo(String ownerUsername, String prevName, String newName, String mainService, String allServices, String mandatorySetting) throws InvalidInputException {
		FlexiBook flexiBook = FlexiBookApplication.getFlexibook();
		String error = "";
		if (!flexiBook.getOwner().getUsername().equals(ownerUsername)) {		// if the user is the owner
			error = "You are not authorized to perform this operation";
			throw new InvalidInputException(error);
		}
		String[] settings = mandatorySetting.split(",");        // parse mandatory settings string
		List<Boolean> boolSettings = new ArrayList<Boolean>();  // convert to list of booleans
		for (String x : settings) {
			boolSettings.add(Boolean.parseBoolean(x));
		}
		String [] myServices = allServices.split(",");         // parse allServices into an array
		List<Service> listOfServices = new ArrayList<Service>();
		int numOfServices = myServices.length;
		if (numOfServices < 2) {							   // check for minimum no. of services
			error = "A service Combo must have at least 2 services";
			throw new InvalidInputException(error);
		}
		boolean found = false;
		boolean mainServiceExists = false;
		for (int i = 0; i < numOfServices; i++) {              // check for main service included and
			if (myServices[i].equals(mainService)) {		   // check for main service is mandatory
				found = true;
				if (boolSettings.get(i).equals(false)) {
					error = "Main service must be mandatory";
					throw new InvalidInputException(error);
				}
			}
		}
		if (found != true) {
			error = "Main service must be included in the services";
			throw new InvalidInputException(error);
		}
		for (BookableService bookableService : flexiBook.getBookableServices()) {
			if (bookableService instanceof Service && bookableService.getName().equals(mainService)) {
				mainServiceExists = true;
			}
			if (bookableService instanceof ServiceCombo && bookableService.getName().contains(newName)) {
				error = "Service combo " + newName + " already exists";   //check for new serviceCombo exists
				throw new InvalidInputException(error); 
			}
		}
		if (mainServiceExists != true) {
			error = "Service" + mainService + "does not exist";  //check for main service exists
			throw new InvalidInputException(error);
		}
		boolean serviceExists;
		for (int i = 0; i < numOfServices; i++) {
			serviceExists = false;
			for (BookableService bookableService : flexiBook.getBookableServices()) {
				if (bookableService instanceof Service && bookableService.getName().equals(myServices[i])) {
					serviceExists = true;
					Service service = (Service) bookableService;
					listOfServices.add(service);      // fetching the services and adding them to list
				}
			}
			if (serviceExists != true) {
				error = "Service" + myServices[i] + "does not exist";  //check for service exists
			}
		}
		for (BookableService bookableService : flexiBook.getBookableServices()) {
			if (bookableService instanceof ServiceCombo && bookableService.getName().contains(prevName)) {
				ServiceCombo updatedCombo = (ServiceCombo) bookableService;
				if (!bookableService.setName(newName)) {
					error = "Service combo " + newName + " already exists";
					throw new InvalidInputException(error);
				}
				updatedCombo.setName(newName);            //set new name
				int j = 0;
				for (Service s : listOfServices) {
					ComboItem c = new ComboItem(boolSettings.get(j), s, updatedCombo);
					updatedCombo.addServiceAt(c, j);
					if (s.getName().equals(mainService)) {
						updatedCombo.setMainService(c);
					}					
					//updatedCombo.getServices().set(j, c);
					j++;
				}	
			}
			try {
				FlexiBookPersistence.saveFlexiBook(flexiBook);
			}catch(RuntimeException e) {
				throw new InvalidInputException(e.getMessage());
			}
		}		
	}
	/** 
	 * @author Abrar Fahad Rahman Anik
	 */
	public static void deleteServiceCombo(String username, String comboName) throws InvalidInputException {
		ServiceCombo myCombo = null;
		String error = "";
		FlexiBook flexiBook = FlexiBookApplication.getFlexibook();
		if(!flexiBook.getOwner().getUsername().equals(username)) {
			error = "You are not authorized to perform this operation";
			throw new InvalidInputException(error);
		}
		for (BookableService bookableService : flexiBook.getBookableServices()) {
			if (bookableService instanceof ServiceCombo && bookableService.getName().equals(comboName)) {
				myCombo = (ServiceCombo) bookableService;
				if(bookableService.getAppointments().size() != 0) {
					for(Appointment appointment : flexiBook.getAppointments()) {
						if(appointment.getTimeSlot().getStartDate().after(FlexiBookApplication.getDate())) {
							error = "Service combo " + comboName + " has future appointments";
							throw new InvalidInputException(error);
						}
					}

				}
			}
		}
		if (myCombo != null) {
			BookableService.getWithName(comboName).delete();
		}
		try {
			FlexiBookPersistence.saveFlexiBook(flexiBook);
		}catch(RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}
	}
	/**
	 * @author Abrar Fahad Rahman Anik, code ends
	 */
	//ABRAR CODE ENDS
	// ZARIF'S CODE STARTS HERE
		/**
		 * @author zarifashraf
		 * @param username
		 * @param password
		 * @param businessName
		 * @param businessAddress
		 * @param businessPhone
		 * @param businessEmail
		 * @throws InvalidInputException
		 */
		public static void setUpBusinessInfo(String username, String businessName, String businessAddress, String businessPhone, String businessEmail) throws InvalidInputException {
			String error = "";
			FlexiBook flexibook = FlexiBookApplication.getFlexibook();
			
			if (flexibook.getBusiness() != null) {
				error = "A business already exists. Please update the existing one.";
				throw new InvalidInputException(error);
			}
			if((!(username.equals(flexibook.getOwner().getUsername())))) {	
				error = "No permission to set up business information";
				throw new InvalidInputException(error);
			}
			
			if((!(businessEmail.contains("@")) || ((!(businessEmail.endsWith(".com")))))) {
				error = "Invalid email";
				throw new InvalidInputException(error);
			}
			
			try {
				if (flexibook.getBusiness() == null) {
					Business myBusiness = new Business(businessName, businessAddress, businessPhone, businessEmail, flexibook);
					flexibook.setBusiness(myBusiness);
				}
				FlexiBookPersistence.saveFlexiBook(flexibook);
			}
			catch(RuntimeException e) {
				throw new InvalidInputException(e.getMessage());
			}
		}
		/**
		 * @author zarifashraf
		 * @param username
		 * @param password
		 * @param day
		 * @param startTime
		 * @param endTime
		 * @throws InvalidInputException
		 */
		public static void addBusinessHours(String username, String day, String startTime, String endTime) throws InvalidInputException {
			String error = "";
			FlexiBook flexibook = FlexiBookApplication.getFlexibook();
			if (startTime.length() < 8) {
				startTime += ":00";
			}
			if (endTime.length() < 8) {
				endTime += ":00";
			}
			DayOfWeek newDay = DayOfWeek.valueOf(day);
			Time newStartTime = java.sql.Time.valueOf(startTime);
			Time newEndTime = java.sql.Time.valueOf(endTime);
			int timeComparison = newStartTime.compareTo(newEndTime);

			if (flexibook.getBusiness() == null) {
				error = "Please set up a business before adding business hours.";
				throw new InvalidInputException(error);
			}
			
			if((!(username.equals(flexibook.getOwner().getUsername())))) {	
			error = "No permission to update business information";
				throw new InvalidInputException(error);
			}
			if (!(timeComparison < 0)) {
				error = "Start time must be before end time";
				throw new InvalidInputException(error);
			}

			for (BusinessHour hours: flexibook.getHours()) {

				if (newDay.equals(hours.getDayOfWeek())) {
					error = "The business hours cannot overlap";
					throw new InvalidInputException(error);
				}
				
			}
			
			try {
				if (flexibook.getBusiness() != null) {
				BusinessHour myBusinessHour = new BusinessHour(newDay, newStartTime, newEndTime, flexibook);
				flexibook.getBusiness().addBusinessHour(myBusinessHour);
				FlexiBookPersistence.saveFlexiBook(flexibook);
				}
			}
			catch(RuntimeException e) {
				throw new InvalidInputException(e.getMessage());
			}
	}	//setupbusinesshours ends
		
		/**
		 *  @author zarifashraf
		 * @param username
		 * @param password
		 * @param newTimeSlot
		 * @param typeOfBreak
		 * @throws InvalidInputException
		 */
		public static void addHolidaysAndVacations(String username, String startDate,String startTime, String endDate, String endTime, String typeOfBreak) throws InvalidInputException {
			String error = "";
			FlexiBook flexibook = FlexiBookApplication.getFlexibook();
			if (startTime.length() < 8) {
				startTime += ":00";
			}
			if (endTime.length() < 8) {
				endTime += ":00";
			}
			Time newStartTime = java.sql.Time.valueOf(startTime);
			Time newEndTime = java.sql.Time.valueOf(endTime);
			Date newStartDate = java.sql.Date.valueOf(startDate);
			Date newEndDate = java.sql.Date.valueOf(endDate);
			int timeComparison = newStartTime.compareTo(newEndTime);
			int dateComparison = newStartDate.compareTo(newEndDate);
			boolean isHoliday = false; 
			boolean isVacation = false;
			if (typeOfBreak.equalsIgnoreCase("holiday")) {
				isHoliday = true;
			}
			else if (typeOfBreak.equalsIgnoreCase("vacation")) {
				isVacation = true;
			}

			if (flexibook.getBusiness() == null) {
				error = "Please set up a business before adding holidays and vacations.";
				throw new InvalidInputException(error);
			}
			if((!(username.equals(flexibook.getOwner().getUsername())))) {			
				error = "No permission to update business information";
				throw new InvalidInputException(error);
			}
			else if ((dateComparison > 0) || ((dateComparison == 0) && ((timeComparison > 0))))  {
				error = "Start time must be before end time";
				throw new InvalidInputException(error);
			}
			else if (((newStartDate.before(FlexiBookApplication.getDate())) == true) && (isHoliday == true)) {
				error = "Holiday cannot start in the past";
				throw new InvalidInputException(error);
			}
			else if (((newStartDate.before(FlexiBookApplication.getDate())) == true) && (isVacation == true)) {
				error = "Vacation cannot start in the past";
				throw new InvalidInputException(error);
			}
			for (TimeSlot vacationSlots: flexibook.getBusiness().getVacation()) {
				if (isVacation == true) {
					if (!(((newStartDate.compareTo(vacationSlots.getStartDate()) < 0) && (newEndDate.compareTo(vacationSlots.getStartDate()) < 0)) || (newStartDate.compareTo(vacationSlots.getEndDate()) > 0)  && (newEndDate.compareTo(vacationSlots.getEndDate()) > 0))) {
						error = "Vacation times cannot overlap";
						throw new InvalidInputException(error);
					}
				}	
				else if (isHoliday == true) {
					if (!(((newStartDate.compareTo(vacationSlots.getStartDate()) < 0) && (newEndDate.compareTo(vacationSlots.getStartDate()) < 0)) || (newStartDate.compareTo(vacationSlots.getEndDate()) > 0)  && (newEndDate.compareTo(vacationSlots.getEndDate()) > 0))) {
						error = "Holiday and vacation times cannot overlap";
						throw new InvalidInputException(error);
					}
				}	
			}		// for loop ends
			
			for (TimeSlot holidaySlots: flexibook.getBusiness().getHolidays()) {
				if (isHoliday == true) {
					if (!(((newStartDate.compareTo(holidaySlots.getStartDate()) < 0) && (newEndDate.compareTo(holidaySlots.getStartDate()) < 0)) || (newStartDate.compareTo(holidaySlots.getEndDate()) > 0)  && (newEndDate.compareTo(holidaySlots.getEndDate()) > 0))) {
						error = "Holiday times cannot overlap";
						throw new InvalidInputException(error);
					}
				}
				else if (isVacation == true) {
					if (!(((newStartDate.compareTo(holidaySlots.getStartDate()) < 0) && (newEndDate.compareTo(holidaySlots.getStartDate()) < 0)) || (newStartDate.compareTo(holidaySlots.getEndDate()) > 0)  && (newEndDate.compareTo(holidaySlots.getEndDate()) > 0))) {
						error = "Holiday and vacation times cannot overlap";
						throw new InvalidInputException(error);
					}
				}
			}		// for loop ends
			try {
				if (flexibook.getBusiness() != null) {
				TimeSlot myTimeSlot = new TimeSlot(newStartDate, newStartTime, newEndDate, newEndTime, flexibook);
				if (isHoliday == true) {
					flexibook.addTimeSlot(myTimeSlot);
					flexibook.getBusiness().addHoliday(myTimeSlot);
				}
				else if (isVacation == true) {
					flexibook.addTimeSlot(myTimeSlot);
					flexibook.getBusiness().addVacation(myTimeSlot);
				}
				FlexiBookPersistence.saveFlexiBook(flexibook);
				}
			} // try block ends
			catch(RuntimeException e) {
				throw new InvalidInputException(e.getMessage());
			}
		}		//setupHolidaysandVacations end
		/**
		 * @author zarifashraf
		 * @param username
		 * @param password
		 * @param businessName
		 * @param businessAddress
		 * @param businessPhone
		 * @param businessEmail
		 * @throws InvalidInputException
		 */

		public static void updateBusinessInfo(String username, String businessName, String businessAddress, String businessPhone, String businessEmail) throws InvalidInputException {
			String error = "";
			FlexiBook flexibook = FlexiBookApplication.getFlexibook();
			
			if (flexibook.getBusiness() == null) {
				error = "Please set up a business first.";
				throw new InvalidInputException(error);
			}
			if((!(username.equals(flexibook.getOwner().getUsername())))) {
			error = "No permission to update business information";
				throw new InvalidInputException(error);
			}
			if	((!(businessEmail.contains("@"))) || ((!(businessEmail.endsWith(".com"))))) {
				error = "Invalid email";
				throw new InvalidInputException(error);
			}
			
			try {
				if (flexibook.getBusiness() != null) {
				flexibook.getBusiness().setName(businessName);
				flexibook.getBusiness().setAddress(businessAddress);
				flexibook.getBusiness().setPhoneNumber(businessPhone);
				flexibook.getBusiness().setEmail(businessEmail);
				FlexiBookPersistence.saveFlexiBook(flexibook);
				}
			}
			catch(RuntimeException e) {
				throw new InvalidInputException(e.getMessage());
			}
		}		//UpdateBusinessInfo ends here
		/**
		 * @author zarifashraf
		 * @param username
		 * @param password
		 * @param oldDay
		 * @param newDay
		 * @param oldStartTime
		 * @param newStartTime
		 * @param newEndTime
		 * @throws InvalidInputException
		 */
		public static void updateBusinessHours(String username, String oldDay, String newDay, String oldStartTime, String newStartTime, String newEndTime) throws InvalidInputException {
			String error = "";
			FlexiBook flexibook = FlexiBookApplication.getFlexibook();
			if (newStartTime.length() < 8) {
					newStartTime += ":00";
					}
			if (newEndTime.length() < 8) {
				newEndTime += ":00";
				}

			if (oldStartTime.length() < 8) {
				oldStartTime += ":00";
				}
			Time finalStartTime = java.sql.Time.valueOf(newStartTime);
			Time finalEndTime = java.sql.Time.valueOf(newEndTime);
			Time previousStartTime = java.sql.Time.valueOf(oldStartTime);
			DayOfWeek previousDay = DayOfWeek.valueOf(oldDay);
			DayOfWeek finalDay = DayOfWeek.valueOf(newDay);
			int timeComparison = newStartTime.compareTo(newEndTime);
			if((!(username.equals(flexibook.getOwner().getUsername())))) {
				error = "No permission to update business information";
				throw new InvalidInputException(error);
			}
			else if (!(timeComparison < 0)) {
				error = "Start time must be before end time";
				throw new InvalidInputException(error);
			}
			else if (!(previousDay.equals(finalDay))) {
				error = "The business hours cannot overlap";
				throw new InvalidInputException(error);
			}
			
			try {
				if (flexibook.getBusiness() != null) {
				for (BusinessHour theHour: flexibook.getBusiness().getBusinessHours()) {
					if (previousDay.equals(theHour.getDayOfWeek())) {
						if (previousStartTime.equals(theHour.getStartTime())) {
							theHour.setDayOfWeek(finalDay);
							theHour.setStartTime(finalStartTime);
							theHour.setEndTime(finalEndTime);
						}
					}
				}
			}
				FlexiBookPersistence.saveFlexiBook(flexibook);
			}
			catch(RuntimeException e) {
				throw new InvalidInputException(e.getMessage());
			}  
		}	//Updatebusinesshours ends
		/**
		 * @author zarifashraf
		 * @param username
		 * @param password
		 * @param oldDay
		 * @param oldStartTime
		 * @throws InvalidInputException
		 */
		public static void deleteBusinessHours(String username, String oldDay, String oldStartTime) throws InvalidInputException {
			String error = "";
			FlexiBook flexibook = FlexiBookApplication.getFlexibook();
			if((oldStartTime.length() < 8)) {
					oldStartTime += ":00";
			}
			DayOfWeek previousDay = DayOfWeek.valueOf(oldDay);
			Time previousStartTime = java.sql.Time.valueOf(oldStartTime);
			if((!(username.equals(flexibook.getOwner().getUsername())))) {
			error = "No permission to update business information";
				throw new InvalidInputException(error);
			}
			try {
				BusinessHour myBusinessHour = null;
				if (flexibook.getBusiness() != null) {
				for (BusinessHour DeleteHour: flexibook.getBusiness().getBusinessHours()) {
					if ((previousDay.equals(DeleteHour.getDayOfWeek())) && (previousStartTime.equals(DeleteHour.getStartTime()))) {
						myBusinessHour = DeleteHour;
					}
				}
				}
				flexibook.getBusiness().removeBusinessHour(myBusinessHour);
				myBusinessHour.delete();
				FlexiBookPersistence.saveFlexiBook(flexibook);
			}
			
			catch(RuntimeException e) {
				throw new InvalidInputException(e.getMessage());
			}  
		}
		/**
		 * @author zarifashraf
		 * @param username
		 * @param password
		 * @param oldStartDate
		 * @param newStartDate
		 * @param oldStartTime
		 * @param newStartTime
		 * @param newEndDate
		 * @param newEndTime
		 * @param typeOfBreak
		 * @throws InvalidInputException
		 */
		public static void updateHolidaysAndVacations(String username, String oldStartDate, String newStartDate, String oldStartTime, String newStartTime, String newEndDate, String newEndTime, String typeOfBreak) throws InvalidInputException {
			String error = "";
			FlexiBook flexibook = FlexiBookApplication.getFlexibook();
			if (newStartTime.length() < 8) {
				newStartTime += ":00";
			}
			if (newEndTime.length() < 8) {
				newEndTime += ":00";
			}
			
			if (oldStartTime.length() < 8) {
				oldStartTime += ":00";
			}
			Time finalStartTime = java.sql.Time.valueOf(newStartTime);
			Time finalEndTime = java.sql.Time.valueOf(newEndTime);
			Time previousStartTime = java.sql.Time.valueOf(oldStartTime);
			Date previousStartDate = java.sql.Date.valueOf(oldStartDate);
			Date finalStartDate = java.sql.Date.valueOf(newStartDate);
			Date finalEndDate = java.sql.Date.valueOf(newEndDate);
			int timeComparison = newStartTime.compareTo(newEndTime);
			int dateComparison = newStartDate.compareTo(newEndDate);
			boolean isHoliday = false; 
			boolean isVacation = false;
			if (typeOfBreak.equalsIgnoreCase("holiday")) {
				isHoliday = true;
			}
			else if (typeOfBreak.equalsIgnoreCase("vacation")) {
				isVacation = true;
			}
			if((!(username.equals(flexibook.getOwner().getUsername())))) {
			error = "No permission to update business information";
				throw new InvalidInputException(error);
			}
			else if ((!(dateComparison < 0)) || ((dateComparison == 0) && (!(timeComparison < 0))))  {
				error = "Start time must be before end time";
				throw new InvalidInputException(error);
			}
			else if ((finalStartDate.compareTo(FlexiBookApplication.getDate()) < 0) && (isHoliday == true)) {
				error = "Holiday cannot be in the past";
				throw new InvalidInputException(error);
			}
			else if ((finalStartDate.compareTo(FlexiBookApplication.getDate()) < 0) && (isVacation == true)) {
				error = "Vacation cannot start in the past";
				throw new InvalidInputException(error);
			}
			for (TimeSlot vacationSlots: flexibook.getBusiness().getVacation()) {
				if (isHoliday == true) {
					if (!(((finalStartDate.compareTo(vacationSlots.getStartDate()) < 0) && (finalEndDate.compareTo(vacationSlots.getStartDate()) < 0)) || (finalStartDate.compareTo(vacationSlots.getEndDate()) > 0)  && (finalEndDate.compareTo(vacationSlots.getEndDate()) > 0))) {
						error = "Holiday and vacation times cannot overlap";
						throw new InvalidInputException(error);
					}
				}	
			}		// for loop ends
			for (TimeSlot holidaySlots: flexibook.getBusiness().getHolidays()) {
				if (isVacation == true) {
					if (!(((finalStartDate.compareTo(holidaySlots.getStartDate()) < 0) && (finalEndDate.compareTo(holidaySlots.getStartDate()) < 0)) || (finalStartDate.compareTo(holidaySlots.getEndDate()) > 0)  && (finalEndDate.compareTo(holidaySlots.getEndDate()) > 0))) {
						error = "Holiday and vacation times cannot overlap";
						throw new InvalidInputException(error);
					}
				}
			}		// for loop ends
			try {
				if (flexibook.getBusiness() != null) {
				if (isHoliday == true) {
					for (TimeSlot holidaySlots: flexibook.getBusiness().getHolidays()) {
						if (previousStartDate.equals(holidaySlots.getStartDate())) {
							if (previousStartTime.equals(holidaySlots.getStartTime())) {
								holidaySlots.setStartDate(finalStartDate);
								holidaySlots.setStartTime(finalStartTime);
								holidaySlots.setEndDate(finalEndDate);
								holidaySlots.setEndTime(finalEndTime);
							}
						}
					}
				}
				else if(isVacation == true) {
					for (TimeSlot vacationSlots: flexibook.getBusiness().getVacation()) {
						if (previousStartDate.equals(vacationSlots.getStartDate())) {
							if (previousStartTime.equals(vacationSlots.getStartTime())) {
								vacationSlots.setStartDate(finalStartDate);
								vacationSlots.setStartTime(finalStartTime);
								vacationSlots.setEndDate(finalEndDate);
								vacationSlots.setEndTime(finalEndTime);
							}
						}
					}
				}
			}
				FlexiBookPersistence.saveFlexiBook(flexibook);
			}		//try block ends
			catch(RuntimeException e) {
				throw new InvalidInputException(e.getMessage());
			}  
		}
		/**
		 * @author zarifashraf
		 * @param username
		 * @param password
		 * @param oldStartDate
		 * @param oldStartTime
		 * @param oldEndDate
		 * @param oldEndTime
		 * @param typeOfBreak
		 * @throws InvalidInputException
		 */
		public static void deleteHolidaysAndVacations(String username, String oldStartDate, String oldStartTime, String oldEndDate, String oldEndTime, String typeOfBreak) throws InvalidInputException {
			String error = "";
			FlexiBook flexibook = FlexiBookApplication.getFlexibook();
			if (oldStartTime.length() < 8) {
				oldStartTime += ":00";
			}
			if (oldEndTime.length() < 8) {
				oldEndTime += ":00";
			}
			Date previousStartDate = java.sql.Date.valueOf(oldStartDate);
			Date previousEndDate = java.sql.Date.valueOf(oldEndDate);
			Time previousStartTime = java.sql.Time.valueOf(oldStartTime);
			Time previousEndTime = java.sql.Time.valueOf(oldEndTime);
			boolean isHoliday = false; 
			boolean isVacation = false;
			
			if (typeOfBreak.equalsIgnoreCase("holiday")) {
				isHoliday = true;
			}
			else if (typeOfBreak.equalsIgnoreCase("vacation")) {
				isVacation = true;
			}

			if((!(username.equals(flexibook.getOwner().getUsername())))) {
			error = "No permission to update business information";
				throw new InvalidInputException(error);
			}

			try {
				if (flexibook.getBusiness() != null) {
				TimeSlot timeslot = null;
				if (isHoliday == true) {
					for (TimeSlot DeleteHolidaySlot: flexibook.getBusiness().getHolidays()) {
						if (previousStartDate.equals(DeleteHolidaySlot.getStartDate())) {
							if(previousStartTime.equals(DeleteHolidaySlot.getStartTime())) {
								if (previousEndDate.equals(DeleteHolidaySlot.getEndDate())) {
									if (previousEndTime.equals(DeleteHolidaySlot.getEndTime())) {
										timeslot = DeleteHolidaySlot;
									}
								}
							}
						}
					}
					
				}
				else if(isVacation == true) {
					for (TimeSlot DeleteVacationSlot: flexibook.getBusiness().getVacation()) {
						if (previousStartDate.equals(DeleteVacationSlot.getStartDate())) {
							if(previousStartTime.equals(DeleteVacationSlot.getStartTime())) {
								if (previousEndDate.equals(DeleteVacationSlot.getEndDate())) {
									if (previousEndTime.equals(DeleteVacationSlot.getEndTime())) {
										timeslot = DeleteVacationSlot;
									}
								}
							}
						}
					}
				}
				
			
				if (timeslot != null) {
					if (isVacation == true) {
					flexibook.getBusiness().removeVacation(timeslot); 
					}
					if(isHoliday == true) {
					flexibook.getBusiness().removeHoliday(timeslot);
					}
					timeslot.delete();
			}
				}
				
				FlexiBookPersistence.saveFlexiBook(flexibook);
			}		
			catch(RuntimeException e) {
				throw new InvalidInputException(e.getMessage());
			}  
		}		//deleteHolidaysandVacations ends here
			
		/**
		 * @author zarifashraf
		 * @return
		 */
		
		public static TOBusiness getBusiness() {
			Business business = FlexiBookApplication.getFlexibook().getBusiness(); 
			TOBusiness tobusiness = new TOBusiness(business.getName(), business.getAddress(), business.getPhoneNumber(), business.getEmail());
			return tobusiness;
		}
		
		/**@author zarifashraf
		 * 
		 * @return
		 */
		
		public static List<TOBusinessHour> getBusinessHourList() {
			List<TOBusinessHour> toBusinessHourList = new ArrayList<TOBusinessHour>();
			for (BusinessHour businesshour: FlexiBookApplication.getFlexibook().getHours()) {
				TOBusinessHour toBusinessHour = new TOBusinessHour(businesshour.getDayOfWeek().toString(), businesshour.getStartTime(), businesshour.getEndTime());
				toBusinessHourList.add(toBusinessHour);
			}
			return toBusinessHourList;
		}
		
		/**
		 * @author zarifashraf
		 * @return
		 */
		public static List<String> getDayOfWeekValues() {
			ArrayList<String> days = new ArrayList<String>();
			for (DayOfWeek day : DayOfWeek.values()) {
				days.add(day.toString());
			}
			return days;
		}
		/** @author zarifashraf
		 * ZARIF'S CODE ENDS HERE
		 */
	//SAGHAR CODE BEGINS
	/**
	 * @author sagharsahebi
	 * @param username
	 * @param password
	 * @throws InvalidInputException
	 */
	public static void signUpCustomerAccount(String username, String password ) throws InvalidInputException {
		String error = "";
		FlexiBook flexiBook = FlexiBookApplication.getFlexibook();
		numOfCustomers = flexiBook.getCustomers().size();		// to determine if customer size has change after wards
		if(FlexiBookApplication.getCurrentUser() != null && FlexiBookApplication.getCurrentUser().getUsername().equals("owner")) {
			error = "You must log out of the owner account before creating a customer account";
			throw new InvalidInputException(error);
		}
		if(username.equals(" ")|| username.equals("")|| username == null) {		//the username is empty 
			error = "The user name cannot be empty";
			throw new InvalidInputException(error);
		}
		if(password.equals(" ") || password.equals("")|| password == null) {//the password is empty 
			error ="The password cannot be empty";
			throw new InvalidInputException(error);
		}
		try {
			Customer customer = new Customer(username, password, flexiBook);
			FlexiBookApplication.setCurrentUser(customer);
			FlexiBookPersistence.saveFlexiBook(flexiBook);
		}
		catch(RuntimeException e) {
			error += "The username already exists";
			throw new InvalidInputException(error);
		}
	}
	/**
	 * @author Saghar
	 * @param newUsername
	 * @param newPassword
	 * @throws InvalidInputException
	 */
	public static void UpdateUser(String newUsername, String newPassword) throws InvalidInputException {
		FlexiBook flexibook = FlexiBookApplication.getFlexibook();
		String error = "";
		User user = FlexiBookApplication.getCurrentUser();
		if(newUsername.equals(" ")|| newUsername.equals("")|| newUsername == null) {		//the username is empty 
			error = "The user name cannot be empty";
			throw new InvalidInputException(error);
		}
		if(newPassword.equals(" ") || newPassword.equals("")|| newPassword == null) {//the password is empty 
			error ="The password cannot be empty";
			throw new InvalidInputException(error);
		}

		if(user instanceof Owner && !(newUsername.equals(user.getUsername()))) {
			error = "Changing username of owner is not allowed";
			throw new InvalidInputException(error);
		}
		for(Customer customer : flexibook.getCustomers()) {
			if(customer.getUsername().equals(newUsername)) {
				error = "Username not available";
				throw new InvalidInputException(error);
			}
		}
		try {
			user.setUsername(newUsername);	
			user.setPassword(newPassword);
			FlexiBookPersistence.saveFlexiBook(flexibook);
		}
		catch(RuntimeException e) {
			throw new InvalidInputException(error);
		}
	}

	/**
	 * @author Saghar
	 * @param username
	 * @throws InvalidInputException
	 */
	public static void deleteUser(String username) throws InvalidInputException{
		FlexiBook flexibook = FlexiBookApplication.getFlexibook();
		String error = "";
		User user = FlexiBookApplication.getCurrentUser();
		try {
			if(!user.getUsername().equals(username) || user.getUsername().equals("owner")) {
				error = "You do not have permission to delete this account";
				throw new InvalidInputException(error);
			}
			if(user.getUsername().equals(username)) {
				User.getWithUsername(username).delete();
				FlexiBookApplication.setCurrentUser(null);
			}
			FlexiBookPersistence.saveFlexiBook(flexibook);
		}
		catch(RuntimeException e) {
			throw new InvalidInputException(error);
		}
	}
	//saghar code ended 
	/**@author saghar
	 *
	 * @param date
	 */
	public static void endAppointment(String date) {
		if(FlexiBookApplication.getCurrentUser() instanceof Owner) {
			Date curDate = FlexiBookApplication.convertToDate(date);
			FlexiBook flexiBook = FlexiBookApplication.getFlexibook();
			for (Appointment appointment : flexiBook.getAppointments()) {
				if(appointment.getTimeSlot().getEndDate().equals(curDate)) {
					appointment.end();
				}
			}
		}
	}
	/**
	 * @author saikouceesay
	 */
	public static void registerNoShow(String dateString) {
		FlexiBook flexibook = FlexiBookApplication.getFlexibook();
		FlexiBookApplication.setSystemTimeAndDate(dateString);
		Time curTime = FlexiBookApplication.getCurrentTime();
		Date curDate = FlexiBookApplication.getDate();

		List<Appointment> appointments = new ArrayList<Appointment>();
		for(Appointment appointment : flexibook.getAppointments()) {
			if(appointment.getTimeSlot().getStartDate().equals(curDate)) {
				appointments.add(appointment);
			}
		}
	}
	public static void updateAppointmentService(String userName, String serviceName) {
		Customer customer = (Customer) User.getWithUsername(userName);
		Service service = (Service) BookableService.getWithName(serviceName);
		int n = customer.getAppointments().size() - 1;		//last added
		Appointment appointment = customer.getAppointment(n);			//WRONG 
		Date date = appointment.getTimeSlot().getStartDate();
		Time startTime = appointment.getTimeSlot().getStartTime();
		Time endTime = appointment.getTimeSlot().getEndTime();
		Date curDate = FlexiBookApplication.getDate();
		appointment.update(curDate, date, service, startTime, endTime);
	}
	
	public static void cancelAppointment(String curUser, Date date) {
		Customer c = (Customer) User.getWithUsername(curUser);
		Appointment a = c.getAppointment(0);		//WRONG
		Date curDate = FlexiBookApplication.getDate();
		a.cancel(curDate, date);
	}
	/**
	 * @author Eloyann
	 */
	public static String getCustomersAppointment(User currentUser, int selectedIndex) throws InvalidInputException {
		String error = "";
		
			if(!(currentUser instanceof Customer)) {
				error = "An owner cannot change an appointment";
				throw new InvalidInputException(error);
			}
			Appointment toDelete=((Customer) currentUser).getAppointment(selectedIndex);
			String nameOfService=toDelete.getBookableService().getName();
			return nameOfService;
	}
	/**
	 * @author Eloyann
	 */
	public static Time getappointmentTime(User currentUser, int selectedIndexappointment) throws InvalidInputException {
		String error = "";
		
		if(!(currentUser instanceof Customer)) {
			error = "An owner cannot change an appointment";
			throw new InvalidInputException(error);
		}
		Appointment toDelete=((Customer) currentUser).getAppointment(selectedIndexappointment);
		Time aTime=toDelete.getTimeSlot().getStartTime();;
		return aTime;
	}
	/**
	 * @author Eloyann
	 */
	public static Date getappointmentDate(User currentUser, int selectedIndexappointment) throws InvalidInputException {
		String error = "";
		
		if(!(currentUser instanceof Customer)) {
			error = "An owner cannot change an appointment";
			throw new InvalidInputException(error);
		}
		Appointment toDelete=((Customer) currentUser).getAppointment(selectedIndexappointment);
		Date aDate=toDelete.getTimeSlot().getStartDate();
		return aDate;
	}
	public static List<String> getAppointments(User currentUser ) throws InvalidInputException {
		ArrayList<String> appts = new ArrayList<String>();
		String error = "";
		if(!(currentUser instanceof Customer)) {
			error = "An owner cannot change an appointment";
			throw new InvalidInputException(error);
		}
		ArrayList<TOAppointment> appointments = new ArrayList<TOAppointment>();
		for (Appointment appointment : ((Customer) currentUser).getAppointments()) {
			Date aStartDate=appointment.getTimeSlot().getStartDate();
			Date aEndDate=appointment.getTimeSlot().getEndDate();
			Time aStartTime=appointment.getTimeSlot().getStartTime();
			Time aEndTime=appointment.getTimeSlot().getEndTime();
			String appt=appointment.getBookableService().getName();
				TOAppointment toAppointment = new TOAppointment( aStartDate, aEndDate, aStartTime, aEndTime);
				appointments.add(toAppointment);
				appts.add(appt);
			
		}
		return appts;
	}
	public static List<String> getServiceOfAppointment(User currentUser, int selectedIndex) throws InvalidInputException {
		ArrayList<String> services = new ArrayList<String>();
		String error = "";
		if(!(currentUser instanceof Customer)) {
			error = "An owner cannot change an appointment";
			throw new InvalidInputException(error);
		}
		for(ComboItem service: ((Customer) currentUser).getAppointment(selectedIndex + 1).getChosenItems()) {
			String servicebooked=service.getService().getName();
			services.add(servicebooked);
		}
		return services;
		
		
		
	}

	/**
	 * @author EloyannRJ
	 */
	public static void makeAppointmentDiff(String customerName, String nameOfService, Date startDate, Time startTime, List<String> optionalServices) throws InvalidInputException {
		String error = "";
		FlexiBook flexiBook = FlexiBookApplication.getFlexibook();
		User user = User.getWithUsername(customerName);	
		//could be a customer or the owner
		if(user instanceof Owner) {
			error = "An owner cannot make an appointment";
			throw new InvalidInputException(error);
		}
		BookableService bookableService = BookableService.getWithName(nameOfService);		//could be service or service combo
		for(Appointment appointment: flexiBook.getAppointments()) {
			if(appointment.getTimeSlot().getStartTime().equals(startTime) && appointment.getTimeSlot().getStartDate().equals(startDate) ) {				//if existing appointment
				error = "There are no available slots for " + bookableService.getName() + " on " + startDate + " at " + startTime.getHours()+":"+startTime.getMinutes()+"0";
				throw new InvalidInputException(error);
			}
			else if(startDate.before(FlexiBookApplication.getDate())) {						//if start date of appoinment is in the past
				error = "There are no available slots for " + bookableService.getName() + " on " + startDate + " at " + startTime.getHours()+":"+startTime.getMinutes()+"0";
				throw new InvalidInputException(error);
			}
		}
		for(BusinessHour openedHours : flexiBook.getHours() ) {
			if (!(flexiBook.getHours().contains(openedHours.getStartTime().equals(startTime)))){
				error = "There are no available slots for " + bookableService.getName() + " on " + startDate + " at " + startTime.getHours()+":"+startTime.getMinutes()+"0";
				throw new InvalidInputException(error);
			}
			break;
		}
		List<Service> optionalServiceList = new ArrayList<>();
		for(String name : optionalServices) {
			Service service = (Service) BookableService.getWithName(name);
			optionalServiceList.add(service);
		}
		try {
			Time endTime = startTime;							//need to add the duration???
			TimeSlot timeSlot = new TimeSlot(startDate, startTime, startDate, endTime, flexiBook);	
			Appointment appointment;
			if(bookableService instanceof Service) {
				appointment = new Appointment((Customer) user, (Service) bookableService,timeSlot, flexiBook);
			}else {
				ServiceCombo serviceCombo = (ServiceCombo) bookableService;
				ComboItem comboItem;
				for(Service service : optionalServiceList) {
					comboItem = new ComboItem(false, service, serviceCombo);
					serviceCombo.addService(comboItem);
				}
				appointment = new Appointment((Customer) user, serviceCombo, timeSlot, flexiBook);
				FlexiBookPersistence.saveFlexiBook(flexiBook);
			}
		}catch(RuntimeException e) {
			error = e.getMessage();
			throw new InvalidInputException(error);
		}
	}
	/**
	 * @author Eloyann
	 * @return 
	 */
	public static  void updateAppointmentDiff(String user, String username, String nameOfService, String action, String comboItem, Date date, Time time)  throws InvalidInputException{
		String result = "unsuccessful";
		String error = "";
		FlexiBook flexiBook = FlexiBookApplication.getFlexibook();
		if(FlexiBookApplication.getCurrentUser() instanceof Owner) {
			error = "Error: An owner cannot update a customer's appointment";
			throw new InvalidInputException(error);
		}
		else if (!(user.equals(username))) {
			error = "Error: A customer can only update their own appointments";
			throw new InvalidInputException(error);
		}
		//cannot update a past appointment
		if(date.before(FlexiBookApplication.getDate())) {
			error = "Error: new time is in the past";
			throw new InvalidInputException(error);
		}
		if(action.equals("")) {		//updating the time a service
			Service bookableService = (Service) BookableService.getWithName(nameOfService);
			Customer customer = (Customer) User.getWithUsername(username);
			//if new time is even during business hours
			for(BusinessHour openedHours : flexiBook.getHours() ) {
				if (!(flexiBook.getHours().contains(openedHours.getStartTime().equals(time)))){
					error = "Error: new time is not even during business hours";
					throw new InvalidInputException(error);
				}
			}
			for(Appointment allappointments : flexiBook.getAppointments()) {
				//checking if the new timeslot exits
				if(allappointments.getTimeSlot().getStartTime().equals(time) && allappointments.getTimeSlot().getStartDate().equals(date) ) {
					error = "Error: new time slot is already is taken";
					throw new InvalidInputException(error);
				}
			}
			for(Appointment appointment : customer.getAppointments()) {
				ServiceCombo bookService = (ServiceCombo) appointment.getBookableService();
				if(bookService.equals(bookableService)) {
					//removing previous time slot associated with that appointment
					TimeSlot removedTimeSLOT=appointment.getTimeSlot();
					flexiBook.removeTimeSlot(removedTimeSLOT);
					//updating time slot of that appointment
					TimeSlot timeSlot = new TimeSlot(date, time, appointment.getTimeSlot().getEndDate(), appointment.getTimeSlot().getEndTime(), flexiBook);
					appointment.setTimeSlot(timeSlot);
					result = "successful";
				}
			}
		}
		if(action.equals("remove")) {
			ServiceCombo serviceCombo = (ServiceCombo) BookableService.getWithName(nameOfService);
			ComboItem mainService = serviceCombo.getMainService();
			if(mainService.getService().getName().equals(comboItem)) {
				error = "Error: Cannot delete a main service of a Combo";
				throw new InvalidInputException(error);
			}
			else {
				ComboItem c = null;
				for(ComboItem coItem : serviceCombo.getServices()) {
					if(coItem.getService().getName().equals(comboItem));
					c = coItem;
				}
				if(c != null) {
					serviceCombo.removeService(c);
					result="succesful";
					//should i add a break statement 
				}
				if(c==null) {
					error = "Error: invalide service name input";
					throw new InvalidInputException(error);
				}
			}
		}
		if(action.equals("add")) {
			BookableService bookableService =  BookableService.getWithName(nameOfService);
			BookableService aChosenItem= BookableService.getWithName(comboItem);
			Customer customer = (Customer) User.getWithUsername(username);
			for(Appointment appointment : customer.getAppointments()) {
				BookableService bookService =  appointment.getBookableService();
				if(bookService.equals(bookableService)) {
					int timeadded=0;
					if(aChosenItem instanceof Service) {
						timeadded= ((Service) aChosenItem).getDuration();
					}
					if(aChosenItem instanceof ServiceCombo) {
						ServiceCombo aChosenCombo=(ServiceCombo) aChosenItem;    
						for (ComboItem aItem: aChosenCombo.getServices()) {
							timeadded+= aItem.getService().getDuration();
						}
					}
					long newendTime=(appointment.getTimeSlot().getEndTime().getTime())+timeadded*6000;	
					//if we have a time duration that requires change
					if(newendTime!=0) {
						Time updatendTime = FlexiBookApplication.getCurrentTime();
						updatendTime.setTime(newendTime);
						//new variable to test if new end time is possible
						//checking if the new timeSlot is available
						for (Appointment aappointment: flexiBook.getAppointments()) {
							if( aappointment.getTimeSlot().getEndTime().equals(updatendTime) && aappointment.getTimeSlot().getStartDate().equals(date) ) {
								//already existing appointment in that slot
								error="the time slot isn't available";
								throw new InvalidInputException(error);
							}
						}
						//update appointment
						//updating the time slot
						appointment.getTimeSlot().setEndTime(updatendTime);
					}
					//creating the combo item
					if(aChosenItem instanceof ServiceCombo) {
						for(ComboItem addedChosenItem:((ServiceCombo) BookableService.getWithName(comboItem)).getServices()) {
							appointment.addChosenItem(addedChosenItem);
						}
					}
					else {
						//adding the service to the appointment
						aChosenItem.addAppointment(appointment);
					}
				}
			}
		}
		try {
			FlexiBookPersistence.saveFlexiBook(flexiBook);
		}catch(RuntimeException e) {
			throw new InvalidInputException(e.getMessage());
		}

	}
	/** @author eloyann
	 * 
	 * @param actionUser
	 * @param username
	 * @param nameOfService
	 * @param date
	 * @param time
	 * @throws InvalidInputException
	 */
	public static void cancelAppointmentDiff(String actionUser, String username, String nameOfService, Date date, Time time ) throws InvalidInputException {
		FlexiBook flexiBook = FlexiBookApplication.getFlexibook();
		User user = User.getWithUsername(username);
		User user2 = User.getWithUsername(actionUser);
		String error=" ";
		Date curDate = FlexiBookApplication.getDate();
		if(user2 instanceof Owner) {
			error = "An owner cannot cancel an appointment";
			throw new InvalidInputException(error);
		}
		else if (!(user.equals(user2))) {
			error = "A customer can only cancel their own appointments";
			throw new InvalidInputException(error);
		}
		Customer customer = (Customer) user;
		BookableService bookableService = BookableService.getWithName(nameOfService);
		List<Appointment> appointments = customer.getAppointments();
		Appointment toDelete = null;
		for(Appointment appointment : appointments) {
			if(appointment.getTimeSlot().getStartDate().equals(date)&&appointment.getTimeSlot().getStartTime().equals(time)) {
				if(!appointment.getTimeSlot().getStartDate().before(date)) {
					error = "Cannot cancel an appointment on the appointment date";
					throw new InvalidInputException(error);
				}else {
					toDelete = appointment;
					if(toDelete != null) {
						toDelete.cancel(curDate, date);		//this method has been updated
					}
				}
			}
			try {
				FlexiBookPersistence.saveFlexiBook(flexiBook);
			}catch(RuntimeException e) {
				throw new InvalidInputException(e.getMessage());
			}
		}
	}
}
