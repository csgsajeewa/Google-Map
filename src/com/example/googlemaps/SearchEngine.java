package com.example.googlemaps;

import java.net.URI;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class SearchEngine extends Activity implements AsyncResponse{
	
	private Context context;
	private TextView textView1,textView2;
	private DBAccessTask dAccessTask;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.search);
        context=this;
        dAccessTask=new DBAccessTask();
        dAccessTask.delegate=this;
        textView1=(TextView)findViewById(R.id.textView1);
        textView2=(TextView)findViewById(R.id.textView2);
	    // Get the intent, verify the action and get the query
	    Intent intent = getIntent();
	    handleIntent(intent);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		dAccessTask=new DBAccessTask();
        dAccessTask.delegate=this;
	    setIntent(intent);
	    handleIntent(intent);
	}

	private void handleIntent(Intent intent) {
	    if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
	    	String query;
	    	Uri uri=intent.getData();
	    	if(uri!=null){
	    		
	    		query=uri.toString();
	    	}
	    	else{
	            query = intent.getStringExtra(SearchManager.QUERY);
	    	}
	      search(query);
	    }
	}
	public void search(String query){
		
		dAccessTask.execute(query);
	}
	
	private class DBAccessTask extends AsyncTask<String,Void,Location>{
		MapDatabase mapDatabase=new MapDatabase(context);
		Location location;
		double geoCodes[]=new double[2];
		
		public AsyncResponse delegate=null;
		@Override
		protected Location doInBackground(String... params) {
			
			//mapDatabase.addNewPlace("Female Hostel", 6.797016,79.902593);
			//mapDatabase.addNewPlace("Male Hostel", 6.797692,79.902464);
			
			geoCodes=mapDatabase.getPlaceInfo(params[0]);
			location=new Location();
			location.setName(params[0]);
			location.setLatitude(geoCodes[0]);
			location.setLongitude(geoCodes[1]);
			
			return location;
			
			
		}
		
		@Override
        protected void onPostExecute(Location location) {
			
			delegate.processFinish(location);
       }
	}

	


	@Override
	public void processFinish(double[] geoCodes) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void processFinish(Location locations) {
		textView1.setText(locations.getName());
		textView2.setText(locations.getLatitude()+ " " + locations.getLongitude());
		
	}
	
   public void onSearchRequested(View view){
 		
 		onSearchRequested();
 	}
}
