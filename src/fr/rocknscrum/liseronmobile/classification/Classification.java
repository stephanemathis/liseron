package fr.rocknscrum.liseronmobile.classification;

public interface Classification {

	/**
	 * @author Mathis
	 * @return int : id of the object in the database
	 */
	public int getId();
	/**
	 * @author Mathis
	 * @return String : name of the object
	 */
	public String getName();
	/**
	 * @author Mathis
	 * @return String : description of the object
	 */
	public String getDescription();

}
