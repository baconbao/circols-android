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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.channels.FileChannel;

import  android.app.Application;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import  android.graphics.Bitmap;
import android.os.Environment;
import android.widget.Toast;

public  class  AppClass  extends  Application {
	
    private  Bitmap mBitmap;
    private String DateTemp;
    private String geo_lat;
    private String geo_long;    
     
    public  Bitmap getBitmap(){
         return  mBitmap;
    }

    public void clearBitmap(){
    	mBitmap.recycle();
    }
     
    public  void  setBitmap(Bitmap bitmap){
         this.mBitmap  =  bitmap;
    }
    
    public void setDate(String newdate){
    	this.DateTemp = newdate;
    }
    public String getDate(){
    	return DateTemp;
    }
    
    public void setGeo(String glat, String glong){
    	this.geo_lat = glat;
    	this.geo_long = glong;
    }
    public String getGeoLat(){
    	return geo_lat;
    }
    public String getGeoLong(){
    	return geo_long;
    }
    
    public void BackupDB() throws IOException {
    	final String inFileName = "/data/data/com.baconbao.circols/databases/CircolsByBaconBao.db";
        File dbFile = new File(inFileName);
        if (dbFile.exists()){
        	File folder = new File(Environment.getExternalStorageDirectory().toString()+"/Circols");
			folder.mkdirs();
        	FileInputStream fis = new FileInputStream(dbFile);
            String outFileName = Environment.getExternalStorageDirectory()+"/Circols/Circols_backupDB.db";
            OutputStream output = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer))>0){
                output.write(buffer, 0, length);
            }
            output.flush();
            output.close();
            fis.close();
        }        
    }
    
    public void RestoreDB() throws IOException {
    	final String inFileName = Environment.getExternalStorageDirectory()+"/Circols/Circols_backupDB.db";
        File SD_dbFile = new File(inFileName);
        if (SD_dbFile.exists()){
        	final String outFileName = "/data/data/com.baconbao.circols/databases/CircolsByBaconBao.db";
        	FileInputStream fis = new FileInputStream(SD_dbFile);            
            OutputStream output = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer))>0){
                output.write(buffer, 0, length);
            }
            output.flush();
            output.close();
            fis.close();
        }        
    }
     
}