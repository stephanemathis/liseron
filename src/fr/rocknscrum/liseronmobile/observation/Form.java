package fr.rocknscrum.liseronmobile.observation;

import java.io.Serializable;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "Form")
public class Form implements Serializable
{
	private static final long serialVersionUID = -7409263896811375328L;
	
	@DatabaseField(id = true,useGetSet = true)
	protected int id;
	@DatabaseField(useGetSet = true)
	protected String name;
	@DatabaseField(useGetSet = true)
	protected String description;
	@DatabaseField(canBeNull = false, foreign = true, useGetSet = true)
	protected Campaign campaign;
	@ForeignCollectionField(eager = true)
	protected ForeignCollection<Field> fields;
	
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
	public Campaign getCampaign() {
		return campaign;
	}
	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}
	public ForeignCollection<Field> getFields() {
		return fields;
	}
	public void setFields(ForeignCollection<Field> fields) {
		this.fields = fields;
	}
	
	
}