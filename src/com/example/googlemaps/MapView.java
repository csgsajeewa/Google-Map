package com.example.googlemaps;
/**
 * Description of MapView
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
    private GMapV2Direction gMapV2Direction;
    private boolean isNetworkAvailable;
    Document doc;
    private static final LocationRequest REQUEST = LocationRequest.create()
            .setInterval(5000)         // 5 seconds
            .setFastestInterval(16)    // 16ms
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_page);
        context=this;
        gMapV2Direction=new GMapV2Direction();
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
		LatLng sourcePosition;//=new LatLng(6.795064,79.900769);
		LatLng destPosition;//=new LatLng(6.798238,79.902695);
		double geoCodes[]=new double[2];
		
		public AsyncResponse delegate=null;
		@Override
		protected Location doInBackground(String... params) {
			
			//mapDatabase.addNewPlace("Female Hostel", 6.797016,79.902593);
			//mapDatabase.addNewPlace("Male Hostel", 6.797692,79.902464);
			mLocationClient.connect();
			while(!mLocationClient.isConnected()){
				
			}
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
			 doc=gMapV2Direction.getDocument(sourcePosition, destPosition,GMapV2Direction.MODE_WALKING);
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
          rectLine.add(directionPoint.get(i));
        }
         mMap.addPolyline(rectLine);
		}
		else {
			 Toast toast = Toast.makeText(this,"No Internet Conenction, Directions Can Not Be Displayed!!", Toast.LENGTH_LONG);
				toast.show();
		}
		
	}

	 public void search(View view){
	 		
	 		onSearchRequested();
	 		
	 	}
	
	
	
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
        	
        	mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                    .getMap();
            
            
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
                mMap.setMyLocationEnabled(true);
                mMap.setOnMyLocationButtonClickListener(this);
            }
        }
    }
    private void setUpLocationClientIfNeeded() {
        if (mLocationClient == null) {
            mLocationClient = new LocationClient(
                    getApplicationContext(),
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
       
        mMap.addMarker(new MarkerOptions().position(new LatLng(6.796856,79.901737)).title("The University of Moratuwa ").snippet(" Campus Road \n Moratuwa \n 10400 \n Phone: 011 2 650534 \n Founded: 1972"));
       
       
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
