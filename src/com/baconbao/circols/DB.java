/*
 * Circols v1.0 By BaconBao (http://baconbao.blogspot.com)
 *
 * Copyright (C) 2013 BaconBao (http://baconbao.blogspot.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.baconbao.circols;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DB {
   private static final String DATABASE_NAME = "CircolsByBaconBao.db";
   private static final int DATABASE_VERSION = 1;
   
   private static final String DATABASE_TABLE = "CircolsByBaconBao";
   
   private static final String DATABASE_CREATE=    //"CREATE TABLE notes (_id INTEGER PRIMARY KEY, note TEXT NOT NULL, created INTEGER, modified INTEGER);";
   "CREATE TABLE "+DATABASE_TABLE+"(" +
   "_id INTEGER PRIMARY KEY autoincrement," +
   "photo TEXT NOT NULL, " +
   "text TEXT, " +
   "date TEXT NOT NULL, " +
   "uniqid TEXT NOT NULL, " +
   "geolat TEXT, " +
   "geolong TEXT " +
//   "created INTEGER" +
   ");";
		   
   private static class DatabaseHelper extends SQLiteOpenHelper{

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub
		db.execSQL("DROP TABLE IF EXISTS "+DATABASE_TABLE);
		onCreate(db);
	}
   }	
	//
	private Context mCtx = null;
	private DatabaseHelper dbHelper;
	private SQLiteDatabase db;
	
	//Contructor
	public DB(Context ctx){
		this.mCtx = ctx;
	}

	public DB open() throws SQLException{
		dbHelper = new DatabaseHelper(mCtx);
		db = dbHelper.getWritableDatabase();
		return this;
	}
	
	public void close(){
		dbHelper.close();
	}
	
	//CRUD
	public static final String KEY_ROWID = "_id";
	public static final String KEY_PHOTO = "photo";
	public static final String KEY_TEXT = "text";
	public static final String KEY_DATE = "date";
	public static final String KEY_UNIQID = "uniqid";
	public static final String KEY_GEOLAT = "geolat";
	public static final String KEY_GEOLONG = "geolong";
	//public static final String KEY_MODIFIED = "modified";
	
	String[] strCols = new String[]{
			KEY_ROWID,
			KEY_PHOTO
			//KEY_CREATED
	};
	
	//get All Entry
	public Cursor getAll(){
		return db.rawQuery("SELECT * FROM "+DATABASE_TABLE+" ORDER BY _id DESC", null);
		//return db.query(DATABASE_TABLE, new String[]{KEY_ROWID,KEY_NOTE,KEY_CREATED}, null, null, null, null, null);
	}

	public Cursor getAllCount(){
		return db.rawQuery("SELECT COUNT(*) FROM "+DATABASE_TABLE, null);
		//return db.query(DATABASE_TABLE, new String[]{KEY_ROWID,KEY_NOTE,KEY_CREATED}, null, null, null, null, null);
	}
	
	public boolean IdOneExists() {
		   Cursor cursor = db.rawQuery("select 1 from "+DATABASE_TABLE+" where _id=1", null);
		   boolean exists = (cursor.getCount() > 0);
		   cursor.close();
		   return exists;
	}
	
	//SearchGet Entries
	public Cursor getSearch(String txtSearch){
	   // String where = KEY_NAME+ "WHERE LIKE =?";
	    //String[] whereValue= new String[] {"%"+txtSearch+"%"};
		String sql = "SELECT * FROM "+ DATABASE_TABLE + " WHERE "+ KEY_PHOTO + " LIKE '%"+txtSearch+"%';";
		Cursor sCursor = db.rawQuery(sql, null);
		//Cursor sCursor = db.query(DATABASE_TABLE, new String[]{KEY_ROWID,KEY_NAME,KEY_EMAIL,KEY_PHONE,KEY_STREET,KEY_CITY}, where, whereValue, null, null, null);
	    //Cursor sCursor =  db.query(true, DATABASE_TABLE, new String[]{KEY_ROWID,KEY_NAME,KEY_EMAIL,KEY_PHONE,KEY_STREET,KEY_CITY}, KEY_NAME+"=?"+txtSearch+"?", null, null, null, null, null);
	    return sCursor;
	}
	
	//add new entry
	public long create(String photo, String text, String date, String uniqid, String geolat, String geolong){
		//Date now = new Date();
		ContentValues args = new ContentValues();
		args.put(KEY_PHOTO, photo);
		args.put(KEY_TEXT, text);
		args.put(KEY_DATE, date);
		args.put(KEY_UNIQID, uniqid);
		args.put(KEY_GEOLAT, geolat);
		args.put(KEY_GEOLONG, geolong);
		//args.put(KEY_CREATED, now.getTime());
		return db.insert(DATABASE_TABLE, null, args);
	}
	
	//remove an entry
	public boolean delete(long rowId){
		
		return db.delete(DATABASE_TABLE, KEY_ROWID+"="+rowId, null)>0;
	}
	
	//query a single entry
	public Cursor get(long rowId){
		//Cursor mCursor = db.query(true, DATABASE_TABLE, new String[]{KEY_ROWID,KEY_NAME,KEY_CREATED}, KEY_ROWID+"="+rowId, null, null, null, null, null);
		Cursor mCursor = db.query(true, DATABASE_TABLE, new String[]{KEY_ROWID,KEY_PHOTO,KEY_TEXT,KEY_DATE,KEY_UNIQID,KEY_GEOLAT,KEY_GEOLONG}, KEY_ROWID+"="+rowId, null, null, null, null, null);
		if (mCursor !=null){
			mCursor.moveToFirst();
		}
		return mCursor;
	}
   }
   
