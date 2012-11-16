package fr.rocknscrum.liseronmobile.custom;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import fr.rocknscrum.liseronmobile.R;

public class SimpleAdapterHACustom extends SimpleAdapter implements SectionIndexer{

    HashMap<String, Integer> letters;
    Object[] sections;
    Context context;
    HashMap<String, Integer> alphaIndexer;
    
    public SimpleAdapterHACustom(Context context, List<? extends Map<String, ?>> data, int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);
        
        this.context = context;
        
        /*Building the sections */
        @SuppressWarnings("unchecked")
		ArrayList<HashMap<String, String>> elements = (ArrayList<HashMap<String, String>>) data;
        alphaIndexer = new HashMap<String, Integer>();

        int size = elements.size();
        for (int i = size - 1; i >= 0; i--) 
        {
                String element = elements.get(i).get("titre");
                if(element != null && element.length()>1)
                	alphaIndexer.put(element.substring(0, 1), i);
                else alphaIndexer.put("", i);
        }

        Set<String> keys = alphaIndexer.keySet(); 
        Iterator<String> it = keys.iterator();
        ArrayList<String> keyList = new ArrayList<String>(); 

        while (it.hasNext()) 
        {
                String key = it.next();
                keyList.add(key);
        }
        Collections.sort(keyList);
        sections = new String[keyList.size()];
        keyList.toArray(sections);

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

    	
    	LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.itemlistviewcustom, null);
        }

		@SuppressWarnings("unchecked")
		HashMap<String, String> data = (HashMap<String, String>)getItem(position);

        ((TextView) convertView.findViewById(R.id.titre)).setText(data.get("titre"));
        
        ImageView image = (ImageView) convertView.findViewById(R.id.img);         
        image.setImageResource(Integer.parseInt(data.get("img")));

        ImageView imageright = (ImageView) convertView.findViewById(R.id.rightimg);         
        imageright.setImageResource(Integer.parseInt(data.get("rightimg")));
        
        return convertView;
    }

	@Override
	public int getPositionForSection(int arg0) {
		if(arg0>=sections.length)
			arg0 = sections.length-1;
        String letter = (String) sections[arg0];
        
        return alphaIndexer.get(letter);
	}

	@Override
	public int getSectionForPosition(int arg0) {
		return 0;
	}

	@Override
	public Object[] getSections() {
		return sections;
	}

}


