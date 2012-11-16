package fr.rocknscrum.liseronmobile.tools;

import java.io.Serializable;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "ConfigClass")
public class ConfigClass implements Serializable {

	private static final long serialVersionUID = -2598849239446902344L;
	
	@DatabaseField(id = true, useGetSet = true)
	protected int id;
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	@DatabaseField(useGetSet = true)
	protected String userName;
	@DatabaseField(useGetSet = true)
	protected String userFirstName;
	@DatabaseField(useGetSet = true)
	protected int idUser;
	@DatabaseField(useGetSet = true)
	protected String login;
	@DatabaseField(useGetSet = true)
	protected boolean isLoggedIn;
	
	@DatabaseField(useGetSet = true)
	protected String dateUpdateForm;
	@DatabaseField(useGetSet = true)
	protected String dateUpdateSpecies;
	
	public String getDateUpdateForm() {
		return dateUpdateForm;
	}
	public void setDateUpdateForm(String dateUpdateForm) {
		this.dateUpdateForm = dateUpdateForm;
	}
	public String getDateUpdateSpecies() {
		return dateUpdateSpecies;
	}
	public void setDateUpdateSpecies(String dateUpdateSpecies) {
		this.dateUpdateSpecies = dateUpdateSpecies;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getUserFirstName() {
		return userFirstName;
	}
	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
	}
	public boolean isLoggedIn() {
		return isLoggedIn;
	}
	public boolean getIsLoggedIn()
	{
		return isLoggedIn;
	}
	public void setIsLoggedIn(boolean isLoggedIn) {
		this.isLoggedIn = isLoggedIn;
	}
	public int getIdUser() {
		return idUser;
	}
	public void setIdUser(int idUser) {
		this.idUser = idUser;
	}
}
