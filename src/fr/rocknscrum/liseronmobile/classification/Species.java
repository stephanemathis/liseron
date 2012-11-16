package fr.rocknscrum.liseronmobile.classification;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Species")
public class Species implements Classification, Serializable, Comparable<Species> {

	private static final long serialVersionUID = 799876909516120218L;
	@DatabaseField(id = true, useGetSet = true)
	protected int id;
	@DatabaseField(useGetSet = true)
	protected String name;
	@DatabaseField(useGetSet = true)
	protected String description;
	@DatabaseField(useGetSet = true)
	protected boolean indanger;
	
	public boolean getIndanger()
	{
		return indanger;
	}
	
	public boolean isIndanger() {
		return indanger;
	}

	public void setIndanger(boolean indanger) {
		this.indanger = indanger;
	}

	@DatabaseField(canBeNull = false, foreign = true, useGetSet = true)
	protected Genre genre;

	/**
	 * @author Mathis
	 * return int : id of the species in the database
	 */
	public int getId() {
		return this.id;
	}

	/**
	 * Should not be used
	 * @author Mathis
	 * @param id : id of the species in the database
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @author Mathis
	 * return String : name of the species
	 */
	public String getName() {
		return this.name;
	}
	
	/**
	 * @author Mathis
	 * @param name : name of the species
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @author Mathis
	 * return String : description of the species
	 */
	public String getDescription() {
		return this.description;
	}

	/**
	 * @author Mathis
	 * @param description : description of the species
	 */
	public void setDescription(String description) {
		this.description = description;
	}


	/**
	 * @author Mathis
	 * return Genre : genre of the species
	 */
	public Genre getGenre() {
		return this.genre;
	}

	
	/**
	 * @author Mathis
	 * @param genre : Genre of the species
	 */
	public void setGenre(Genre genre) {
		this.genre = genre;
	}

	@Override
	public int compareTo(Species another) {
		if(another==null)
			return -1;
		else return this.getName().compareTo(another.getName());
	}
	
	

}