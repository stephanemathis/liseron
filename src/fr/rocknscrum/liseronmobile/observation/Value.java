package fr.rocknscrum.liseronmobile.observation;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Value")
public class Value implements Serializable
{
	private static final long serialVersionUID = -4851611920129289782L;
	

	@DatabaseField(generatedId=true)
	protected int id;
	@DatabaseField(useGetSet = true)
	protected String value;
	@DatabaseField(canBeNull = false, foreign = true, useGetSet = true)
	protected Observation observation;
	@DatabaseField(canBeNull = false, foreign = true, useGetSet = true)
	protected Field field;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public Observation getObservation() {
		return observation;
	}
	public void setObservation(Observation observation) {
		this.observation = observation;
	}
	public Field getField() {
		return field;
	}
	public void setField(Field field) {
		this.field = field;
	}
	
	
}