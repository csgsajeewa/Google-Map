package com.example.googlemaps;
/**
 * Description of XML Parser
 * 
 *
 * @author chamath sajeewa
 * chamaths.10@cse.mrt.ac.lk
 */

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import android.util.Log;


public class XMLParser extends DefaultHandler {
	
	private boolean inPlace = false;
	private boolean inName = false;
	
	private boolean inLatitude = false;
	private boolean inLongitude = false;

	private String name = null;
	private double latitude = 0;
	private double longitude = 0;
	private ArrayList<Location>locations=new ArrayList<Location>();
	private int index=-1;
	
	public void startElement(String uri, String localName, String qName,
			Attributes attributes) throws SAXException {
		
		if (qName.equals("place")) 
			{
			  inPlace=true;
			  index++;    
			  locations.add(new Location());
			   
			}
		
		
		if (qName.equals("name")) 
					inName=true;
		
		
				
		if (qName.equals("latitude")) 
					inLatitude = true;
					
				
		if (qName.equals("longitude")) 
					inLongitude = true;
				
	}
		
	
	
	 public void characters(char ch[], int start, int length) {
		
		 
		 if (inName && inPlace) { 
			
			inName=false;
			name=new String(ch,start,length);
			locations.get(index).setName(name);
		 }
		 
		
			 
		 
		 if (inLatitude && inPlace) { 
			
			 inLatitude=false;
			 int decimalPoint=0;
			 for (int i = start; i < length; i++) {
				if(ch[i]=='.'){
					decimalPoint=i;
					break;
				}
			 }
			 
			double temp;
			 double number=0;
			 
			 for (int i = start; i < length; i++) {
				 temp=ch[i];
				 temp=temp-48;
				 if(i<decimalPoint){
					
					 number=number+temp* Math.pow(10,decimalPoint-i-1);
				 }
				 
				 if(i>decimalPoint){
					number=number+temp/Math.pow(10,i-decimalPoint);
				 }
				
			}
			 latitude=number;
			  locations.get(index).setLatitude(latitude);
			 
		 }
		 
		 if (inLongitude && inPlace){
			 
			 inLongitude=false;
			 int decimalPoint=0;
			 for (int i = 0; i < length; i++) {
				if(ch[i]=='.'){
					decimalPoint=i;
					break;
				}
			 }
			 
			double temp;
			 double number=0;
			 
			 for (int i = 0; i < length; i++) {
				 temp=ch[i];
				 temp=temp-48;
				 if(i<decimalPoint){
					
					 number=number+temp* Math.pow(10,decimalPoint-i-1);
				 }
				 
				 if(i>decimalPoint){
					number=number+temp/Math.pow(10,i-decimalPoint);
				 }
				
			}
			 longitude=number;
	         locations.get(index).setLongitude(longitude);
		 }
		 
	 }
	 
	 
	
	public void processFeed( URL url) {
        try {
        	
            SAXParserFactory spf = SAXParserFactory.newInstance();
            SAXParser sp = spf.newSAXParser();
            XMLReader xr = sp.getXMLReader();
            xr.setContentHandler(this);
         
            xr.parse(new InputSource(url.openStream()));
         
        } catch (IOException e) {
            Log.e("", e.toString());
        } catch (SAXException e) {
            Log.e("", e.toString());
        } catch (ParserConfigurationException e) {
            Log.e("", e.toString());
        }
	}

  public ArrayList<Location> getLocations() {
	return locations;
  }
	
	
	
}


