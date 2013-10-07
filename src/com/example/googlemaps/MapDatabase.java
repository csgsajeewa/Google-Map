package com.example.googlemaps;
/**
 * Description of MapDatabase
 * Represent the underlying database and act as a content provide for suggestion menu
 *
 * @author chamath sajeewa
 * chamaths.10@cse.mrt.ac.lk
 */


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.util.Log;

public class MapDatabase extends ContentProvider{
  
  public static final String KEY_ID = "_id";
  public static final String SUGGEST_COLUMN_TEXT_1="suggest_text_1"; // for suggestion menu
  public static final String SUGGEST_COLUMN_INTENT_DATA="suggest_intent_data"; // for suggestion menu
  public static final String PLACE =   "PLACE";
  public static final String LATITUDE ="LATITUDE";
  public static final String LONGITUDE ="LONGITUDE";
 
  private MapDBOpenHelper mapDBOpenHelper;
   //two constructors
  //this is for extending ContentProvider- required by the system
  public MapDatabase() {
	
    }

  // this is used within the application
  public MapDatabase(Context context) {
    mapDBOpenHelper = new MapDBOpenHelper(context, MapDBOpenHelper.DATABASE_NAME, null, MapDBOpenHelper.DATABASE_VERSION);
  }
  
  // Called when  no longer need access to the database.
  public void closeDatabase() {
    mapDBOpenHelper.close();
  }

  
  private Cursor getPlaceLongLat(String place1) {
    
    // Specify the result column projection.
    String[] result_columns = new String[] {KEY_ID, PLACE,LATITUDE,LONGITUDE}; 
    
    // Specify the where clause 
    String where = SUGGEST_COLUMN_INTENT_DATA + "=" +"'"+ place1 + "'";
    String whereArgs[] = null;
    String groupBy = null;
    String having = null;
    String order = null;
    
    SQLiteDatabase db = mapDBOpenHelper.getWritableDatabase();
    Cursor cursor = db.query(MapDBOpenHelper.DATABASE_TABLE,  result_columns, where,whereArgs, groupBy, having, order);
    return cursor;
  }
  
  public double[] getPlaceInfo(String place) {
    Cursor cursor = getPlaceLongLat(place);// get cursor
    
    double latitude=0;
    double longitude=0;
    double geoCodes[]=new double[2];
    
    // Find the index to the columns being used.
    int LATITUDE_INDEX =cursor.getColumnIndexOrThrow(LATITUDE);
    int LONGITUDE_INDEX =cursor.getColumnIndexOrThrow(LONGITUDE);

    // Iterate over the cursors rows. 
   while (cursor.moveToNext()) {
    	
         latitude= cursor.getDouble(LATITUDE_INDEX);
         longitude=cursor.getDouble(LONGITUDE_INDEX);
         geoCodes[0]=latitude;
         geoCodes[1]=longitude;
    }
      return geoCodes;
  }
  
  public void addNewPlace(String place, double latitude, double longitude) {
    
    // Create a new row of values to insert.
    ContentValues newValues = new ContentValues();
  
    // Assign values for each row.
    newValues.put(PLACE, place);
    newValues.put(SUGGEST_COLUMN_TEXT_1 , place);
    newValues.put(SUGGEST_COLUMN_INTENT_DATA , place);
    newValues.put(LATITUDE, latitude);
    newValues.put(LONGITUDE, longitude);
    
  
    // Insert the row into your table
    SQLiteDatabase db = mapDBOpenHelper.getWritableDatabase();
    db.insert(MapDBOpenHelper.DATABASE_TABLE, null, newValues); 
   }
  
  public void updatePlaceInfo(String place, double latitude, double longitude) {
   
    // Create the updated row Content Values.
    ContentValues updatedValues = new ContentValues();
  
    // Assign values for each row.
    updatedValues.put(LATITUDE, latitude);
    updatedValues.put(LONGITUDE, longitude);
    
    // Specify a where clause the defines which rows should be
    // updated. 
    String where = PLACE + "=" + place;
    String whereArgs[] = null;
  
    // Update the row with the specified index with the new values.
    SQLiteDatabase db = mapDBOpenHelper.getWritableDatabase();
    db.update(MapDBOpenHelper.DATABASE_TABLE, updatedValues, 
              where, whereArgs);
  }
  
  
  //SQLiteOpenHelper-A helper class to manage database creation and version management.
  private static class MapDBOpenHelper extends SQLiteOpenHelper {
    
    private static final String DATABASE_NAME = "MapDatabase8.db";
    private static final String DATABASE_TABLE = "PLACES";
    private static final int DATABASE_VERSION = 1;
    
    // SQL Statement to create a new database.
    private static final String DATABASE_CREATE = "create table " +
      DATABASE_TABLE + " (" + KEY_ID +
      " integer primary key autoincrement, " +
     PLACE + " text not null, " +
     SUGGEST_COLUMN_TEXT_1  + " text not null, " +
     SUGGEST_COLUMN_INTENT_DATA + " text not null, " +
     LATITUDE + " double, " +
     LONGITUDE + " double);";

    public MapDBOpenHelper(Context context, String name,
                      CursorFactory factory, int version) {
      super(context, name, factory, version);
    }

    // Called when no database exists in disk and the helper class needs to create a new one.
    @Override
    public void onCreate(SQLiteDatabase db) {
      db.execSQL(DATABASE_CREATE);
    }

    // Called when there is a database version mismatch meaning that the version of the database on disk needs to be upgraded to the current version.
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      // Log the version upgrade.
      Log.w("TaskDBAdapter", "Upgrading from version " +oldVersion + " to " +newVersion + ", which will destroy all old data");

      //  drop the old table and create a new one.
      db.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
      // Create a new one.
      onCreate(db);
    }
  }

////////////////////////inherited from the content provider class////////////////////
@Override
public int delete(Uri arg0, String arg1, String[] arg2) {
	// TODO Auto-generated method stub
	return 0;
}

@Override
public String getType(Uri arg0) {
	// TODO Auto-generated method stub
	return null;
}

@Override
public Uri insert(Uri arg0, ContentValues arg1) {
	// TODO Auto-generated method stub
	return null;
}

@Override
public boolean onCreate() {
	
		mapDBOpenHelper = new MapDBOpenHelper(getContext(), MapDBOpenHelper.DATABASE_NAME, null, MapDBOpenHelper.DATABASE_VERSION);
		return true;
	

}
//this is used by the system to provide suggestions using the menu
@Override
public Cursor query(Uri arg0, String[] arg1, String arg2, String[] arg3,String arg4) {

	String[] result_columns = new String[] {  KEY_ID, SUGGEST_COLUMN_TEXT_1,SUGGEST_COLUMN_INTENT_DATA}; 
	// Specify the where clause 
	String query = arg0.getLastPathSegment();
    String where = SUGGEST_COLUMN_TEXT_1+ " Like "+"'%"+ query +"%'";
		    String whereArgs[] = null;
		    String groupBy = null;
		    String having = null;
		    String order = null;
		  
		    SQLiteDatabase db = mapDBOpenHelper.getWritableDatabase();
		    Cursor cursor = db.query(MapDBOpenHelper.DATABASE_TABLE,  result_columns, where,whereArgs, groupBy, having, order);
		    return cursor;
	  
}

@Override
public int update(Uri arg0, ContentValues arg1, String arg2, String[] arg3) {
	// TODO Auto-generated method stub
	return 0;
}

//////////////////////////////////////end of inherited methods///////////////////////////////////////////////////

}