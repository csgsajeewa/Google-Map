package com.example.googlemaps;



import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;


public class MapView extends FragmentActivity {
    /**
     * Note that this may be null if the Google Play services APK is not available.
     */
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
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
            mMap.addMarker(new MarkerOptions().position(new LatLng(6.796856,79.901737)).title("The University of Moratuwa ").snippet(" Campus Road \n Moratuwa \n 10400 \n Phone: 011 2 650534 \n Founded: 1972"));
            mMap.addMarker(new MarkerOptions().position(new LatLng(6.797623,79.900602)).title("Indoor Badminton Court"));
            PolylineOptions rectOptions = new PolylineOptions().add(new LatLng(6.795056,79.900763)).add(new LatLng(6.796563,79.90086));
            Polyline polyline = mMap.addPolyline(rectOptions);
            
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

   
    private void setUpMap() {
        mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
    }
}
