package fr.rocknscrum.liseronmobile.database;

import java.util.ArrayList;

import fr.rocknscrum.liseronmobile.tools.ToolsString;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ToolsBDD {

	public static ToolsBDD instance;
	public SQLiteDatabase bdd;
	private MyDBOpenHelper database;
	
	/**
	 * 
	 * This class is used to have a direct acces to the database.
	 * @param context
	 */
	public ToolsBDD(Context context)
	{
		database = new MyDBOpenHelper(context, DatabaseHelper.DATABASE_NAME, null, DatabaseHelper.DATABASE_VERSION);
		open();
	}
	
	public static ToolsBDD getInstance(Context context)
	{
		if(instance == null)
			instance = new ToolsBDD(context);
		return instance;		
	}
	
	public void open()
	{
		bdd = database.getWritableDatabase();
	}
 
	public void close()
	{
		bdd.close();
	}
	
	public SQLiteDatabase getBDD()
	{
		return bdd;
	}

	public ArrayList<Integer> getNbChildrenFromFamilies(String search)
	{
		ArrayList<Integer> res = new ArrayList<Integer>();
		String where = "";
		if(!ToolsString.isNullOrempty(search))
			where = " WHERE lower(f.name) like '"+search.replace("'", "''")+"' ";
		
		Cursor c = bdd.rawQuery("SELECT (SELECT count(id) FROM Genre g WHERE g.family_id=f.id ) FROM Family f "+where+" ORDER BY f.name", null);
		if(c.moveToFirst())
		{
			while(!c.isAfterLast())
			{
				res.add(c.getInt(0));
				c.moveToNext();
			}
		}

		return res;
	}
	
	public ArrayList<Integer> getNbChildrenFromGenre(String search, String id)
	{
		ArrayList<Integer> res = new ArrayList<Integer>();
		String where = "";
		where ="WHERE g.family_id="+id;
		if(!ToolsString.isNullOrempty(search))
			where += " AND lower(g.name) like '"+search.replace("'", "''")+"' ";
		
		Cursor c = bdd.rawQuery("SELECT (SELECT count(id) FROM Species s WHERE s.genre_id=g.id ) FROM Genre g "+where+" ORDER BY g.name", null);
		if(c.moveToFirst())
		{
			while(!c.isAfterLast())
			{
				res.add(c.getInt(0));
				c.moveToNext();
			}
		}

		return res;
	}




	

 
	
}
