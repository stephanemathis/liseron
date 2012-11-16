package fr.rocknscrum.liseronmobile;

import java.io.InputStream;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.webkit.WebView;

public class HelpActivity extends Activity{
	
	private static final int MENU_QUIT = 0;
	WebView faq;

	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.help);
        
        faq = (WebView)findViewById(R.id.wvfaq);
        
        try{
	        Resources res = getResources();
	        InputStream in_s = res.openRawResource(R.raw.helphtml);
	        byte[] b = new byte[in_s.available()];
	        in_s.read(b);
	        faq.setBackgroundColor(0x00000000);
	        faq.loadData(new String(b), "text/html;charset=UTF-8", null);
        }
    	catch(Exception e)
    	{
    		e.printStackTrace();
    		Log.e("help loading", e.getMessage());
    	}
    
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

	/*ROTATION OF THE SCREEN*/
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
	}
	
}
