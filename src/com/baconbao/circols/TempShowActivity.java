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

import java.io.ByteArrayOutputStream;
import java.util.UUID;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import android.graphics.Bitmap.Config;  
public class TempShowActivity extends Activity {

	ImageView imageView;
	public static DB mDbHelper;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tempshow_layout);
		
		mDbHelper = new DB(this);
		mDbHelper.open();
		
		imageView = (ImageView)findViewById(R.id.tempImage);
		AppClass myTemp = (AppClass)getApplication();
		//imageView.setImageBitmap(myTemp.getBitmap());
		
		final Bitmap result = Bitmap.createBitmap(600, 600, Config.ARGB_8888);
		Canvas canvas = new Canvas(result);
		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG); 
		canvas.drawCircle(myTemp.getBitmap().getWidth()/2, myTemp.getBitmap().getHeight()/2, 300, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(myTemp.getBitmap(),0,0,paint);
		imageView.setImageBitmap(result);
		
		
		final EditText TempTxt = (EditText)findViewById(R.id.tempText);
		ImageView OkBtn = (ImageView)findViewById(R.id.tempSend);
		OkBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				
				Toast.makeText(TempShowActivity.this, "Adding data...", Toast.LENGTH_SHORT).show();
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				result.compress(Bitmap.CompressFormat.JPEG, 70, baos);
	            byte[] b = baos.toByteArray();
	            String encodedImageString = Base64.encodeToString(b, Base64.DEFAULT);
	            
	            String uniqueID = UUID.randomUUID().toString();
	            AppClass appTemp = (AppClass)getApplication();
				mDbHelper.create(encodedImageString, TempTxt.getText().toString(), appTemp.getDate(), uniqueID, appTemp.getGeoLat(), appTemp.getGeoLong());
				result.recycle();
				
				
				Intent intent_2main = new Intent();
    		    intent_2main.setClass(TempShowActivity.this, DataShowActivity.class);
    		    intent_2main.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    		    startActivity(intent_2main);
    		    finish();
				
			}
        });
		
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDbHelper != null) {
        	mDbHelper.close();
        }
    }


}
