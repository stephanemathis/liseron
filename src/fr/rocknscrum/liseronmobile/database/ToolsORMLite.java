package fr.rocknscrum.liseronmobile.database;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;


public class ToolsORMLite {

	static ToolsORMLite instance = null;
	Context c;
	public ToolsORMLite(Context c)
	{
		this.c = c;
	}
	
	public static ToolsORMLite getInstance(Context c)
	{
		if(instance == null)
			instance = new ToolsORMLite(c);
		return instance;
	}
	
	public DatabaseHelper getHelper()
	{
		return OpenHelperManager.getHelper(c, DatabaseHelper.class);		
	}
}
