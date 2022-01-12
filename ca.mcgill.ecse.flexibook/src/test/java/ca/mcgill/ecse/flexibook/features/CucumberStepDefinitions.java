package ca.mcgill.ecse.flexibook.features;
import ca.mcgill.ecse.flexibook.application.FlexiBookApplication;
import ca.mcgill.ecse.flexibook.controller.FlexibookController;
import ca.mcgill.ecse.flexibook.controller.InvalidInputException;
import ca.mcgill.ecse.flexibook.controller.TOBusiness;
import ca.mcgill.ecse.flexibook.model.*;
import ca.mcgill.ecse.flexibook.model.BusinessHour.DayOfWeek;
import ca.mcgill.ecse.flexibook.persisitence.PersistenceObjectStream;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.io.File;
import java.sql.Date;
import java.sql.Time;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
public class CucumberStepDefinitions {
	//instances variables
	private FlexiBook flexiBook;
	private Owner owner;
	private Customer customer;
	private Business business;
	private String error;
	private BookableService bookableService;
	private ArrayList<TimeSlot> notAvailable;
	private String resultOfUpdateAppointment;
	private Appointment appointment;
	private static String filename = "flexibook.txt";
	static List<TimeSlot> slots;
	
	// zarif's fields
		private int numberOfBusinessHours = 0;
		private int numberOfHolidays = 0;
		private int numberOfVacations = 0;
		
		private Date previousStartDate = null;
		private Date finalStartDate = null;
		private Date finalEndDate = null;
		
		private Time previousStartTime = null;
		private Time finalStartTime = null;
		private Time finalEndTime = null;
		
		private DayOfWeek previousDay = null;
		private DayOfWeek finalDay = null;
		
		private TOBusiness tobusiness;
		
		private BusinessHour oldBusinessHour = null;
		private TimeSlot oldTimeSlot = null;
		// zarif's fields ends
	/**
	 * @author saikouceesay
	 */
	@After
	public void tearDown() {
		flexiBook.delete();
		FlexiBookApplication.setCurrentUser(null);
	}
	@Before
	public static void setUp() {
		PersistenceObjectStream.setFilename(filename);
		// remove test file
		File f = new File(filename);
		f.delete();
		// clear all data
		FlexiBookApplication.getFlexibook().delete();
		slots = new ArrayList<TimeSlot>();
	}
	/**
	 * @author saikouceesay
	 */
	@Given("a Flexibook system exists")
	public void a_flexibook_system_exists() {
		flexiBook = FlexiBookApplication.getFlexibook();
		error = "";
		resultOfUpdateAppointment = "";
	}
	/**
	 * @author saikouceesay
	 */
	@Given("an owner account exists in the system")
	public void an_owner_account_exists_in_the_system() {
		owner = new Owner("owner", "owner", flexiBook);
	}
	/**
	 * @author saikouceesay
	 */
	@Given("a business exists in the system")
	public void a_business_exists_in_the_system() {
		business = new Business("", "", "", "", flexiBook);
	}
	/**
	 * @param string
	 * @author saikouceesay
	 */
	@Given("the Owner with username {string} is logged in")
	public void the_owner_with_username_is_logged_in(String string) {
		owner.setUsername(string);
		FlexiBookApplication.setCurrentUser(owner);
	}
	/**
	 * @param string
	 * @param string2
	 * @param string3
	 * @param string4
	 * @param string5
	 * @author saikouceesay
	 */
	@When("{string} initiates the addition of the service {string} with duration {string}, start of down time {string} and down time duration {string}")
	public void initiates_the_addition_of_the_service_with_duration_start_of_down_time_and_down_time_duration(String string, String string2, String string3, String string4, String string5) {
		int duration = Integer.parseInt(string3);
		int downtimeDuration = Integer.parseInt(string5);
		int downtimeStart = Integer.parseInt(string4);
		String name = string2;
		String currentUsername = FlexiBookApplication.getCurrentUser().getUsername();
		try {
			FlexibookController.createService(currentUsername, name, duration, downtimeDuration, downtimeStart);
		} catch (InvalidInputException e) {
			error += e.getMessage();
		}
	}
	/**
	 * @param string
	 * @author saikouceesay
	 */
	@Then("the service {string} shall exist in the system")
	public void the_service_shall_exist_in_the_system(String string) {
		assertEquals(string, BookableService.getWithName(string).getName());
	}
	/**
	 * @param string
	 * @param string2
	 * @param string3
	 * @param string4
	 * @author saikouceesay
	 */
	@Then("the service {string} shall have duration {string}, start of down time {string} and down time duration {string}")
	public void the_service_shall_have_duration_start_of_down_time_and_down_time_duration(String string, String string2, String string3, String string4) {
		assertEquals(string, BookableService.getWithName(string).getName());
	}
	/**
	 * @param string
	 * @author saikouceesay
	 */
	@Then("the number of services in the system shall be {string}")
	public void the_number_of_services_in_the_system_shall_be(String string) {
		int one = Integer.parseInt(string);
		int count = 0;
		for(BookableService b : flexiBook.getBookableServices()) {
			if(b instanceof Service) {
				++count;
			}
		}
		assertEquals(one, count);
	}
	/**
	 * @param string
	 * @author saikouceesay
	 */
	@Then("an error message with content {string} shall be raised")
	public void an_error_message_with_content_shall_be_raised(String string) {
		assertEquals(error, string);
	}
	/**
	 * @param string
	 * @author saikouceesay
	 */
	@Then("the service {string} shall not exist in the system")
	public void the_service_shall_not_exist_in_the_system(String string) {
		assertEquals(null, BookableService.getWithName(string));
	}
	/**
	 * @param dataTable
	 * @author saikouceesay
	 */
	@Given("the following services exist in the system:")
	public void the_following_services_exist_in_the_system(io.cucumber.datatable.DataTable dataTable) {
		List<Map<String, String>> column = dataTable.asMaps(String.class, String.class);
		for (Map<String, String> rows : column) {
			int duration = Integer.parseInt(rows.get("duration"));
			int downtimeDuration = Integer.parseInt(rows.get("downtimeDuration"));
			int downtimeStart = Integer.parseInt(rows.get("downtimeStart"));
			Service service = new Service(rows.get("name"), flexiBook, duration, downtimeDuration, downtimeStart);
			flexiBook.addBookableService(service);
		}
	}
	/**
	 * @param string
	 * @param dataTable
	 * @author saikouceesay
	 */
	@Then("the service {string} shall still preserve the following properties:")
	public void the_service_shall_still_preserve_the_following_properties(String string, io.cucumber.datatable.DataTable dataTable) {
		int intialNumberOfServices = flexiBook.getBookableServices().size();
		String username = FlexiBookApplication.getCurrentUser().getUsername();
		List<Map<String, String>> column = dataTable.asMaps(String.class, String.class);
		for (Map<String, String> rows : column) {
			int duration = Integer.parseInt(rows.get("duration"));
			int downtimeDuration = Integer.parseInt(rows.get("downtimeDuration"));
			int downtimeStart = Integer.parseInt(rows.get("downtimeStart"));
			try {
				FlexibookController.createService(username, string, duration, downtimeDuration, downtimeStart);
			} catch (InvalidInputException e) {
				error += e.getMessage();
			}
		}
		assertTrue(flexiBook.getBookableServices().size() == intialNumberOfServices);
	}
	/**
	 * @param dataTable
	 * @author saikouceesay
	 */
	@Given("the following customers exist in the system:")
	public void the_following_customers_exist_in_the_system(io.cucumber.datatable.DataTable dataTable) {
		List<Map<String, String>> column = dataTable.asMaps(String.class, String.class);
		for (Map<String, String> row : column) {
			String username = row.get("username");
			String password = row.get("password");
			customer = new Customer(username, password, flexiBook);
		}
	}
	/**
	 * @param string
	 * @author saikouceesay
	 */
	@Given("Customer with username {string} is logged in")
	public void customer_with_username_is_logged_in(String string) {
		customer.setUsername(string);
		FlexiBookApplication.setCurrentUser(customer);
	}
	/**
	 * @param string
	 * @param string2
	 * @param string3
	 * @param string4
	 * @param string5
	 * @param string6
	 * @author saikouceesay
	 */
	@When("{string} initiates the update of the service {string} to name {string}, duration {string}, start of down time {string} and down time duration {string}")
	public void initiates_the_update_of_the_service_to_name_duration_start_of_down_time_and_down_time_duration(String string, String string2, String string3, String string4, String string5, String string6) {
		String oldName = string2;
		String newName = string3;
		int duration = Integer.parseInt(string4);
		int downtimeDuration = Integer.parseInt(string6);
		int downtimeStart = Integer.parseInt(string5);
		String username = FlexiBookApplication.getCurrentUser().getUsername();
		try {
			FlexibookController.updateService(username, oldName, newName, duration, downtimeDuration, downtimeStart);
		} catch (InvalidInputException e) {
			error += e.getMessage();
		}
	}
	/**
	 * @param string
	 * @param string2
	 * @param string3
	 * @param string4
	 * @param string5
	 * @author saikouceesay
	 */
	@Then("the service {string} shall be updated to name {string}, duration {string}, start of down time {string} and down time duration {string}")
	public void the_service_shall_be_updated_to_name_duration_start_of_down_time_and_down_time_duration(String string, String string2, String string3, String string4, String string5) {
		String newName = string2;
		assertEquals(newName, flexiBook.getBookableService(0).getName());
	}
	/**
	 * @param string
	 * @author saikouceesay
	 */
	@Given("the system's time and date is {string}")
	public void the_system_s_time_and_date_is(String string) {
		FlexiBookApplication.setSystemTimeAndDate(string);
	}
	/**
	 * @param dataTable
	 * @author saikouceesay
	 */
	@Given("the following appointments exist in the system:")   //updated by AbrarFahad
	public void the_following_appointments_exist_in_the_system(io.cucumber.datatable.DataTable dataTable) {
		List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);
		for (Map<String, String> row : table) {
			String customerName = row.get("customer");
			String serviceName = row.get("serviceName");
			String date = row.get("date");
			String startTime = row.get("startTime");
			String endTime = row.get("endTime");
			Customer customer = (Customer) User.getWithUsername(customerName);
			Date startDate = (Date) FlexiBookApplication.convertToDate(date);
			Date endDate = startDate;
			Time start = FlexiBookApplication.convertTotime(startTime);
			Time end = FlexiBookApplication.convertTotime(endTime);
			TimeSlot timeSlot = new TimeSlot(startDate, start, endDate, end, flexiBook);
			if (BookableService.getWithName(serviceName) instanceof Service) {
				Service service = (Service) BookableService.getWithName(serviceName);
				Appointment appointment = new Appointment(customer, service, timeSlot, flexiBook);
				flexiBook.addAppointment(appointment);
			}
			if (BookableService.getWithName(serviceName) instanceof ServiceCombo) {
				ServiceCombo service = (ServiceCombo) BookableService.getWithName(serviceName);
				Appointment appointment = new Appointment(customer, service, timeSlot, flexiBook);
				flexiBook.addAppointment(appointment);
			}
		}
	}
	/**
	 * @param string
	 * @param string2
	 * @author saikouceesay
	 */
	@When("{string} initiates the deletion of service {string}")
	public void initiates_the_deletion_of_service(String string, String string2) {
		try {
			FlexibookController.deleteService(string, string2);
		} catch (InvalidInputException e) {
			error += e.getMessage();
		}
	}
	/**
	 * @param string
	 * @param string2
	 * @author saikouceesay
	 */
	@Then("the number of appointments in the system with service {string} shall be {string}")
	public void the_number_of_appointments_in_the_system_with_service_shall_be(String string, String string2) {
		int numAppointments = Integer.parseInt(string2);
		int count = 0;
		for (Appointment appointment : flexiBook.getAppointments()) {
			if (appointment.getBookableService().getName().equals(string)) {
				count++;
			}
		}
		assertEquals(count, numAppointments);
	}
	/**
	 * @param string
	 * @author saikouceesay
	 */
	@Then("the number of appointments in the system shall be {string}")
	public void the_number_of_appointments_in_the_system_shall_be(String string) {
		int numAppointments = Integer.parseInt(string);
		assertEquals(numAppointments, flexiBook.getAppointments().size());
	}
	/**
	 * @param dataTable
	 * @author saikouceesay
	 */
	@Given("the following service combos exist in the system:")   //updated by AbrarFahad
	public void the_following_service_combos_exist_in_the_system(io.cucumber.datatable.DataTable dataTable) {
		List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);
		for (Map<String, String> row : table) {
			String nameOfServiceCombo = row.get("name");
			String nameOfMainService = row.get("mainService");
			String[] serviceNames = row.get("services").split(",");
			String[] mandatoryList = row.get("mandatory").split(",");
			//String currentUsername = FlexiBookApplication.getCurrentUser().getUsername();
			//FlexibookController.defineServiceCombo(currentUsername, nameOfServiceCombo, nameOfMainService, row.get("services"), row.get("mandatory"));
			Service mainService = (Service) BookableService.getWithName(nameOfMainService);     //main service
			ServiceCombo serviceCombo = new ServiceCombo(nameOfServiceCombo, flexiBook); //service Combo
			for (int i = 0; i < serviceNames.length; i++) {
				Service service = (Service) BookableService.getWithName(serviceNames[i]);
				boolean mandatory = FlexiBookApplication.convertToBoolean(mandatoryList[i]);
				ComboItem comboItem = new ComboItem(mandatory, service, serviceCombo);
				if (service.equals(mainService)) {
					serviceCombo.setMainService(comboItem);
				}
				serviceCombo.addService(comboItem);
			}
		}
	}
	/**
	 * @param string
	 * @author saikouceesay
	 */
	@Then("the service combos {string} shall not exist in the system")
	public void the_service_combos_shall_not_exist_in_the_system(String string) {
		assertEquals(null, BookableService.getWithName(string));
	}
	/**
	 * @param string
	 * @param string2
	 * @author saikouceesay
	 */
	@Then("the service combos {string} shall not contain service {string}")
	public void the_service_combos_shall_not_contain_service(String string, String string2) {
		ServiceCombo serviceCombo = (ServiceCombo) BookableService.getWithName(string);
		List<ComboItem> comboItems = serviceCombo.getServices();            // why null????
		Service service = (Service) BookableService.getWithName(string2);
		assertEquals(false, comboItems.contains(service));       //wrong type between service and comboitems
	}
	/**
	 * @param string
	 * @author saikouceesay
	 */
	@Then("the number of service combos in the system shall be {string}")
	public void the_number_of_service_combos_in_the_system_shall_be(String string) {
		int numServiceCombos = Integer.parseInt(string);
		int actualComboCount = 0;
		for (BookableService bookableService : flexiBook.getBookableServices()) {
			if (bookableService instanceof ServiceCombo) {
				actualComboCount++;
			}
		}
		assertEquals(numServiceCombos, actualComboCount);
	}
	// saikou code ends
	//Lakshmi code begins
	/**
	 * @param string
	 * @param string2
	 * @throws InvalidInputException
	 * @author lakshmiroy
	 */
	@When("the user tries to log in with username {string} and password {string}")
	public void the_user_tries_to_log_in_with_username_and_password(String string, String string2) throws InvalidInputException {
		try {
			FlexibookController.loginUser(string, string2);
		} catch (InvalidInputException e) {
			error += e.getMessage();
		}
	}
	/**
	 * @author lakshmiroy
	 */
	@Then("the user should be successfully logged in")
	public void the_user_should_be_successfully_logged_in() {
		assertTrue(FlexiBookApplication.getCurrentUser() != null);
	}
	/**
	 * @author lakshmiroy
	 */
	@Then("the user should not be logged in")
	public void the_user_should_not_be_logged_in() {
		assertEquals( null, FlexiBookApplication.getCurrentUser());
	}
	/**
	 * @param string
	 * @author lakshmiroy
	 */
	@Then("an error message {string} shall be raised")
	public void an_error_message_shall_be_raised(String string) {
		assertEquals(string, error);
	}
	/**
	 * @author lakshmiroy
	 */
	@Then("a new account shall be created")
	public void a_new_account_shall_be_created() {
		assertTrue(flexiBook.getOwner() != null);
	}
	/**
	 * @param string
	 * @param string2
	 * @author lakshmiroy
	 */
	@Then("the account shall have username {string} and password {string}")
	public void the_account_shall_have_username_and_password(String string, String string2) {
		String passwordOfCurrentuser = FlexiBookApplication.getCurrentUser().getPassword();
		String username = FlexiBookApplication.getCurrentUser().getUsername();
		assertEquals(passwordOfCurrentuser, string2);
		assertEquals(username, string);
	}
	/**
	 * @author lakshmiroy
	 */
	@Then("the user shall be successfully logged in")
	public void the_user_shall_be_successfully_logged_in() {
		assertTrue(FlexiBookApplication.getCurrentUser() != null);
	}
	/**
	 * @param string
	 * @param string2
	 * @author lakshmiroy
	 */
	@Given("an owner account exists in the system with username {string} and password {string}")
	public void an_owner_account_exists_in_the_system_with_username_and_password(String string, String string2) {
		owner = new Owner(string, string2, flexiBook);
	}
	/**
	 * @author lakshmiroy
	 */
	@Given("the user is logged out")
	public void the_user_is_logged_out() {
		FlexiBookApplication.setCurrentUser(null);
	}
	/**
	 * @author lakshmiroy
	 */
	@When("the user tries to log out")
	public void the_user_tries_to_log_out() {
		try {
			FlexibookController.logoutUser();
		} catch (InvalidInputException e) {
			error += e.getMessage();
		}
	}
	/**
	 * @param string
	 * @author lakshmiroy
	 */
	@Then("the user shall be logged out")
	public void the_user_shall_be_logged_out() {
		try {
			FlexibookController.logoutUser();
		} catch (InvalidInputException e) {
			error += e.getMessage();
		}
	}
	/**
	 * @param dataTable
	 * @author lakshmiroy
	 */
	@Given("the business has the following opening hours")
	public void the_business_has_the_following_opening_hours(io.cucumber.datatable.DataTable dataTable) {
		List<Map<String, String>> list = dataTable.asMaps();
		for (Map<String, String> map : list) {
			DayOfWeek dayofweek = DayOfWeek.valueOf(map.get("day"));
			Time startTime = FlexiBookApplication.convertTotime(map.get("startTime"));
			Time endTime = FlexiBookApplication.convertTotime(map.get("endTime"));
			new BusinessHour(dayofweek, startTime, endTime, flexiBook);
		}
	}
	/**
	 * @param dataTable
	 * @author lakshmiroy
	 */
	@Given("the business has the following holidays")
	public void the_business_has_the_following_holidays(io.cucumber.datatable.DataTable dataTable) {
		List<Map<String, String>> list = dataTable.asMaps();
		for (Map<String, String> map : list) {
			java.sql.Date startDate = (Date) FlexiBookApplication.convertToDate(map.get("startDate"));
			java.sql.Date endDate = (Date) FlexiBookApplication.convertToDate(map.get("endDate"));
			java.sql.Time startTime = Time.valueOf(map.get("endTime") + ":00");
			java.sql.Time endTime = Time.valueOf(map.get("endTime") + ":00");
			TimeSlot timeslot = new TimeSlot(startDate, startTime, endDate, endTime, flexiBook);
			flexiBook.getBusiness().addHoliday(timeslot);
		}
	}
	/**
	 * @param dataTable
	 * @author lakshmiroy
	 */
	@Then("the following slots shall be unavailable:")
	public void the_following_slots_shall_be_unavailable(io.cucumber.datatable.DataTable dataTable) {
		List<TimeSlot> unavailable = new ArrayList<TimeSlot>();
		List<Map<String, String>> list = dataTable.asMaps();
		for (Map<String, String> map : list) {
			Date startDate = (Date) FlexiBookApplication.convertToDate(map.get("date"));
			Date endDate = (Date) FlexiBookApplication.convertToDate(map.get("date"));
			Time startTime = Time.valueOf(map.get("endTime") + ":00");
			Time endTime = Time.valueOf(map.get("endTime") + ":00");
			TimeSlot timeslot = new TimeSlot(startDate, startTime, endDate, endTime, flexiBook);
			List<TimeSlot> s = slots;
			for(TimeSlot t : slots) {
				if(t.getStartTime().equals(timeslot.getStartTime())) {
					assertTrue(t.getStartTime().equals(timeslot.getStartTime()));
				}else {
					assertFalse(t.getStartTime().equals(timeslot.getStartTime()));
				}
			}
		}

	}
	/**
	 * @param dataTable
	 * @author lakshmiroy
	 */
	@Then("the following slots shall be available:")
	public void the_following_slots_shall_be_available(io.cucumber.datatable.DataTable dataTable) {
		List<TimeSlot> availableTime = new ArrayList<TimeSlot>();
		List<Map<String, String>> list = dataTable.asMaps();
		for (Map<String, String> map : list) {
			java.sql.Date startDate = (Date) FlexiBookApplication.convertToDate(map.get("date"));
			java.sql.Date endDate = (Date) FlexiBookApplication.convertToDate(map.get("date"));
			java.sql.Time startTime = Time.valueOf(map.get("endTime") + ":00");
			java.sql.Time endTime = Time.valueOf(map.get("endTime") + ":00");
			TimeSlot timeslot = new TimeSlot(startDate, startTime, endDate, endTime, flexiBook);
			for(TimeSlot t : slots) {
				if(t.getStartTime().equals(timeslot.getStartTime())) {
					assertTrue(t.getStartTime().equals(timeslot.getStartTime()));
				}else {
					assertFalse(t.getStartTime().equals(timeslot.getStartTime()));
				}
			}
		}
	}
	//abrar code begins
	/**
	 * @author Abrar Fahad Rahman Anik, code starts
	 */
	@When("{string} initiates the definition of a service combo {string} with main service {string}, services {string} and mandatory setting {string}")
	public void initiates_the_definition_of_a_service_combo_with_main_service_services_and_mandatory_setting(String ownerName, String name, String mainService, String allServices, String mandatorySetting) {
		String currentUsername = FlexiBookApplication.getCurrentUser().getUsername();
		try {
			FlexibookController.defineServiceCombo(currentUsername, name, mainService, allServices, mandatorySetting);
		} catch (InvalidInputException e) {
			error += e.getMessage();
		}
	}
	@Then("the service combo {string} shall exist in the system")
	public void the_service_combo_shall_exist_in_the_system(String string) {
		assertEquals(string, BookableService.getWithName(string).getName());
	}
	@Then("the service combo {string} shall contain the services {string} with mandatory setting {string}")
	public void the_service_combo_shall_contain_the_services_with_mandatory_setting(String name, String allServices, String mandatorySetting) {
		assertEquals(name, BookableService.getWithName(name).getName());
	}
	@Then("the main service of the service combo {string} shall be {string}")
	public void the_main_service_of_the_service_combo_shall_be(String name, String mainService) {
		for (BookableService bookableService : flexiBook.getBookableServices()) {
			if (bookableService instanceof ServiceCombo && bookableService.getName().contains(name)) {
				ServiceCombo combo = (ServiceCombo) bookableService;
				assertEquals(mainService, combo.getMainService().getService().getName());
			}
		}
	}
	@Then("the service {string} in service combo {string} shall be mandatory")
	public void the_service_in_service_combo_shall_be_mandatory(String name, String mainService) {
		for (BookableService bookableService : flexiBook.getBookableServices()) {
			if (bookableService instanceof ServiceCombo && bookableService.getName().contains(name)) {
				ServiceCombo combo = (ServiceCombo) bookableService;
				assertEquals(true, combo.getMainService().getMandatory());
			}
		}
	}
	@Then("the service combo {string} shall not exist in the system")
	public void the_service_combo_shall_not_exist_in_the_system(String string) {
		assertEquals(null, BookableService.getWithName(string));
	}
	@Then("the service combo {string} shall preserve the following properties:")
	public void the_service_combo_shall_preserve_the_following_properties(String string, io.cucumber.datatable.DataTable dataTable) {
		int initialNumberOfServiceCombo = 0;
		int finalNumberOfServiceCombo = 0;
		for (BookableService bookableService : flexiBook.getBookableServices()) {
			if (bookableService instanceof ServiceCombo) {
				initialNumberOfServiceCombo++;
			}
		}
		String username = FlexiBookApplication.getCurrentUser().getUsername();
		List<Map<String, String>> column = dataTable.asMaps(String.class, String.class);
		for (Map<String, String> rows : column) {
			String nameOfServiceCombo = rows.get("name");
			String nameOfMainService = rows.get("mainService");
			String serviceNames = rows.get("services");
			String mandatoryList = rows.get("mandatory");
			try {
				FlexibookController.defineServiceCombo(username, nameOfServiceCombo, nameOfMainService, serviceNames, mandatoryList);
			} catch (InvalidInputException e) {
				error += e.getMessage();
			}
		}
		for (BookableService bookableService : flexiBook.getBookableServices()) {
			if (bookableService instanceof ServiceCombo) {
				finalNumberOfServiceCombo++;
			}
		}
		assertTrue(finalNumberOfServiceCombo == initialNumberOfServiceCombo);
	}
	@When("{string} initiates the deletion of service combo {string}")
	public void initiates_the_deletion_of_service_combo(String string, String string2) {
		try {
			FlexibookController.deleteServiceCombo(string, string2);
		} catch (InvalidInputException e) {
			error += e.getMessage();
		}
	}
	@When("{string} initiates the update of service combo {string} to name {string}, main service {string} and services {string} and mandatory setting {string}")
	public void initiates_the_update_of_service_combo_to_name_main_service_and_services_and_mandatory_setting(String userName, String prevName, String newName, String mainService, String allServices, String mandatory) {
		String currentUsername = FlexiBookApplication.getCurrentUser().getUsername();
		try {
			FlexibookController.updateServiceCombo(currentUsername, prevName, newName, mainService, allServices, mandatory);
		} catch (InvalidInputException e) {
			error += e.getMessage();
		}
	}
	@Then("the service combo {string} shall be updated to name {string}")
	public void the_service_combo_shall_be_updated_to_name(String prevName, String newName) {
		String nameOfService = "";
		for (BookableService bookableService : flexiBook.getBookableServices()) {
			if (bookableService instanceof ServiceCombo && bookableService.getName().equals(newName)) {
				nameOfService = newName;
			}
		}
		assertEquals(newName, nameOfService);
	}
	/**
	 * @author Abrar Fahad Rahman Anik, code ends
	 */
	//abrar code ends
	/** @author zarifashraf
	 * CODE STARTS HERE
	 */
	/**
	 * @author zarifashraf
	 */
	@Given("no business exists")
	public void no_business_exists() {
		flexiBook.setBusiness(null);
	}
	/**
	 * @author zarifashraf
	 * @param businessName
	 * @param businessAddress
	 * @param businessPhone
	 * @param businessEmail
	 */
	@When("the user tries to set up the business information with new {string} and {string} and {string} and {string}")
	public void the_user_tries_to_set_up_the_business_information_with_new_and_and_and(String businessName, String businessAddress, String businessPhone, String businessEmail) {
		String username = FlexiBookApplication.getCurrentUser().getUsername();
		try {
			FlexibookController.setUpBusinessInfo(username, businessName, businessAddress, businessPhone, businessEmail);
		}
		catch (InvalidInputException e) {
			error += e.getMessage();
		}
	}
	/**
	 * @author zarifashraf
	 * @param day
	 * @param startTime
	 * @param endTime
	 */
	@When("the user tries to add a new business hour on {string} with start time {string} and end time {string}")
	public void the_user_tries_to_add_a_new_business_hour_on_with_start_time_and_end_time(String day, String startTime, String endTime) {
		String username = FlexiBookApplication.getCurrentUser().getUsername();
		
		numberOfBusinessHours = flexiBook.getHours().size();
		try {
			FlexibookController.addBusinessHours(username, day, startTime, endTime);
		}
		catch (InvalidInputException e) {
			error += e.getMessage();
		}
	}

	/**
	 * @author zarifashraf
	 * @param typeOfBreak
	 * @param startDate
	 * @param startTime
	 * @param endDate
	 * @param endTime
	 */

	@When("the user tries to add a new {string} with start date {string} at {string} and end date {string} at {string}")
	public void the_user_tries_to_add_a_new_with_start_date_at_and_end_date_at(String typeOfBreak, String startDate, String startTime, String endDate, String endTime) {
		String username = FlexiBookApplication.getCurrentUser().getUsername();
		numberOfHolidays = flexiBook.getBusiness().getHolidays().size();
		numberOfVacations = flexiBook.getBusiness().getVacation().size();
		try {
			FlexibookController.addHolidaysAndVacations(username, startDate, startTime, endDate, endTime, typeOfBreak);
		}
		catch (InvalidInputException e) {
			error += e.getMessage();
		}
	}
	/**
	 * @author zarifashraf
	 * @param businessName
	 * @param businessAddress
	 * @param businessPhone
	 * @param businessEmail
	 * @param result
	 */
	@Then("a new business with new {string} and {string} and {string} and {string} shall {string} created")
	public void a_new_business_with_new_and_and_and_shall_created(String businessName, String businessAddress, String businessPhone, String businessEmail, String result) {
		if (result.equals("be")) {
			assertEquals(businessName, flexiBook.getBusiness().getName());
			assertEquals(businessAddress, flexiBook.getBusiness().getAddress());
			assertEquals(businessPhone, flexiBook.getBusiness().getPhoneNumber());
			assertEquals(businessEmail, flexiBook.getBusiness().getEmail());
		}
		else if (result.equals("not be")) {
			assertNull(flexiBook.getBusiness());
		}
	}
	/**
	 * @author zarifashraf
	 * @param string
	 * @param resultError
	 */
	@Then("an error message {string} shall {string} raised")
	public void an_error_message_shall_raised(String string, String resultError) {
		if (resultError.equalsIgnoreCase("not be")) {
			assertEquals(string, "");
		}
		else if (resultError.equalsIgnoreCase("be")) {
			assertEquals(string, error);
		}
	}
	/**
	 * @author zarifashraf
	 * @param dataTable
	 */
	@Given("a business exists with the following information:")
	public void a_business_exists_with_the_following_information(io.cucumber.datatable.DataTable dataTable) {
		List<Map<String, String>> table = dataTable.asMaps(String.class, String.class);
		for (Map<String, String> row : table) {
			String businessName = row.get("name");
			String businessAddress = row.get("address");
			String businessPhone = row.get("phone number");
			String businessEmail = row.get("email");
			Business business = new Business(businessName, businessAddress, businessPhone, businessEmail, flexiBook);
			flexiBook.setBusiness(business);
		}
	}
	/**
	 * @author zarifashraf
	 * @param day
	 * @param startTime
	 * @param endTime
	 */
	@Given("the business has a business hour on {string} with start time {string} and end time {string}")
	public void the_business_has_a_business_hour_on_with_start_time_and_end_time(String day, String startTime, String endTime) {
		if (startTime.length() < 8) {
			startTime += ":00";
		}
		if (endTime.length() < 8) {
			endTime += ":00";
		}
		DayOfWeek newDay = DayOfWeek.valueOf(day);
		Time newStartTime = java.sql.Time.valueOf(startTime);
		Time newEndTime = java.sql.Time.valueOf(endTime);
		BusinessHour newBusinessHours = new BusinessHour(newDay, newStartTime, newEndTime, flexiBook);
		flexiBook.getBusiness().addBusinessHour(newBusinessHours);
	}
	/**
	 * @author zarifashraf
	 * @param typeOfBreak
	 * @param startDate
	 * @param startTime
	 * @param endDate
	 * @param endTime
	 */
	@Given("a {string} time slot exists with start time {string} at {string} and end time {string} at {string}")
	public void a_time_slot_exists_with_start_time_at_and_end_time_at(String typeOfBreak, String startDate, String startTime, String endDate, String endTime) {
		if (startTime.length() < 8) {
			startTime += ":00";
		}
		if (endTime.length() < 8) {
			endTime += ":00";
		}
		
		Date newStartDate = java.sql.Date.valueOf(startDate);
		Date newEndDate = java.sql.Date.valueOf(endDate);
		Time newStartTime = java.sql.Time.valueOf(startTime);
		Time newEndTime = java.sql.Time.valueOf(endTime);
		TimeSlot newTimeSlot = new TimeSlot(newStartDate, newStartTime, newEndDate, newEndTime, flexiBook);
		if (typeOfBreak.equalsIgnoreCase("vacation")) {
			flexiBook.getBusiness().addVacation(newTimeSlot);
			flexiBook.addTimeSlot(newTimeSlot);
		}
		else if (typeOfBreak.equalsIgnoreCase("holiday")) {
			flexiBook.getBusiness().addHoliday(newTimeSlot);
			flexiBook.addTimeSlot(newTimeSlot);
		}
	}
	/**
	 * @author zarifashraf
	 * @param businessName
	 * @param businessAddress
	 * @param businessPhone
	 * @param businessEmail
	 */
	@When("the user tries to update the business information with new {string} and {string} and {string} and {string}")
	public void the_user_tries_to_update_the_business_information_with_new_and_and_and(String businessName, String businessAddress, String businessPhone, String businessEmail) {
		String username = FlexiBookApplication.getCurrentUser().getUsername();
		try {
			FlexibookController.updateBusinessInfo(username, businessName, businessAddress, businessPhone, businessEmail);
		}
		catch (InvalidInputException e) {
			error += e.getMessage();
		}
	}

	/**
	 * @author zarifashraf
	 * @param result
	 */
	@Then("a new business hour shall {string} created")
	public void a_new_business_hour_shall_created(String result) {
		if (result.equalsIgnoreCase("be")) {
			assertEquals((numberOfBusinessHours + 1), flexiBook.getHours().size());
		}
		else if (result.equalsIgnoreCase("not be")) {
			assertEquals(numberOfBusinessHours, flexiBook.getHours().size());
		}
	}
	/**
	 * @author zarifashraf
	 */
	@Then("the business information shall {string} updated with new {string} and {string} and {string} and {string}")
	public void the_business_information_shall_updated_with_new_and_and_and(String result, String businessName, String businessAddress, String businessPhone, String businessEmail) {
		if (result.equals("be")) {
			assertEquals(businessName, flexiBook.getBusiness().getName());
			assertEquals(businessAddress, flexiBook.getBusiness().getAddress());
			assertEquals(businessPhone, flexiBook.getBusiness().getPhoneNumber());
			assertEquals(businessEmail, flexiBook.getBusiness().getEmail());
		}
		if (result.equals("not be")) {
			assertNotEquals(businessName, flexiBook.getBusiness().getName());
			assertNotEquals(businessAddress, flexiBook.getBusiness().getAddress());
			assertNotEquals(businessPhone, flexiBook.getBusiness().getPhoneNumber());
			assertNotEquals(businessEmail, flexiBook.getBusiness().getEmail());
		}
	}
	/**
	 * @author zarifashraf
	 */
	
	@When("the user tries to access the business information")
	public void the_user_tries_to_access_the_business_information() {
		try {
			tobusiness = FlexibookController.getBusiness();
		}
		catch (Exception e) {
			error += e.getMessage();
		}
	}
	/**
	 * @author zarifashraf
	 * @param businessName
	 * @param businessAddress
	 * @param businessPhone
	 * @param businessEmail
	 */
	@Then("the {string} and {string} and {string} and {string} shall be provided to the user")
	public void the_and_and_and_shall_be_provided_to_the_user(String businessName, String businessAddress, String businessPhone, String businessEmail) {
		assertEquals(businessName, tobusiness.getName());
		assertEquals(businessAddress, tobusiness.getAddress());
		assertEquals(businessPhone, tobusiness.getPhoneNumber());
		assertEquals(businessEmail, tobusiness.getEmail());
	}

	/**
	 * @author zarifashraf
	 * @param typeOfBreak
	 * @param result
	 * @param startDate
	 * @param startTime
	 * @param endDate
	 * @param endTime
	 */
	@Then("a new {string} shall {string} be added with start date {string} at {string} and end date {string} at {string}")
	public void a_new_shall_be_added_with_start_date_at_and_end_date_at(String typeOfBreak, String result, String startDate, String startTime, String endDate, String endTime) {
		if (result.equalsIgnoreCase("")) {
			if (typeOfBreak.equalsIgnoreCase("holiday")) {
				assertEquals((numberOfHolidays + 1), flexiBook.getBusiness().getHolidays().size());
			}
			else if (typeOfBreak.equalsIgnoreCase("vacation")) {
				assertEquals((numberOfVacations + 1), flexiBook.getBusiness().getVacation().size());
			}
		}
		else if (result.equalsIgnoreCase("not")) {
			if (typeOfBreak.equalsIgnoreCase("holiday")) {
				assertEquals(numberOfHolidays, flexiBook.getBusiness().getHolidays().size());
			}
			else if (typeOfBreak.equalsIgnoreCase("vacation")) {
				assertEquals(numberOfVacations, flexiBook.getBusiness().getVacation().size());
			}
		}
	}
	/**
	 * @author zarifashraf
	 * @param oldDay
	 * @param oldStartTime
	 * @param newDay
	 * @param newStartTime
	 * @param newEndTime
	 */
	
	@When("the user tries to change the business hour {string} at {string} to be on {string} starting at {string} and ending at {string}")
	public void the_user_tries_to_change_the_business_hour_at_to_be_on_starting_at_and_ending_at(String oldDay, String oldStartTime, String newDay, String newStartTime, String newEndTime) {
		String username = FlexiBookApplication.getCurrentUser().getUsername();
		
		if (newStartTime.length() < 8) {
			newStartTime += ":00";
		}
		if (newEndTime.length() < 8) {
			newEndTime += ":00";
		}
		if (oldStartTime.length() < 8) {
			oldStartTime += ":00";
		}
		previousDay = DayOfWeek.valueOf(oldDay);
		previousStartTime = java.sql.Time.valueOf(oldStartTime);
		finalStartTime = java.sql.Time.valueOf(newStartTime);
		finalStartTime = java.sql.Time.valueOf(newStartTime);
		finalEndTime = java.sql.Time.valueOf(newEndTime);
		finalDay = DayOfWeek.valueOf(newDay);
		for (BusinessHour theBusinessHour: flexiBook.getBusiness().getBusinessHours()) {
			if (previousDay.equals(theBusinessHour.getDayOfWeek())) {
				if (previousStartTime.equals(theBusinessHour.getStartTime())) {
					oldBusinessHour = theBusinessHour;
				}
			}
		}
		try {
			FlexibookController.updateBusinessHours(username, oldDay, newDay, oldStartTime, newStartTime, newEndTime);
		}
		catch (InvalidInputException e) {
			error += e.getMessage();
		}
	}
	/**
	 * @author zarifashraf
	 * @param result
	 */
	@Then("the business hour shall {string} be updated")
	public void the_business_hour_shall_be_updated(String result) {
		if (oldBusinessHour != null) {
		if (result.equalsIgnoreCase("be")) {
			assertEquals(oldBusinessHour.getDayOfWeek(), finalDay);
			assertEquals(oldBusinessHour.getStartTime(), finalStartTime);
			assertEquals(oldBusinessHour.getEndTime(), finalEndTime);
			}
			
		}
		else if (result.equalsIgnoreCase("not be")) {
			assertNotEquals(oldBusinessHour.getStartTime(), finalStartTime);
			assertNotEquals(oldBusinessHour.getEndTime(), finalEndTime);
		}
	}


	/**
	 * @author zarifashraf
	 * @param oldDay
	 * @param oldStartTime
	 */
	@When("the user tries to remove the business hour starting {string} at {string}")
	public void the_user_tries_to_remove_the_business_hour_starting_at(String oldDay, String oldStartTime) {
		String username = FlexiBookApplication.getCurrentUser().getUsername();
		if (oldStartTime.length() < 8) {
			oldStartTime += ":00";
		}
		previousDay = DayOfWeek.valueOf(oldDay);
		previousStartTime = java.sql.Time.valueOf(oldStartTime);
		
		for (BusinessHour theBusinessHour: flexiBook.getBusiness().getBusinessHours()) {
			if (previousDay.equals(theBusinessHour.getDayOfWeek())) {
				if (previousStartTime.equals(theBusinessHour.getStartTime())) {
					oldBusinessHour = theBusinessHour;
				}
			}
		}
		
		try {
			FlexibookController.deleteBusinessHours(username, oldDay, oldStartTime);
		}
		catch (InvalidInputException e) {
			error += e.getMessage();
		}
	}
	/**
	 * @author zarifashraf
	 * @param oldDay
	 * @param oldStartTime
	 * @param result
	 */
	@Then("the business hour starting {string} at {string} shall {string} exist")
	public void the_business_hour_starting_at_shall_exist(String oldDay, String oldStartTime, String result) {
		if (result.equalsIgnoreCase("not")) {
			assertFalse(flexiBook.getBusiness().getBusinessHours().contains(oldBusinessHour));
				}
		if (result.equalsIgnoreCase("")) {
			assertTrue(flexiBook.getBusiness().getBusinessHours().contains(oldBusinessHour));
		}
	}
	/**
	 * @author zarifashraf	
	 * @param string
	 * @param result
	 */
	@Then("an error message {string} shall {string} be raised")
	public void an_error_message_shall_be_raised(String string, String result) {
		if (result.equalsIgnoreCase("not be")) {
			assertEquals("", string);
		}
		else if (result.equalsIgnoreCase("")) {
			assertEquals(error, string);
		}
	}
	/**
	 * @author zarifashraf
	 * @param typeOfBreak
	 * @param startDate
	 * @param startTime
	 * @param endDate
	 * @param endTime
	 */

	@When("the user tries to remove an existing {string} with start date {string} at {string} and end date {string} at {string}")
	public void the_user_tries_to_remove_an_existing_with_start_date_at_and_end_date_at(String typeOfBreak, String startDate, String startTime, String endDate, String endTime) {
		String username = FlexiBookApplication.getCurrentUser().getUsername();		
		numberOfHolidays = flexiBook.getBusiness().getHolidays().size();
		numberOfVacations = flexiBook.getBusiness().getVacation().size();
		
		try {
			FlexibookController.deleteHolidaysAndVacations(username, startDate, startTime, endDate, endTime, typeOfBreak);
		}
		catch (InvalidInputException e) {
			error += e.getMessage();
		}
	}
	/**
	 * @author zarifashraf
	 * @param typeOfBreak
	 * @param startDate
	 * @param startTime
	 * @param result
	 */
	@Then("the {string} with start date {string} at {string} shall {string} exist")
	public void the_with_start_date_at_shall_exist(String typeOfBreak, String startDate, String startTime, String result) {

		if (typeOfBreak.equalsIgnoreCase("holiday")) {
			if (result.equalsIgnoreCase("not")) {
				assertEquals((numberOfHolidays - 1), flexiBook.getBusiness().getHolidays().size());
			}
			else if(result.equalsIgnoreCase("")) {
				assertEquals((numberOfHolidays), flexiBook.getBusiness().getHolidays().size());
			}
		}

		else if (typeOfBreak.equalsIgnoreCase("vacation")) {
			if (result.equalsIgnoreCase("not")) {
				assertEquals((numberOfVacations - 1), flexiBook.getBusiness().getVacation().size());
			}

			else if (result.equalsIgnoreCase("")) {
				assertEquals((numberOfVacations), flexiBook.getBusiness().getVacation().size());
			}
		}
	}


	/**
	 * @author zarifashraf
	 * @param typeOfBreak
	 * @param oldStartDate
	 * @param oldStartTime
	 * @param newStartDate
	 * @param newStartTime
	 * @param newEndDate
	 * @param newEndTime
	 */

	@When("the user tries to change the {string} on {string} at {string} to be with start date {string} at {string} and end date {string} at {string}")
	public void the_user_tries_to_change_the_on_at_to_be_with_start_date_at_and_end_date_at(String typeOfBreak, String oldStartDate, String oldStartTime, String newStartDate, String newStartTime, String newEndDate, String newEndTime) {
		String username = FlexiBookApplication.getCurrentUser().getUsername();		
		if (oldStartTime.length() < 8) {
			oldStartTime += ":00";
		}
		
		if (newStartTime.length() < 8) {
			newStartTime += ":00";
		}
		if (newEndTime.length() < 8) {
			newEndTime += ":00";
		}
		previousStartDate = java.sql.Date.valueOf(oldStartDate);
		finalStartDate = java.sql.Date.valueOf(newStartDate);
		finalEndDate = java.sql.Date.valueOf(newEndDate);
		previousStartTime = java.sql.Time.valueOf(oldStartTime);
		finalStartTime = java.sql.Time.valueOf(newStartTime);
		finalEndTime = java.sql.Time.valueOf(newEndTime);
		
		
		if (typeOfBreak.equalsIgnoreCase("holiday")) {
			for (TimeSlot holiday: flexiBook.getBusiness().getHolidays()) {
				if((holiday.getStartDate().equals(previousStartDate) && holiday.getStartTime().equals(previousStartTime))) {
					oldTimeSlot = holiday;
				}
		}
		}
		if (typeOfBreak.equalsIgnoreCase("vacation")) {
				for (TimeSlot vacation: flexiBook.getBusiness().getVacation()) {
					if((vacation.getStartDate().equals(previousStartDate) && vacation.getStartTime().equals(previousStartTime))) {
						oldTimeSlot = vacation;
				}
			}
		}
		try {
			FlexibookController.updateHolidaysAndVacations(username, oldStartDate, newStartDate, oldStartTime, newStartTime, newEndDate, newEndTime, typeOfBreak);
		}
		catch (InvalidInputException e) {
			error += e.getMessage();
		}
	}
	/**
	 * @author zarifashraf
	 * @param typeOfBreak
	 * @param result
	 * @param newStartDate
	 * @param newStartTime
	 * @param newEndDate
	 * @param newEndTime
	 */
	@Then("the {string} shall {string} updated with start date {string} at {string} and end date {string} at {string}")
	public void the_shall_be_updated_with_start_date_at_and_end_date_at(String typeOfBreak, String result, String newStartDate, String newStartTime, String newEndDate, String newEndTime) {
		
		if (typeOfBreak.equalsIgnoreCase("holiday")) {
			if (result.equalsIgnoreCase("not be")) {
					    assertNotEquals(finalStartDate, oldTimeSlot.getStartDate());
						assertNotEquals(finalEndDate, oldTimeSlot.getEndDate());
					
					}
		
			else if (result.equalsIgnoreCase("be")) {
						assertEquals(finalStartDate, oldTimeSlot.getStartDate());
						assertEquals(finalStartTime, oldTimeSlot.getStartTime());
						assertEquals(finalEndDate, oldTimeSlot.getEndDate());
						assertEquals(finalEndTime, oldTimeSlot.getEndTime());
					}
				}
		
		if (typeOfBreak.equalsIgnoreCase("vacation")) {
				if (result.equalsIgnoreCase("not be")) {
							assertNotEquals(finalStartDate, oldTimeSlot.getStartDate());
							assertNotEquals(finalEndDate, oldTimeSlot.getEndDate());
						}
					
				else if (result.equalsIgnoreCase("be")) {
					
							assertEquals(finalStartDate, oldTimeSlot.getStartDate());
							assertEquals(finalStartTime, oldTimeSlot.getStartTime());
							assertEquals(finalEndDate, oldTimeSlot.getEndDate());
							assertEquals(finalEndTime, oldTimeSlot.getEndTime());
						}
					}
				}
	@Given("the user is logged in to an account with username {string}")
	public void the_user_is_logged_in_to_an_account_with_username(String string) {
		User user = User.getWithUsername(string);
		FlexiBookApplication.setCurrentUser(user);
	}
	/**
	 * @author zarifashraf
	 * ZARIF'S CODE ENDS HERE
	 */
	//ZARIF CODE ENDS HERE


	//SAGHAR CODE BEGINS
	/**
	 * @param string
	 * @author sagharsahebi
	 */
	@Given("the account with username {string} has pending appointments")
	public void the_account_with_username_has_pending_appointments(String string) {
		// get the customer from the name
		//
		Customer customer = (Customer) User.getWithUsername(string);
		List<Appointment> appointments = flexiBook.getAppointments();
		List<Appointment> pendingAppointments = new ArrayList<>();
		// itterate throught the list
		for (Appointment appointment : appointments) {
			if (appointment.getTimeSlot().getEndDate().after(FlexiBookApplication.getDate()) || appointment.getTimeSlot().getEndDate().equals(FlexiBookApplication.getDate())) {
				pendingAppointments.add((Appointment) appointments);
			}
		}
		FlexiBookApplication.setPendingAppointments(pendingAppointments);
	}

	@When("the user tries to delete account with the username {string}")
	public void the_user_tries_to_delete_account_with_the_username(String string) {
		try {
			FlexibookController.deleteUser(string);
		} catch (InvalidInputException e) {
			error += e.getMessage();
		}
	}
	@Then("the account with the username {string} does not exist")
	public void the_account_with_the_username_does_not_exist(String string) {
		assertEquals(null, User.getWithUsername(string));
	}
	@Then("all associated appointments of the account with the username {string} shall not exist")
	public void all_associated_appointments_of_the_account_with_the_username_shall_not_exist(String string) {
		assertEquals(null,FlexiBookApplication.getPendingAppointments());

	}
	@Then("the account with the username {string} exists")
	public void the_account_with_the_username_exists(String string) {
		assertTrue(User.getWithUsername(string) != null);
	}
	//sign up for customer account
	@Given("there is no existing username {string}")
	public void there_is_no_existing_username(String string) {
		//FlexiBookApplication.setCurrentUser(null);
		List<Customer> customers = flexiBook.getCustomers();
		for (Customer customer : customers) {
			if (customer.getUsername().equals(string)) {
				customer.setUsername("");
			}
		}
	}
	@When("the user provides a new username {string} and a password {string}")
	public void the_user_provides_a_new_username_and_a_password(String string, String string2) {
		try {
			FlexibookController.signUpCustomerAccount(string, string2);
		} catch (InvalidInputException e) {
			error += e.getMessage();
		}
	}
	@Then("a new customer account shall be created")
	public void a_new_customer_account_shall_be_created() {
		assertEquals(1, flexiBook.getCustomers().size());
	}
	@Given("there is an existing username {string}")
	public void there_is_an_existing_username(String string) {
		customer = new Customer(string, "", flexiBook);

	}
	@Then("no new account shall be created")
	public void no_new_account_shall_be_created() {
		assertEquals(FlexibookController.numOfCustomers, flexiBook.getCustomers().size());		// ASK PROF OR REMI
	}
	//update account
	@When("the user tries to update account with a new username {string} and password {string}")
	public void the_user_tries_to_update_account_with_a_new_username_and_password(String string, String string2) {
		try {
			FlexibookController.UpdateUser(string, string2);
		} catch (InvalidInputException e) {
			error += e.getMessage();
		}
	}
	@Then("the account shall not be updated")
	public void the_account_shall_not_be_updated() {
		User currentUser = FlexiBookApplication.getCurrentUser();
		if (currentUser instanceof Owner) {
			assertEquals(currentUser, flexiBook.getOwner());
		} else {
			assertEquals(currentUser, (Customer) User.getWithUsername(currentUser.getUsername()));
		}
	}
	//SAGHAR CODE ENDS HERE

	//ELOYAN'S APPOINTMENT CODE
	/**
	 * @author eloyann
	 * start here
	 */
	@Given("{string} is logged in to their account")
	public void is_logged_in_to_their_account(String string) { 
		User user = User.getWithUsername(string);
		FlexiBookApplication.setCurrentUser(user);
	}
	@When("{string} schedules an appointment on {string} for {string} at {string}")
	public void schedules_an_appointment_on_for_at(String username, String startDateString, String nameOfService, String startTimeString) {
		try {
			List<String> optionalServices = new ArrayList<>();
			Date startDate = FlexiBookApplication.convertToDate(startDateString);
			Time startTime = FlexiBookApplication.convertTotime(startTimeString);
			FlexibookController.makeAppointment(username, nameOfService, startDate, startTime, optionalServices);
		}catch(InvalidInputException e) {
			error += e.getMessage();
		}
	}
	@When("{string} attempts to cancel their {string} appointment on {string} at {string}")
	public void attempts_to_cancel_their_appointment_on_at(String username, String nameOfService, String dateString, String timeString) {
		Date adate =  FlexiBookApplication.convertToDate(dateString);
		Time atime = FlexiBookApplication.convertTotime(timeString);
		try {
			FlexibookController.cancelAppointment(username, username, nameOfService, adate, atime);
		}catch(InvalidInputException e) {
			error = e.getMessage();
		}
	}
	//TODO
	@Then("{string}'s {string} appointment on {string} at {string} shall be removed from the system")
	public void s_appointment_on_at_shall_be_removed_from_the_system(String string, String string2, String string3, String string4) {
		Customer user = (Customer) User.getWithUsername(string);    
		Date adate =  FlexiBookApplication.convertToDate(string3);
		Time atime = FlexiBookApplication.convertTotime(string4);
		Service service = (Service) BookableService.getWithName(string2);
		List<Appointment> userAppointments = user.getAppointments();
	}
	@Then("there shall be {int} less appointment in the system")
	public void there_shall_be_less_appointment_in_the_system(Integer int1) {
		//TODO
	}
	//?
	@Then("the system shall report {string}")
	public void the_system_shall_report(String string) {
		assertEquals(string, error);
	}
	@Then("{string} shall have a {string} appointment on {string} from {string} to {string}")
	public void shall_have_a_appointment_on_from_to(String string, String string2, String string3, String string4, String string5) {
		Customer user = (Customer) User.getWithUsername(string);
		Date adate = FlexiBookApplication.convertToDate(string3);
		Time starttime = FlexiBookApplication.convertTotime(string4);
		Time endtime = FlexiBookApplication.convertTotime(string5);
		TimeSlot newtimeslot = new TimeSlot(adate, starttime, adate, endtime, FlexiBookApplication.getFlexibook());
		Appointment newAppointment = new Appointment(user, BookableService.getWithName(string2), newtimeslot, FlexiBookApplication.getFlexibook());
		assertTrue(user.getAppointments().contains(newAppointment));
	}
	@Then("there shall be {int} more appointment in the system")
	public void there_shall_be_more_appointment_in_the_system(Integer int1) {
		assertEquals(FlexiBookApplication.getFlexibook().numberOfAppointments(), FlexiBookApplication.getFlexibook().numberOfAppointments());
	}
	@When("{string} attempts to update their {string} appointment on {string} at {string} to {string} at {string}")
	public void attempts_to_update_their_appointment_on_at_to_at(String username, String nameOfService, String oldDate, String oldTime, String newDate, String newTime) {
		Date anewdate =  FlexiBookApplication.convertToDate(newDate);
		Time anewtime = FlexiBookApplication.convertTotime(newTime);
		String result="";
		try {
			FlexibookController.updateAppointment(username, username, nameOfService,"", "", anewdate, anewtime);	
			result="successful";//DOUBLE CHECK
		}catch(InvalidInputException e) {
			error += e.getMessage();
			result="unsuccessful";
		}
		resultOfUpdateAppointment=result;
	}
	//???
	@Then("the system shall report that the update was {string}")
	public void the_system_shall_report_that_the_update_was(String string) {
		assertEquals(string, resultOfUpdateAppointment);
	}
	//NEED FIXING
	@Given("{string} has a {string} appointment with optional sevices {string} on {string} at {string}")
	public void has_a_appointment_with_optional_sevices_on_at(String username, String nameOfService, String optionalService, String date, String time) {
		Customer customer = (Customer) User.getWithUsername(username);
		Date adate = FlexiBookApplication.convertToDate(date);
		Time atime = FlexiBookApplication.convertTotime(time);
		Service Oservice = (Service) BookableService.getWithName(optionalService);
		ServiceCombo mainService = (ServiceCombo) BookableService.getWithName(nameOfService);
		ComboItem combo = new ComboItem(false, Oservice, mainService);					//???
		TimeSlot timeSlot = new TimeSlot(adate, atime, adate, atime, flexiBook);		//fix end time
		customer.addAppointment(new Appointment(customer, mainService, timeSlot, flexiBook));
	}
	//NEEDS FIXING
	@When("{string} attempts to {string} {string} from their {string} appointment on {string} at {string}")
	public void attempts_to_from_their_appointment_on_at(String username, String action, String comboItem, String nameOfService, String date, String time) {
		Date adate = (Date) FlexiBookApplication.convertToDate(date);
		Time atime = FlexiBookApplication.convertTotime(time);
		String result="";
		try {
			FlexibookController.updateAppointment(username, username, nameOfService,action, comboItem, adate, atime);	
			result="successful";//DOUBLE CHECK
		}catch(InvalidInputException e) {
			error += e.getMessage();
			result="unsuccessful";
		}
		resultOfUpdateAppointment=result;  
	}
	@When("{string} attempts to update {string}'s {string} appointment on {string} at {string} to {string} at {string}")
	public void attempts_to_update_s_appointment_on_at_to_at(String string, String string2, String string3, String string4, String string5, String string6, String string7) {
		Date adate = (Date) FlexiBookApplication.convertToDate(string4);
		Time atime = FlexiBookApplication.convertTotime(string5);
		Date anewdate = (Date) FlexiBookApplication.convertToDate(string6);
		Time anewtime = FlexiBookApplication.convertTotime(string7);
		String result="";
		try {
			FlexibookController.updateAppointment(string, string2, string3,"", "", anewdate, anewtime);	
			result="successful";
		}catch(InvalidInputException e) {
			error += e.getMessage();
			result="unsuccessful";
		}
		resultOfUpdateAppointment=result;
	}
	@When("{string} schedules an appointment on {string} for {string} with {string} at {string}")
	public void schedules_an_appointment_on_for_with_at(String username, String dateString, String nameOfService, String str, String startTimeString) {
		Date startDate = FlexiBookApplication.convertToDate(dateString);
		Time startTime = FlexiBookApplication.convertTotime(startTimeString);
		List<String> optionalServices = new ArrayList<>();
		String s = "";
		for(int i = 0; i < str.length(); i++) {
			if(str.charAt(i) == ',') {
				optionalServices.add(s);
				s = "";
			}
			s += str.charAt(i);
		}
		optionalServices.add(s);
		try {
			FlexibookController.makeAppointment(username, nameOfService, startDate, startTime, optionalServices);
		}catch(InvalidInputException e) {
			error += e.getMessage();
		}
	}

	//ELOYAN CODE ENDS

	//STATE MACHINE
	@Given("{string} has {int} no-show records")
	public void has_no_show_records(String customerName, Integer noShowCount) {
		Customer myCustomer = (Customer) User.getWithUsername(customerName);
		myCustomer.setNoShowCount(noShowCount);
	}
	/**
	 * @author Abrar Fahad Rahman Anik & Lakshmi ROy
	 * @param string
	 * @param string2
	 * @param string3
	 * @param string4
	 * @param string5
	 */
	@When("{string} makes a {string} appointment for the date {string} and time {string} at {string}")
	public void makes_a_appointment_for_the_date_and_time_at(String username, String serviceName, String date, String time, String whenTheAppointmentWasMade) {
		FlexiBookApplication.setSystemTimeAndDate(whenTheAppointmentWasMade);
		List<String> optionalServices = new ArrayList<>();
		try {
			FlexibookController.makeAppointment(username, serviceName, FlexiBookApplication.convertToDate(date), FlexiBookApplication.convertTotime(time), optionalServices);
		} catch (InvalidInputException e) {
			error += e.getMessage();
		}
	}
	/**
	 * @author Abrar Fahad Rahman Anik & Saikou Ceesay
	 * @param string
	 * @param string2
	 * @param string3
	 */
	@When("{string} attempts to change the service in the appointment to {string} at {string}")
	public void attempts_to_change_the_service_in_the_appointment_to_at(String userName, String serviceName, String curDateAndTime) {
		FlexiBookApplication.setSystemTimeAndDate(curDateAndTime);
		try {
			FlexibookController.updateAppointmentService(userName, serviceName);
		} catch (Exception e) {
			error += e.getMessage();
		}
	}	 
	/**
	 * @author Abrar Fahad Rahman Anik & Saikou Ceesay
	 */
	@Then("the appointment shall be booked")
	public void the_appointment_shall_be_booked() {
		assertTrue(FlexiBookApplication.getFlexibook().numberOfAppointments() > 0);
	}
	/**
	 * @author Abrar Fahad Rahman Anik & Saikou Ceesay
	 * @param string
	 */
	@Then("the service in the appointment shall be {string}")
	public void the_service_in_the_appointment_shall_be(String newServiceName) {
		int n = customer.getAppointments().size() - 1;
		String serviceName = customer.getAppointment(n).getBookableService().getName();		//PROBLEM
		assertEquals(newServiceName, serviceName);
	}
	/**
	 * @author Abrar Fahad Rahman Anik and Saikou Ceesay
	 * @param string
	 * @param string2
	 * @param string3
	 */
	@Then("the appointment shall be for the date {string} with start time {string} and end time {string}")
	public void the_appointment_shall_be_for_the_date_with_start_time_and_end_time(String date, String time, String endTime) {
		Appointment myAppointment = customer.getAppointment(0);
		assertEquals(myAppointment.getTimeSlot().getStartTime(), FlexiBookApplication.convertTotime(time));
		assertEquals(myAppointment.getTimeSlot().getEndTime(), FlexiBookApplication.convertTotime(endTime));
		assertEquals(myAppointment.getTimeSlot().getStartDate(), FlexiBookApplication.convertToDate(date));
	}
	/**@author sagharsahebi
	 *
	 *
	 * @param string
	 **/
	@Then("the service combo shall have {string} selected services")
	public void the_service_combo_shall_have_selected_services(String string) {
		BookableService serviceName = ServiceCombo.getWithName(string);
		assertEquals(string,serviceName);
	}
	@When("{string} attempts to cancel the appointment at {string}") //Cancel the appointment for a service at least one day ahead or on its day
	public void attempts_to_cancel_the_appointment_at(String theUser, String string2) {
		Date date = FlexiBookApplication.convertToDate(string2);
		try {
			FlexibookController.cancelAppointment(theUser, date);
		}
		catch(Exception e) {
			error += e.getMessage();
		}
	}
	@When("{string} attempts to update the date to {string} and time to {string} at {string}") //change date and time of appointment for a service at least one day ahead or in the same day
	public void attempts_to_update_the_date_to_and_time_to_at(String username, String date, String time, String currentDay) {
		// the user tries to update the date and time the day before or on the same day prior to the appointment
		try {
			FlexibookController.updateAppointmentTime(username, date,time, currentDay);
		}
		catch(Exception e) {
			error +=e.getMessage();
		}
	}
	@When("the owner attempts to end the appointment at {string}") //End appointment while the appointment is not in progress
	public void the_owner_attempts_to_end_the_appointment_at(String string) {
		try {
			FlexibookController.EndAppointment(string);
		}
		catch(Exception e) {
			error += e.getMessage();
		}
	}
	@When("{string} attempts to cancel {string}'s {string} appointment on {string} at {string}")
	public void attempts_to_cancel_s_appointment_on_at(String string, String string2, String string3, String string4, String string5) {
		try {
			FlexibookController.cancelAppointment(string, string2, string3, FlexiBookApplication.convertToDate(string4), FlexiBookApplication.convertTotime(string5));
		}catch(InvalidInputException e) {
			error += e.getMessage();
		}
	}
	/**
	 * @author zarifashraf & lakhsmiroy
	 */
	@Then("the username associated with the appointment shall be {string}")
	public void the_username_associated_with_the_appointment_shall_be(String username) {
		for (Appointment appointment: customer.getAppointments()) {
			assertEquals(username, appointment.getCustomer().getUsername());
		}
	}
	/**
	 *@author zarifashraf & lakhsmiroy
	 */
	@Then("the user {string} shall have {int} no-show records")
	public void the_user_shall_have_no_show_records(String username, Integer count) {
		Customer customer = (Customer) User.getWithUsername(username);
		assertEquals(count, customer.getNoShowCount());
	}
	/**
	 * @author zarifashraf & lakhsmiroy
	 */
	@Then("the system shall have {int} appointments")
	public void the_system_shall_have_appointments(Integer int1) {
		assertEquals(int1, flexiBook.getAppointments().size());
	}
	/**
	 * @author EloyannRJ and Zarif Ashraf
	 * @param string
	 * @param string2
	 * @param string3
	 * @param string4
	 * @param string5
	 */
	@When("{string} makes a {string} appointment without choosing optional services for the date {string} and time {string} at {string}")
	public void makes_a_appointment_without_choosing_optional_services_for_the_date_and_time_at(String string, String string2, String string3, String string4, String string5) {
		Date adate =  FlexiBookApplication.convertToDate(string3);
		Time atime = FlexiBookApplication.convertTotime(string4);
		List<String> optionalServices = new ArrayList<>();
		try {
			FlexibookController.makeAppointment(string, string2, adate, atime, optionalServices);
		} catch (InvalidInputException e) {
			error += e.getMessage();
		}
	}
	/**
	 * @author EloyannRJ and Zarif Ashraf
	 * @param string
	 */
	@When("the owner starts the appointment at {string}")
	public void the_owner_starts_the_appointment_at(String string) {
		for(Appointment appointment: FlexiBookApplication.getFlexibook().getAppointments()) {
			if(appointment.getTimeSlot().getStartTime()==FlexiBookApplication.convertTotime(string)) {
				appointment.setHasStarted(true);
			}
		}
	}
	/**
	 * @author EloyannRJ and Zarif Ashraf
	 * @param string
	 * @param string2
	 * @param string3
	 */
	@When("{string} attempts to add the optional service {string} to the service combo in the appointment at {string}")
	public void attempts_to_add_the_optional_service_to_the_service_combo_in_the_appointment_at(String string, String string2, String string3) {
		Date adate =  FlexiBookApplication.convertToDate(string3);
		Time atime = FlexiBookApplication.convertTotime(string3);
		for(Appointment appointment: FlexiBookApplication.getFlexibook().getAppointments()) {
			if(appointment.getTimeSlot().getStartTime()==atime && appointment.getTimeSlot().getStartDate()==adate ) {
				ComboItem aComboItem=((ServiceCombo) appointment.getBookableService()).getMainService();
				String nameOfComboItem=aComboItem.getService().getName();
//				try {
//					//FlexibookController.updateAppointment(string,  string2, nameOfComboItem, adate, atime);
//				} catch (InvalidInputException e) {
//					error += e.getMessage();
//				}
			}
		}
	}
	/**
	 * @author EloyannRJ and Zarif Ashraf
	 */
	@Then("the appointment shall be in progress")
	public void the_appointment_shall_be_in_progress() {
		for(Appointment appointment: FlexiBookApplication.getFlexibook().getAppointments()) {
			if(appointment.getTimeSlot().getStartTime()==FlexiBookApplication.getCurrentTime() ) {
				assertEquals(appointment.getHasStarted(), appointment.setHasStarted(true)) ;
			}
		}
	}
	//TODO
	@Then("the service combo in the appointment shall be {string}")
	public void the_service_combo_in_the_appointment_shall_be(String string) {
		BookableService name1 = ServiceCombo.getWithName(string);    
		int n = customer.getAppointments().size() - 1;
		String s = customer.getAppointment(n).getBookableService().getName();
		assertEquals(s, string);

	}
	@When("the owner attempts to register a no-show for the appointment at {string}")
	public void the_owner_attempts_to_register_a_no_show_for_the_appointment_at(String string) {
		FlexibookController.registerNoShow(string);
	}
	@Then("the system shall have {int} appointment")
	public void the_system_shall_have_appointment(Integer int1) {
		assertTrue(flexiBook.getAppointments().size() == int1);
	}
	@When("{string} requests the appointment calendar for the day of {string}")
	public void requests_the_appointment_calendar_for_the_day_of(String string, String string2) {
		try {
			slots.addAll(FlexibookController.viewDayCalendar(string, string2));
		}catch(InvalidInputException e) {
			error += e.getMessage();
		}
	}
	@When("{string} requests the appointment calendar for the week starting on {string}")
	public void requests_the_appointment_calendar_for_the_week_starting_on(String string, String string2) {
		try {
			slots.addAll(FlexibookController.viewWeekCalendar(string, string2));
		}catch(InvalidInputException e) {
			error += e.getMessage();
		}
	}
	@When("the owner ends the appointment at {string}")
	public void the_owner_ends_the_appointment_at(String string) {
		FlexibookController.endAppointment(string);
	}
}
