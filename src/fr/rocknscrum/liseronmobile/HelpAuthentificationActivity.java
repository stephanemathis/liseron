package fr.rocknscrum.liseronmobile;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import fr.rocknscrum.liseronmobile.classification.Classification;
import fr.rocknscrum.liseronmobile.classification.Family;
import fr.rocknscrum.liseronmobile.classification.Genre;
import fr.rocknscrum.liseronmobile.classification.Species;
import fr.rocknscrum.liseronmobile.custom.SimpleAdapterHACustom;
import fr.rocknscrum.liseronmobile.database.ToolsBDD;
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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class HelpAuthentificationActivity extends FragmentActivity implements OnItemClickListener, OnClickListener, OnFocusChangeListener, OnItemLongClickListener {

	private static final int MENU_SEARCH = 0;
	private static final int MENU_ALLSPECIES = 1;
	private static final int MENU_OBS = 2;
	private static final int OBS_SELECT_FORM = 3;
	private static final int OBS_SUBMIT = 4;
	private static final int MENU_QUIT = 5;
	ArrayList<HashMap<String, String>> listItem;
	ArrayAdapter<String> adapter;
	ArrayList<String> distinctList;
	private ListView lv;
	String type = null;
	String id = null;
	String selectedId = null;
	String campaignid;
	boolean observationProcess = false;
	SimpleAdapterHACustom mSchedule;
	AutoCompleteTextView actv;
	Button actvButton;
	int nbrotation =0;
 	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.listviewcustom);
        new LoadPage().execute();
    }

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

		if(type.compareTo("Species")!=0)
		{
			if(Integer.parseInt(listItem.get(arg2).get("nbChildren"))==0)
			{
				if(type.compareTo("Family")==0)
					Toast.makeText(this, R.string.nogenre, Toast.LENGTH_SHORT).show();
				else
					Toast.makeText(this, R.string.nospecies, Toast.LENGTH_SHORT).show();
				Object o = findViewById(R.id.fragspeciesinformation);
				if(o!=null)
				{
					int id = Integer.parseInt(listItem.get(arg2).get("id"));
					showDetail(id,type);
				}
			}
			else {
				int id = Integer.parseInt(listItem.get(arg2).get("id"));
				Bundle objetbunble = new Bundle();
				objetbunble.putString("id", id+"");
				if(type.compareTo("Family")==0)
					objetbunble.putString("type", "Genre");
				else objetbunble.putString("type", "Species");
				Intent intent = new Intent(HelpAuthentificationActivity.this, HelpAuthentificationActivity.class);
				intent.putExtras(objetbunble);
				startActivityForResult(intent,OBS_SUBMIT);
			}
		}
		else {
			if(observationProcess)
			{
				RuntimeExceptionDao<Campaign, Integer> daocampaign = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(Campaign.class);
				Campaign currentCampaign = daocampaign.queryForId(Integer.parseInt(campaignid));
				
				if(currentCampaign.getForms().size()==1)
				{
					Bundle objetbunble = new Bundle();
					objetbunble.putString("campaignid", campaignid);
					objetbunble.putString("speciesid", listItem.get(arg2).get("id"));
					objetbunble.putString("formid", currentCampaign.getForms().getWrappedIterable().iterator().next().getId()+"");
					Intent intent = new Intent(HelpAuthentificationActivity.this, ObservationSubmit.class);
					intent.putExtras(objetbunble);
					startActivityForResult(intent, OBS_SELECT_FORM);
				}
				else {
					Bundle objetbunble = new Bundle();
					objetbunble.putString("campaignid", campaignid+"");
					objetbunble.putString("speciesid", listItem.get(arg2).get("id"));
					Intent intent = new Intent(HelpAuthentificationActivity.this, ObservationSelectForm.class);
					intent.putExtras(objetbunble);
					startActivityForResult(intent, OBS_SELECT_FORM);
				}
			}
			else {
				Object o = findViewById(R.id.fragspeciesinformation);
				if(o==null)
				{
					int id = Integer.parseInt(listItem.get(arg2).get("id"));
					Bundle objetbunble = new Bundle();
					objetbunble.putString("id", id+"");
					objetbunble.putString("type", type);
					Intent intent = new Intent(HelpAuthentificationActivity.this, SpeciesInformationActivity.class);
					intent.putExtras(objetbunble);
					startActivityForResult(intent,OBS_SUBMIT);
				}
				else {
					int id = Integer.parseInt(listItem.get(arg2).get("id"));
					selectedId = listItem.get(arg2).get("id");
					showDetail(id,type);
				}
			}
		}

	}
	
	/*MENU*/
	public boolean onCreateOptionsMenu(Menu menu) {
	    menu.add(0, MENU_SEARCH, 0, R.string.menusearch).setIcon(android.R.drawable.ic_menu_search);
	    if(!observationProcess)
	    	menu.add(0, MENU_ALLSPECIES, 0, R.string.displayallspecies).setIcon(android.R.drawable.ic_menu_compass);
		if(type.compareTo("Species")==0 && !observationProcess)
		{
			Object o = findViewById(R.id.fragspeciesinformation);
			if(o!=null)
			{
				menu.add(0, MENU_OBS, 0, R.string.obswithoutcampaign).setIcon(android.R.drawable.ic_menu_edit);
			}
		}
		menu.add(0,MENU_QUIT,0,R.string.home).setIcon(android.R.drawable.ic_menu_revert);
	    return true;
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	    case MENU_SEARCH:
    		search();
	    	return true;
	    case MENU_ALLSPECIES:
    		viewAllSpecies();
	    	return true;
	    case MENU_OBS:
    		doObservation();
	    	return true;
	    case MENU_QUIT:
    		setResult(RESULT_OK);
    		finish();
	    	return true;
	    }
	    return false;
	}

	public void doObservation()
	{
		if(selectedId!= null)
		{
			Bundle objetbunble = new Bundle();
			objetbunble.putString("speciesid", selectedId);
			Intent intent = new Intent(HelpAuthentificationActivity.this, ObservationSubmit.class);
			intent.putExtras(objetbunble);
			startActivityForResult(intent, OBS_SUBMIT);
		}
		else {
			Toast.makeText(this, R.string.selectedspeciesneeded, Toast.LENGTH_SHORT).show();
		}
	}
	
	private void viewAllSpecies() {
		
				Bundle objetbunble = new Bundle();
				objetbunble.putString("id","");
				objetbunble.putString("type", "Species");
				Intent intent = new Intent(HelpAuthentificationActivity.this, HelpAuthentificationActivity.class);
				intent.putExtras(objetbunble);
				startActivityForResult(intent,OBS_SUBMIT);
	}

	private void search() {

        actv.setVisibility(View.VISIBLE);
        actvButton.setVisibility(View.VISIBLE);    
	}

	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.listviewsearchbutton)
		{
			InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(actv.getWindowToken(), 0);

        	final String input = actv.getText().toString();
        	String smallInput = input.toLowerCase();
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
	    		if(observationProcess)
	    		{
	    			objetbunble.putString("campaignid",campaignid);
	    			objetbunble.putString("type", "Species");
	    		}
	    		else 
	    		{
	    			objetbunble.putString("id", id+"");
	    			objetbunble.putString("type", type);
	    		}
	    		objetbunble.putString("search", input);
	    		Intent intent = new Intent(HelpAuthentificationActivity.this, HelpAuthentificationActivity.class);
	    		intent.putExtras(objetbunble);
	    		startActivityForResult(intent,OBS_SUBMIT);
        	}
        	else {
        		Toast.makeText(this, R.string.nosearchresult, Toast.LENGTH_SHORT).show();
        	}
		}
	}

	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		if(v.getId()==R.id.listviewsearchtextview)
			if(hasFocus)
				;
			else if(ToolsString.isNullOrempty(actv.getText().toString()))
					actv.setHint(R.string.inputdialoghint);
		
	}
	
    /*ROTATION OF THE SCREEN*/
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
	    Object o = findViewById(R.id.fragspeciesinformation);
	    if(o!= null)
	    {
	    	LinearLayout ll = (LinearLayout)findViewById(R.id.lllistview);
	    	nbrotation++;
	    	LayoutParams param;
	    	if(nbrotation%2==0)
	    		param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, (float) 75.0);
	    	else param = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT, (float) 40.0);
	    	ll.setLayoutParams(param);
	    }
	}

	@Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		if (resultCode == RESULT_OK) {  
			if(requestCode == OBS_SELECT_FORM || requestCode == OBS_SUBMIT)
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
	
	@Override
	public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		
		//fragspeciesinformation is only declared with a large screen in landscape
		Object o = findViewById(R.id.fragspeciesinformation);
		if(o==null)
		{
			int id = Integer.parseInt(listItem.get(arg2).get("id"));
			Bundle objetbunble = new Bundle();
			objetbunble.putString("id", id+"");
			objetbunble.putString("type", type);
			Intent intent = new Intent(HelpAuthentificationActivity.this, SpeciesInformationActivity.class);
			intent.putExtras(objetbunble);
			startActivityForResult(intent,OBS_SUBMIT);
		}
		else {
			int id = Integer.parseInt(listItem.get(arg2).get("id"));
			showDetail(id,type);
		}
		return true;
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    if ((keyCode == KeyEvent.KEYCODE_BACK)) {
	        finish();
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * Update the right panel
	 * @author Mathis
	 * @param id : the id of the classification
	 * @param objectType : the type of the classification
	 */
	public void showDetail(int id, String objectType)
	{
		Fragment fragment = new SpeciesInfoFragment(id, objectType);
	    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
	    transaction.replace(R.id.fragspeciesinformation, fragment);
	    transaction.addToBackStack(null);
	    transaction.commit();
	}
	
	private class LoadPage extends AsyncTask <Void, String, Void>
	{
		ProgressDialog mProgressDialog;
		
	    @Override
	    protected void onPreExecute() {
	        super.onPreExecute();
			
			mProgressDialog = new ProgressDialog(HelpAuthentificationActivity.this);
			mProgressDialog.setMessage(getApplicationContext().getResources().getString(R.string.synchronizeWait));
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
	    }
	 
	    @Override
	    protected Void doInBackground(Void... arg0) {
	    	ArrayList<Integer> nbChildren = new ArrayList<Integer>();
	    	
	        lv = (ListView)findViewById(R.id.listviewperso);        
	        type = HelpAuthentificationActivity.this.getIntent().getStringExtra("type");
	        id = HelpAuthentificationActivity.this.getIntent().getStringExtra("id");
	        campaignid = HelpAuthentificationActivity.this.getIntent().getStringExtra("campaignid");
	        if(!ToolsString.isNullOrempty(campaignid))
	        	observationProcess = true;
	        
	        actv = (AutoCompleteTextView)findViewById(R.id.listviewsearchtextview);
	        actvButton = (Button)findViewById(R.id.listviewsearchbutton);
	        actvButton.setOnClickListener(HelpAuthentificationActivity.this);
	        actv.setOnFocusChangeListener(HelpAuthentificationActivity.this);
	        
			SharedPreferences mgr = PreferenceManager.getDefaultSharedPreferences(HelpAuthentificationActivity.this);
			boolean hideEmptyResults = mgr.getBoolean("hideemptyresults", false);	
	        
	        String searchQuery = HelpAuthentificationActivity.this.getIntent().getStringExtra("search");
	        if(searchQuery!=null)
	        	searchQuery ="%"+searchQuery.toLowerCase().replace(" ", "%")+"%";
	        
	        //fill a list with Classification
	        listItem = new ArrayList<HashMap<String, String>>();
	        HashMap<String, String> map = new HashMap<String, String>();
	        ArrayList<Classification> l = new ArrayList<Classification>();
	        
	        if(type.compareTo("Family")==0)
	        {
	        	try{
	        		
		        	RuntimeExceptionDao<Family, Integer> simpleDao = ToolsORMLite.getInstance(HelpAuthentificationActivity.this).getHelper().getRuntimeExceptionDao(Family.class);
		        	List<Family> ll;
		        	if(!ToolsString.isNullOrempty(searchQuery))
		        			ll = simpleDao.queryBuilder().orderBy("name", true).where().like("name",searchQuery).query();
		        	else ll = simpleDao.queryBuilder().orderBy("name", true).query();
		        	nbChildren = ToolsBDD.getInstance(getApplicationContext()).getNbChildrenFromFamilies(searchQuery);
		        	int i=0;
		        	for(Family f : ll)
		        	{
		        		if(nbChildren.get(i)!=0 || !hideEmptyResults)
		        		{
		        			l.add(f);
		        			i++;
		        		}
		        		else nbChildren.remove(i);
		        	}
	        	} catch (SQLException e) {e.printStackTrace();}
	        }
	        else if(type.compareTo("Genre")==0)
	        {
	        	try {
	        		RuntimeExceptionDao<Genre, Integer> simpleDao = ToolsORMLite.getInstance(HelpAuthentificationActivity.this).getHelper().getRuntimeExceptionDao(Genre.class);
	        		List<Genre> ll;
	        		if(!ToolsString.isNullOrempty(searchQuery))
	        			ll = simpleDao.queryBuilder().orderBy("name", true).where().eq("family_id", id).and().like("name",searchQuery).query();
	        		else ll = simpleDao.queryBuilder().orderBy("name", true).where().eq("family_id", id).query();
		        	nbChildren = ToolsBDD.getInstance(getApplicationContext()).getNbChildrenFromGenre(searchQuery, id);
		        	int i=0;
	        		for(Genre f : ll)
		        	{
		        		if(nbChildren.get(i)!=0 || !hideEmptyResults)
		        		{
		        			l.add(f);
		        			i++;
		        		}
		        		else nbChildren.remove(i);
		        	}
				} catch (SQLException e) {e.printStackTrace();}
	        }
	        else {
				try {
					RuntimeExceptionDao<Species, Integer> simpleDao = ToolsORMLite.getInstance(HelpAuthentificationActivity.this).getHelper().getRuntimeExceptionDao(Species.class);
					RuntimeExceptionDao<Campaign, Integer> daocampaign = ToolsORMLite.getInstance(HelpAuthentificationActivity.this).getHelper().getRuntimeExceptionDao(Campaign.class);
					
					List<Species> ll = new ArrayList<Species>();
					
					if(observationProcess)
					{
						Campaign c = daocampaign.queryForId(Integer.parseInt(campaignid));
						if(c.getSpecies()!=null)
						{
							List<Species> llll = c.getSpecies();
							Collections.sort(llll);
							if(!ToolsString.isNullOrempty(searchQuery))
							{
								searchQuery = searchQuery.substring(1,searchQuery.length()-1);
								searchQuery = searchQuery.replace("%", " ");
								for(int i = 0 ; i < llll.size();i++)
								{
									if(llll.get(i).getName().toLowerCase().contains(searchQuery))
										ll.add(llll.get(i));
								}
							}
							else ll = llll;
						}
						else ll = new ArrayList<Species>();
					}
					else if(!ToolsString.isNullOrempty(searchQuery))
						if(!ToolsString.isNullOrempty(id))
							ll = simpleDao.queryBuilder().orderBy("name", true).where().eq("genre_id", id).and().like("name",searchQuery).query();
						else ll = simpleDao.queryBuilder().orderBy("name", true).where().like("name",searchQuery).query();
					else if(ToolsString.isNullOrempty(id))
							ll = simpleDao.queryBuilder().orderBy("name", true).query();
						else ll = simpleDao.queryBuilder().orderBy("name", true).where().eq("genre_id", id).query();
					
		        	for(Species f : ll)
		        	{
	        			l.add(f);
	        			nbChildren.add(0);
		        	}
				} catch (SQLException e) {e.printStackTrace();}
	        }
	        
	        ArrayList<String> autocomplet = new ArrayList<String>();
	        //fill the hashmaps to send to the listview
	        for(int i = 0 ; i < l.size();i++)
	        {
		        map = new HashMap<String, String>();
		        map.put("id", l.get(i).getId()+"");
		        
		        if(type.compareTo("Species")==0)
		        {
		        	Species s = (Species)l.get(i);
		        	if(observationProcess)
		        		if(s.isIndanger())
		        			map.put("rightimg", String.valueOf(R.drawable.listnextwarning));
		        		else map.put("rightimg",String.valueOf(R.drawable.listnext));
		        	else
			        	if(s.isIndanger())
			        		map.put("rightimg", String.valueOf(R.drawable.listsubmitwarning));
			        	else map.put("rightimg", String.valueOf(R.drawable.listsubmit));
		        	map.put("titre",l.get(i).getName());
		        	map.put("img", String.valueOf(R.drawable.icone));
		        }
		        else {
		        	if(nbChildren.get(i)==0)
		        		map.put("rightimg", String.valueOf(R.drawable.listnonext));
		        	else map.put("rightimg", String.valueOf(R.drawable.listnext));
		        	map.put("titre",l.get(i).getName()+ " ("+nbChildren.get(i)+")");
		        	if(type.compareTo("Family")==0)
		        		if(nbChildren.get(i)==0)
		        			map.put("img", String.valueOf(R.drawable.listfamilygrey));
		        		else map.put("img", String.valueOf(R.drawable.listfamily));
		        	else if(nbChildren.get(i)==0)
		        			map.put("img", String.valueOf(R.drawable.listgenregrey));
		        	else map.put("img", String.valueOf(R.drawable.listgenre));
		        }
		        map.put("nbChildren", nbChildren.get(i)+"");
		        
		        listItem.add(map);
		        
		        //autocompletion
		    	autocomplet.add(l.get(i).getName());
	        }      
	        mSchedule = new SimpleAdapterHACustom(getApplicationContext(), listItem, R.layout.itemlistviewcustom, new String[] {"img", "titre", "rightimg"}, new int[] {R.id.img, R.id.titre, R.id.rightimg});
        
	        SortedSet<String> set = new TreeSet<String>() ;
	        set.addAll(autocomplet) ;
	        distinctList = new ArrayList<String>(set);
	        adapter = new ArrayAdapter<String>(HelpAuthentificationActivity.this,android.R.layout.simple_dropdown_item_1line, distinctList);
			
	        return null;
	    }
	 
	    @Override
	    protected void onPostExecute(Void result) {
	    	mProgressDialog.dismiss();
	        actv.setAdapter(adapter);
	        
	        actv.setOnItemClickListener(new OnItemClickListener() {
	        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	        	{
	        		View v = findViewById(R.id.listviewsearchbutton);
	        		onClick(v);
	        	}
			});
	        
			lv.setAdapter(mSchedule);
			lv.setFastScrollEnabled(true);
	        lv.setOnItemClickListener(HelpAuthentificationActivity.this);
	        lv.setOnItemLongClickListener(HelpAuthentificationActivity.this);
			
			//stuff you can't do in another thread
	        
	        if(observationProcess)
	        	((TextView)findViewById(R.id.lvtitleliste)).setText(R.string.selectspecies);
	        
	        TextView tv = (TextView)findViewById(R.id.listbreadcrumb);
	        if(ToolsString.isNullOrempty(id) || observationProcess)
        	{
        		tv.setHeight(0);
        	}
	        else {
	      		LinearLayout ll = (LinearLayout)findViewById(R.id.separatorlist);
        		ll.setVisibility(View.VISIBLE);
		        if(type.compareTo("Family")==0)
		        {
		            tv.setText(getString(R.string.dotCercle)+getString(R.string.family));
		        }
		        else if(type.compareTo("Genre")==0)
		        {
		        	RuntimeExceptionDao<Family, Integer> simpleDao = ToolsORMLite.getInstance(HelpAuthentificationActivity.this).getHelper().getRuntimeExceptionDao(Family.class);
		            Family listF = simpleDao.queryForId(Integer.parseInt(id));
		            tv.setText(getString(R.string.dotCercle)+getString(R.string.family)+" : "+listF.getName());
		        }
		        else {
		        	RuntimeExceptionDao<Genre, Integer> simpleDao = ToolsORMLite.getInstance(HelpAuthentificationActivity.this).getHelper().getRuntimeExceptionDao(Genre.class);
		            Genre listG = simpleDao.queryForId(Integer.parseInt(id));
		            RuntimeExceptionDao<Family, Integer> simpleDao2 = ToolsORMLite.getInstance(HelpAuthentificationActivity.this).getHelper().getRuntimeExceptionDao(Family.class);
		            Family f = simpleDao2.queryForId(listG.getFamily().getId());
		            tv.setText(getString(R.string.dotCercle)+getString(R.string.family)+" : "+f.getName()+"\n"+getString(R.string.dotCercle)+getString(R.string.genre)+" : "+listG.getName());
		        }
	        }
			
			//Direct render of the selected item on the right side
			Object o = findViewById(R.id.fragspeciesinformation);
			if(o!= null && !ToolsString.isNullOrempty(id) && type.compareTo("Family")!=0)
			{
				String aboveType = "";
				if(type.compareTo("Genre")==0)
					aboveType ="Family";
				else aboveType="Genre";
				
				showDetail(Integer.parseInt(id),aboveType);
			}
	    }
	}
}
