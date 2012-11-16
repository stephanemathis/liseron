package fr.rocknscrum.liseronmobile.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import fr.rocknscrum.liseronmobile.R;
import fr.rocknscrum.liseronmobile.classification.Family;
import fr.rocknscrum.liseronmobile.classification.Genre;
import fr.rocknscrum.liseronmobile.classification.Species;
import fr.rocknscrum.liseronmobile.observation.Campaign;
import fr.rocknscrum.liseronmobile.observation.Field;
import fr.rocknscrum.liseronmobile.observation.Form;
import fr.rocknscrum.liseronmobile.observation.Observation;
import fr.rocknscrum.liseronmobile.observation.Value;
import fr.rocknscrum.liseronmobile.tools.ConfigClass;

public class DatabaseHelper  extends OrmLiteSqliteOpenHelper{
	
	// name of the database file for your application -- change to something appropriate for your app
	public static final String DATABASE_NAME = "Liseron.db";
	// any time you make changes to your database objects, you may have to increase the database version
	public static final int DATABASE_VERSION = 9;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
	}

	/**
	 * This is called when the database is first created. Usually you should call createTable statements here to create
	 * the tables that will store your data.
	 */
	@Override
	public void onCreate(SQLiteDatabase db, ConnectionSource connectionSource) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onCreate");
			TableUtils.createTable(connectionSource, Family.class);
			TableUtils.createTable(connectionSource, Genre.class);
			TableUtils.createTable(connectionSource, Species.class);
			TableUtils.createTable(connectionSource, ConfigClass.class);
			TableUtils.createTable(connectionSource, Campaign.class);
			TableUtils.createTable(connectionSource, Form.class);
			TableUtils.createTable(connectionSource, Field.class);
			TableUtils.createTable(connectionSource, Value.class);
			TableUtils.createTable(connectionSource, Observation.class);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
			throw new RuntimeException(e);
		}
	}
  
	/**
	 * This is called when your application is upgraded and it has a higher version number. This allows you to adjust
	 * the various data to match the new version number.
	 */
	@Override
	public void onUpgrade(SQLiteDatabase db, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			Log.i(DatabaseHelper.class.getName(), "onUpgrade");
			TableUtils.dropTable(connectionSource, Family.class, true);
			TableUtils.dropTable(connectionSource, Genre.class, true);
			TableUtils.dropTable(connectionSource, Species.class, true);
			TableUtils.dropTable(connectionSource, ConfigClass.class, true);
			TableUtils.dropTable(connectionSource, Observation.class, true);
			TableUtils.dropTable(connectionSource, Value.class, true);
			TableUtils.dropTable(connectionSource, Field.class, true);
			TableUtils.dropTable(connectionSource, Form.class, true);
			TableUtils.dropTable(connectionSource, Campaign.class, true);
			// after we drop the old databases, we create the new ones
			onCreate(db, connectionSource);
		} catch (SQLException e) {
			Log.e(DatabaseHelper.class.getName(), "Can't drop databases", e);
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Create a copy of the database on the root of the SDCard.
	 * @author Mathis
	 * @throws IOException
	 */
	public static void backupDatabase() throws IOException {
	    //Open your local db as the input stream
	    String inFileName = "/data/data/fr.rocknscrum.liseronmobile/databases/"+DATABASE_NAME;
	    File dbFile = new File(inFileName);
	    FileInputStream fis = new FileInputStream(dbFile);

	    String outFileName = Environment.getExternalStorageDirectory()+"/"+DATABASE_NAME;
	    //Open the empty db as the output stream
	    OutputStream output = new FileOutputStream(outFileName);
	    //transfer bytes from the inputfile to the outputfile
	    byte[] buffer = new byte[1024];
	    int length;
	    while ((length = fis.read(buffer))>0){
	        output.write(buffer, 0, length);
	    }
	    //Close the streams
	    output.flush();
	    output.close();
	    fis.close();
	}

}


	


