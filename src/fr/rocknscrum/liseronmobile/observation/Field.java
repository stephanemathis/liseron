package fr.rocknscrum.liseronmobile.observation;

import java.io.Serializable;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Field")
public class Field implements Serializable
{
	private static final long serialVersionUID = 3771269830384159089L;
	
	@DatabaseField(id = true,useGetSet = true)
	protected int id;
	@DatabaseField(canBeNull = false, foreign = true, useGetSet = true)
	protected Form form;
	@DatabaseField(useGetSet = true)
	protected String name;
	@DatabaseField(useGetSet = true)
	protected String type;
	@DatabaseField(useGetSet = true)
	protected boolean required;
	@DatabaseField(useGetSet = true)
	protected String choices;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Form getForm() {
		return form;
	}
	public void setForm(Form form) {
		this.form = form;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public boolean isRequired() {
		return required;
	}
	public void setRequired(boolean required) {
		this.required = required;
	}
	public boolean getRequired() {
		return required;
	}
	public String getChoices() {
		return choices;
	}
	public void setChoices(String choices) {
		this.choices = choices;
	}
	
	

}