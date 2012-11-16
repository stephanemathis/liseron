package fr.rocknscrum.liseronmobile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images.Media;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TextView.BufferType;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import fr.rocknscrum.liseronmobile.classification.Species;
import fr.rocknscrum.liseronmobile.database.ToolsORMLite;
import fr.rocknscrum.liseronmobile.observation.Campaign;
import fr.rocknscrum.liseronmobile.observation.Field;
import fr.rocknscrum.liseronmobile.observation.Form;
import fr.rocknscrum.liseronmobile.observation.Observation;
import fr.rocknscrum.liseronmobile.observation.Value;
import fr.rocknscrum.liseronmobile.tools.ToolsConnection;
import fr.rocknscrum.liseronmobile.tools.ToolsString;

public class ObservationSubmit extends FragmentActivity implements OnClickListener, LocationListener{

	private static final int TAKE_PHOTO_CODE = 1;
	private static final int MENU_QUIT = 0;
	private static final int ACTIVITY_SELECT_IMAGE = 2;  

	int campaignid,speciesid,formid;
	TextView tvspecies, tvform, tvcampaign, tvlocation, tvlocationtitle, tvphototitle;
	RuntimeExceptionDao<Form, Integer> daoform;
	RuntimeExceptionDao<Campaign, Integer> daocampaign;
	RuntimeExceptionDao<Species, Integer> daospecies;
	RuntimeExceptionDao<Field, Integer> daofield;
	RuntimeExceptionDao<Observation, Integer> daoobservation;
	RuntimeExceptionDao<Value, Integer> daovalue;

	String imageName;
	String[] values;
	Campaign campaign;
	Species species;
	Form form;
	List<Field> fields;
	Button submit, buttonphoto, buttonmap;
	EditText etcomment;
	ImageView imgTaken;
	boolean imageValid;
	LocationManager lManager;
	Location location;
	Observation o;
	int observationid;
	boolean isUpdate, isGeneric;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.observationsubmit);

		new LoadPage().execute();
	}


	private class LoadPage extends AsyncTask <Void, String, Void>
	{
		ProgressDialog mProgressDialog;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mProgressDialog = new ProgressDialog(ObservationSubmit.this);
			mProgressDialog.setMessage(getApplicationContext().getResources().getString(R.string.synchronizeWait));
			mProgressDialog.setCancelable(false);
			mProgressDialog.show();
		}

		@Override
		protected Void doInBackground(Void... arg0) {

			try{
				daocampaign = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(Campaign.class);
				daospecies = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(Species.class);
				daoform = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(Form.class);
				daofield = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(Field.class);
				daovalue = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(Value.class);
				daoobservation = ToolsORMLite.getInstance(getApplicationContext()).getHelper().getRuntimeExceptionDao(Observation.class);


				isUpdate = !ToolsString.isNullOrempty(ObservationSubmit.this.getIntent().getStringExtra("observationid"));
				if(isUpdate)
				{
					observationid = Integer.parseInt(ObservationSubmit.this.getIntent().getStringExtra("observationid"));
					o = daoobservation.queryForId(observationid);
					speciesid = o.getSpecies().getId();
					
					if(o.getForm()!=null)
					{
						Form f = daoform.queryForId(o.getForm().getId());
						campaignid = f.getCampaign().getId();
						formid = o.getForm().getId();
						isGeneric = false;
					}
					else {
						campaignid = -1;
						formid = -1;
						isGeneric = true;
					}
				}
				else 
				{
					observationid = -1;
					speciesid = Integer.parseInt(ObservationSubmit.this.getIntent().getStringExtra("speciesid"));
					
					if(!ToolsString.isNullOrempty(ObservationSubmit.this.getIntent().getStringExtra("campaignid")))
						campaignid = Integer.parseInt(ObservationSubmit.this.getIntent().getStringExtra("campaignid"));
					else campaignid = -1;

					if(!ToolsString.isNullOrempty(ObservationSubmit.this.getIntent().getStringExtra("formid")))
						formid = Integer.parseInt(ObservationSubmit.this.getIntent().getStringExtra("formid"));
					else formid = -1;
					
					o = new Observation();
					if(formid == -1 && campaignid == -1)
						isGeneric = true;
					else isGeneric = false;
				}

				tvcampaign = (TextView)findViewById(R.id.ostvcampaign);
				tvform = (TextView)findViewById(R.id.ostvform);
				tvspecies = (TextView)findViewById(R.id.ostvspecies);
				tvlocation = (TextView)findViewById(R.id.oslocationtext);
				tvphototitle = (TextView)findViewById(R.id.osphototitle);
				tvlocationtitle = (TextView)findViewById(R.id.oslocationtitle);

				campaign = daocampaign.queryForId(campaignid);
				form = daoform.queryForId(formid);
				species = daospecies.queryForId(speciesid);
				fields = daofield.queryBuilder().where().eq("form_id", formid).query();

				submit = (Button)findViewById(R.id.ossubmit);
				submit.setOnClickListener(ObservationSubmit.this);

				buttonmap = (Button)findViewById(R.id.osmap);
				buttonmap.setOnClickListener(ObservationSubmit.this);
				buttonphoto = (Button)findViewById(R.id.osphoto);
				buttonphoto.setOnClickListener(ObservationSubmit.this);
				imgTaken = (ImageView)findViewById(R.id.osphotoviewer);

				imageValid = false;
				imageName = String.valueOf(UUID.randomUUID())+".jpg";
				
				lManager = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
			}
			catch(Exception e)
			{
				e.printStackTrace();
				Log.e("ObservationSubmit loading", e.getCause()+ "-"+ e.getMessage());
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			
			if(!isGeneric)
			{
				//A span is used to use particular style inside a textview or other views. It has better performance than Html.fromHtml() who parse the string with a huge dtd
				SpannableString campspan = new SpannableString(getString(R.string.oscampaign)+" "+campaign.getName());
				campspan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), getString(R.string.oscampaign).length(), campspan.length(), 0);
				tvcampaign.setText(campspan,BufferType.SPANNABLE);
	
				SpannableString formspan = new SpannableString(getString(R.string.osform)+" "+form.getName());
				formspan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), getString(R.string.osform).length(), formspan.length(), 0);
				tvform.setText(formspan,BufferType.SPANNABLE);
			}
			
			SpannableString speciesspan = new SpannableString(getString(R.string.osspecies)+" "+species.getName());
			speciesspan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), getString(R.string.osspecies).length(), speciesspan.length(), 0);
			tvspecies.setText(speciesspan,BufferType.SPANNABLE);

			LinearLayout layout = (LinearLayout)findViewById(R.id.oscounterlayout);
			
			if(!isGeneric)
			{
				values = new String[fields.size()];
				
				for(int i = 0 ; i < fields.size() ; i++)
				{
					LinearLayout ltv =(LinearLayout)getLayoutInflater().inflate(R.layout.formtextview, null); 
					TextView tv = (TextView)ltv.getChildAt(0);
					String required;
					if(fields.get(i).getRequired())
						required = "("+getString(R.string.fieldrequired)+") ";
					else required="";
	
					String tvtext = fields.get(i).getName()+" "+required+":";
					SpannableString fieldnamespan = new SpannableString(tvtext);
					fieldnamespan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, tvtext.length(), 0);
	
					tv.setText(fieldnamespan);
					layout.addView(ltv);
					final int index = i;
					if(fields.get(i).getType().compareTo("text")==0)
					{
						LinearLayout let = (LinearLayout)getLayoutInflater().inflate(R.layout.formedittext, null);
						final EditText et = (EditText)let.getChildAt(0);
						et.setHint(R.string.hinttext);
						et.addTextChangedListener(new TextWatcher() {
							@Override
							public void afterTextChanged(Editable s) {
								values[index] = et.getText().toString();
							}
	
							@Override
							public void beforeTextChanged(CharSequence s, int start, int count, int after) {
							}
	
							@Override
							public void onTextChanged(CharSequence s, int start, int before, int count) {
							} 
						});
						if(isUpdate)
						{
							String defaultValue = "";
							List<Value> v;
							try {
								v = daovalue.queryBuilder().where().eq("observation_id", observationid).and().eq("field_id", fields.get(i).getId()).query();
								if(v.size()==1)
								{
									defaultValue = v.get(0).getValue();
									et.setText(defaultValue);
								}
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						layout.addView(let);
					}
					else if(fields.get(i).getType().compareTo("numeric")==0)
					{
						LinearLayout let = (LinearLayout)getLayoutInflater().inflate(R.layout.formedittext, null);
						final EditText et = (EditText)let.getChildAt(0);
						et.setHint(R.string.hintnumeric);
						et.setInputType(InputType.TYPE_CLASS_PHONE);
						et.setKeyListener(DigitsKeyListener.getInstance("0123456789.,-"));
						et.addTextChangedListener(new TextWatcher() {
							@Override
							public void afterTextChanged(Editable s) {
								if(ToolsString.isFloat(et.getText().toString().replace(',','.')))
									values[index] = et.getText().toString().replace(',','.');
								else values[index] ="";
							}
	
							@Override
							public void beforeTextChanged(CharSequence s, int start, int count, int after) {
							}
	
							@Override
							public void onTextChanged(CharSequence s, int start, int before, int count) {
							} 
						});
						if(isUpdate)
						{
							String defaultValue = "";
							List<Value> v;
							try {
								v = daovalue.queryBuilder().where().eq("observation_id", observationid).and().eq("field_id", fields.get(i).getId()).query();
								if(v.size()==1)
								{
									defaultValue = v.get(0).getValue();
									et.setText(defaultValue);
								}
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						layout.addView(let);
					}
					else if(fields.get(i).getType().compareTo("list")==0)
					{
						LinearLayout lsp = (LinearLayout)getLayoutInflater().inflate(R.layout.formspinner, null);
						final Spinner s = (Spinner)lsp.getChildAt(0);
						final String[] choices = getString(R.string.hintlist).concat(";").concat(fields.get(i).getChoices()).split(";");
						ArrayAdapter<String> a =new ArrayAdapter<String>(ObservationSubmit.this,android.R.layout.simple_spinner_item, choices);
						a.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						s.setAdapter(a);
						s.setOnItemSelectedListener(new OnItemSelectedListener() {
							@Override
							public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
								if(position!=0)
									values[index] = choices[position];
								else values[index] = "";
							}
	
							@Override
							public void onNothingSelected(AdapterView<?> parentView) {
								values[index] = "";
							}
						});    
						if(isUpdate)
						{
							String defaultValue = "";
							List<Value> v;
							try {
								v = daovalue.queryBuilder().where().eq("observation_id", observationid).and().eq("field_id", fields.get(i).getId()).query();
								if(v.size()==1)
								{
									defaultValue = v.get(0).getValue();
									for(int cc = 0 ; cc < choices.length;cc++)
									{
										if(choices[cc].compareTo(defaultValue)==0)
											s.setSelection(cc);
									}
									
								}
							} catch (SQLException e) {
								e.printStackTrace();
							}
						}
						layout.addView(lsp);
					}
				}
			}

			SpannableString commentspan = new SpannableString(getString(R.string.observationcommenttext));
			commentspan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, getString(R.string.observationcommenttext).length(), 0);

			LinearLayout ltv =(LinearLayout)getLayoutInflater().inflate(R.layout.formtextview, null); 
			TextView tvcomment = (TextView)ltv.getChildAt(0);
			tvcomment.setText(commentspan);
			layout.addView(ltv);

			LinearLayout let = (LinearLayout)getLayoutInflater().inflate(R.layout.formedittext, null);
			etcomment = (EditText)let.getChildAt(0);
			etcomment.setHint(R.string.hinttext);
			etcomment.setSingleLine(false);
			etcomment.setMinimumHeight(100);

			if(isUpdate)
			{
				if(!ToolsString.isNullOrempty(o.getComment()))
					etcomment.setText(o.getComment());
				location = new Location("network");
				location.setLatitude(Double.parseDouble(o.getLattitude()));
				location.setLongitude(Double.parseDouble(o.getLongitude()));
				displayLocationOnScreen();
				
				try{
					File file = new File(o.getImagePath());
					Bitmap captureBmp = Media.getBitmap(getContentResolver(), Uri.fromFile(file) ); 
					imgTaken.setImageBitmap(captureBmp);
					imgTaken.setVisibility(View.VISIBLE);
					tvphototitle.setVisibility(View.VISIBLE);
					imgTaken.setMaxHeight(captureBmp.getHeight());
					imageValid = true;
				}
				catch(Exception e)
				{Log.e("Loading photo", e.getMessage()); e.printStackTrace();}
			}
			layout.addView(let);
			
			if(isUpdate)
			{
				if(o.getValid())
				{
					submit.setEnabled(false);
					Toast.makeText(ObservationSubmit.this, R.string.modificationnotallowed, Toast.LENGTH_LONG).show();
				}
				submit.setText(R.string.modify);
			}
			mProgressDialog.dismiss();
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
	public void onClick(View arg0) {
		if(arg0.getId()==R.id.ossubmit)
		{
			boolean valid = true;
			String invalidfields = "";
			if(!isGeneric)
			{
				for(int i = 0 ; i < values.length ; i++)
				{
					if(fields.get(i).getRequired())
						if(ToolsString.isNullOrempty(values[i]))
						{
							valid = false;
							invalidfields +=", "+fields.get(i).getName();
						}
				}
			}
			else valid = true;
			if(!valid)
			{
				invalidfields = invalidfields.substring(2);
				AlertDialog alertDialog = new AlertDialog.Builder(ObservationSubmit.this).create();
				alertDialog.setTitle(R.string.observationformnotvalid);
				alertDialog.setMessage(invalidfields);
				alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {

					} }); 
				alertDialog.setIcon(android.R.drawable.ic_dialog_info);
				alertDialog.show();
			}
			else {
				if(imageValid)
				{
					if(location!=null)
					{
						o.setSpecies(species);
						if(!isGeneric)
							o.setForm(form);
						o.setLattitude(String.valueOf(location.getLatitude()));
						o.setLongitude(String.valueOf(location.getLongitude()));
						o.setValid(false);
						o.setComment(etcomment.getText().toString().replace('"', ' '));
						o.setDate(ToolsString.getCurrentDateString());
						File fv = new File(getTempFile(this).getAbsolutePath());
						if(fv.exists())
							o.setImagePath(getTempFile(this).getAbsolutePath());
						daoobservation.createOrUpdate(o);
						try{
							if(!isGeneric)
							{
								for(int i = 0 ; i < fields.size() ; i++)
								{
									List<Value> vlist = daovalue.queryBuilder().where().eq("observation_id",o.getId()).and().eq("field_id", fields.get(i).getId()).query();
									Value v;
									if(vlist.size()==1)
										v = vlist.get(0);
									else v = new Value();
									v.setField(fields.get(i));
									v.setObservation(o);
									if(values[i]==null) values[i] = "";
									v.setValue(values[i]);
									daovalue.createOrUpdate(v);
								}
							}
						}
						catch(Exception e)
						{Log.e("Save obs", e.getMessage());e.printStackTrace();}

						Toast.makeText(this, R.string.observationsaved, Toast.LENGTH_LONG).show();
						submit.setText(R.string.modify);
						
						if (getParent() == null) {
						    setResult(Activity.RESULT_OK, null);
						}
						else {
						    getParent().setResult(Activity.RESULT_OK, null);
						}
					}
					else {
						Toast.makeText(this, R.string.mandatorylocation, Toast.LENGTH_SHORT).show();
					}
				}
				else {
					Toast.makeText(this, R.string.mandatoryimage, Toast.LENGTH_SHORT).show();
				}
			}
		} 
		else if(arg0.getId()==R.id.osmap)
		{
			AlertDialog alertDialog = new AlertDialog.Builder(ObservationSubmit.this).create();
			alertDialog.setTitle(R.string.locationsourcechoice);
			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,getString(R.string.locationsourcegps), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					getAutoPosition();
				} }); 
			alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,getString(R.string.locationsourcemano), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					getManualPosition();
				} }); 

			alertDialog.setIcon(android.R.drawable.ic_dialog_info);
			alertDialog.show();	
		}
		else if(arg0.getId()==R.id.osphoto)
		{
			AlertDialog alertDialog = new AlertDialog.Builder(ObservationSubmit.this).create();
			alertDialog.setTitle(R.string.photosource);
			alertDialog.setButton(DialogInterface.BUTTON_POSITIVE,getString(R.string.takephoto), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					takePhoto();
				} }); 
			alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,getString(R.string.choosephoto), new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					choosePhoto();
				} }); 

			alertDialog.setIcon(android.R.drawable.ic_dialog_info);
			alertDialog.show();	
		}

	}

	private void takePhoto(){
		final Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);  
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(getTempFile(this)) );   
		startActivityForResult(intent, TAKE_PHOTO_CODE);  
	}  
	
	private void choosePhoto()
	{
		Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
		photoPickerIntent.setType("image/*");
		startActivityForResult(photoPickerIntent, ACTIVITY_SELECT_IMAGE);   
	}

	public File getTempFile(Context context){  
		//create the directory
		final File path = new File( Environment.getExternalStorageDirectory(), context.getPackageName() );  
		if(!path.exists()){  
			path.mkdir();  
		}  
		return new File(path, imageName);  
	}  

	@Override  
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {  
		if (resultCode == RESULT_OK) {  
			switch(requestCode){  
			case TAKE_PHOTO_CODE:  
				final File file = getTempFile(this);  
				try {  
					Bitmap captureBmp = Media.getBitmap(getContentResolver(), Uri.fromFile(file)); 
					Bitmap resized = Bitmap.createScaledBitmap(captureBmp, 150, 120, false);
					try {
					       FileOutputStream out = new FileOutputStream(getTempFile(this).getAbsolutePath().replace(".jpg", "-tiny.jpg"));
					       resized.compress(Bitmap.CompressFormat.JPEG, 90, out);
					} catch (Exception e) {
					       e.printStackTrace();
					}
					imgTaken.setImageBitmap(captureBmp);
					imgTaken.setVisibility(View.VISIBLE);
					tvphototitle.setVisibility(View.VISIBLE);
					imgTaken.setMaxHeight(captureBmp.getHeight());
					imageValid = true;
				} catch (FileNotFoundException e) {  
					e.printStackTrace();  
				} catch (IOException e) {  
					e.printStackTrace();  
				}  
				break;
			case ACTIVITY_SELECT_IMAGE: 
				Uri selectedImage = data.getData();
	            InputStream imageStream = null;
				try {
					imageStream = getContentResolver().openInputStream(selectedImage);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
					Log.e("take photo",e.getMessage());
				}
				if(imageStream!=null)
				{
					Bitmap yourSelectedImage = BitmapFactory.decodeStream(imageStream);
			        FileOutputStream out1;
					try {
						out1 = new FileOutputStream(getTempFile(this).getAbsolutePath());
						yourSelectedImage.compress(Bitmap.CompressFormat.JPEG, 100, out1);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
					Bitmap resized = Bitmap.createScaledBitmap(yourSelectedImage, 150, 120, false);
					try {
					       FileOutputStream out = new FileOutputStream(getTempFile(this).getAbsolutePath().substring(0,getTempFile(this).getAbsolutePath().lastIndexOf('.'))+"-tiny.jpg");
					       resized.compress(Bitmap.CompressFormat.JPEG, 90, out);
					} catch (Exception e) {
					       e.printStackTrace();
					}
					imgTaken.setImageBitmap(yourSelectedImage);
					imgTaken.setVisibility(View.VISIBLE);
					tvphototitle.setVisibility(View.VISIBLE);
					imgTaken.setMaxHeight(yourSelectedImage.getHeight());
					imageValid = true;
				}
				break;
			}  
		}  
	}

	public void getAutoPosition()
	{
		List <String> providers = lManager.getProviders(true);
		int source = 0;
		if(providers.contains("network"))
		{
			source = providers.indexOf("network");
			Location l = lManager.getLastKnownLocation("network");
			if(l!=null)
				onLocationChanged(l);
			lManager.requestLocationUpdates(providers.get(source), 60000, 0, this);
		}
		if(source==0 && providers.contains("gps"))
		{
			source = providers.indexOf("gps");
			Location l = lManager.getLastKnownLocation("gps");
			if(l!=null)
				onLocationChanged(l);
			lManager.requestLocationUpdates(providers.get(source), 60000, 0, this);
		}

		lManager.requestLocationUpdates(providers.get(0), 60000, 0, this);
		tvlocation.setVisibility(View.VISIBLE);
		tvlocationtitle.setVisibility(View.VISIBLE);
		tvlocation.setText(R.string.locationloading);		
	}

	public void getManualPosition()
	{
		LayoutInflater factory = LayoutInflater.from(this);
		final View alertDialogView = factory.inflate(R.layout.alertdialogrequestlocation, null);
		AlertDialog.Builder adb = new AlertDialog.Builder(this);
		adb.setView(alertDialogView);
		adb.setCancelable(false);
		adb.setTitle(R.string.alertdialoglocationtitle);
		adb.setIcon(android.R.drawable.ic_dialog_info);
		final EditText etlat = (EditText)alertDialogView.findViewById(R.id.adllat);
		final EditText etlong = (EditText)alertDialogView.findViewById(R.id.adllong);
		final EditText etadress = (EditText)alertDialogView.findViewById(R.id.adladress);
		
		etlat.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if(etlat.getText().toString().length()>1)
					if(Float.parseFloat(etlat.getText().toString())>=-90.0f && Float.parseFloat(etlat.getText().toString()) <= +90.0f)
						;
					else {
						Toast.makeText(ObservationSubmit.this, R.string.wronglat, Toast.LENGTH_SHORT).show();
						etlat.setText(null);
					}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			} 
		});
		
		etlong.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
				if(etlong.getText().toString().length()>1)
					if(Float.parseFloat(etlong.getText().toString())>=-180.0f && Float.parseFloat(etlong.getText().toString()) <= +180.0f)
						;
					else {
						Toast.makeText(ObservationSubmit.this, R.string.wronglong, Toast.LENGTH_SHORT).show();
						etlong.setText(null);
					}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			} 
		});
		
		adb.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {


				if(!ToolsString.isNullOrempty(etlat.getText().toString()) && !ToolsString.isNullOrempty(etlong.getText().toString()))
				{
					Location l = new Location("network");
					l.setLatitude(Double.parseDouble(etlat.getText().toString()));
					l.setLongitude(Double.parseDouble(etlong.getText().toString()));
					location = l;
					displayLocationOnScreen();
				}
				else if(!ToolsString.isNullOrempty(etadress.getText().toString()))
				{
					if(ToolsConnection.isOnline(ObservationSubmit.this))
					{
						Geocoder geo = new Geocoder(ObservationSubmit.this);
						try {
							List <Address> adresses = geo.getFromLocationName(etadress.getText().toString(), 1);
							if(adresses != null && adresses.size() > 0){
								Address adresse = adresses.get(0);
								Location l = new Location("network");
								l.setLatitude(adresse.getLatitude());
								l.setLongitude(adresse.getLongitude());
								location = l;
								displayLocationOnScreen();
							}
							else{
								Toast.makeText(ObservationSubmit.this, R.string.geocodingnoresult, Toast.LENGTH_LONG).show();
							}
						}
						catch(Exception e)
						{e.printStackTrace();}
					}
					else {
						Toast.makeText(ObservationSubmit.this, R.string.noconnectionavailable, Toast.LENGTH_SHORT).show();
					}
				}
				else {
					Toast.makeText(ObservationSubmit.this, R.string.invalidvalues, Toast.LENGTH_SHORT).show();
				}
			} });

		adb.setNegativeButton(R.string.undo, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {

			} });
		adb.show();
	}

	@Override
	public void onLocationChanged(Location location) {
		this.location = location;
		displayLocationOnScreen();
	}

	public void displayLocationOnScreen()
	{
		String latlong = String.format("Lat. : %s - Long. : %s",location.getLatitude(),location.getLongitude());
		SpannableString locspanfast = new SpannableString(latlong);
		locspanfast.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, latlong.length(), 0);
		tvlocation.setText(locspanfast,BufferType.SPANNABLE);
		tvlocation.setVisibility(View.VISIBLE);
		tvlocationtitle.setVisibility(View.VISIBLE);
		if(ToolsConnection.isOnline(ObservationSubmit.this))
		{
			Geocoder geo = new Geocoder(ObservationSubmit.this);
			try {
				List <Address> adresses = geo.getFromLocation(location.getLatitude(),location.getLongitude(),1);
				if(adresses != null && adresses.size() > 0){
					Address adresse = adresses.get(0);
					latlong = String.format("Lat. : %s - Long. : %s / %s, %s %s",
							location.getLatitude(),
							location.getLongitude(),
							adresse.getAddressLine(0),
							adresse.getPostalCode(),
							adresse.getLocality());
					SpannableString locspan = new SpannableString(latlong);
					locspan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, latlong.length(), 0);
					tvlocation.setText(locspan,BufferType.SPANNABLE);
				}
				else {
					SpannableString locspan = new SpannableString(String.format("Lat. : %s - Long. : %s",
							location.getLatitude(),
							location.getLongitude()));
					locspan.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, String.format("Lat. : %s - Long. : %s",
							location.getLatitude(),
							location.getLongitude()).length(), 0);
					tvlocation.setText(locspan,BufferType.SPANNABLE);
				}
			} catch (IOException e) {
				e.printStackTrace();
				Log.i("Geocode",e.getMessage());
			}
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		lManager.removeUpdates(this);
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}  

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			lManager.removeUpdates(this);
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onStop()
	{
		lManager.removeUpdates(this);
		super.onStop();
	}

	@Override
	public void onDestroy()
	{
		lManager.removeUpdates(this);
		super.onDestroy();
	}

}
