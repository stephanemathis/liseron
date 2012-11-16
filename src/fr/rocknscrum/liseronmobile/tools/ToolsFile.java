package fr.rocknscrum.liseronmobile.tools;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpResponse;
import org.apache.http.util.ByteArrayBuffer;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import android.util.Log;

public class ToolsFile {

    public static byte[] DownloadFromUrl(String fileUrl) { 
        try {

                URL url = new URL(fileUrl);
                URLConnection ucon = url.openConnection();
                InputStream is = ucon.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                ByteArrayBuffer baf = new ByteArrayBuffer(50);
                
                //int length = ucon.getContentLength();
                
                int current = 0;
                while ((current = bis.read()) != -1) {
                        baf.append((byte) current);                       
                }

                return baf.toByteArray();

        		} catch (IOException e) {
                Log.d("DownloadFromUrl", "Error: " + e);
                
                return null;
                
        }
	}
    
    public static Document getDocumentFromHttpResponse(HttpResponse response) throws IllegalStateException, IOException, ParserConfigurationException, SAXException
    {
    	 BufferedInputStream bis = new BufferedInputStream(response.getEntity().getContent());
         ByteArrayBuffer baf = new ByteArrayBuffer(50);
         int current = 0;
         while ((current = bis.read()) != -1) {
                 baf.append((byte) current);                       
         }
        DocumentBuilderFactory dbfac = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = dbfac.newDocumentBuilder();
        byte[] data = baf.toByteArray();
        if(data != null && data.length != 0)
        {
	        Document doc = docBuilder.parse(new ByteArrayInputStream(data));
	        return doc;
        }
        else return null;

    }

}
