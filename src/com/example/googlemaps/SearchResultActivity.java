package com.example.googlemaps;

import android.app.Activity;


import android.os.Bundle;
import android.view.View;

public class SearchResultActivity extends Activity{
	
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.search1);

	    
	    }
	
   public void onSearchRequested(View view){
 		
 		onSearchRequested();
 	}
}


