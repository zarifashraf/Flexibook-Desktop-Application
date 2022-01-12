/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.1.5099.60569f335 modeling language!*/

package ca.mcgill.ecse.flexibook.model;
import java.io.Serializable;
import java.sql.Time;
import java.sql.Date;
import java.util.*;

// line 49 "../../../../../FlexibookPersistence.ump"
// line 7 "../../../../../FlexibookStates.ump"
// line 88 "../../../../../FlexiBook.ump"
public class Appointment implements Serializable
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //Appointment Attributes
  private boolean hasStarted;
  private boolean hasEnded;

  //Appointment State Machines
  public enum Status { Booked, Final, InProgress }
  private Status status;

  //Appointment Associations
  private Customer customer;
  private BookableService bookableService;
  private List<ComboItem> chosenItems;
  private TimeSlot timeSlot;
  private FlexiBook flexiBook;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public Appointment(Customer aCustomer, BookableService aBookableService, TimeSlot aTimeSlot, FlexiBook aFlexiBook)
  {
    hasStarted = false;
    hasEnded = false;
    boolean didAddCustomer = setCustomer(aCustomer);
    if (!didAddCustomer)
    {
      throw new RuntimeException("Unable to create appointment due to customer. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    boolean didAddBookableService = setBookableService(aBookableService);
    if (!didAddBookableService)
    {
      throw new RuntimeException("Unable to create appointment due to bookableService. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    chosenItems = new ArrayList<ComboItem>();
    if (!setTimeSlot(aTimeSlot))
    {
      throw new RuntimeException("Unable to create Appointment due to aTimeSlot. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    boolean didAddFlexiBook = setFlexiBook(aFlexiBook);
    if (!didAddFlexiBook)
    {
      throw new RuntimeException("Unable to create appointment due to flexiBook. See http://manual.umple.org?RE002ViolationofAssociationMultiplicity.html");
    }
    setStatus(Status.Booked);
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setHasStarted(boolean aHasStarted)
  {
    boolean wasSet = false;
    hasStarted = aHasStarted;
    wasSet = true;
    return wasSet;
  }

  public boolean setHasEnded(boolean aHasEnded)
  {
    boolean wasSet = false;
    hasEnded = aHasEnded;
    wasSet = true;
    return wasSet;
  }

  public boolean getHasStarted()
  {
    return hasStarted;
  }

  public boolean getHasEnded()
  {
    return hasEnded;
  }
  /* Code from template attribute_IsBoolean */
  public boolean isHasStarted()
  {
    return hasStarted;
  }
  /* Code from template attribute_IsBoolean */
  public boolean isHasEnded()
  {
    return hasEnded;
  }

  public String getStatusFullName()
  {
    String answer = status.toString();
    return answer;
  }

  public Status getStatus()
  {
    return status;
  }

  public boolean update(Date curDate,Date date,Service service,Time startTime,Time endTime)
  {
    boolean wasEventProcessed = false;
    
    Status aStatus = status;
    switch (aStatus)
    {
      case Booked:
        if (atLeastOneDayBefore(curDate,date)&&timeSlotAvailable(startTime,endTime))
        {
        // line 13 "../../../../../FlexibookStates.ump"
          doUpdate(date, service, startTime, endTime);
          setStatus(Status.Booked);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean cancel(Date curDate,Date date)
  {
    boolean wasEventProcessed = false;
    
    Status aStatus = status;
    switch (aStatus)
    {
      case Booked:
        if (atLeastOneDayBefore(curDate,date))
        {
          setStatus(Status.Final);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean start(Date date,Time time)
  {
    boolean wasEventProcessed = false;
    
    Status aStatus = status;
    switch (aStatus)
    {
      case Booked:
        if (!(beforeAppointmentDateTime(date,time)))
        {
          setStatus(Status.InProgress);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean noShow(Date date,Time time)
  {
    boolean wasEventProcessed = false;
    
    Status aStatus = status;
    switch (aStatus)
    {
      case Booked:
        if (!(beforeAppointmentDateTime(date,time)))
        {
        // line 18 "../../../../../FlexibookStates.ump"
          doNoShow();
          setStatus(Status.Final);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean updateOptionals(Time startTime,Time endTime,Service service)
  {
    boolean wasEventProcessed = false;
    
    Status aStatus = status;
    switch (aStatus)
    {
      case InProgress:
        if (timeSlotAvailable(startTime,endTime))
        {
        // line 23 "../../../../../FlexibookStates.ump"
          doUpdateOptionals(service, startTime, endTime);
          setStatus(Status.InProgress);
          wasEventProcessed = true;
          break;
        }
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  public boolean end()
  {
    boolean wasEventProcessed = false;
    
    Status aStatus = status;
    switch (aStatus)
    {
      case InProgress:
        setStatus(Status.Final);
        wasEventProcessed = true;
        break;
      default:
        // Other states do respond to this event
    }

    return wasEventProcessed;
  }

  private void setStatus(Status aStatus)
  {
    status = aStatus;

    // entry actions and do activities
    switch(status)
    {
      case Final:
        delete();
        break;
    }
  }
  /* Code from template association_GetOne */
  public Customer getCustomer()
  {
    return customer;
  }
  /* Code from template association_GetOne */
  public BookableService getBookableService()
  {
    return bookableService;
  }
  /* Code from template association_GetMany */
  public ComboItem getChosenItem(int index)
  {
    ComboItem aChosenItem = chosenItems.get(index);
    return aChosenItem;
  }

  public List<ComboItem> getChosenItems()
  {
    List<ComboItem> newChosenItems = Collections.unmodifiableList(chosenItems);
    return newChosenItems;
  }

  public int numberOfChosenItems()
  {
    int number = chosenItems.size();
    return number;
  }

  public boolean hasChosenItems()
  {
    boolean has = chosenItems.size() > 0;
    return has;
  }

  public int indexOfChosenItem(ComboItem aChosenItem)
  {
    int index = chosenItems.indexOf(aChosenItem);
    return index;
  }
  /* Code from template association_GetOne */
  public TimeSlot getTimeSlot()
  {
    return timeSlot;
  }
  /* Code from template association_GetOne */
  public FlexiBook getFlexiBook()
  {
    return flexiBook;
  }
  /* Code from template association_SetOneToMany */
  public boolean setCustomer(Customer aCustomer)
  {
    boolean wasSet = false;
    if (aCustomer == null)
    {
      return wasSet;
    }

    Customer existingCustomer = customer;
    customer = aCustomer;
    if (existingCustomer != null && !existingCustomer.equals(aCustomer))
    {
      existingCustomer.removeAppointment(this);
    }
    customer.addAppointment(this);
    wasSet = true;
    return wasSet;
  }
  /* Code from template association_SetOneToMany */
  public boolean setBookableService(BookableService aBookableService)
  {
    boolean wasSet = false;
    if (aBookableService == null)
    {
      return wasSet;
    }

    BookableService existingBookableService = bookableService;
    bookableService = aBookableService;
    if (existingBookableService != null && !existingBookableService.equals(aBookableService))
    {
      existingBookableService.removeAppointment(this);
    }
    bookableService.addAppointment(this);
    wasSet = true;
    return wasSet;
  }
  /* Code from template association_MinimumNumberOfMethod */
  public static int minimumNumberOfChosenItems()
  {
    return 0;
  }
  /* Code from template association_AddUnidirectionalMany */
  public boolean addChosenItem(ComboItem aChosenItem)
  {
    boolean wasAdded = false;
    if (chosenItems.contains(aChosenItem)) { return false; }
    chosenItems.add(aChosenItem);
    wasAdded = true;
    return wasAdded;
  }

  public boolean removeChosenItem(ComboItem aChosenItem)
  {
    boolean wasRemoved = false;
    if (chosenItems.contains(aChosenItem))
    {
      chosenItems.remove(aChosenItem);
      wasRemoved = true;
    }
    return wasRemoved;
  }
  /* Code from template association_AddIndexControlFunctions */
  public boolean addChosenItemAt(ComboItem aChosenItem, int index)
  {  
    boolean wasAdded = false;
    if(addChosenItem(aChosenItem))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfChosenItems()) { index = numberOfChosenItems() - 1; }
      chosenItems.remove(aChosenItem);
      chosenItems.add(index, aChosenItem);
      wasAdded = true;
    }
    return wasAdded;
  }

  public boolean addOrMoveChosenItemAt(ComboItem aChosenItem, int index)
  {
    boolean wasAdded = false;
    if(chosenItems.contains(aChosenItem))
    {
      if(index < 0 ) { index = 0; }
      if(index > numberOfChosenItems()) { index = numberOfChosenItems() - 1; }
      chosenItems.remove(aChosenItem);
      chosenItems.add(index, aChosenItem);
      wasAdded = true;
    } 
    else 
    {
      wasAdded = addChosenItemAt(aChosenItem, index);
    }
    return wasAdded;
  }
  /* Code from template association_SetUnidirectionalOne */
  public boolean setTimeSlot(TimeSlot aNewTimeSlot)
  {
    boolean wasSet = false;
    if (aNewTimeSlot != null)
    {
      timeSlot = aNewTimeSlot;
      wasSet = true;
    }
    return wasSet;
  }
  /* Code from template association_SetOneToMany */
  public boolean setFlexiBook(FlexiBook aFlexiBook)
  {
    boolean wasSet = false;
    if (aFlexiBook == null)
    {
      return wasSet;
    }

    FlexiBook existingFlexiBook = flexiBook;
    flexiBook = aFlexiBook;
    if (existingFlexiBook != null && !existingFlexiBook.equals(aFlexiBook))
    {
      existingFlexiBook.removeAppointment(this);
    }
    flexiBook.addAppointment(this);
    wasSet = true;
    return wasSet;
  }

  public void delete()
  {
    Customer placeholderCustomer = customer;
    this.customer = null;
    if(placeholderCustomer != null)
    {
      placeholderCustomer.removeAppointment(this);
    }
    BookableService placeholderBookableService = bookableService;
    this.bookableService = null;
    if(placeholderBookableService != null)
    {
      placeholderBookableService.removeAppointment(this);
    }
    chosenItems.clear();
    timeSlot = null;
    FlexiBook placeholderFlexiBook = flexiBook;
    this.flexiBook = null;
    if(placeholderFlexiBook != null)
    {
      placeholderFlexiBook.removeAppointment(this);
    }
  }

  // line 31 "../../../../../FlexibookStates.ump"
   private boolean atLeastOneDayBefore(Date curDate, Date date){
    return (getTimeSlot().getStartDate()).after(curDate);
  }

  // line 35 "../../../../../FlexibookStates.ump"
   private boolean beforeAppointmentDateTime(Date date, Time time){
    return date.before(getTimeSlot().getStartDate()) && time.before(getTimeSlot().getStartTime());
  }

  // line 39 "../../../../../FlexibookStates.ump"
   private boolean timeSlotAvailable(Time startTime, Time endTime){
    for(TimeSlot timeSlot : flexiBook.getTimeSlots()){
      		if (timeSlot.getStartTime().equals(endTime)){
      			return false;
      		}
      }
      return true;
  }

  // line 48 "../../../../../FlexibookStates.ump"
   private void doNoShow(){
    int count = customer.getNoShowCount();
  	customer.setNoShowCount(count++);
  }

  // line 53 "../../../../../FlexibookStates.ump"
   private void doUpdate(Date date, Service service, Time startTime, Time endTime){
    getTimeSlot().setStartTime(startTime);
        getTimeSlot().setStartDate(date);
    	setBookableService(service);
  }

  // line 60 "../../../../../FlexibookStates.ump"
   private void doUpdateOptionals(Service service, Time startTime, Time endTime){
    
  }


  public String toString()
  {
    return super.toString() + "["+
            "hasStarted" + ":" + getHasStarted()+ "," +
            "hasEnded" + ":" + getHasEnded()+ "]" + System.getProperties().getProperty("line.separator") +
            "  " + "customer = "+(getCustomer()!=null?Integer.toHexString(System.identityHashCode(getCustomer())):"null") + System.getProperties().getProperty("line.separator") +
            "  " + "bookableService = "+(getBookableService()!=null?Integer.toHexString(System.identityHashCode(getBookableService())):"null") + System.getProperties().getProperty("line.separator") +
            "  " + "timeSlot = "+(getTimeSlot()!=null?Integer.toHexString(System.identityHashCode(getTimeSlot())):"null") + System.getProperties().getProperty("line.separator") +
            "  " + "flexiBook = "+(getFlexiBook()!=null?Integer.toHexString(System.identityHashCode(getFlexiBook())):"null");
  }  
  //------------------------
  // DEVELOPER CODE - PROVIDED AS-IS
  //------------------------
  
  // line 52 "../../../../../FlexibookPersistence.ump"
  private static final long serialVersionUID = 1792949983290480932L ;

  
}