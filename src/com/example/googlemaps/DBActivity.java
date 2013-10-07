package com.example.googlemaps;
/**
 * Description of DBActivity
 * create and update the database
 *
 * @author chamath sajeewa
 * chamaths.10@cse.mrt.ac.lk
 */

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.Menu;
import android.widget.TextView;
import android.widget.Toast;

public class DBActivity extends Activity implements AsyncResponse{

	private DBAccessTask dAccessTask;
	private Context context;
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.db_page);
		context=this;
		dAccessTask=new DBAccessTask();
		dAccessTask.delegate=this;
		progressDialog=ProgressDialog.show(this,"Updating","Updating Location Data");
		dAccessTask.execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	// to access database asynchronously
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
		if(!isNetworkAvailable()){
			progressDialog.dismiss();
			Toast toast = Toast.makeText(this,"No Internet Conenction!!", Toast.LENGTH_LONG);
			toast.show();
			finish();
		}
		else{
			progressDialog.dismiss();
			Toast toast = Toast.makeText(this,"update is successful", Toast.LENGTH_LONG);
			toast.show();
			finish();
		}
		
	}

	@Override
	public void processFinish(Location locations) {
		// TODO Auto-generated method stub
		
	}
	
	public boolean isNetworkAvailable() {
	    ConnectivityManager cm = (ConnectivityManager) 
	      getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = cm.getActiveNetworkInfo();
	    // if no network is available networkInfo will be null
	    // otherwise check if we are connected
	    if (networkInfo != null && networkInfo.isConnected()) {
	        return true;
	    }
	    return false;
	} 

}
