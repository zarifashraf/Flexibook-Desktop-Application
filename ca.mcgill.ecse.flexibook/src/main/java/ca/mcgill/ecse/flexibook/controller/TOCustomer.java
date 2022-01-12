/*PLEASE DO NOT EDIT THIS CODE*/
/*This code was generated using the UMPLE 1.30.1.5099.60569f335 modeling language!*/

package ca.mcgill.ecse.flexibook.controller;

// line 32 "../../../../../FlexibookTransferObjects.ump"
public class TOCustomer
{

  //------------------------
  // MEMBER VARIABLES
  //------------------------

  //TOCustomer Attributes
  private String name;
  private String password;

  //------------------------
  // CONSTRUCTOR
  //------------------------

  public TOCustomer(String aName, String aPassword)
  {
    name = aName;
    password = aPassword;
  }

  //------------------------
  // INTERFACE
  //------------------------

  public boolean setName(String aName)
  {
    boolean wasSet = false;
    name = aName;
    wasSet = true;
    return wasSet;
  }

  public boolean setPassword(String aPassword)
  {
    boolean wasSet = false;
    password = aPassword;
    wasSet = true;
    return wasSet;
  }

  public String getName()
  {
    return name;
  }

  public String getPassword()
  {
    return password;
  }

  public void delete()
  {}


  public String toString()
  {
    return super.toString() + "["+
            "name" + ":" + getName()+ "," +
            "password" + ":" + getPassword()+ "]";
  }
}