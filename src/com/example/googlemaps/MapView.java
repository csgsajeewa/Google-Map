package com.example.googlemaps;



import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


public class MapView extends FragmentActivity implements AsyncResponse {
    /**
     * Note that this may be null if the Google Play services APK is not available.
     */
    private GoogleMap mMap;
    private Context context;
    private Location location;
    private DBAccessTask dAccessTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        context=this;
        dAccessTask=new DBAccessTask();
        dAccessTask.delegate=this;
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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
            }
        }
    }

   
    private void setUpMap() {
       
        mMap.addMarker(new MarkerOptions().position(new LatLng(6.796856,79.901737)).title("The University of Moratuwa ").snippet(" Campus Road \n Moratuwa \n 10400 \n Phone: 011 2 650534 \n Founded: 1972"));
        mMap.addMarker(new MarkerOptions().position(new LatLng(6.797623,79.900602)).title("Indoor Badminton Court"));
        dAccessTask.execute("New Auditorium");
        PolylineOptions rectOptions = new PolylineOptions().add(new LatLng(6.795056,79.900763)).add(new LatLng(6.796563,79.90086));
        Polyline polyline = mMap.addPolyline(rectOptions);
    }
    
    private class DBAccessTask extends AsyncTask<String,Void,Location>{
		MapDatabase mapDatabase=new MapDatabase(context);
		
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
		
		
	}

	@Override
	public void processFinish(Location locations) {
		mMap.addMarker(new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())).title(location.getName()));
		
	}

}
