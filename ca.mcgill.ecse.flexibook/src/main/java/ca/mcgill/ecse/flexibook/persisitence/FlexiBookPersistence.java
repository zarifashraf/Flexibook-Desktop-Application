package ca.mcgill.ecse.flexibook.persisitence;

import ca.mcgill.ecse.flexibook.model.FlexiBook;

public class FlexiBookPersistence {
	private static String filename = "new.flexibook";
	
	/**
	 * @author saikouceesay
	 * @param flexiBook
	 * saves the state of the flexibook in the persistence layer
	 */
	public static void saveFlexiBook(FlexiBook flexiBook) {
		PersistenceObjectStream.serialize(flexiBook);
	}
	/**
	 * @author zarifashraf
	 * @return FlexiBook
	 * loads the status of the application on boot-up
	 */
	public static FlexiBook loadFlexiBook() {
		PersistenceObjectStream.setFilename(filename);
		FlexiBook flexiBook = (FlexiBook) PersistenceObjectStream.deserialize();
		//flexiBook.model cannot be loaded - create empty flexibook
		if(flexiBook == null ) {	
			flexiBook = new FlexiBook();
		}
		else {
			flexiBook.reinitialize();
		}
		return flexiBook;
	}
}
