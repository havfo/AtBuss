package net.fosstveit.atbuss.managers;

import java.util.ArrayList;

import net.fosstveit.atbuss.objects.BusStop;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AtBussDataManager extends SQLiteOpenHelper {

	private static final int DATABASE_VERSION = 1;
	private static final String DATABASE_NAME = "AtBuss";

	private static final String TABLE_STOPS = "stops";
	private static final String STOPS_KEY_ID = "id";
	private static final String STOPS_KEY_NAME = "name";
	private static final String STOPS_KEY_LATITUDE = "latitude";
	private static final String STOPS_KEY_LONGITUDE = "longitude";
	private static final String STOPS_KEY_NUMUSED = "numused";
	private static final String[] STOPS_COLUMNS = { STOPS_KEY_ID,
			STOPS_KEY_NAME, STOPS_KEY_LATITUDE, STOPS_KEY_LONGITUDE,
			STOPS_KEY_NUMUSED };
	private static final String CREATE_STOPS_TABLE = "CREATE TABLE "
			+ TABLE_STOPS + " (" + STOPS_KEY_ID + " INTEGER PRIMARY KEY, "
			+ STOPS_KEY_NAME + " TEXT, " + STOPS_KEY_LATITUDE + " REAL, "
			+ STOPS_KEY_LONGITUDE + " REAL, " + STOPS_KEY_NUMUSED + " INTEGER)";

	private static final String TABLE_VERSION = "version";
	private static final String VERSION_KEY_ID = "id";
	private static final String VERSION_KEY_VERSION = "version";
	private static final String[] VERSION_COLUMNS = { VERSION_KEY_ID,
			VERSION_KEY_VERSION };
	private static final String CREATE_VERSION_TABLE = "CREATE TABLE "
			+ TABLE_VERSION + " (" + VERSION_KEY_ID
			+ " INTEGER PRIMARY KEY AUTOINCREMENT, " + VERSION_KEY_VERSION
			+ " INTEGER)";

	public AtBussDataManager(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_STOPS_TABLE);
		db.execSQL(CREATE_VERSION_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOPS);
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_VERSION);
		this.onCreate(db);
	}

	public void addBusStop(BusStop stop) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(STOPS_KEY_ID, stop.getId());
		values.put(STOPS_KEY_NAME, stop.getName());
		values.put(STOPS_KEY_LATITUDE, stop.getLatitude());
		values.put(STOPS_KEY_LONGITUDE, stop.getLongitude());
		values.put(STOPS_KEY_NUMUSED, 0);
		db.insert(TABLE_STOPS, null, values);
		db.close();
	}

	public void addBusStops(String[] stops) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		db.beginTransaction();
		for (int i = 0; i < stops.length; i++) {
			String[] tmp = stops[i].split("<ln>", -1);
			values.put(STOPS_KEY_ID, tmp[0]);
			values.put(STOPS_KEY_NAME, tmp[1]);
			values.put(STOPS_KEY_LATITUDE, tmp[2]);
			values.put(STOPS_KEY_LONGITUDE, tmp[3]);
			values.put(STOPS_KEY_NUMUSED, 0);
			db.insert(TABLE_STOPS, null, values);
			values.clear();
		}
		db.setTransactionSuccessful();
		db.endTransaction();
		db.close();
	}

	public void clearBusStops() {
		SQLiteDatabase db = this.getWritableDatabase();
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_STOPS);
		db.execSQL(CREATE_STOPS_TABLE);
		db.close();
	}

	public void addBusStops(ArrayList<BusStop> stops) {
		for (BusStop stop : stops) {
			addBusStop(stop);
		}
	}

	public void addVersion(String version) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(VERSION_KEY_VERSION, version);
		db.insert(TABLE_VERSION, null, values);
		db.close();
	}

	public BusStop getBusStop(int id) {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_STOPS, STOPS_COLUMNS, " id = ?",
				new String[] { String.valueOf(id) }, null, null, null, null);

		if (cursor != null && cursor.moveToFirst()) {
			BusStop stop = new BusStop(Integer.parseInt(cursor.getString(0)),
					cursor.getString(1),
					Double.parseDouble(cursor.getString(2)),
					Double.parseDouble(cursor.getString(3)),
					Integer.parseInt(cursor.getString(4)));

			return stop;
		} else {
			return null;
		}
	}

	public int getLatestVersion() {
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_VERSION, VERSION_COLUMNS, null, null,
				null, null, VERSION_KEY_ID + " DESC", "1");

		if (cursor != null && cursor.moveToFirst()) {
			return cursor.getInt(1);
		} else {
			return -1;
		}
	}

	public ArrayList<BusStop> getBusStopsInRange(double latitude,
			double longitude, double dist) {
		ArrayList<BusStop> stops = new ArrayList<BusStop>();
		SQLiteDatabase db = this.getReadableDatabase();
		
		double delta = dist / 500.0;

		String where = "(latitude > " + (latitude - (0.0045 * delta))
				+ ") and (latitude < " + (latitude + (0.0045 * delta))
				+ ") and (longitude > " + (longitude - (0.01 * delta))
				+ ") and (longitude < " + (longitude + (0.01 * delta)) + ")";

		Cursor cursor = db.query(TABLE_STOPS, STOPS_COLUMNS, where, null, null,
				null, STOPS_KEY_NUMUSED + " DESC", null);
		BusStop stop = null;
		if (cursor != null && cursor.moveToFirst()) {
			do {
				stop = new BusStop(Integer.parseInt(cursor.getString(0)),
						cursor.getString(1), Double.parseDouble(cursor
								.getString(2)), Double.parseDouble(cursor
								.getString(3)), Integer.parseInt(cursor
								.getString(4)));

				stops.add(stop);
			} while (cursor.moveToNext());
		}

		return stops;
	}

	public ArrayList<BusStop> getAllBusStops() {
		ArrayList<BusStop> stops = new ArrayList<BusStop>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_STOPS, STOPS_COLUMNS, null, null, null,
				null, STOPS_KEY_NUMUSED + " DESC", null);
		BusStop stop = null;
		if (cursor != null && cursor.moveToFirst()) {
			do {
				stop = new BusStop(Integer.parseInt(cursor.getString(0)),
						cursor.getString(1), Double.parseDouble(cursor
								.getString(2)), Double.parseDouble(cursor
								.getString(3)), Integer.parseInt(cursor
								.getString(4)));

				stops.add(stop);
			} while (cursor.moveToNext());
		}

		return stops;
	}

	public ArrayList<BusStop> getMostUsedBusStops(int count) {
		ArrayList<BusStop> stops = new ArrayList<BusStop>();
		SQLiteDatabase db = this.getReadableDatabase();
		Cursor cursor = db.query(TABLE_STOPS, STOPS_COLUMNS, null, null, null,
				null, STOPS_KEY_NUMUSED + " DESC", "" + count);
		BusStop stop = null;
		if (cursor != null && cursor.moveToFirst()) {
			do {
				stop = new BusStop(Integer.parseInt(cursor.getString(0)),
						cursor.getString(1), Double.parseDouble(cursor
								.getString(2)), Double.parseDouble(cursor
								.getString(3)), Integer.parseInt(cursor
								.getString(4)));

				stops.add(stop);
			} while (cursor.moveToNext());
		}

		return stops;
	}

	public int updateBusStop(BusStop stop) {
		SQLiteDatabase db = this.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put(STOPS_KEY_NAME, stop.getName());
		values.put(STOPS_KEY_LATITUDE, stop.getLatitude());
		values.put(STOPS_KEY_LONGITUDE, stop.getLongitude());
		values.put(STOPS_KEY_NUMUSED, stop.getNumUsed());
		int i = db.update(TABLE_STOPS, values, STOPS_KEY_ID + " = ?",
				new String[] { String.valueOf(stop.getId()) });
		db.close();

		return i;
	}

	public void deleteBusStop(BusStop stop) {
		SQLiteDatabase db = this.getWritableDatabase();
		db.delete(TABLE_STOPS, STOPS_KEY_ID + " = ?",
				new String[] { String.valueOf(stop.getId()) });
		db.close();
	}
}