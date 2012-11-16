package fr.rocknscrum.liseronmobile;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.ByteArrayBuffer;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import fr.rocknscrum.liseronmobile.classification.Family;
import fr.rocknscrum.liseronmobile.classification.Genre;
import fr.rocknscrum.liseronmobile.classification.Species;
import fr.rocknscrum.liseronmobile.database.ToolsBDD;
import fr.rocknscrum.liseronmobile.database.ToolsORMLite;
import fr.rocknscrum.liseronmobile.observation.Campaign;
import fr.rocknscrum.liseronmobile.observation.Field;
import fr.rocknscrum.liseronmobile.observation.Form;
import fr.rocknscrum.liseronmobile.observation.Observation;
import fr.rocknscrum.liseronmobile.observation.Value;
import fr.rocknscrum.liseronmobile.tools.ConfigClass;
import fr.rocknscrum.liseronmobile.tools.ToolsConnection;
import fr.rocknscrum.liseronmobile.tools.ToolsString;

public class SyncActivity extends Activity implements OnClickListener{

	private static final int TV_FORM = 0;
	private static final int TV_SPE = 2;
	private static final int MENU_QUIT = 0;
	Button btnForm, btnObs, btnSpecies;
	TextView tvForm, tvObs, tvSpecies;
	RuntimeExceptionDao<ConfigClass, Integer> simpleDao;
	ConfigClass s;
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.sync);
    	       
    	simpleDao = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(ConfigClass.class);
    	s = simpleDao.queryForId(0);
        
        btnForm = (Button)findViewById(R.id.buttonSyncForm);
        btnObs = (Button)findViewById(R.id.buttonSyncObservation);
        btnSpecies = (Button)findViewById(R.id.buttonSyncSpecies);
        
        tvForm = (TextView)findViewById(R.id.textViewSyncForm);
        tvObs = (TextView)findViewById(R.id.textViewSyncObservation);
        tvSpecies = (TextView)findViewById(R.id.textViewSyncSpecies);
        
        btnForm.setOnClickListener(this);
        btnObs.setOnClickListener(this);
        btnSpecies.setOnClickListener(this);
        
        tvForm.setText(getString(R.string.lastupdate)+getNiceDate(TV_FORM));
        tvObs.setText(getString(R.string.lastupdate)+getUpdateObs());
        tvSpecies.setText(getString(R.string.lastupdate)+getNiceDate(TV_SPE));
    }

    private String getUpdateObs()
    {
    	try {
    		RuntimeExceptionDao<Observation, Integer> daoobs = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(Observation.class);
    		List<Observation> obs = daoobs.queryBuilder().where().eq("valid", false).query();

    		if(obs.size()==0)
    		{
    			btnObs.setEnabled(false);
    			return getString(R.string.syncareuptodate);
    		}
    		else if(obs.size()==1)
    			return " "+obs.size()+" "+getString(R.string.synctosend);
    		else return " "+obs.size()+" "+getString(R.string.multiplesynctosend);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return "";
    }
    
	private String getNiceDate(int n) {
		String res ="";
    	
    	if(n==TV_SPE)
    	{
    		res = s.getDateUpdateSpecies();
    		if(ToolsString.isNullOrempty(res))
    			res = getString(R.string.nolastupdatedate);
    	}
    	else if(n==TV_FORM)
    	{
    		res = s.getDateUpdateForm();
    		if(ToolsString.isNullOrempty(res))
    			res = getString(R.string.nolastupdatedate);
    	}
    	
		return " "+res;
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.buttonSyncForm)
		{
			if(ToolsConnection.isOnline(this))
			{
		        LayoutInflater factory = LayoutInflater.from(this);
		        final View alertDialogView = factory.inflate(R.layout.alertdialogsyncform, null);
		        AlertDialog.Builder adb = new AlertDialog.Builder(this);
		        adb.setView(alertDialogView);
		        adb.setTitle(R.string.synccampaignsalertdialogtitle);
		        adb.setIcon(android.R.drawable.ic_dialog_info);
		 
		        adb.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		               	loadClassification(true);
		          } });
		 
		        adb.setNegativeButton(R.string.undo, new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
		            	
		          } });
		        adb.show();
			}
			else {
				Toast.makeText(this, R.string.noconnectionavailable, Toast.LENGTH_SHORT).show();
			}
			
		}
		else if(v.getId()==R.id.buttonSyncObservation)
		{
			RuntimeExceptionDao<ConfigClass, Integer> simpleDao = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(ConfigClass.class);
	    	ConfigClass s = simpleDao.queryForId(0);
    		if(ToolsConnection.isOnline(this))
    		{
				if(s.getIsLoggedIn())
				{
					new SendingObservations().execute();
				}
				else {
					Toast.makeText(this, R.string.authentificationneeded, Toast.LENGTH_SHORT).show();
				}
    		}
    		else {
    			Toast.makeText(this, R.string.noconnectionavailable, Toast.LENGTH_SHORT).show();
    		}
		}
		else if(v.getId() == R.id.buttonSyncSpecies)
		{
			loadClassification(false);
		}
	}
	
    /*ROTATION OF THE SCREEN*/
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	}

	/*MENU*/
	public boolean onCreateOptionsMenu(Menu menu) {
	    menu.add(0,MENU_QUIT,0,R.string.home).setIcon(android.R.drawable.ic_menu_revert);
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case MENU_QUIT:
    		setResult(RESULT_OK);
    		finish();
	    	return true;
	    }
	    return false;
	}
	/**
	 * Generate the database
	 * @author Rock'N'Scrum
	 */
	public void loadClassification(boolean calledFromForms)
	{
		if(ToolsConnection.isOnline(this))
		{
			new LoadSpecies(calledFromForms).execute();
		}
		else {
			Toast.makeText(this, R.string.noconnectionavailable, Toast.LENGTH_SHORT).show();
		}
	}	
	
	public void loadCampaigns()
	{
		if(ToolsConnection.isOnline(this))
		{
			new LoadCampaigns().execute();
		}
		else {
			Toast.makeText(this, R.string.noconnectionavailable, Toast.LENGTH_SHORT).show();
		}
	}	
		
	private class LoadSpecies extends AsyncTask<Void, String, Void>
	{
		boolean loadForms = false;
		private final Integer SHOW_PROGRESS = 0;
		private final Integer SHOW_TOAST = 1;
		int nbAllGenre,nbAllSpecies,percent,nbFamilles;
		ProgressDialog mProgressDialog;
		boolean succes = true;
		
	    public LoadSpecies(boolean originForms) {
	        super();
	        loadForms = originForms;
	    }
		
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
			
			mProgressDialog = new ProgressDialog(SyncActivity.this);
			mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
			mProgressDialog.setMessage(getApplicationContext().getResources().getString(R.string.synchronizeWait));
			mProgressDialog.setCancelable(false);
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.show();
	    }
	 
	    @Override
	    protected void onProgressUpdate(String... values){
	        super.onProgressUpdate(values);
            if (Integer.parseInt(values[0]) == SHOW_PROGRESS) {
            	mProgressDialog.setIndeterminate(false);
                mProgressDialog.setMessage(""+values[1]);
                mProgressDialog.setProgress(Integer.parseInt(values[2]));
            }
            else if(Integer.parseInt(values[0])==SHOW_TOAST)
            {
            	Toast.makeText(getApplicationContext(), values[1], Toast.LENGTH_LONG).show();
            }
            
	    }
	 
	    @Override
	    protected Void doInBackground(Void... arg0) {
	    	
	    	try{
		    	String msgProgress = getApplication().getResources().getString(R.string.synchronizeLoading);
		    	Properties p = new Properties();
				try {
					p.loadFromXML(getApplicationContext().getResources().openRawResource(R.raw.properties));
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				boolean firstSync = ToolsString.isNullOrempty(s.getDateUpdateSpecies());
	
				//download the xml file as byte[]	
	            @SuppressWarnings("unused")
				HttpClient httpclient = new DefaultHttpClient();
	            HttpPost httppost = null;
	            
	            if(!firstSync)
	            {
	            	httppost = new HttpPost(p.getProperty("urlServiceClassificationUpdate"));
	            	List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	            	nameValuePairs.add(new BasicNameValuePair("date", s.getDateUpdateSpecies()));
	            	httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	            }
	            else httppost = new HttpPost(p.getProperty("urlServiceClassificationExport"));
				
	            //HttpResponse response = httpclient.execute(httppost);
	            //Document doc = ToolsFile.getDocumentFromHttpResponse(response);           
	            
				BufferedInputStream bis = new BufferedInputStream(getApplicationContext().getResources().openRawResource(R.raw.ccexport));
				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				int current = 0;
				while ((current = bis.read()) != -1) {
					baf.append((byte) current);                       
				}
				DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
				byte[] data = baf.toByteArray();
				Document doc = docBuilder.parse(new ByteArrayInputStream(data));
	            
				publishProgress(SHOW_PROGRESS+"",msgProgress,0+"");
				//if the server doesn't respond or an empty file, we end the function
				if(doc==null)
				{
					succes = false;
				}
				else {
					//Get a direct acces to the database created by ormlite
					SQLiteDatabase bdd = ToolsBDD.getInstance(getApplicationContext()).bdd;
					bdd.execSQL("BEGIN;");
					//to calculate the sum
					nbAllGenre = 0;
					nbAllSpecies = 0;
					nbFamilles = 0;
					percent=0;

					if(firstSync)
					{
						//download all the classifications
						NodeList familles = doc.getElementsByTagName("famille");					
						nbFamilles = familles.getLength();
						for(int f = 0 ; f < nbFamilles ; f++)
						{
							Element currentFamily = (Element)familles.item(f);
							Family family = new Family();
							family.setId(Integer.parseInt(currentFamily.getAttribute("id")));
							family.setName(ToolsString.toFirstUpperCase(currentFamily.getAttribute("nom")));
							family.setDescription(currentFamily.getAttribute("description"));
							
							bdd.execSQL("INSERT OR REPLACE INTO Family VALUES('"+family.getDescription().replace("'", "''")+"','"+family.getName().replace("'", "''")+"',"+family.getId()+");");
							
							NodeList genres = currentFamily.getElementsByTagName("genre");
							int nbGenres = genres.getLength();
							nbAllGenre += nbGenres;
							for(int g = 0 ; g < nbGenres ; g++)
							{
								Element currentGenre = (Element)genres.item(g);
								Genre genre = new Genre();
								genre.setId(Integer.parseInt(currentGenre.getAttribute("id")));
								genre.setName(ToolsString.toFirstUpperCase(currentGenre.getAttribute("nom")));
								genre.setDescription(currentGenre.getAttribute("description"));
								genre.setFamily(family);
	
								bdd.execSQL("INSERT OR REPLACE INTO Genre VALUES('"+genre.getDescription().replace("'", "''")+"',"+family.getId()+",'"+genre.getName().replace("'", "''")+"',"+genre.getId()+");");
								
								NodeList specieS = currentGenre.getElementsByTagName("espece");
								int nbSpecies = specieS.getLength();
								nbAllSpecies += nbSpecies;
								for(int s = 0 ; s < nbSpecies ; s++)
								{
									Element currentSpecies = (Element)specieS.item(s);
									Species species = new Species();
									species.setId(Integer.parseInt(currentSpecies.getAttribute("id")));
									species.setName(ToolsString.toFirstUpperCase(currentSpecies.getAttribute("nom")));
									species.setDescription(currentSpecies.getAttribute("description"));
									species.setIndanger(currentSpecies.getAttribute("indanger").compareTo("true")==0);
									species.setGenre(genre);
									int danger = 0;
									if (currentSpecies.getAttribute("indanger").compareTo("true")==0)
										danger = 1;
									bdd.execSQL("INSERT OR REPLACE INTO Species VALues('"+species.getDescription().replace("'", "''")+"',"+genre.getId()+",'"+species.getName().replace("'", "''")+"',"+danger+","+species.getId()+");");
									
								}
							}
							if(percent != (f*100/nbFamilles))
							{
								percent = (f*100/nbFamilles);
								publishProgress(SHOW_PROGRESS+"",msgProgress,percent+"");
							}
						}
					}
					else {
						//update 
						msgProgress = getApplication().getResources().getString(R.string.updateloading);
						
						NodeList updates = doc.getElementsByTagName("update");
						if(updates.getLength()>0)
						{
							Element nodeupdates = (Element)updates.item(0);
							NodeList families = nodeupdates.getElementsByTagName("family");
							NodeList genres = nodeupdates.getElementsByTagName("genre");
							NodeList species = nodeupdates.getElementsByTagName("species");
							nbAllGenre = genres.getLength();
							nbAllSpecies = species.getLength();
							nbFamilles = families.getLength() ;
							int nbTotal = families.getLength() + genres.getLength() + species.getLength();
							int currentTotal = 0;
							
							for(int i = 0 ; i < families.getLength();i++)
							{
								Element currentFamily = (Element)families.item(i);
								Family family = new Family();
								family.setId(Integer.parseInt(currentFamily.getAttribute("id")));
								family.setName(ToolsString.toFirstUpperCase(currentFamily.getAttribute("name")));
								family.setDescription(currentFamily.getAttribute("description"));
								
								bdd.execSQL("INSERT OR REPLACE INTO Family VALUES('"+family.getDescription().replace("'", "''")+"','"+family.getName().replace("'", "''")+"',"+family.getId()+");");
							
								if(percent != (i*100/nbTotal))
								{
									percent = (i*100/nbTotal);
									publishProgress(SHOW_PROGRESS+"",msgProgress,percent+"");
								}
							}
							
							currentTotal = families.getLength();
							
							for(int i = 0 ; i < genres.getLength();i++)
							{
								Element currentGenre = (Element)genres.item(i);
								Genre genre = new Genre();
								genre.setId(Integer.parseInt(currentGenre.getAttribute("id")));
								genre.setName(ToolsString.toFirstUpperCase(currentGenre.getAttribute("name")));
								genre.setDescription(currentGenre.getAttribute("description"));
	
								bdd.execSQL("INSERT OR REPLACE INTO Genre VALUES('"+genre.getDescription().replace("'", "''")+"',"+currentGenre.getAttribute("idparent")+",'"+genre.getName().replace("'", "''")+"',"+genre.getId()+");");
								
								if(percent != ((currentTotal+i)*100/nbTotal))
								{
									percent = ((currentTotal+i)*100/nbTotal);
									publishProgress(SHOW_PROGRESS+"",msgProgress,percent+"");
								}
							}
							
							currentTotal += genres.getLength();
							updateSpeciesFromCampaigns(species);
							for(int i = 0 ; i < species.getLength();i++)
							{
								Element currentSpecies = (Element)species.item(i);
								if(!ToolsString.isNullOrempty(currentSpecies.getAttribute("id")))
								{
									Species s = new Species();
									s.setId(Integer.parseInt(currentSpecies.getAttribute("id")));
									s.setName(ToolsString.toFirstUpperCase(currentSpecies.getAttribute("name")));
									s.setDescription(currentSpecies.getAttribute("description"));
									s.setIndanger(currentSpecies.getAttribute("indanger").compareTo("true")==0);
									int danger = 0;
									if (currentSpecies.getAttribute("indanger").compareTo("1")==0)
										danger = 1;
									bdd.execSQL("INSERT OR REPLACE INTO Species VALues('"+s.getDescription().replace("'", "''")+"',"+currentSpecies.getAttribute("idparent")+",'"+s.getName().replace("'", "''")+"',"+danger+","+s.getId()+");");
								}
								if(percent != ((currentTotal+i)*100/nbTotal))
								{
									percent = ((currentTotal+i)*100/nbTotal);
									publishProgress(SHOW_PROGRESS+"",msgProgress,percent+"");
								}
							}
						}
						
						//delete
						msgProgress = getApplication().getResources().getString(R.string.deleteloading);
						publishProgress(SHOW_PROGRESS+"",msgProgress,0+"");
						
						NodeList deletes = doc.getElementsByTagName("delete");
						if(deletes.getLength()>0)
						{
							Element nodeupdates = (Element)deletes.item(0);
							NodeList families = nodeupdates.getElementsByTagName("family");
							NodeList genres = nodeupdates.getElementsByTagName("genre");
							NodeList species = nodeupdates.getElementsByTagName("species");
							nbAllGenre += genres.getLength();
							nbAllSpecies += species.getLength();
							nbFamilles += families.getLength() ;
							int nbTotal = families.getLength() + genres.getLength() + species.getLength();
							int currentTotal = 0;
							
							for(int i = 0 ; i < families.getLength();i++)
							{
								Element currentFamily = (Element)families.item(i);
								removeSpeciesFromCampaign("Family",bdd,currentFamily.getAttribute("id"));
								bdd.execSQL("DELETE FROM Species WHERE genre_id IN (SELECT id FROM Genre WHERE family_id = "+currentFamily.getAttribute("id")+")");
								bdd.execSQL("DELETE FROM Genre WHERE family_id = "+currentFamily.getAttribute("id"));
								bdd.execSQL("DELETE FROM Family WHERE id = "+currentFamily.getAttribute("id"));
							
								if(percent != (i*100/nbTotal))
								{
									percent = (i*100/nbTotal);
									publishProgress(SHOW_PROGRESS+"",msgProgress,percent+"");
								}
							}
							
							currentTotal = families.getLength();
							
							for(int i = 0 ; i < genres.getLength();i++)
							{
								Element currentGenre = (Element)genres.item(i);
								removeSpeciesFromCampaign("Genre",bdd,currentGenre.getAttribute("id"));
								bdd.execSQL("DELETE FROM Species WHERE genre_id = "+currentGenre.getAttribute("id")+";");
								bdd.execSQL("DELETE FROM Genre WHERE id = "+currentGenre.getAttribute("id")+";");
								if(percent != ((currentTotal+i)*100/nbTotal))
								{
									percent = ((currentTotal+i)*100/nbTotal);
									publishProgress(SHOW_PROGRESS+"",msgProgress,percent+"");
								}
							}
							
							currentTotal += genres.getLength();
							
							for(int i = 0 ; i < species.getLength();i++)
							{
								Element currentSpecies = (Element)species.item(i);
								if(!ToolsString.isNullOrempty(currentSpecies.getAttribute("id")))
								{
									removeSpeciesFromCampaign("Species",bdd,currentSpecies.getAttribute("id"));
									bdd.execSQL("DELETE FROM Species WHERE id = "+currentSpecies.getAttribute("id")+";");
								}
								if(percent != ((currentTotal+i)*100/nbTotal))
								{
									percent = ((currentTotal+i)*100/nbTotal);
									publishProgress(SHOW_PROGRESS+"",msgProgress,percent+"");
								}
							}
						}
					}
					bdd.execSQL("COMMIT;");
				}
	    	}
			catch(Exception e)
			{
				e.printStackTrace();
				Log.e("Synchro species", "ERREUR :"+e.getMessage()+"//" +e.getCause());
			}
	        return null;
	    }
	 
	    private void updateSpeciesFromCampaigns(NodeList species) {
	    	if(species.getLength()>0)
	    	{
		    	RuntimeExceptionDao<Campaign, Integer> simpleDao = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(Campaign.class);
		    	List<Campaign> campaigns = simpleDao.queryForAll();  	
				for(int i = 0 ; i < species.getLength();i++)
				{
					Element currentSpecies = (Element)species.item(i);
					if(!ToolsString.isNullOrempty(currentSpecies.getAttribute("id")))
					{	
				    	for(Campaign c : campaigns)
				    	{
				    		boolean isUpdated = false;
				    		for(int j = 0 ; j < c.getSpecies().size();j++)
				    		{
				    			if(c.getSpecies().get(j).getId()== Integer.parseInt(currentSpecies.getAttribute("id")))
				    			{
				    				c.getSpecies().get(j).setDescription(currentSpecies.getAttribute("description"));
				    				c.getSpecies().get(j).setName(currentSpecies.getAttribute("name"));
				    				boolean indanger = currentSpecies.getAttribute("indanger").compareTo("1")==0;
				    				c.getSpecies().get(j).setIndanger(indanger);
				    				isUpdated=true;
				    			}
				    		}
				    		if(isUpdated)
				    			simpleDao.createOrUpdate(c);
				    	}
					}
					
				}
	    	}
			
		}

		private void removeSpeciesFromCampaign(String type, SQLiteDatabase bdd, String id) {
			ArrayList<Integer> species = new ArrayList<Integer>();
			if(type.compareTo("Family")==0)
			{
				Cursor c = bdd.rawQuery("SELECT id FROM Species WHERE genre_id IN (SELECT id FROM Genre WHERE family_id = "+id+")", null);
				if (c.moveToFirst())
					while(!c.isAfterLast())
					{
						species.add(c.getInt(0));
						c.moveToNext();
					}
			}
			else if(type.compareTo("Genre")==0)
			{
				Cursor c = bdd.rawQuery("SELECT id FROM Species WHERE genre_id = "+id, null);
				if (c.moveToFirst())
					while(!c.isAfterLast())
					{
						species.add(c.getInt(0));
						c.moveToNext();
					}
			}
			else if(type.compareTo("Species")==0)
			{
				species.add(Integer.parseInt(id));
			}
			
			if(species.size()!=0)
			{
				removeSpeciesFromCampaign(species);
				removeSpeciesReferences(species, bdd);
			}
		}
	    
	    private void removeSpeciesReferences(ArrayList<Integer> species, SQLiteDatabase bdd) {
			//remove values and observations
	    	for(int i = 0; i < species.size();i++)
	    	{
	    		bdd.execSQL("DELETE FROM Value WHERE observation_id IN (SELECT id FROM Observation WHERE species_id = "+species.get(i)+")");
	    		bdd.execSQL("DELETE FROM Observation WHERE species_id = "+species.get(i));
	    	}
		}

		private void removeSpeciesFromCampaign(List<Integer> species)
	    {
	    	RuntimeExceptionDao<Campaign, Integer> simpleDao = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(Campaign.class);
	    	List<Campaign> campaigns = simpleDao.queryForAll();
	    	for(Campaign c : campaigns)
	    	{
	    		for(int i = 0 ; i < c.getSpecies().size();i++)
	    		{
	    			for(Integer ii : species)
	    			{
	    				if(c.getSpecies().get(i).getId()==ii)
	    				{
	    					c.getSpecies().remove(i);
	    					i--;
	    					break;
	    				}
	    			}
	    		}
	    		simpleDao.createOrUpdate(c);
	    	}
	    	
	    }

		@Override
	    protected void onPostExecute(Void result) {
			if (mProgressDialog.isShowing()) {
	            mProgressDialog.dismiss();
	        }
	    	if(succes)
	    	{
	    		String message = "";
	    		if(nbFamilles != 0)
	    			message += nbFamilles+" "+getApplicationContext().getResources().getString(R.string.families).toLowerCase()+ " ";
	    		if(nbAllGenre!=0)
	    			message += nbAllGenre+" "+getApplicationContext().getResources().getString(R.string.genres).toLowerCase()+" ";
	    		if(nbAllSpecies != 0)
	    			message += nbAllSpecies+" "+getApplicationContext().getResources().getString(R.string.speciesplurial).toLowerCase();
		        if(message.length()>0)
		        	message = " : "+message;
	    		Toast.makeText(getApplicationContext(), getApplicationContext().getResources().getString(R.string.synchronizeddata)+ message, Toast.LENGTH_LONG).show();
		        tvSpecies.setText(getString(R.string.updatecompleted));
		        
		        Calendar c = Calendar.getInstance();
		        s.setDateUpdateSpecies(ToolsString.getTwoDigitNumber(c.get(Calendar.DAY_OF_MONTH))+"/"+ToolsString.getTwoDigitNumber(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.YEAR)+"-"+ToolsString.getTwoDigitNumber(c.get(Calendar.HOUR_OF_DAY))+":"+ToolsString.getTwoDigitNumber(c.get(Calendar.MINUTE))+":"+ToolsString.getTwoDigitNumber(c.get(Calendar.SECOND)));
		        simpleDao.createOrUpdate(s);
	    	}
	    	else {
	    		Toast.makeText(getApplicationContext(), getApplication().getResources().getString(R.string.noserverconnection), Toast.LENGTH_LONG).show();
	    	}
	    	
	    	if(loadForms)
	    		new LoadCampaigns().execute();
	    }
	    
	}
	
	private class LoadCampaigns extends AsyncTask<Void, String, Void>
	{
		ProgressDialog mProgressDialog;
		
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
			mProgressDialog = new ProgressDialog(SyncActivity.this);
			mProgressDialog.setMessage(getApplicationContext().getResources().getString(R.string.synchronizeWait));
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
	    }
	 	 
	    @SuppressWarnings("unused")
		@Override
	    protected Void doInBackground(Void... arg0)   
	    {
	    	try{
	    		RuntimeExceptionDao<Campaign, Integer> daocampaign = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(Campaign.class);
	    		RuntimeExceptionDao<Species, Integer> daospecies = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(Species.class);
	    		RuntimeExceptionDao<Form, Integer> daoform = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(Form.class);
	    		RuntimeExceptionDao<Field, Integer> daofield = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(Field.class);
	        	
		    	Properties p = new Properties();
				p.loadFromXML(getApplicationContext().getResources().openRawResource(R.raw.properties));
		    	
		    	HttpClient httpclient = new DefaultHttpClient();
	            HttpPost httppost = new HttpPost(p.getProperty("urlServiceCampaignsExport"));
				
	            //HttpResponse response = httpclient.execute(httppost);
	            //Document doc = ToolsFile.getDocumentFromHttpResponse(response);
	            
				BufferedInputStream bis = new BufferedInputStream(getApplicationContext().getResources().openRawResource(R.raw.ceport));
				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				int current = 0;
				while ((current = bis.read()) != -1) {
					baf.append((byte) current);                       
				}
				DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
				DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
				byte[] data = baf.toByteArray();
				Document doc = docBuilder.parse(new ByteArrayInputStream(data));

	            ArrayList<Integer> campaignIds = new ArrayList<Integer>();
	            
	            //getting the new campaign, no update
				NodeList campaigns = doc.getElementsByTagName("campaign");					
				for(int c = 0 ; c < campaigns.getLength() ; c++)
				{
					Element currentCampaign = (Element)campaigns.item(c);
					campaignIds.add(Integer.parseInt(currentCampaign.getAttribute("id")));
					Campaign camp = daocampaign.queryForId(Integer.parseInt(currentCampaign.getAttribute("id")));
					
					if(camp == null || camp.getStart().after(new Date()))
					{
						Campaign campaign = new Campaign();
						campaign.setId(Integer.parseInt(currentCampaign.getAttribute("id")));
						campaign.setName(ToolsString.toFirstUpperCase(currentCampaign.getAttribute("name")));
						campaign.setDescription(currentCampaign.getAttribute("description"));
						String[] dates = currentCampaign.getAttribute("start").split("/");
						campaign.setStart(new Date((Integer.parseInt(dates[2])+100), (Integer.parseInt(dates[1])-1), Integer.parseInt(dates[0])));
						dates = currentCampaign.getAttribute("end").split("/");
						campaign.setEnd(new Date((Integer.parseInt(dates[2])+100), (Integer.parseInt(dates[1])-1), Integer.parseInt(dates[0])));

						NodeList species = currentCampaign.getElementsByTagName("species");
						ArrayList<Species> spes = new ArrayList<Species>();
						for(int s = 0; s < species.getLength() ; s++)
						{
							Element currentSpecies = (Element)species.item(s);
							spes.add(daospecies.queryForId(Integer.parseInt(currentSpecies.getAttribute("id"))));
						}
						campaign.setSpecies(spes);
						daocampaign.createOrUpdate(campaign);
						
						NodeList forms = currentCampaign.getElementsByTagName("form");
						for(int f = 0 ; f < forms.getLength() ; f++)
						{
							Element currentForm = (Element)forms.item(f);
							Form form = new Form();
							form.setId(Integer.parseInt(currentForm.getAttribute("id")));
							form.setDescription(currentForm.getAttribute("description"));
							form.setName(ToolsString.toFirstUpperCase(currentForm.getAttribute("name")));
							form.setCampaign(campaign);
							daoform.createOrUpdate(form);
							
							NodeList fields = currentForm.getElementsByTagName("field");
							for(int i = 0 ; i < fields.getLength() ; i++)
							{
								Element currentField = (Element)fields.item(i);
								Field field = new Field();
								field.setId(Integer.parseInt(currentField.getAttribute("id")));
								field.setName(currentField.getAttribute("name"));
								field.setType(currentField.getAttribute("type"));
								boolean required = currentField.getAttribute("required").compareTo("1")==0;
								field.setRequired(required);
								field.setChoices(currentField.getAttribute("choices"));
								field.setForm(form);
								
								daofield.createOrUpdate(field);
							}
						}
					}
				}
				SQLiteDatabase bdd = ToolsBDD.getInstance(SyncActivity.this).getBDD();
				
				List<Campaign> campaignToDelete = daocampaign.queryBuilder().where().notIn("id",campaignIds).query();
				bdd.execSQL("BEGIN;");
				for(int i = 0 ; i < campaignToDelete.size() ; i++)
				{
					bdd.execSQL("DELETE FROM Value WHERE observation_id IN (SELECT id FROM Observation WHERE Form_id IN (SELECT id FROM Form WHERE campaign_id="+campaignToDelete.get(i).getId()+"))");
					bdd.execSQL("DELETE FROM Observation WHERE Form_id IN(SELECT id FROM Form WHERE campaign_id="+campaignToDelete.get(i).getId()+")");
					bdd.execSQL("DELETE FROM Field WHERE form_id IN (SELECT id FROM Form WHERE campaign_id="+campaignToDelete.get(i).getId()+")");
					bdd.execSQL("DELETE FROM Form WHERE campaign_id="+campaignToDelete.get(i).getId());
					bdd.execSQL("DELETE FROM Campaign WHERE id ="+campaignToDelete.get(i).getId());
				}
				bdd.execSQL("COMMIT;");
	    	}
	    	catch(Exception e)
	    	{
	    		e.printStackTrace();
	    		Log.e("Sync campaign",e.getMessage());
	    	}
			return null;
	    }
	    
	    protected void onPostExecute(Void result) {
			if (mProgressDialog.isShowing()) {
	            mProgressDialog.dismiss();
	        }
			Toast.makeText(SyncActivity.this, R.string.updatecompleted, Toast.LENGTH_LONG).show();
			tvForm.setText(getString(R.string.updatecompleted));
	        Calendar c = Calendar.getInstance();
	        c.add(Calendar.MINUTE, -1);
	        s.setDateUpdateForm(ToolsString.getTwoDigitNumber(c.get(Calendar.DAY_OF_MONTH))+"/"+ToolsString.getTwoDigitNumber(c.get(Calendar.MONTH)+1)+"/"+c.get(Calendar.YEAR)+"-"+ToolsString.getTwoDigitNumber(c.get(Calendar.HOUR_OF_DAY))+":"+ToolsString.getTwoDigitNumber(c.get(Calendar.MINUTE))+":"+ToolsString.getTwoDigitNumber(c.get(Calendar.SECOND)));
	        simpleDao.createOrUpdate(s);
			
	    }
	}
	
	private class SendingObservations extends AsyncTask<Void, String, Void>
	{
		ProgressDialog mProgressDialog;
		
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
			mProgressDialog = new ProgressDialog(SyncActivity.this);
			mProgressDialog.setMessage(getApplicationContext().getResources().getString(R.string.currentlysendingobservation));
			mProgressDialog.setCancelable(false);
			mProgressDialog.setIndeterminate(false);
			mProgressDialog.show();
	    }

	    @Override
	    protected Void doInBackground(Void... arg0) {
            try {
			    Properties p = new Properties();
				p.loadFromXML(getApplicationContext().getResources().openRawResource(R.raw.properties));
	        	
				String xmlContent = "";
				RuntimeExceptionDao<Value, Integer> daovalue = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(Value.class);
				RuntimeExceptionDao<Observation, Integer> daoobs = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(Observation.class);
				
				List<Observation> obs = daoobs.queryBuilder().where().eq("valid", false).query();
				for(int o = 0 ; o < obs.size() ; o++)
				{
					publishProgress(" : "+(o+1)+"/"+obs.size());
					String userid, formid;
					if(s.getIdUser()==0)
						userid = " iduser=\"\" ";
					else userid = " iduser=\""+s.getIdUser()+"\"";
					if(obs.get(o).getForm()!= null)
						formid = obs.get(o).getForm().getId()+"";
					else formid = "";
					xmlContent += "<observation "+userid+" namePostAttributeForImage=\"image\" idform=\""+formid+"\" idspecies=\""+obs.get(o).getSpecies().getId()+"\" lattitude=\""+obs.get(o).getLattitude()+"\" longitude=\""+obs.get(o).getLongitude()+"\" comment=\""+obs.get(o).getComment()+"\" date=\""+obs.get(o).getDate()+"\">";
					xmlContent += "<values>";
					List<Value> values = daovalue.queryBuilder().where().eq("observation_id", obs.get(o).getId()).query();
					for(int v = 0 ; v < values.size();v++)
					{
						xmlContent += "<value idfield=\""+values.get(v).getField().getId()+"\" val=\""+values.get(v).getValue()+"\" />";
					}
					xmlContent += "</values>";
					xmlContent+="</observation>";
					
		            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
		            nameValuePairs.add(new BasicNameValuePair("xmlobservation", xmlContent));
		            nameValuePairs.add(new BasicNameValuePair("image", obs.get(o).getImagePath()));
		            post(p.getProperty("urlServiceSendObservation"),nameValuePairs);
		            
	            	obs.get(o).setValid(true);
	            	daoobs.createOrUpdate(obs.get(o));
				}

				/*
	            HttpClient httpclient = new DefaultHttpClient();
	            HttpPost httppost = new HttpPost(p.getProperty("urlServiceSendObservation"));
	            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
	            nameValuePairs.add(new BasicNameValuePair("xmlobservation", xmlContent));
	            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	            httpclient.execute(httppost);
	            */
            } catch (Exception e) {
               	Log.e("Connexion", "Erreur :"+e.getMessage());
            }
	        return null;
	    }

	    @Override
	    protected void onProgressUpdate(String... values){
	        super.onProgressUpdate(values);
            mProgressDialog.setMessage(getApplicationContext().getResources().getString(R.string.currentlysendingobservation)+" "+values[0]);
	    }

		@Override
	    protected void onPostExecute(Void result) {
			if (mProgressDialog.isShowing()) {
	            mProgressDialog.dismiss();
	        }
			tvObs.setText(R.string.syncareuptodate);
			Toast.makeText(SyncActivity.this, R.string.obssended, Toast.LENGTH_SHORT).show();
	    }
	    
		public void post(String url, List<NameValuePair> nameValuePairs) {
		    HttpClient httpClient = new DefaultHttpClient();
		    HttpPost httpPost = new HttpPost(url);

		    try {
		        MultipartEntity entity = new MultipartEntity();

		        for(int index=0; index < nameValuePairs.size(); index++) {
		            if(nameValuePairs.get(index).getName().equalsIgnoreCase("image")) {
		                entity.addPart(nameValuePairs.get(index).getName(), new FileBody(new File(nameValuePairs.get(index).getValue())));
		            } else {
		                entity.addPart(nameValuePairs.get(index).getName(), new StringBody(nameValuePairs.get(index).getValue()));
		            }
		        }
		        httpPost.setEntity(entity);
		        httpClient.execute(httpPost);
		    } catch (IOException e) {
		        e.printStackTrace();
		    }
		}

		
	}
}


