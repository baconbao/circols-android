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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.Size;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

public class CameraActivity extends Activity implements SurfaceHolder.Callback{
	
	SurfaceHolder surfaceHolder;
	SurfaceView surfaceView;
	Camera camera;
	ImageView holepaper;
	 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera_layout);
		
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		
		surfaceView = (SurfaceView)findViewById(R.id.surfaceView1);
		holepaper = (ImageView)findViewById(R.id.HolePaper);
		surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolder.addCallback(this);
        
        
        Bitmap holeBmp = Bitmap.createBitmap(getWindowManager().getDefaultDisplay().getWidth(), getWindowManager().getDefaultDisplay().getHeight(), Config.ARGB_8888);
        Canvas holeCanvas = new Canvas(holeBmp);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        holeCanvas.drawCircle(holeBmp.getWidth()/2, holeBmp.getHeight()/2, holeBmp.getWidth()/2, paint);
        //paint.setColor(Color.rgb(245, 247, 245));
        paint.setXfermode(new PorterDuffXfermode(Mode.XOR));
        holeCanvas.drawRect(0, 0, holeBmp.getWidth(), holeBmp.getHeight(), paint);
        holepaper.setImageBitmap(holeBmp);
        
        
        surfaceView.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				Toast.makeText(CameraActivity.this, "Taking picture...", Toast.LENGTH_SHORT).show();
				camera.autoFocus(afcb);
			}
        });
        
	}

	PictureCallback jpeg = new PictureCallback(){  
		public void onPictureTaken(byte[] data, Camera camera) {    
			Bitmap orig_bmp = BitmapFactory.decodeByteArray(data, 0, data.length);
			Matrix vMatrix = new Matrix();
			vMatrix.setRotate(90);
			Bitmap dstBmp;
			if (orig_bmp.getWidth() >= orig_bmp.getHeight()){
				int fix = 200;
				if(orig_bmp.getHeight()-(fix*2)<0){
					fix = 0;
				}
		    	dstBmp = Bitmap.createBitmap(
			         orig_bmp, 
			         (orig_bmp.getWidth()/2 - orig_bmp.getHeight()/2)+fix,
			         0+fix,
			         orig_bmp.getHeight()-(fix*2), 
			         orig_bmp.getHeight()-(fix*2),
			         vMatrix,
			         true
		         );
		    }else{
				int fix = 200;
				if(orig_bmp.getWidth()-(fix*2)<0){
					fix = 0;
				}
		    	dstBmp = Bitmap.createBitmap(
			         orig_bmp,
			         0+fix, 
			         (orig_bmp.getHeight()/2 - orig_bmp.getWidth()/2)+fix,
			         orig_bmp.getWidth()-(fix*2),
			         orig_bmp.getWidth()-(fix*2),
			         vMatrix,
			         true
		         );
		    }

		    Bitmap scale_bmp = Bitmap.createScaledBitmap(dstBmp, 600, 600, true);
		        
		    Intent intent_2temp = new Intent();
		    intent_2temp.setClass(CameraActivity.this, TempShowActivity.class);
		    
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			String currentDateandTime = sdf.format(new Date());
		    
		    AppClass myTemp = (AppClass)getApplication();
		    myTemp.setBitmap(scale_bmp);
		    myTemp.setDate(currentDateandTime);
		    
		    LocationManager locMan = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
	        Criteria criteria = new Criteria();
	        String bestProvider = locMan.getBestProvider(criteria,true);
	        Location loc = null;
	        if(bestProvider!=null){
	        	loc = locMan.getLastKnownLocation(bestProvider);
	        }	        
		    if(loc==null||loc.getLatitude()==Double.NaN||loc.getLongitude()==Double.NaN){
		    	myTemp.setGeo("0","0");
		    }else{
		    	myTemp.setGeo(String.valueOf(loc.getLatitude()), String.valueOf(loc.getLongitude()));	
		    }       
		    
		    startActivity(intent_2temp);
		    finish();
		    
			//camera.startPreview();
		    camera.stopPreview();		
		}
	};
	
	 AutoFocusCallback afcb= new AutoFocusCallback(){	  
		 public void onAutoFocus(boolean success, Camera camera) {	    
			   if(success){
				   camera.takePicture(null, null, jpeg);
			   }else{
				   //Toast.makeText(CameraActivity.this, "Can not focus... Please try again.", Toast.LENGTH_SHORT).show();
				   camera.takePicture(null, null, jpeg);
			   }
		 }	  
	 };	

	@Override
	public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {	
	}

	@Override
	public void surfaceCreated(SurfaceHolder arg0) {
		  camera=Camera.open();
		  try {		  
			   Camera.Parameters parameters=camera.getParameters();
			   List<Size> allPictureSize = parameters.getSupportedPictureSizes();
	           List<Size> allPreviewSize = parameters.getSupportedPreviewSizes();
	           Size thePictureSize = null;
	           Size thePreviewSize = null;
	           if((allPictureSize.get(0).width * allPictureSize.get(0).height) < (allPictureSize.get(1).width * allPictureSize.get(1).height)){
	        	   thePictureSize = allPictureSize.get(allPictureSize.size()-1);
	        	   thePreviewSize = allPreviewSize.get(allPreviewSize.size()-1);
	           }else{
	        	   thePictureSize = allPictureSize.get(0);
	        	   thePreviewSize = allPreviewSize.get(0);	        	   
	           }
			   parameters.setPreviewSize(thePreviewSize.width, thePreviewSize.height);
			   parameters.setPictureSize(thePictureSize.width, thePictureSize.height);
			   Log.d("getDefaultParametersSetting", "Preview: "+String.valueOf(parameters.getPreviewSize().width)+", "+String.valueOf(parameters.getPreviewSize().height));
			   Log.d("getDefaultParametersSetting", "Picture: "+String.valueOf(parameters.getPictureSize().width)+", "+String.valueOf(parameters.getPictureSize().height));
			   parameters.setPictureFormat(PixelFormat.JPEG);
			   camera.setParameters(parameters);
			   camera.setPreviewDisplay(surfaceHolder);
			   camera.setDisplayOrientation(90);
			   camera.startPreview();
		  } catch (IOException e) {		    
			  e.printStackTrace();
		  }
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder arg0) {
		System.out.println("surfaceDestroyed");
		camera.stopPreview();
		camera.release();
	}

}
