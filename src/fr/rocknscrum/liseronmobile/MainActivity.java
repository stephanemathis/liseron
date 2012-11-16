package fr.rocknscrum.liseronmobile;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources.NotFoundException;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.j256.ormlite.dao.GenericRawResults;
import com.j256.ormlite.dao.RuntimeExceptionDao;

import fr.rocknscrum.liseronmobile.classification.Family;
import fr.rocknscrum.liseronmobile.database.DatabaseHelper;
import fr.rocknscrum.liseronmobile.database.ToolsORMLite;
import fr.rocknscrum.liseronmobile.observation.Campaign;
import fr.rocknscrum.liseronmobile.observation.Observation;
import fr.rocknscrum.liseronmobile.tools.ConfigClass;
import fr.rocknscrum.liseronmobile.tools.ToolsConnection;
import fr.rocknscrum.liseronmobile.tools.ToolsFile;
import fr.rocknscrum.liseronmobile.tools.ToolsString;


public class MainActivity extends Activity implements OnClickListener, SensorEventListener, OnTouchListener {

	private static final int MENU_PREF = 2;
	private static final int MENU_GENERATE = 0;
	private static final int TOUCH_LEFT = 1;
	private static final int TOUCH_RIGHT = 2;
	private static final int TOUCH_TOP = 3;
	private static final int TOUCH_BOTTOM = 4;
	ToggleButton tbLogin;
	TextView tvLogin;
	View alertDialogView,alertDialogCreate;
	String inputName, inputPassword;
	long lastUpdate = -1;
	float x,y,z;
	float last_x = 0;
	float last_y = 0;
	float last_z = 0;
	private static final int SHAKE_THRESHOLD = 3000;
	private static final int MENU_HELP = 1;
	boolean firstEventCall = true;
	boolean actionEventDone = false;
	long lastActivityResult = -1;
	int max_x, max_y;
	ArrayList<Integer> konamiCodes;
	int currentCode;
	TextView tvKonami;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);

		ScrollView l = (ScrollView)findViewById(R.id.mainscrollview);
		if(l!=null)
			l.setOnTouchListener(this);
		
		
		tvLogin = (TextView)findViewById(R.id.login);
		Button helpAuthentification = (Button)findViewById(R.id.buttonHelpAuthentification);
		helpAuthentification.setOnClickListener(this);
		Button newObservation = (Button)findViewById(R.id.buttonNewObservation);
		Button toSync = (Button)findViewById(R.id.buttonSync);
		toSync.setOnClickListener(this);
		newObservation.setOnClickListener(this);
		tbLogin = (ToggleButton)findViewById(R.id.toggleButtonlogin);
		tbLogin.setOnClickListener(this);
		Button myObservation = (Button)findViewById(R.id.buttonMyObservation);
		myObservation.setOnClickListener(this);
		//Use to oblige ORMLite to create the database
		RuntimeExceptionDao<ConfigClass, Integer> simpleDao = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(ConfigClass.class);
		ConfigClass s = simpleDao.queryForId(0);
		if(s == null)
		{
			s = new ConfigClass();
			s.setId(0);
			simpleDao.create(s);
		}
		else {
			if(s.getIsLoggedIn())
			{
				tbLogin.setChecked(true);
				tvLogin.setText(getString(R.string.connectionsuccedmessage)+" "+s.getUserFirstName()+" "+s.getUserName()+" ("+s.getLogin()+")");
			}
		}
		
		Display display = getWindowManager().getDefaultDisplay();
		max_x = display.getWidth();
		max_y = display.getHeight();
		initListCode();
		tvKonami = (TextView)findViewById(R.id.konami);
		updateKonami(0);
	}

	/** Called when the click on a button is detected*/
	@Override
	public void onClick(View v) {
		if(v.getId()==R.id.buttonHelpAuthentification)
		{
			RuntimeExceptionDao<Family, Integer> simpleDao = ToolsORMLite.getInstance(this).getHelper().getRuntimeExceptionDao(Family.class);
			GenericRawResults<String[]> nb = simpleDao.queryRaw("select count(id) from Family");
			try {
				if(Integer.parseInt(nb.getResults().get(0)[0])==0)
				{
					Toast.makeText(this, R.string.nofamily, Toast.LENGTH_SHORT).show();
				}
				else {
					Bundle objetbunble = new Bundle();
					objetbunble.putString("id", "");
					objetbunble.putString("type", "Family");
					Intent intent = new Intent(MainActivity.this, HelpAuthentificationActivity.class);
					intent.putExtras(objetbunble);
					startActivityForResult(intent, 0);
				}
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (NotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		else if(v.getId()==R.id.buttonNewObservation)
		{
			RuntimeExceptionDao<Campaign, Integer> simpleDao = ToolsORMLite.getInstance(this).getHelper().getRuntimeExceptionDao(Campaign.class);
			GenericRawResults<String[]> nb = simpleDao.queryRaw("select count(id) from Campaign");
			try {
				if(Integer.parseInt(nb.getResults().get(0)[0])==0)
				{
					Toast.makeText(this, R.string.nocampaign, Toast.LENGTH_SHORT).show();
				}
				else {
					Intent intent = new Intent(MainActivity.this,ObservationSelectCampagn.class);
					startActivityForResult(intent, 0);
				}
			}
			catch(Exception e)
			{e.printStackTrace();}
		}
		else if(v.getId()==R.id.toggleButtonlogin)
		{
			if(tbLogin.isChecked())
			{
				AlertDialog.Builder adb = new AlertDialog.Builder(this);
				adb.setTitle(R.string.adassociate);
				adb.setCancelable(true);
				adb.setIcon(android.R.drawable.ic_dialog_info);
				tbLogin.setChecked(false);
				adb.setPositiveButton(R.string.adlogin, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						login();
					} });

				adb.setNegativeButton(R.string.adcreate, new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						createAccount();
					} });
				adb.show();
			}
			else {
				RuntimeExceptionDao<ConfigClass, Integer> simpleDao = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(ConfigClass.class);
				ConfigClass s = simpleDao.queryForId(0); 
				s.setIsLoggedIn(false);
				simpleDao.createOrUpdate(s);
				tvLogin.setText(R.string.homelogintext);
				Toast.makeText(this, R.string.logindeconnect,Toast.LENGTH_SHORT).show();
			}
		}
		else if (v.getId() == R.id.buttonSync)
		{
			Intent intent = new Intent(MainActivity.this, SyncActivity.class);
			startActivityForResult(intent, 0);
		}
		else if(v.getId()==R.id.buttonMyObservation)
		{
			RuntimeExceptionDao<Observation, Integer> daoobs = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(Observation.class);
			long nb = daoobs.countOf();
			if(nb == 0)
			{
				Toast.makeText(this, R.string.noobservation, Toast.LENGTH_SHORT).show();
			}
			else {
				Intent intent = new Intent(MainActivity.this, ObservationList.class);
				startActivityForResult(intent, 0);
			}
		}

	}

	/*MENU*/
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, MENU_PREF, 0, R.string.menupref).setIcon(android.R.drawable.ic_menu_preferences);
		//menu.add(0, MENU_GENERATE, 0, "Copier la base de donnees sur la carte SD");
		menu.add(0, MENU_HELP, 0, R.string.helppage).setIcon(android.R.drawable.ic_menu_info_details);
		return true;
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case MENU_GENERATE:
			generateDB();	    	
			return true;
		case MENU_PREF:
			Intent intent = new Intent(MainActivity.this, PrefActivity.class);
			startActivityForResult(intent, 0);
			return true;
		case MENU_HELP:
			Intent i = new Intent(MainActivity.this, HelpActivity.class);
			startActivityForResult(i, 0);
			return true;
		}
		return false;
	}

	/*ROTATION OF THE SCREEN*/
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Display display = getWindowManager().getDefaultDisplay();
		max_x = display.getWidth();
		max_y = display.getHeight();
	}

	@SuppressWarnings("unused")
	public void createAccount()
	{
		if(ToolsConnection.isOnline(this) && false==true)
		{
			LayoutInflater factory = LayoutInflater.from(this);
			alertDialogCreate = factory.inflate(R.layout.alertdialogaccountcreation, null);
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setView(alertDialogCreate);
			adb.setTitle(R.string.adcreationtitle);
			adb.setIcon(android.R.drawable.ic_dialog_info);

			adb.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					EditText etlogin =  (EditText)alertDialogCreate.findViewById(R.id.aclogin);
					EditText etmdp1 =  (EditText)alertDialogCreate.findViewById(R.id.acmdp1);
					EditText etmdp2 =  (EditText)alertDialogCreate.findViewById(R.id.acmdp2);
					EditText etemail =  (EditText)alertDialogCreate.findViewById(R.id.acemail);

					if(!ToolsString.isNullOrempty(etlogin.getText().toString()) && !ToolsString.isNullOrempty(etemail.getText().toString()) && !ToolsString.isNullOrempty(etmdp1.getText().toString())&& !ToolsString.isNullOrempty(etmdp2.getText().toString()) && etmdp1.getText().toString().compareTo(etmdp2.getText().toString())==0)
					{
						if(ToolsString.checkEmail(etemail.getText().toString()))
						{
							new CreateAccount().execute();
						}
						else {
							tbLogin.setChecked(false);
							Toast.makeText(MainActivity.this, getString(R.string.wrongmail)+" "+etemail.getText().toString(), Toast.LENGTH_SHORT).show();
						}
					}
					else {
						tbLogin.setChecked(false);
						Toast.makeText(MainActivity.this, R.string.invalidvalues, Toast.LENGTH_SHORT).show();
					}
				} });

			adb.setNegativeButton(R.string.undo, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					tbLogin.setChecked(false);
				} });
			adb.show();
		}
		else {
			tbLogin.setChecked(false);
			Toast.makeText(this, R.string.noconnectionavailable, Toast.LENGTH_SHORT).show();
		}
	}

	@SuppressWarnings("unused")
	public void login()
	{
		if(ToolsConnection.isOnline(this)&& false==true)
		{
			LayoutInflater factory = LayoutInflater.from(this);
			alertDialogView = factory.inflate(R.layout.inputdialogbox, null);
			AlertDialog.Builder adb = new AlertDialog.Builder(this);
			adb.setView(alertDialogView);

			adb.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					EditText etName = (EditText)alertDialogView.findViewById(R.id.edittextloginname);
					EditText etPassword = (EditText)alertDialogView.findViewById(R.id.edittextloginpassword);
					inputName = etName.getText().toString();
					inputPassword = etPassword.getText().toString();
					new WaitAuthentification().execute();

				} });

			adb.setNegativeButton(R.string.undo, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					tbLogin.setChecked(false);
					RuntimeExceptionDao<ConfigClass, Integer> simpleDao = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(ConfigClass.class);
					ConfigClass s = simpleDao.queryForId(0); 
					s.setIsLoggedIn(false);
					tvLogin.setText(R.string.homelogintext);
					simpleDao.createOrUpdate(s);
				} });
			adb.show();		
		}
		else {
			tbLogin.setChecked(false);
			Toast.makeText(this, R.string.noconnectionavailable, Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * Generate the database
	 * @author Rock'N'Scrum
	 */
	public void generateDB()
	{	

		try {
			DatabaseHelper.backupDatabase();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Toast.makeText(this, "Base sur la SD", Toast.LENGTH_LONG).show();
	}	

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		Window window = getWindow();
		window.setFormat(PixelFormat.RGBA_8888);
	}

	private class WaitAuthentification extends AsyncTask<Void, String, Void>
	{
		ProgressDialog mProgressDialog;
		boolean succes = true;
		String name,description,login;
		int toastres;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			mProgressDialog = new ProgressDialog(MainActivity.this);
			mProgressDialog.setMessage(getApplicationContext().getResources().getString(R.string.synchronizeWait));
			mProgressDialog.setCancelable(false);
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			Properties p = new Properties();
			try {
				p.loadFromXML(getApplicationContext().getResources().openRawResource(R.raw.properties));
			} catch (Exception e) {
				e.printStackTrace();
			}

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(p.getProperty("urlServiceAuthentification"));

			try {
				// Add your data
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("login", inputName));
				nameValuePairs.add(new BasicNameValuePair("password", inputPassword));
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				Document doc = ToolsFile.getDocumentFromHttpResponse(response);
				if(doc != null)
				{
					NodeList xmlRes = doc.getElementsByTagName("user");
					if(xmlRes.getLength()==1)
					{
						Element res = (Element)xmlRes.item(0);
						RuntimeExceptionDao<ConfigClass, Integer> simpleDao = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(ConfigClass.class);
						ConfigClass s = simpleDao.queryForId(0); 
						if(res.getAttribute("result").compareTo("true")==0)
						{
							succes = true;
							name = res.getAttribute("lastName");
							description =res.getAttribute("firstName");
							login = res.getAttribute("login");
							s.setIsLoggedIn(true);
							s.setUserFirstName(res.getAttribute("firstName"));
							s.setUserName(res.getAttribute("lastName"));
							s.setLogin(res.getAttribute("login"));
							s.setIdUser(Integer.parseInt(res.getAttribute("id")));
							simpleDao.createOrUpdate(s);
						}
						else {
							succes = false;
							toastres = R.string.connectiondenied;
							s.setIsLoggedIn(false);
							simpleDao.createOrUpdate(s);
						}
					}
					else {			                    	
						succes = false;
						toastres = R.string.connectiondenied;
					}
				}
				else {
					succes = false;
					toastres = R.string.noserverconnection;
				}
			} catch (Exception e) {
				Log.e("Connexion", "Erreur :"+e.getMessage());
			}
			return null;
		}


		@Override
		protected void onPostExecute(Void result) {
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}

			if(succes)
			{
				tvLogin.setText(getString(R.string.connectionsuccedmessage)+" "+name+" "+description+" ("+login+")");
				Toast.makeText(getApplicationContext(), R.string.connectionsuccedtoast, Toast.LENGTH_LONG).show();
				tbLogin.setChecked(true);
			}
			else {
				Toast.makeText(getApplicationContext(), toastres, Toast.LENGTH_SHORT).show();
				tbLogin.setChecked(false);
				tvLogin.setText(R.string.homelogintext);
			}
		}

	}

	private class CreateAccount extends AsyncTask<Void, String, Void>
	{
		ProgressDialog mProgressDialog;
		String login, mdp1, mdp2, email;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			mProgressDialog = new ProgressDialog(MainActivity.this);
			mProgressDialog.setMessage(getApplicationContext().getResources().getString(R.string.synchronizeWait));
			mProgressDialog.setCancelable(false);
			mProgressDialog.setIndeterminate(true);
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			login = ((EditText)alertDialogCreate.findViewById(R.id.aclogin)).getText().toString();
			mdp1 = ((EditText)alertDialogCreate.findViewById(R.id.acmdp1)).getText().toString();
			mdp2 = ((EditText)alertDialogCreate.findViewById(R.id.acmdp2)).getText().toString();
			email = ((EditText)alertDialogCreate.findViewById(R.id.acemail)).getText().toString();
			inputName = login;
			inputPassword = mdp1;
			Properties p = new Properties();
			try {
				p.loadFromXML(getApplicationContext().getResources().openRawResource(R.raw.properties));
			} catch (Exception e) {
				e.printStackTrace();
			}

			HttpClient httpclient = new DefaultHttpClient();
			HttpPost httppost = new HttpPost(p.getProperty("urlServiceAccountCreation"));

			try {
				List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
				nameValuePairs.add(new BasicNameValuePair("login", login));
				nameValuePairs.add(new BasicNameValuePair("password", mdp1));
				nameValuePairs.add(new BasicNameValuePair("password2", mdp2));
				nameValuePairs.add(new BasicNameValuePair("email", email));

				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
				httpclient.execute(httppost);

			} catch (Exception e) {
				Log.e("Connexion", "Erreur :"+e.getMessage());
			}
			return null;
		}


		@Override
		protected void onPostExecute(Void result) {
			if (mProgressDialog.isShowing()) {
				mProgressDialog.dismiss();
			}
			//tbLogin.setChecked(false);
			new WaitAuthentification().execute();
		}

	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {

	}

	@Override public void onSensorChanged(SensorEvent event) { 
		if (event.sensor.getType()==Sensor.TYPE_ACCELEROMETER)
		{ 
			long curTime = System.currentTimeMillis(); 
			if ((curTime - lastUpdate) > 100) 
			{ 
				long diffTime = (curTime - lastUpdate); 
				lastUpdate = curTime; 
				x = event.values[0]; 
				y = event.values[1]; 
				z = event.values[2]; 

				if(firstEventCall)
				{
					last_x = event.values[0]; 
					last_y = event.values[1]; 
					last_z = event.values[2];
					firstEventCall = false;
				}

				float speed = Math.abs(x+y+z - last_x - last_y - last_z) / diffTime * 10000; 
				if (speed > SHAKE_THRESHOLD) 
				{ 
					if(!actionEventDone)
					{
						if(ToolsConnection.isOnline(this))
						{
							new AlertDialog.Builder(MainActivity.this).setTitle(R.string.congrats).setMessage(R.string.nostress).setNeutralButton("Lancer",new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog,int which) {startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=C9haTFoGcvk")));}}).show(); 
						}
						actionEventDone = true;
					}
				} 
				last_x = x; 
				last_y = y; 
				last_z = z; } 
		} 
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) 
		{
			long curTime = System.currentTimeMillis(); 
			if(lastActivityResult!=-1 &&(curTime-lastActivityResult)<1500)
			{
					Toast.makeText(this,R.string.doubletapquit, Toast.LENGTH_SHORT).show();
					return true;
				}
			else return super.onKeyDown(keyCode, event);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		lastActivityResult = System.currentTimeMillis();
	}

	@Override
    public boolean onTouchEvent(MotionEvent event)
    {
        if (event != null)
        {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
            {            	
            	int zone = -1;
            	if(event.getRawX()<(max_x/2+max_x/4) && event.getRawX()>(max_x/2-max_x/4) && event.getRawY()<max_y/2)
            		zone = TOUCH_TOP;
            	
            	if(event.getRawX()<(max_x/2+max_x/4) && event.getRawX()>(max_x/2-max_x/4) && event.getRawY()>max_y/2)
            		zone = TOUCH_BOTTOM;
            	
            	if(event.getRawY()<(max_y/2+max_y/4) && event.getRawY()>(max_y/2-max_y/4) && event.getRawX()<max_x/2)
            		zone = TOUCH_LEFT;
            	
            	if(event.getRawY()<(max_y/2+max_y/4) && event.getRawY()>(max_y/2-max_y/4) && event.getRawX()>max_x/2)
            		zone = TOUCH_RIGHT;
            	
            	konamiCode(zone);
            }
        }
        return super.onTouchEvent(event);
    }

	public void konamiCode(int touch)
	{
		if(konamiCodes.get(currentCode)==touch)
			updateKonami(currentCode +1);
		else updateKonami(0);
		if(currentCode == konamiCodes.size())
			congratsKonami();
	}
	
	public void initListCode()
	{
		konamiCodes = new ArrayList<Integer>();
		konamiCodes.add(TOUCH_TOP);
		konamiCodes.add(TOUCH_TOP);
		konamiCodes.add(TOUCH_BOTTOM);
		konamiCodes.add(TOUCH_BOTTOM);
		konamiCodes.add(TOUCH_LEFT);
		konamiCodes.add(TOUCH_RIGHT);
		konamiCodes.add(TOUCH_LEFT);
		konamiCodes.add(TOUCH_RIGHT);
	}
	
	public void updateKonami(int index)
	{
		currentCode = index;
		String k = "";
		for(int i = 0 ; i < currentCode ; i++)
			k+="•";
		tvKonami.setText(k);
	}
	
	public void congratsKonami()
	{
		updateKonami(0);
		new AlertDialog.Builder(MainActivity.this).setTitle(R.string.congrats).setMessage(R.string.nostress).setNeutralButton("Lancer",new DialogInterface.OnClickListener() {public void onClick(DialogInterface dialog,int which) {startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=mUnrWo6z9WY")));}}).show(); 
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

        if (event != null)
        {
            if (event.getAction() == MotionEvent.ACTION_DOWN)
            {            	
            	int zone = -1;
            	if(event.getRawX()<(max_x/2+max_x/4) && event.getRawX()>(max_x/2-max_x/4) && event.getRawY()<max_y/2)
            		zone = TOUCH_TOP;
            	
            	if(event.getRawX()<(max_x/2+max_x/4) && event.getRawX()>(max_x/2-max_x/4) && event.getRawY()>max_y/2)
            		zone = TOUCH_BOTTOM;
            	
            	if(event.getRawY()<(max_y/2+max_y/4) && event.getRawY()>(max_y/2-max_y/4) && event.getRawX()<max_x/2)
            		zone = TOUCH_LEFT;
            	
            	if(event.getRawY()<(max_y/2+max_y/4) && event.getRawY()>(max_y/2-max_y/4) && event.getRawX()>max_x/2)
            		zone = TOUCH_RIGHT;
            	
            	konamiCode(zone);
            }
        }
		
		return false;
	}
}


















