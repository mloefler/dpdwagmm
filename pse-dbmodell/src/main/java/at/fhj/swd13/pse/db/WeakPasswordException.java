package at.fhj.swd13.pse.db;
/**
 * The password given did not meet the set criteria 
 * and is not accepted by the system
 */
public class WeakPasswordException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Create a weak-password exception
	 * 
	 * @param message a message to the user 
	 */
	public WeakPasswordException( final String message ) {
		
		super( message );
	}	
}
