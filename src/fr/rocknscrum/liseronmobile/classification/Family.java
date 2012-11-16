package fr.rocknscrum.liseronmobile.classification;

import java.io.Serializable;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Family")
public class Family implements Classification, Serializable {

	private static final long serialVersionUID = -831057225621080528L;
	@DatabaseField(id = true, useGetSet = true)
	protected int id;
	@DatabaseField(useGetSet = true)
	protected String name;
	@DatabaseField(useGetSet = true)
	protected String description;
	@ForeignCollectionField(eager = false)
	protected ForeignCollection<Genre> genres;

	/**
	 * @author Mathis
	 * @return int : id in the database
	 */	
	public int getId() {
		return this.id;
	}

	
	/**
	 * @author Mathis
	 * @param int : id in the database
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @author Mathis
	 * @return String : name of the family
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @author Mathis
	 * @param name : name of the family
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @author Mathis
	 * @return String : description of the family
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @author Mathis
	 * @param description : description of the family
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @author Mathis
	 * @return ForeignCollection<Genre> : a collection of all genre contained in the family
	 */
	public ForeignCollection<Genre> getGenres() {
		return this.genres;
	}

	/**
	 * @author Mathis
	 * @param you should not use this method. Use setFamily(Genre g) from Genre
	 */
	public void setGenres(ForeignCollection<Genre> genres) {
		this.genres = genres;
	}
	
	/**
	 * @author Mathis
	 * @return Family : a blank family
	 */
	public Family()
	{
		// ORMLite needs a no-argument constructor 
		
	}
}