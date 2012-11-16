package fr.rocknscrum.liseronmobile;

import android.app.Activity;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

public class SpeciesInformationWebViewActivity extends Activity implements OnClickListener{

	private static final int MENU_QUIT = 0;
	Button seeGoogle, seeWikipedia;
	WebView webView;
	String currentGenreName, currentSpeciesName;
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
			requestWindowFeature(Window.FEATURE_NO_TITLE);
	        setContentView(R.layout.speciesinformationwebview);
	        
	        webView = (WebView)findViewById(R.id.webViewspeciesinformationwebview);
	        seeGoogle = (Button)findViewById(R.id.buttonSpeInfoGoogleWebView);
	        seeWikipedia = (Button)findViewById(R.id.buttonSpeInfoWikiWebView);

	        seeGoogle.setOnClickListener(this);
	        seeWikipedia.setOnClickListener(this);
	        
	        currentGenreName = this.getIntent().getStringExtra("genre");
	        currentSpeciesName = this.getIntent().getStringExtra("species");
	        
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
	        
	        loadWikipedia(currentGenreName, currentSpeciesName);
	        

	        
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
			if(arg0.getId()==R.id.buttonSpeInfoGoogleWebView)
				loadGoogle(currentGenreName, currentSpeciesName);
			else if(arg0.getId()==R.id.buttonSpeInfoWikiWebView)
				loadWikipedia(currentGenreName, currentSpeciesName);
			
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
}
