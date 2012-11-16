package fr.rocknscrum.liseronmobile.observation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import fr.rocknscrum.liseronmobile.classification.Species;

@DatabaseTable(tableName = "Campaign")
public class Campaign implements Serializable
{
	private static final long serialVersionUID = 5678293974880339977L;
	
	@DatabaseField(id = true,useGetSet = true)
	protected int id;
	@DatabaseField(useGetSet = true)
	protected String name;
	@DatabaseField(useGetSet = true)
	protected String description;
	@DatabaseField(useGetSet = true)
	protected Date start;
	@DatabaseField(useGetSet = true)
	protected Date end;	
	@ForeignCollectionField(eager = true)
	protected ForeignCollection<Form> forms;
	@DatabaseField(dataType = DataType.SERIALIZABLE)
	protected ArrayList<Species> species;

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getEnd() {
		return end;
	}
	public void setEnd(Date end) {
		this.end = end;
	}
	public ForeignCollection<Form> getForms() {
		return forms;
	}
	public void setForms(ForeignCollection<Form> forms) {
		this.forms = forms;
	}
	public ArrayList<Species> getSpecies() {
		return species;
	}
	public void setSpecies(ArrayList<Species> species) {
		this.species = species;
	}
}