package fr.rocknscrum.liseronmobile;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import fr.rocknscrum.liseronmobile.classification.Family;
import fr.rocknscrum.liseronmobile.classification.Genre;
import fr.rocknscrum.liseronmobile.classification.Species;
import fr.rocknscrum.liseronmobile.database.ToolsORMLite;
import fr.rocknscrum.liseronmobile.tools.ToolsConnection;
import fr.rocknscrum.liseronmobile.tools.ToolsString;

public class SpeciesInformationActivity extends Activity{

	private static final int MENU_SEARCHINFO = 0;
	private static final int MENU_OBS = 1;
	private static final int OBS_SUBMIT = 2;
	private static final int MENU_QUIT = 3;
	String id, type, currentGenreName, currentSpeciesName;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.speciesinformation);
        
        id = this.getIntent().getStringExtra("id");
        type = this.getIntent().getStringExtra("type");
        
        if(type.compareTo("Species")==0)
        {
	    	RuntimeExceptionDao<Species, Integer> simpleDao = ToolsORMLite.getInstance(this).getHelper().getRuntimeExceptionDao(Species.class);
	        Species s = simpleDao.queryForId(Integer.parseInt(id));        
	        TextView tvspeciestitle = (TextView)findViewById(R.id.speciesinformationspeciestitle);
	        tvspeciestitle.setText(getString(R.string.species)+" : "+s.getName());
	        TextView tvspeciesdesc = (TextView)findViewById(R.id.speciesinformationspeciesdescription);
	        if(!ToolsString.isNullOrempty(s.getDescription()))
	        	tvspeciesdesc.setText(s.getDescription());
        
	    	RuntimeExceptionDao<Genre, Integer> simpleDao2 = ToolsORMLite.getInstance(this).getHelper().getRuntimeExceptionDao(Genre.class);
	        Genre g = simpleDao2.queryForId(s.getGenre().getId());        
	        TextView tvgenretitle = (TextView)findViewById(R.id.speciesinformationgenretitle);
	        tvgenretitle.setText(getString(R.string.genre)+" : "+g.getName());
	        TextView tvgenredesc = (TextView)findViewById(R.id.speciesinformationgenredescription);
	        if(!ToolsString.isNullOrempty(g.getDescription()))
	        	tvgenredesc.setText(g.getDescription());
	        
	    	RuntimeExceptionDao<Family, Integer> simpleDao3 = ToolsORMLite.getInstance(this).getHelper().getRuntimeExceptionDao(Family.class);
	    	Family f = simpleDao3.queryForId(g.getFamily().getId());        
	        TextView tvfamilytitle = (TextView)findViewById(R.id.speciesinformationfamilytitle);
	        tvfamilytitle.setText(getString(R.string.family)+" : "+f.getName());
	        TextView tvfamilydesc = (TextView)findViewById(R.id.speciesinformationfamilydescription);
	        if(!ToolsString.isNullOrempty(f.getDescription()))
	        	tvfamilydesc.setText(f.getDescription());
	        
	        if(!s.isIndanger())
	        {
		        TextView tvwarning = (TextView)findViewById(R.id.speciesinformationwarning);
		        tvwarning.setVisibility(View.GONE);
	        }
	        
	        currentGenreName = g.getName();
	        currentSpeciesName = s.getName();
        }
        else if(type.compareTo("Genre")==0)
        {
	        TextView tvspeciestitle = (TextView)findViewById(R.id.speciesinformationspeciestitle);
	        tvspeciestitle.setVisibility(View.GONE);
	        TextView tvspeciesdesc = (TextView)findViewById(R.id.speciesinformationspeciesdescription);
	        tvspeciesdesc.setVisibility(View.GONE);
        
	    	RuntimeExceptionDao<Genre, Integer> simpleDao2 = ToolsORMLite.getInstance(this).getHelper().getRuntimeExceptionDao(Genre.class);
	        Genre g = simpleDao2.queryForId(Integer.parseInt(id));        
	        TextView tvgenretitle = (TextView)findViewById(R.id.speciesinformationgenretitle);
	        tvgenretitle.setText(getString(R.string.genre)+" : "+g.getName());
	        TextView tvgenredesc = (TextView)findViewById(R.id.speciesinformationgenredescription);
	        if(!ToolsString.isNullOrempty(g.getDescription()))
	        	tvgenredesc.setText(g.getDescription());
	        
	    	RuntimeExceptionDao<Family, Integer> simpleDao3 = ToolsORMLite.getInstance(this).getHelper().getRuntimeExceptionDao(Family.class);
	    	Family f = simpleDao3.queryForId(g.getFamily().getId());        
	        TextView tvfamilytitle = (TextView)findViewById(R.id.speciesinformationfamilytitle);
	        tvfamilytitle.setText(getString(R.string.family)+" : "+f.getName());
	        TextView tvfamilydesc = (TextView)findViewById(R.id.speciesinformationfamilydescription);
	        if(!ToolsString.isNullOrempty(f.getDescription()))
	        	tvfamilydesc.setText(f.getDescription());
	        
	        TextView tvwarning = (TextView)findViewById(R.id.speciesinformationwarning);
	        tvwarning.setVisibility(View.GONE);
        }
        else {    
	        TextView tvspeciestitle = (TextView)findViewById(R.id.speciesinformationspeciestitle);
	        tvspeciestitle.setVisibility(View.GONE);
	        TextView tvspeciesdesc = (TextView)findViewById(R.id.speciesinformationspeciesdescription);
	        tvspeciesdesc.setVisibility(View.GONE);
      
	        TextView tvgenretitle = (TextView)findViewById(R.id.speciesinformationgenretitle);
	        tvgenretitle.setVisibility(View.GONE);
	        TextView tvgenredesc = (TextView)findViewById(R.id.speciesinformationgenredescription);
	        tvgenredesc.setVisibility(View.GONE);
	        
	    	RuntimeExceptionDao<Family, Integer> simpleDao3 = ToolsORMLite.getInstance(this).getHelper().getRuntimeExceptionDao(Family.class);
	    	Family f = simpleDao3.queryForId(Integer.parseInt(id));        
	        TextView tvfamilytitle = (TextView)findViewById(R.id.speciesinformationfamilytitle);
	        tvfamilytitle.setText(getString(R.string.family)+" : "+f.getName());
	        TextView tvfamilydesc = (TextView)findViewById(R.id.speciesinformationfamilydescription);
	        if(!ToolsString.isNullOrempty(f.getDescription()))
	        	tvfamilydesc.setText(f.getDescription());
	        
	        TextView tvwarning = (TextView)findViewById(R.id.speciesinformationwarning);
	        tvwarning.setVisibility(View.GONE);
        }
	 }
	 
	 
		/*MENU*/
		public boolean onCreateOptionsMenu(Menu menu) {
			if(type.compareTo("Species")==0)
				menu.add(0, MENU_SEARCHINFO, 0, R.string.speciesinfomoredata).setIcon(android.R.drawable.ic_menu_info_details);
			menu.add(0, MENU_OBS, 0, R.string.obswithoutcampaign).setIcon(android.R.drawable.ic_menu_edit);
			menu.add(0,MENU_QUIT,0,R.string.home).setIcon(android.R.drawable.ic_menu_revert);
			
		    return true;
		}
		
		public boolean onOptionsItemSelected(MenuItem item) {
		    switch (item.getItemId()) {
		    case MENU_QUIT:
	    		setResult(RESULT_OK);
	    		finish();
		    	return true;
		    case MENU_SEARCHINFO:
		    	if(ToolsConnection.isOnline(this))
		    	{
		    		SharedPreferences mgr = PreferenceManager.getDefaultSharedPreferences(this);
		    		boolean useinternetconnection = mgr.getBoolean("useinternetconnection", true);	
		    		if(useinternetconnection)
		    		{
						Bundle objetbunble = new Bundle();
						objetbunble.putString("genre", currentGenreName);
						objetbunble.putString("species", currentSpeciesName);
						Intent intent = new Intent(SpeciesInformationActivity.this, SpeciesInformationWebViewActivity.class);
						intent.putExtras(objetbunble);
						startActivityForResult(intent, OBS_SUBMIT);
		    		}
			    	else {
			    		Toast.makeText(this, R.string.noconnectionallowed, Toast.LENGTH_SHORT).show();
			    	}
		    	}
		    	else {
		    		Toast.makeText(this, R.string.noconnectionavailable, Toast.LENGTH_SHORT).show();
		    	}
		    	return true;
		    case MENU_OBS:
				Bundle objetbunble = new Bundle();
				objetbunble.putString("speciesid", id);
				Intent intent = new Intent(SpeciesInformationActivity.this, ObservationSubmit.class);
				intent.putExtras(objetbunble);
				startActivityForResult(intent,OBS_SUBMIT);
		    	return true;
		    }
		    return false;
		}
	
		@Override  
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
			if (resultCode == RESULT_OK) {  
				if(requestCode == OBS_SUBMIT)
				{
					if (getParent() == null) {
					    setResult(Activity.RESULT_OK, null);
					}
					else {
					    getParent().setResult(Activity.RESULT_OK, null);
					}
					finish();
				}
			}
		}
}
