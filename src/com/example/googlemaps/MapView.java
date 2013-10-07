package com.example.googlemaps;
/**
 * Description of MapView
 * Display map 
 *
 * @author chamath sajeewa
 * chamaths.10@cse.mrt.ac.lk
 */

import java.util.ArrayList;

import org.w3c.dom.Document;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient.ConnectionCallbacks;
import com.google.android.gms.common.GooglePlayServicesClient.OnConnectionFailedListener;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Toast;


public class MapView extends FragmentActivity implements AsyncResponse, ConnectionCallbacks,OnConnectionFailedListener,LocationListener,OnMyLocationButtonClickListener{
    
    private GoogleMap mMap;
    private Context context;
    private Location location;
    private DBAccessTask dAccessTask;
    private LocationClient mLocationClient;
    private GoogleMapV2Direction gMapV2Direction;
    private boolean isNetworkAvailable;
    Document doc;
    // LocationRequest is used by the location listeners to register in location client
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_page);
        context=this;
        gMapV2Direction=new GoogleMapV2Direction();
        dAccessTask=new DBAccessTask();
        dAccessTask.delegate=this;
        isNetworkAvailable=false;
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        setUpLocationClientIfNeeded();
        mLocationClient.connect();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLocationClient != null) {
        	
            mLocationClient.disconnect();
        }
    }
    //single top activity- defined in manifest
    @Override
	protected void onNewIntent(Intent intent) {
		dAccessTask=new DBAccessTask();
        dAccessTask.delegate=this;
	    setIntent(intent);
	    handleIntent(intent);
	}
    //intent is used when user clicks and item in the suggestion menu, 
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
   //access database to find geocodes of destination
	private class DBAccessTask extends AsyncTask<String,Void,Location>{
		MapDatabase mapDatabase=new MapDatabase(context);
		LatLng sourcePosition;
		LatLng destPosition;
		double geoCodes[]=new double[2];
		
		public AsyncResponse delegate=null;
		@Override
		protected Location doInBackground(String... params) {
			
			
			mLocationClient.connect();
			//wait for connection
			while(!mLocationClient.isConnected()){
				
			}
			//get last know location to derive geocodes of source position
			android.location.Location l=mLocationClient.getLastLocation();
			sourcePosition=new LatLng(l.getLatitude(), l.getLongitude());
			geoCodes=mapDatabase.getPlaceInfo(params[0]);
			location=new Location();
			location.setName(params[0]);
			location.setLatitude(geoCodes[0]);
			location.setLongitude(geoCodes[1]);
			destPosition=new LatLng(geoCodes[0], geoCodes[1]);
			if(isNetworkAvailable()){
				isNetworkAvailable=true;
			 doc=gMapV2Direction.getDocument(sourcePosition, destPosition,GoogleMapV2Direction.MODE_OF_WALKING);
			}
			return location;
			
			
		}
		
		@Override
        protected void onPostExecute(Location location) {
			
			delegate.processFinish(location);
       }
		
	}

	

	@Override
	public void processFinish(double[] geoCodes) {
		
		
	}

	@Override
	public void processFinish(Location locations) {
		mMap.clear();
		mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())).title(location.getName()));
		if(isNetworkAvailable){
		ArrayList<LatLng> directionPoint = gMapV2Direction.getDirection(doc);
        PolylineOptions rectLine = new PolylineOptions().width(3).color(Color.RED);

        for (int i = 0; i < directionPoint.size(); i++) {
          rectLine.add(directionPoint.get(i));// add each point of the path to the polyline
        }
         mMap.addPolyline(rectLine);//add polyine to the map
		}
		else {
			 Toast toast = Toast.makeText(this,"No Internet Conenction, Directions Can Not Be Displayed!!", Toast.LENGTH_LONG);
				toast.show();
		}
		
	}
     // for the button
	 public void search(View view){
	 		
	 		onSearchRequested();
	 		
	 	}
	
	
	
    private void setUpMapIfNeeded() {
        
        if (mMap == null) {
            //  obtain the map from the SupportMapFragment.
        	
        	mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
            
            
           if (mMap != null) {
                setUpMap();
                mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationButtonClickListener(this);
            }
        }
    }
    private void setUpLocationClientIfNeeded() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(getApplicationContext(),
                    this,  // ConnectionCallbacks
                    this); // OnConnectionFailedListener
        }
    }
   
    private void setUpMap() {
    	UiSettings settings = mMap.getUiSettings();        
    	 mMap.animateCamera(CameraUpdateFactory.newCameraPosition( new CameraPosition(new LatLng(6.796856, 79.901737), 17f, 30, 10))); // zoom, tilt, bearing
    	 mMap.setTrafficEnabled(false);
    	 
    	 settings.setAllGesturesEnabled(true);
    	 settings.setRotateGesturesEnabled(true);
    	 settings.setScrollGesturesEnabled(true);
    	 settings.setTiltGesturesEnabled(true);
    	 settings.setZoomControlsEnabled(true);
    	 settings.setZoomGesturesEnabled(true);
       
        mMap.addMarker(new MarkerOptions().position(new LatLng(6.796856,79.901737)).title("The University of Moratuwa ").snippet(" Campus Road \n Moratuwa \n 10400 \n Phone: 011 2 650534 "));
       
       
    }

	@Override
	public boolean onMyLocationButtonClick() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onLocationChanged(android.location.Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		 mLocationClient.requestLocationUpdates( REQUEST,this); 
		
	}

	@Override
	public void onDisconnected() {
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
