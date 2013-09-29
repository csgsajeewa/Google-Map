package com.example.googlemaps;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.view.Menu;
import android.widget.TextView;

public class DBActivity extends Activity implements AsyncResponse{

	DBAccessTask dAccessTask;
	Context context;
	TextView textView1,textView2;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.db_page);
		textView1=(TextView)findViewById(R.id.latitude);
		textView2=(TextView)findViewById(R.id.longitude);
		
		context=this;
		dAccessTask=new DBAccessTask();
		dAccessTask.delegate=this;
		dAccessTask.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	private class DBAccessTask extends AsyncTask<Void,Void,String>{
		MapDatabase mapDatabase=new MapDatabase(context);
		XMLParser locationDataParser=new XMLParser();
		double geoCodes[]=new double[2];
		public AsyncResponse delegate=null;
		@Override
		protected String doInBackground(Void... arg0) {
			
			String URL="http://192.168.42.35/WebServer/ProvideLocationInfo.php";
			try {
				locationDataParser.processFeed(new URL(URL));
			} catch (MalformedURLException e) {
				
				e.printStackTrace();
			}
			ArrayList<Location>locations=locationDataParser.getLocations();
		    Iterator<Location> iter=locations.iterator();
		    while(iter.hasNext()){
		    	Location tempLocation=(Location)iter.next();
		    	
		    	mapDatabase.addNewPlace(tempLocation.getName(), tempLocation.getLatitude(),tempLocation.getLongitude());
		    }
			
			//mapDatabase.addNewPlace("Female Hostel", 6.797016,79.902593);
			//mapDatabase.addNewPlace("Male Hostel", 6.797692,79.902464);
			
			geoCodes=mapDatabase.getPlaceInfo("Library");
			
			
			return null;
			
			
		}
		
		@Override
        protected void onPostExecute(String result) {
			
			delegate.processFinish(geoCodes);
       }
		
	}

	@Override
	public void processFinish(double[] geoCodes) {
		
		textView1.setText(String.valueOf(geoCodes[0]));
		textView2.setText(String.valueOf(geoCodes[1]));
	}

	@Override
	public void processFinish(Location locations) {
		// TODO Auto-generated method stub
		
	}

}
