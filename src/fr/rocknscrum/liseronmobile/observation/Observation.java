package fr.rocknscrum.liseronmobile.observation;

import java.io.Serializable;

import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

import fr.rocknscrum.liseronmobile.classification.Species;

@DatabaseTable(tableName = "Observation")
public class Observation implements Serializable
{
	private static final long serialVersionUID = -2562523658331118585L;
	
	@DatabaseField(generatedId = true)
	protected int id;
	@DatabaseField(useGetSet = true)
	protected String lattitude;
	@DatabaseField(useGetSet = true)
	protected String longitude;
	@DatabaseField(useGetSet = true)
	protected String comment;
	@DatabaseField(useGetSet = true)
	protected String imagePath;
	@DatabaseField(useGetSet = true)
	protected boolean valid;
	@DatabaseField(canBeNull = true, foreign = true, useGetSet = true)
	protected Form Form;
	@DatabaseField(canBeNull = false, foreign = true, useGetSet = true)
	protected Species species;
	@ForeignCollectionField(eager = true)
	protected ForeignCollection<Value> values;
	@DatabaseField(useGetSet = true)
	protected String date;
	
	public ForeignCollection<Value> getValues() {
		return values;
	}
	public void setValues(ForeignCollection<Value> values) {
		this.values = values;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLattitude() {
		return lattitude;
	}
	public void setLattitude(String lattitude) {
		this.lattitude = lattitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public boolean isValid() {
		return valid;
	}
	public void setValid(boolean valid) {
		this.valid = valid;
	}
	public boolean getValid(){
		return valid;
	}
	public Species getSpecies() {
		return species;
	}
	public void setSpecies(Species species) {
		this.species = species;
	}
	public Form getForm() {
		return Form;
	}
	public void setForm(Form form) {
		Form = form;
	}
	public String getImagePath() {
		return imagePath;
	}
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
	
}