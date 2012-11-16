package fr.rocknscrum.liseronmobile;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
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

import com.j256.ormlite.dao.RuntimeExceptionDao;

import fr.rocknscrum.liseronmobile.database.ToolsORMLite;
import fr.rocknscrum.liseronmobile.observation.Form;
import fr.rocknscrum.liseronmobile.tools.ToolsString;

public class ObservationSelectForm extends Activity implements OnClickListener, OnFocusChangeListener, OnItemClickListener{
    private static final int MENU_SEARCH = 0;
	private static final int SUBMIT_OBSERVATION = 0;
	private static final int MENU_QUIT = 1;

    ListView lv;
	AutoCompleteTextView actv;
	Button actvButton;
	ArrayList<HashMap<String, String>> listItem;
	ArrayAdapter<String> adapter;
	ArrayList<String> distinctList;
	String campaignid;
	String speciesid;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.listviewselectform);

        new LoadPage().execute();
    }
    
    /*ROTATION OF THE SCREEN*/
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
	    super.onConfigurationChanged(newConfig);
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
			
			mProgressDialog = new ProgressDialog(ObservationSelectForm.this);
			mProgressDialog.setMessage(getApplicationContext().getResources().getString(R.string.synchronizeWait));
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
	    }
	 
	    @Override
	    protected Void doInBackground(Void... arg0) {
	    	
	    	lv = (ListView)findViewById(R.id.lsflv); 
	    	
	        actv = (AutoCompleteTextView)findViewById(R.id.lsfsearchtextview);
	        actvButton = (Button)findViewById(R.id.lsfsearchbutton);
	        actvButton.setOnClickListener(ObservationSelectForm.this);
	        actv.setOnFocusChangeListener(ObservationSelectForm.this);

	        actv.setOnItemClickListener(new OnItemClickListener() {
	        	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3)
	        	{
	        		View v = findViewById(R.id.lsfsearchbutton);
	        		onClick(v);
	        	}
			});
	        
	        String query = ObservationSelectForm.this.getIntent().getStringExtra("search");
	        
	    	campaignid =ObservationSelectForm.this.getIntent().getStringExtra("campaignid");
	    	speciesid = ObservationSelectForm.this.getIntent().getStringExtra("speciesid");
	        
	        listItem = new ArrayList<HashMap<String, String>>();
	        HashMap<String, String> map = new HashMap<String, String>();
	        List<Form> l = new ArrayList<Form>();
	        try {
	        	RuntimeExceptionDao<Form, Integer> daoform = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(Form.class);
				if(ToolsString.isNullOrempty(query))
					l = daoform.queryBuilder().orderBy("name", true).where().eq("campaign_id", campaignid).query();
				else
					l=daoform.queryBuilder().orderBy("name", true).where().eq("campaign_id", campaignid).and().like("name","%"+query.replace(" ", "%")+"%").query();
				ArrayList<String> autocomplet = new ArrayList<String>();

		        for(int i = 0 ; i < l.size();i++)
		        {
			        map = new HashMap<String, String>();
			        map.put("id", l.get(i).getId()+"");
			        map.put("name",l.get(i).getName());
			        map.put("description",l.get(i).getDescription());
			        listItem.add(map);
			    	autocomplet.add(l.get(i).getName());
		        }  
				
		        SortedSet<String> set = new TreeSet<String>() ;
		        set.addAll(autocomplet) ;
		        distinctList = new ArrayList<String>(set);
		        adapter = new ArrayAdapter<String>(ObservationSelectForm.this,android.R.layout.simple_dropdown_item_1line, distinctList);
				
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
	        SimpleAdapter mSchedule = new SimpleAdapter(getApplicationContext(), listItem, R.layout.itemlistviewselectform, new String[] {"name", "description"}, new int[] {R.id.lvftitle, R.id.lvfdesc});
	        lv.setAdapter(mSchedule);
	        lv.setFastScrollEnabled(true);
	        lv.setOnItemClickListener(ObservationSelectForm.this);
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
		if(v.getId()==R.id.lsfsearchbutton)
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
				objetbunble.putString("campaignid", campaignid);
				objetbunble.putString("speciesid", speciesid);
				Intent intent = new Intent(ObservationSelectForm.this, ObservationSelectForm.class);
				intent.putExtras(objetbunble);
				startActivity(intent);
        	}
        	else Toast.makeText(this,R.string.nosearchresult,Toast.LENGTH_SHORT).show();
		}
		
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String id = listItem.get(arg2).get("id");
		Bundle objetbunble = new Bundle();
		objetbunble.putString("campaignid", campaignid);
		objetbunble.putString("speciesid", speciesid);
		objetbunble.putString("formid", id);
		Intent intent = new Intent(ObservationSelectForm.this, ObservationSubmit.class);
		intent.putExtras(objetbunble);
		startActivityForResult(intent, SUBMIT_OBSERVATION);
	}
	
	@Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		if (resultCode == RESULT_OK) {  
			if(requestCode == SUBMIT_OBSERVATION)
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
