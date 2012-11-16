package fr.rocknscrum.liseronmobile;

import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import fr.rocknscrum.liseronmobile.database.ToolsORMLite;
import fr.rocknscrum.liseronmobile.observation.Campaign;
import fr.rocknscrum.liseronmobile.tools.ToolsString;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ObservationSelectCampagn extends Activity implements OnFocusChangeListener, OnClickListener, OnItemClickListener {
	private static final int MENU_SEARCH = 0;
	private static final int OBS_SELECT_SPECIES = 1;
	private static final int MENU_QUIT = 2;
	ListView lv;
	AutoCompleteTextView actv;
	Button actvButton;
	ArrayList<HashMap<String, String>> listItem;
	ArrayAdapter<String> adapter;
	ArrayList<String> distinctList;
	List<Campaign> l;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.listviewselectobservation);
        new LoadPage().execute();
    }
	
	/*MENU*/
	public boolean onCreateOptionsMenu(Menu menu) {
	    menu.add(0, MENU_SEARCH, 0, R.string.menusearch).setIcon(android.R.drawable.ic_menu_search);
	    menu.add(0,MENU_QUIT,0,R.string.home).setIcon(android.R.drawable.ic_menu_revert);
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case MENU_SEARCH:
    		search();
	    	return true;
	    case MENU_QUIT:
    		setResult(RESULT_OK);
    		finish();
	    	return true;
	    }
	    return false;
	}
    
	private void search() {
		actv.setVisibility(View.VISIBLE);
		actvButton.setVisibility(View.VISIBLE);    
		actv.setText("");
	}


	private class LoadPage extends AsyncTask <Void, String, Void>
	{
		ProgressDialog mProgressDialog;
		
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
			
			mProgressDialog = new ProgressDialog(ObservationSelectCampagn.this);
			mProgressDialog.setMessage(getApplicationContext().getResources().getString(R.string.synchronizeWait));
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
	    }
	 
	    @Override
	    protected Void doInBackground(Void... arg0) {
	    	
	    	lv = (ListView)findViewById(R.id.lsolv); 
	    	
	        actv = (AutoCompleteTextView)findViewById(R.id.lsosearchtextview);
	        actvButton = (Button)findViewById(R.id.lsosearchbutton);
	        actvButton.setOnClickListener(ObservationSelectCampagn.this);
	        actv.setOnFocusChangeListener(ObservationSelectCampagn.this);

			SharedPreferences mgr = PreferenceManager.getDefaultSharedPreferences(ObservationSelectCampagn.this);
			boolean hideEmptyResults = mgr.getBoolean("hideemptyresults", false);	
	        
	        actv.setOnItemClickListener(new OnItemClickListener() {
	        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	        	{
	        		View v = findViewById(R.id.lsosearchbutton);
	        		onClick(v);
	        	}
			});
	        
	        String query = ObservationSelectCampagn.this.getIntent().getStringExtra("search");
	        
	        listItem = new ArrayList<HashMap<String, String>>();
	        HashMap<String, String> map = new HashMap<String, String>();
	        l = new ArrayList<Campaign>();
	        try {
	        	RuntimeExceptionDao<Campaign, Integer> daocampaign = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(Campaign.class);
				if(ToolsString.isNullOrempty(query))
					l = daocampaign.queryBuilder().orderBy("name", true).query();
				else
					l=daocampaign.queryBuilder().orderBy("name", true).where().like("name", "%"+query.toLowerCase().replace(" ", "%")+"%").query();
				ArrayList<String> autocomplet = new ArrayList<String>();
				
				DateFormat dateFormat = new SimpleDateFormat("dd MMMM yyyy");
		        for(int i = 0 ; i < l.size();i++)
		        {
			        map = new HashMap<String, String>();
			        map.put("id", l.get(i).getId()+"");
			        map.put("name",l.get(i).getName());
			        map.put("description",l.get(i).getDescription());
			        map.put("datestart"," " + dateFormat.format(l.get(i).getStart()));
			        map.put("dateend"," " + dateFormat.format(l.get(i).getEnd()));
			        
			        Date today = new Date();
			        if(l.get(i).getSpecies().size()>0)
			        	if(l.get(i).getForms().size()>0)
			        		if(l.get(i).getStart().before(today) && l.get(i).getEnd().after(today))
			        		{
			        			map.put("valid", "true");
			        			map.put("smallfleche", String.valueOf(R.drawable.listnext));
			        		}
			        		else {
			        			map.put("valid", "false");
			        			map.put("smallfleche", String.valueOf(R.drawable.listnonext));
			        		}
			        	else {
		        			map.put("valid", "false");
		        			map.put("smallfleche", String.valueOf(R.drawable.listnonext));
		        		}
			        else {
	        			map.put("valid", "false");
	        			map.put("smallfleche", String.valueOf(R.drawable.listnonext));
	        		}

			        if(hideEmptyResults==false)
				        listItem.add(map);
			        else if (((String)map.get("valid")).compareTo("true")==0)
			        	listItem.add(map);
			        	
			    	autocomplet.add(l.get(i).getName());
		        }  
				
		        SortedSet<String> set = new TreeSet<String>() ;
		        set.addAll(autocomplet) ;
		        distinctList = new ArrayList<String>(set);
		        adapter = new ArrayAdapter<String>(ObservationSelectCampagn.this,android.R.layout.simple_dropdown_item_1line, distinctList);
				
			} catch (SQLException e) {
				mProgressDialog.dismiss();
				e.printStackTrace();
			}
	        return null;
	    }
	    
	    @Override
	    protected void onPostExecute(Void result) {
	    	mProgressDialog.dismiss();
	    	actv.setAdapter(adapter);
	        SimpleAdapter mSchedule = new SimpleAdapter(getApplicationContext(), listItem, R.layout.itemlistviewselectcompaign, new String[] {"name", "description", "datestart", "dateend","smallfleche"}, new int[] {R.id.lsotitle, R.id.lsodesc,R.id.lsostart,R.id.lsoend,R.id.lsoimageView1});
	        lv.setAdapter(mSchedule);
	        lv.setFastScrollEnabled(true);
	        lv.setOnItemClickListener(ObservationSelectCampagn.this);
	    }
	}
	
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(v.getId()==R.id.lsosearchtextview)
			if(hasFocus)
				actv.setText("");
			else if(ToolsString.isNullOrempty(actv.getText().toString()))
					actv.setHint(R.string.inputdialoghint);		
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.lsosearchbutton)
		{
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(actv.getWindowToken(), 0);
			
			String smallInput = actv.getText().toString().toLowerCase();
			
        	boolean res = false;
        	for(String s : distinctList)
        	{
        		if(s.toLowerCase().contains(smallInput))
        		{
        			res = true;
        			break;
        		}
        	}
			
        	if(res)
        	{
				Bundle objetbunble = new Bundle();
				objetbunble.putString("search", actv.getText().toString());
				Intent intent = new Intent(ObservationSelectCampagn.this, ObservationSelectCampagn.class);
				intent.putExtras(objetbunble);
				startActivity(intent);
        	}
        	else Toast.makeText(this, R.string.nosearchresult, Toast.LENGTH_SHORT).show();
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(listItem.get(arg2).get("valid").compareTo("false")==0)
		{
			Toast.makeText(this, R.string.nospeciesorformcampaign, Toast.LENGTH_SHORT).show();
		}
		else {
			String id = listItem.get(arg2).get("id");
			if(l.get(arg2).getSpecies().size()==1)
			{
				if(l.get(arg2).getForms().size()>1)
				{
					Bundle objetbunble = new Bundle();
					objetbunble.putString("campaignid", id+"");
					objetbunble.putString("speciesid", l.get(arg2).getSpecies().get(0).getId()+"");
					Intent intent = new Intent(ObservationSelectCampagn.this, ObservationSelectForm.class);
					intent.putExtras(objetbunble);
					startActivityForResult(intent, OBS_SELECT_SPECIES);
				}
				else {
					Bundle objetbunble = new Bundle();
					objetbunble.putString("campaignid", id+"");
					objetbunble.putString("speciesid", l.get(arg2).getSpecies().get(0).getId()+"");
					objetbunble.putString("formid", l.get(arg2).getForms().getWrappedIterable().iterator().next().getId()+"");
					Intent intent = new Intent(ObservationSelectCampagn.this, ObservationSubmit.class);
					intent.putExtras(objetbunble);
					startActivityForResult(intent, OBS_SELECT_SPECIES);
				}
			}
			else 
			{
				Bundle objetbunble = new Bundle();
				objetbunble.putString("campaignid", id+"");
				objetbunble.putString("type", "Species");
				Intent intent = new Intent(ObservationSelectCampagn.this, HelpAuthentificationActivity.class);
				intent.putExtras(objetbunble);
				startActivityForResult(intent, OBS_SELECT_SPECIES);
			}
		}
	}
	
    /*ROTATION OF THE SCREEN*/
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	}
	
	@Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		if (resultCode == RESULT_OK) {  
			if(requestCode == OBS_SELECT_SPECIES)
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
