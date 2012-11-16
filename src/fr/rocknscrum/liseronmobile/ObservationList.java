package fr.rocknscrum.liseronmobile;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import fr.rocknscrum.liseronmobile.classification.Species;
import fr.rocknscrum.liseronmobile.database.ToolsBDD;
import fr.rocknscrum.liseronmobile.database.ToolsORMLite;
import fr.rocknscrum.liseronmobile.observation.Campaign;
import fr.rocknscrum.liseronmobile.observation.Form;
import fr.rocknscrum.liseronmobile.observation.Observation;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ObservationList extends Activity implements OnItemClickListener, OnItemLongClickListener {

	private static final int MENU_QUIT = 0;
	private static final int PROC_CONS = 0;
	ListView lv;
	ArrayList<HashMap<String, String>> listItem;
	ArrayAdapter<String> adapter;
	RuntimeExceptionDao<Observation, Integer> daoobs;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.listviewobservations);
		new LoadPage().execute();
	}


	private class LoadPage extends AsyncTask <Void, String, Void>
	{
		ProgressDialog mProgressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			mProgressDialog = new ProgressDialog(ObservationList.this);
			mProgressDialog.setMessage(getApplicationContext().getResources().getString(R.string.synchronizeWait));
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			lv = (ListView)findViewById(R.id.ollv); 

			listItem = new ArrayList<HashMap<String, String>>();
			HashMap<String, String> map = new HashMap<String, String>();
			List<Observation> l = new ArrayList<Observation>();
			daoobs = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(Observation.class);
			RuntimeExceptionDao<Species, Integer> daospecies = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(Species.class);
			RuntimeExceptionDao<Campaign, Integer> daocampaign = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(Campaign.class);			
			RuntimeExceptionDao<Form, Integer> daoform = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(Form.class);
			l = daoobs.queryForAll();
			SharedPreferences mgr = PreferenceManager.getDefaultSharedPreferences(ObservationList.this);
			boolean hideEmptyResults = mgr.getBoolean("hideemptyresults", false);	
			for(int i = l.size()-1 ; i >=0 ; i--)
			{
				Species s = daospecies.queryForId(l.get(i).getSpecies().getId());
				Form f = null;
				if(l.get(i).getForm()!=null)
					f = daoform.queryForId(l.get(i).getForm().getId());
				Campaign c = null;
				if(f!=null)
					c = daocampaign.queryForId(f.getCampaign().getId());
				map = new HashMap<String, String>();
				map.put("id", l.get(i).getId()+"");
				map.put("species", "- "+s.getName());
				if(f != null)
				{
					map.put("campaign","- "+c.getName());
					map.put("form", "- "+f.getName());
				}
				else {
					map.put("campaign","- "+getString(R.string.nofilled));
					map.put("form", "- "+getString(R.string.nofilled));
				}
				map.put("img", l.get(i).getImagePath().replace(".bmp", "-tiny.jpg"));
				if(l.get(i).getValid())
				{
					map.put("smallfleche", String.valueOf(R.drawable.listnonext));
					map.put("valid", "true");
				}
				else 
					{
						map.put("smallfleche", String.valueOf(R.drawable.listnext));
						map.put("valid", "false");
					}
				if(l.get(i).getValid())
				{
					if(!hideEmptyResults)
						listItem.add(map);
				}
				else listItem.add(map);
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			mProgressDialog.dismiss();
			SimpleAdapter mSchedule = new SimpleAdapter(getApplicationContext(), listItem, R.layout.itemlistviewobservations, new String[] {"campaign", "form","species", "img","smallfleche"}, new int[] {R.id.olcampaign,R.id.olform, R.id.olspecies, R.id.olimg, R.id.olrightimg});
			lv.setAdapter(mSchedule);
			lv.setFastScrollEnabled(true);
			lv.setOnItemClickListener(ObservationList.this);
			lv.setOnItemLongClickListener(ObservationList.this);
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

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String id = listItem.get(arg2).get("id");
		Bundle objetbunble = new Bundle();
		objetbunble.putString("observationid", id+"");
		Intent intent = new Intent(ObservationList.this, ObservationSubmit.class);
		intent.putExtras(objetbunble);
		startActivityForResult(intent, PROC_CONS);
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
	
		if(listItem.get(arg2).get("valid").compareTo("true")==0)
		{
			Toast.makeText(ObservationList.this, R.string.alreadysent, Toast.LENGTH_SHORT).show();
		}
		else {
			final String id = listItem.get(arg2).get("id");
	        AlertDialog.Builder adb = new AlertDialog.Builder(this);
	        adb.setTitle(R.string.delete);
	        adb.setIcon(android.R.drawable.ic_dialog_info);
	 
	        adb.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	               	
	            	SQLiteDatabase bdd = ToolsBDD.getInstance(ObservationList.this).getBDD();
	            	bdd.execSQL("DELETE FROM Value WHERE observation_id="+id);
	            	bdd.execSQL("DELETE FROM Observation WHERE id="+id);
	            	
	            	Toast.makeText(ObservationList.this, R.string.observationdeleted, Toast.LENGTH_LONG).show();
	        		Intent intent = new Intent(ObservationList.this, ObservationList.class);
	        		startActivityForResult(intent,PROC_CONS);
	            	finish();
	          } });
	 
	        adb.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {	
	          } });
	        adb.show();
		}
		return false;
	}
	
	@Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		if (resultCode == RESULT_OK) {  
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
