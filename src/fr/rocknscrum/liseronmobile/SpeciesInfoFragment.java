package fr.rocknscrum.liseronmobile;

import com.j256.ormlite.dao.RuntimeExceptionDao;

import fr.rocknscrum.liseronmobile.classification.Family;
import fr.rocknscrum.liseronmobile.classification.Genre;
import fr.rocknscrum.liseronmobile.classification.Species;
import fr.rocknscrum.liseronmobile.database.ToolsORMLite;
import fr.rocknscrum.liseronmobile.tools.ToolsConnection;
import fr.rocknscrum.liseronmobile.tools.ToolsString;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SpeciesInfoFragment extends Fragment implements OnClickListener {

	View v;
	int id;
	String type, currentGenreName, currentSpeciesName;
	WebView webView;
	Button seeGoogle, seeWikipedia;

	public SpeciesInfoFragment()
	{
		type="";
		id=-1;
	}
	
	public SpeciesInfoFragment(int id, String type) {
		this.id = id;
		this.type = type;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		v = inflater.inflate(R.layout.speciesinformation, container, false);
		
		if(type.compareTo("Species")==0)
        {
	    	RuntimeExceptionDao<Species, Integer> simpleDao = ToolsORMLite.getInstance(getActivity()).getHelper().getRuntimeExceptionDao(Species.class);
	        Species s = simpleDao.queryForId(id);        
	        TextView tvspeciestitle = (TextView)v.findViewById(R.id.speciesinformationspeciestitle);
	        tvspeciestitle.setText(getString(R.string.species)+" : "+s.getName());
	        TextView tvspeciesdesc = (TextView)v.findViewById(R.id.speciesinformationspeciesdescription);
	        if(!ToolsString.isNullOrempty(s.getDescription()))
	        	tvspeciesdesc.setText(s.getDescription());
        
	    	RuntimeExceptionDao<Genre, Integer> simpleDao2 = ToolsORMLite.getInstance(getActivity()).getHelper().getRuntimeExceptionDao(Genre.class);
	        Genre g = simpleDao2.queryForId(s.getGenre().getId());        
	        TextView tvgenretitle = (TextView)v.findViewById(R.id.speciesinformationgenretitle);
	        tvgenretitle.setText(getString(R.string.genre)+" : "+g.getName());
	        TextView tvgenredesc = (TextView)v.findViewById(R.id.speciesinformationgenredescription);
	        if(!ToolsString.isNullOrempty(g.getDescription()))
	        	tvgenredesc.setText(g.getDescription());
	        
	    	RuntimeExceptionDao<Family, Integer> simpleDao3 = ToolsORMLite.getInstance(getActivity()).getHelper().getRuntimeExceptionDao(Family.class);
	    	Family f = simpleDao3.queryForId(g.getFamily().getId());        
	        TextView tvfamilytitle = (TextView)v.findViewById(R.id.speciesinformationfamilytitle);
	        tvfamilytitle.setText(getString(R.string.family)+" : "+f.getName());
	        TextView tvfamilydesc = (TextView)v.findViewById(R.id.speciesinformationfamilydescription);
	        if(!ToolsString.isNullOrempty(f.getDescription()))
	        	tvfamilydesc.setText(f.getDescription());
	        if(!s.isIndanger())
	        {
		        TextView tvwarning = (TextView)v.findViewById(R.id.speciesinformationwarning);
		        tvwarning.setVisibility(View.GONE);
	        }
	        
	    	if(ToolsConnection.isOnline(getActivity()))
	    	{
	    		SharedPreferences mgr = PreferenceManager.getDefaultSharedPreferences(getActivity());
	    		boolean useinternetconnection = mgr.getBoolean("useinternetconnection", true);	
	    		if(useinternetconnection)
	    		{
			        webView = (WebView) v.findViewById(R.id.webViewSpeciesInfo);
			        seeGoogle = (Button)v.findViewById(R.id.buttonSpeInfoGoogle);
			        seeWikipedia = (Button)v.findViewById(R.id.buttonSpeInfoWiki);
			        
			        webView.setVisibility(View.VISIBLE);
			        seeGoogle.setVisibility(View.VISIBLE);
			        seeWikipedia.setVisibility(View.VISIBLE);
			        
			        seeGoogle.setOnClickListener(this);
			        seeWikipedia.setOnClickListener(this);
			        
			        currentGenreName = g.getName();
			        currentSpeciesName = s.getName();
			        
			        loadWikipedia(currentGenreName, currentSpeciesName);
			        
			        webView.setWebViewClient(new WebViewClient(){
			            public boolean shouldOverrideUrlLoading(WebView view, String url) {
			                if (url != null && url.startsWith("http://")) {
			                		webView.loadUrl(url);
			                    return true;
			                } else {
			                    return false;
			                }
			            }
			        });
	    		}
	    	}
	    }
        else if(type.compareTo("Genre")==0)
        {
	        TextView tvspeciestitle = (TextView)v.findViewById(R.id.speciesinformationspeciestitle);
	        tvspeciestitle.setVisibility(View.GONE);
	        TextView tvspeciesdesc = (TextView)v.findViewById(R.id.speciesinformationspeciesdescription);
	        tvspeciesdesc.setVisibility(View.GONE);
        
	    	RuntimeExceptionDao<Genre, Integer> simpleDao2 = ToolsORMLite.getInstance(getActivity()).getHelper().getRuntimeExceptionDao(Genre.class);
	        Genre g = simpleDao2.queryForId(id);        
	        TextView tvgenretitle = (TextView)v.findViewById(R.id.speciesinformationgenretitle);
	        tvgenretitle.setText(getString(R.string.genre)+" : "+g.getName());
	        TextView tvgenredesc = (TextView)v.findViewById(R.id.speciesinformationgenredescription);
	        if(!ToolsString.isNullOrempty(g.getDescription()))
	        	tvgenredesc.setText(g.getDescription());
	        
	    	RuntimeExceptionDao<Family, Integer> simpleDao3 = ToolsORMLite.getInstance(getActivity()).getHelper().getRuntimeExceptionDao(Family.class);
	    	Family f = simpleDao3.queryForId(g.getFamily().getId());        
	        TextView tvfamilytitle = (TextView)v.findViewById(R.id.speciesinformationfamilytitle);
	        tvfamilytitle.setText(getString(R.string.family)+" : "+f.getName());
	        TextView tvfamilydesc = (TextView)v.findViewById(R.id.speciesinformationfamilydescription);
	        if(!ToolsString.isNullOrempty(f.getDescription()))
	        	tvfamilydesc.setText(f.getDescription());
	        TextView tvwarning = (TextView)v.findViewById(R.id.speciesinformationwarning);
	        tvwarning.setVisibility(View.GONE);
        }
        else if(type.compareTo("Family")==0){    
	        TextView tvspeciestitle = (TextView)v.findViewById(R.id.speciesinformationspeciestitle);
	        tvspeciestitle.setVisibility(View.GONE);
	        TextView tvspeciesdesc = (TextView)v.findViewById(R.id.speciesinformationspeciesdescription);
	        tvspeciesdesc.setVisibility(View.GONE);
      
	        TextView tvgenretitle = (TextView)v.findViewById(R.id.speciesinformationgenretitle);
	        tvgenretitle.setVisibility(View.GONE);
	        TextView tvgenredesc = (TextView)v.findViewById(R.id.speciesinformationgenredescription);
	        tvgenredesc.setVisibility(View.GONE);
	        
	    	RuntimeExceptionDao<Family, Integer> simpleDao3 = ToolsORMLite.getInstance(getActivity()).getHelper().getRuntimeExceptionDao(Family.class);
	    	Family f = simpleDao3.queryForId(id);        
	        TextView tvfamilytitle = (TextView)v.findViewById(R.id.speciesinformationfamilytitle);
	        tvfamilytitle.setText(getString(R.string.family)+" : "+f.getName());
	        TextView tvfamilydesc = (TextView)v.findViewById(R.id.speciesinformationfamilydescription);
	        if(!ToolsString.isNullOrempty(f.getDescription()))
	        	tvfamilydesc.setText(f.getDescription());
	        TextView tvwarning = (TextView)v.findViewById(R.id.speciesinformationwarning);
	        tvwarning.setVisibility(View.GONE);
        }
        else {
	        TextView tvwarning = (TextView)v.findViewById(R.id.speciesinformationwarning);
	        tvwarning.setVisibility(View.GONE);
	        LinearLayout ll = (LinearLayout)v.findViewById(R.id.linearlayoutSpeciInfo2);
	        ll.setVisibility(View.GONE);
        }
		
		return v;
	}

	
	public void loadGoogle(String genreName, String speciesName)
	{
		seeGoogle.setEnabled(false);
		seeWikipedia.setEnabled(true);
	    webView.loadUrl("https://www.google.com/search?tbm=isch&q="+genreName+" "+speciesName);
	}
	
	public void loadWikipedia(String genreName, String speciesName)
	{
		seeGoogle.setEnabled(true);
		seeWikipedia.setEnabled(false);
	    webView.loadUrl("http://fr.wikipedia.org/w/index.php?search="+genreName.replace(" ","+")+"+"+speciesName.replace(" ","+"));
	       
	}

	@Override
	public void onClick(View arg0) {
		if(arg0.getId()==R.id.buttonSpeInfoGoogle)
			loadGoogle(currentGenreName, currentSpeciesName);
		else if(arg0.getId()==R.id.buttonSpeInfoWiki)
			loadWikipedia(currentGenreName, currentSpeciesName);
		
	}
}
