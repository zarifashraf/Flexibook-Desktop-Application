package ca.mcgill.ecse.flexibook.controller;

public class InvalidInputException extends Exception {
	
	/**
	 * @author saikouceesay
	 */
	public InvalidInputException (String errorMessage) {
		super(errorMessage);
	}
}
