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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuffXfermode;
import android.graphics.Bitmap.Config;
import android.graphics.PorterDuff.Mode;
import android.graphics.drawable.StateListDrawable;
import android.media.MediaScannerConnection;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DataShowActivity extends FragmentActivity {
	
	public static int PageCount;
	public static DB dbUtil;

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(0, 0, 0, "About Circols");
		return true;
	}

	@Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch(item.getItemId()) {
		case 0:
			Intent intent_2about = new Intent();
		    intent_2about.setClass(DataShowActivity.this, AboutPageActivity.class);	    
		    startActivity(intent_2about); 
		default: break;
		}
		return super.onMenuItemSelected(featureId, item);
    }
	
    /*
    @Override
    public boolean onKeyDown(int keycode, KeyEvent e) {
        switch(keycode) {
            case KeyEvent.KEYCODE_MENU:
            	Intent intent_2about = new Intent();
    		    intent_2about.setClass(DataShowActivity.this, AboutPageActivity.class);	    
    		    startActivity(intent_2about); 
                return true;
        }
        return super.onKeyDown(keycode, e);
    }
	*/
	
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dbUtil != null) {
        	dbUtil.close();
        }
        
	    try {
	    	AppClass myBackup = (AppClass)getApplication();
			myBackup.BackupDB();
	    } catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.datashow_layout);
        
        dbUtil = new DB(this);
        dbUtil.open();
        
        ImageView openCam = (ImageView)findViewById(R.id.openCam);
        ImageView refreshFragment = (ImageView)findViewById(R.id.refreshFragment);
        openCam.setClickable(true); 
        openCam.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
            	Toast.makeText(DataShowActivity.this, "Loding Camera...", Toast.LENGTH_SHORT).show();
            	Intent intent_2cam = new Intent();
    		    intent_2cam.setClass(DataShowActivity.this, CameraActivity.class);	    
    		    startActivity(intent_2cam);  
    		    //finish();    		    
            }
        });
        refreshFragment.setClickable(true);
        refreshFragment.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View vx) {
            	//addFragmentToStack(FragmentParent.newInstance(0));
            	Cursor mNoteCursor;
        		mNoteCursor = dbUtil.getAllCount();
        		startManagingCursor(mNoteCursor);
        		mNoteCursor.moveToFirst();
        		PageCount = Integer.valueOf(mNoteCursor.getString(0));
        		
            	FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.replace(R.id.fragment_container, FragmentParent.newInstance(0));
                ft.commit();
            }
        });
        //findViewById(R.id.openCam).performClick();
        addFragmentToStack(FragmentParent.newInstance(0));
        
        Cursor mNoteCursor;
		mNoteCursor = dbUtil.getAllCount();
		startManagingCursor(mNoteCursor);
		mNoteCursor.moveToFirst();
		PageCount = Integer.valueOf(mNoteCursor.getString(0));
    	final String inFileName = Environment.getExternalStorageDirectory()+"/Circols/";
        File SD_dbFile = new File(inFileName);
		if(PageCount==0 && !dbUtil.IdOneExists() && SD_dbFile.exists()){
        	try {
        		AppClass myRestore = (AppClass)getApplication();
    			myRestore.RestoreDB();
    			Toast.makeText(this, "Auto import the backup data on the device.", Toast.LENGTH_LONG).show();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
        	findViewById(R.id.refreshFragment).performClick();
		}		
    }
    
    private void addFragmentToStack(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, fragment);
        ft.commit();
    }

    public final static class FragmentParent extends Fragment {

        public static final FragmentParent newInstance(int position) {
            FragmentParent f = new FragmentParent();
            Bundle args = new Bundle(2);
            args.putInt("position", position);
            f.setArguments(args);
            return f;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View convertView = inflater.inflate(R.layout.viewpager_fragments, container, false);
            ViewPager pager = (ViewPager) convertView.findViewById(R.id.pager);

        	if(PageCount==0){
        		LinearLayout vg = new LinearLayout(getActivity());
        		vg.setOrientation(LinearLayout.VERTICAL);
        		vg.setGravity(Gravity.CENTER);
        		ImageView imghome = new ImageView(getActivity());
        		ImageView imgletgo = new ImageView(getActivity());
        		imghome.setLayoutParams(new ViewGroup.LayoutParams(getActivity().getWindowManager().getDefaultDisplay().getWidth()-50, getActivity().getWindowManager().getDefaultDisplay().getWidth()-50));
        		imgletgo.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        		imghome.setImageResource(R.drawable.circols_logo_home);
        		imgletgo.setImageResource(R.drawable.circols_letsgo_home);
        		vg.addView(imghome);
        		vg.addView(imgletgo);
        		return vg;
        	}
            
            final int parent_position = getArguments().getInt("position");
            pager.setAdapter(new FragmentStatePagerAdapter(getChildFragmentManager()) {
            	
                @Override
                public Fragment getItem(final int position) {
                    return new Fragment() {                   	
                    	
                        @Override
                        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
                        	
                        	LinearLayout vg = new LinearLayout(getActivity());
                        	vg.setOrientation(LinearLayout.VERTICAL);
                        	vg.setBackgroundResource(R.drawable.background);
                        	LinearLayout hg = new LinearLayout(getActivity());
                        	hg.setOrientation(LinearLayout.HORIZONTAL);
                        	hg.setGravity(Gravity.CENTER);
                        	final ImageView imgv = new ImageView(getActivity());
                        	TextView hr = new TextView(getActivity());
                        	TextView txtv = new TextView(getActivity());
                        	TextView datev = new TextView(getActivity());
                        	ImageView delv = new ImageView(getActivity());
                        	ImageView savev = new ImageView(getActivity());
                        	ImageView mapv = new ImageView(getActivity());
                        	imgv.setLayoutParams(new ViewGroup.LayoutParams(getActivity().getWindowManager().getDefaultDisplay().getWidth()-50, getActivity().getWindowManager().getDefaultDisplay().getWidth()-50));
                        	hr.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        	txtv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        	datev.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                        	
                        	//int btnicon_w = (int)convertPixelsToDp(60, getActivity());
                        	//int btnicon_h = (int)convertPixelsToDp(48, getActivity());
                        	int btnicon_w = (int)convertDpToPixel(40, getActivity());
                        	int btnicon_h = (int)convertDpToPixel(32, getActivity());
                        	delv.setLayoutParams(new ViewGroup.LayoutParams(btnicon_w, btnicon_h));
                        	savev.setLayoutParams(new ViewGroup.LayoutParams(btnicon_w, btnicon_h));
                        	mapv.setLayoutParams(new ViewGroup.LayoutParams(btnicon_w, btnicon_h));
                        	
                        	StateListDrawable savev_states = new StateListDrawable();
                        	savev_states.addState(new int[] {android.R.attr.state_pressed},
                        	    getResources().getDrawable(R.drawable.icon_save_click));
                        	savev_states.addState(new int[] { },
                        	    getResources().getDrawable(R.drawable.icon_save));
                        	
                        	StateListDrawable mapv_states = new StateListDrawable();
                        	mapv_states.addState(new int[] {android.R.attr.state_pressed},
                        	    getResources().getDrawable(R.drawable.icon_map_click));
                        	mapv_states.addState(new int[] { },
                        	    getResources().getDrawable(R.drawable.icon_map));
                        	
                        	StateListDrawable delv_states = new StateListDrawable();
                        	delv_states.addState(new int[] {android.R.attr.state_pressed},
                        	    getResources().getDrawable(R.drawable.icon_delete_click));
                        	delv_states.addState(new int[] { },
                        	    getResources().getDrawable(R.drawable.icon_delete));
                        	
                        	vg.setGravity(Gravity.CENTER);
                        	txtv.setGravity(Gravity.CENTER);
                        	datev.setGravity(Gravity.CENTER);
                        	hr.setGravity(Gravity.CENTER);
                        	txtv.setTextSize(18);
                        	datev.setTextSize(11);
                        	hr.setTextSize(25);
                        	txtv.setTextColor(Color.BLACK);
                        	datev.setTextColor(Color.BLACK);
                            
                            Cursor mCursor;

                            //mCursor = dbUtil.get(PageCount-position);
                            mCursor = dbUtil.getAll();
                            getActivity().startManagingCursor(mCursor);
                            mCursor.moveToPosition(position);                    
                            byte[] bytarray = Base64.decode(mCursor.getString(1), Base64.DEFAULT);
                            final Bitmap sql_bmp = BitmapFactory.decodeByteArray(bytarray, 0, bytarray.length);
                            
                            final Bitmap circleNewBmp = Bitmap.createBitmap(600, 600, Config.ARGB_8888);
                            Canvas canvas = new Canvas(circleNewBmp);
                    		Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG); 
                    		canvas.drawCircle(sql_bmp.getWidth()/2, sql_bmp.getHeight()/2, 300, paint);
                    		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
                    		canvas.drawBitmap(sql_bmp,0,0,paint);
                    		
                            imgv.setImageBitmap(circleNewBmp);                            
                            txtv.setText(mCursor.getString(2));
                            datev.setText(mCursor.getString(3));
                            delv.setBackgroundDrawable(delv_states);
                            delv.setClickable(true);
                            savev.setBackgroundDrawable(savev_states);
                            savev.setClickable(true);
                            mapv.setBackgroundDrawable(mapv_states);
                            mapv.setClickable(true);
                            final String glat = mCursor.getString(5);
                            final String glong = mCursor.getString(6);
                            
                            final int imgid = Integer.valueOf(mCursor.getString(0));
                            delv.setOnClickListener(new OnClickListener(){
								@Override
								public void onClick(View arg0) {
									//Toast.makeText(getActivity(), String.valueOf(imgid), Toast.LENGTH_SHORT).show();
									new AlertDialog.Builder(getActivity())
								    .setTitle("Delete")
								    .setMessage("Are you sure you want to delete this photo data?")
								    .setPositiveButton("YES!", new DialogInterface.OnClickListener() {
								        public void onClick(DialogInterface dialog, int which) { 
				                            dbUtil.delete(imgid);
				                            getActivity().findViewById(R.id.refreshFragment).performClick();
								        }
								     })
								    .setNegativeButton("No", new DialogInterface.OnClickListener() {
								        public void onClick(DialogInterface dialog, int which) { 
								            // do nothing
								        }
								     })
								     .show();
								}
                            });
                            savev.setOnClickListener(new OnClickListener(){
								@Override
								public void onClick(View arg0) {
									
									final CharSequence[] saveimgbgitems = {"Transparent","Black","White"};
									new AlertDialog.Builder(getActivity())
									.setTitle("Which backgroung style you want to save?")
									.setSingleChoiceItems(saveimgbgitems, -1, new DialogInterface.OnClickListener() {
										public void onClick(DialogInterface dialog, int item) {
											Bitmap saveNewBmp = null;
											String namefix = "";
											if(item==1||item==2){
												saveNewBmp = Bitmap.createBitmap(600, 600, Config.ARGB_8888);
												Canvas canvas = new Canvas(saveNewBmp);
												Paint paint = new Paint();
												if(item==1){
													paint.setColor(Color.BLACK);
													namefix = "_black";
												}
												if(item==2){
													paint.setColor(Color.WHITE);
													namefix = "_white";
												}
												canvas.drawRect(0,0, 600, 600,paint);
												canvas.drawBitmap(circleNewBmp,0,0,paint);
											}
											if(item==0){
												saveNewBmp = circleNewBmp;
												namefix = "_trans";
											}
											dialog.dismiss();
											//Toast.makeText(getActivity(), "Saving Photo...", Toast.LENGTH_SHORT).show();									
											if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) { 
											
												File folder = new File(Environment.getExternalStorageDirectory().toString()+"/Circols");
												folder.mkdirs();
												String img_save_path = folder.toString();
												OutputStream fOut = null;
												SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
												String currentDateandTime = sdf.format(new Date());
												File file = new File(img_save_path, currentDateandTime + namefix + ".png");
												try {
													fOut = new FileOutputStream(file);
													saveNewBmp.compress(Bitmap.CompressFormat.PNG, 100, fOut);										
													fOut.flush();
													fOut.close();
													Toast.makeText(getActivity(), "SAVE DONE!\npath: \""+img_save_path+"/"+currentDateandTime + namefix + ".png\"", Toast.LENGTH_LONG).show();
													MediaScannerConnection.scanFile(getActivity(), new String[] { file.toString() }, null,
													new MediaScannerConnection.OnScanCompletedListener() {
													@Override
													public void onScanCompleted(String path, Uri uri) {
													          Log.i("ExternalStorage", "Scanned " + path + ":");
													          Log.i("ExternalStorage", "-> uri=" + uri);
													      }
													 });		
												}
												catch (FileNotFoundException e) {
													e.printStackTrace();
													Toast.makeText(getActivity(), "ERROR![code:1]", Toast.LENGTH_LONG).show();
												} catch (IOException e) {
													e.printStackTrace();
													Toast.makeText(getActivity(), "ERROR![code:2]", Toast.LENGTH_LONG).show();
												}
											}else{
												Toast.makeText(getActivity(), "Error, no SD Card!", Toast.LENGTH_LONG).show();
											}
										}
									})
									.show();
								}
                            });
                            mapv.setOnClickListener(new OnClickListener(){
								@Override
								public void onClick(View arg0) {									
									ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
									    NetworkInfo netInfo = cm.getActiveNetworkInfo();
									if (netInfo != null && netInfo.isConnectedOrConnecting()) {
										//Toast.makeText(getActivity(), glat+", "+glong, Toast.LENGTH_LONG).show();
										Intent intent_2mapweb = new Intent();
						    		    intent_2mapweb.setClass(getActivity(), MapWebActivity.class);
						    		    intent_2mapweb.putExtra("ScreenData_GeoLat", glat);
						    		    intent_2mapweb.putExtra("ScreenData_GeoLong", glong);
						    		    startActivity(intent_2mapweb);
									}else{
										Toast.makeText(getActivity(), "Need to connect to internet.", Toast.LENGTH_LONG).show();
									}									
								}                            	
                            });
                            
                            
                            hg.addView(savev);
                            if(!glat.equals("0")&&!glong.equals("0")) hg.addView(mapv);
                            hg.addView(delv);
                        	vg.addView(imgv);
                        	vg.addView(hr);
                        	vg.addView(txtv);
                        	vg.addView(datev);
                        	vg.addView(hg);
                        	
                            return vg;
                        }
                    };
                }

                @Override
                public int getCount() {
                	int p = PageCount;
                    return p;
                }

            });
            
            return convertView;
        }
    }
    
    public static float convertPixelsToDp(float px, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float dp = px / (metrics.densityDpi / 160f);
        return dp;
    }
    
    public static float convertDpToPixel(float dp, Context context){
        Resources resources = context.getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        float px = dp * (metrics.densityDpi / 160f);
        return px;
    }
}
