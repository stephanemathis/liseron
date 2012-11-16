package fr.rocknscrum.liseronmobile.classification;

import java.io.Serializable;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Genre")
public class Genre implements Classification, Serializable {

	private static final long serialVersionUID = -5799303985284111807L;
	@DatabaseField(id = true,useGetSet = true)
	protected int id;
	@DatabaseField(useGetSet = true)
	protected String name;
	@DatabaseField(useGetSet = true)
	protected String description;
	@DatabaseField(canBeNull = false, foreign = true, useGetSet = true)
	protected Family family;
	@ForeignCollectionField(eager = false)
	protected ForeignCollection<Species> species;

	/**
	 * @author Mathis
	 * @return int : the id of the genre in the database
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Should not be used
	 * @author Mathis
	 * @param id : the id of the Genre in the
	 * bbgbz database
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @author Mathis
	 * @return String : the name of the genre
	 */
	public String getName() {
		return this.name;
	}

	/**
	 * @author Mathis
	 * @param name : the name of the Genre
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @author Mathis
	 * @return String : the description of the genre
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @author Mathis
	 * @param description : the description of the Genre
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @author Mathis
	 * @return ForeignCollection<Species> : a collection of all species contained in the genre
	 */
	public ForeignCollection<Species> getSpecies() {
		return this.species;
	}

	/**
	 * @author Mathis
	 * @param you should not use this method. Use setGenre(Genre g) from Species
	 */
	public void setSpecies(ForeignCollection<Species> species) {
		this.species = species;
	}

	/**
	 * @author Mathis
	 * @return Family : the family associated with this object
	 */
	public Family getFamily() {
		return this.family;
	}

	/**
	 * @author Mathis
	 * @param family : the family associated with this object
	 */
	public void setFamily(Family family) {
		this.family = family;
	}


}