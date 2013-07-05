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

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;

import org.apache.http.util.EncodingUtils;

import android.os.Bundle;
import android.app.Activity;
import android.app.ProgressDialog;
import android.webkit.WebView;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebViewClient;

public class MapWebActivity extends Activity {
	
	ProgressDialog dialog = null;  
	
    public void loadurl(final WebView view,final String url){
        new Thread(){
            public void run(){
                view.loadUrl(url);
            }
        }.start();
    }
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.mapweb_layout);
		
		dialog = ProgressDialog.show(MapWebActivity.this,null,"Loading...");
		
		String GeoLat_str = getIntent().getExtras().getString("ScreenData_GeoLat");
		String GeoLong_str = getIntent().getExtras().getString("ScreenData_GeoLong");
		
		WebView wv = (WebView) findViewById(R.id.MapPageWebView);
		wv.getSettings().setSupportZoom( false );
		wv.getSettings().setRenderPriority( RenderPriority.HIGH );
		wv.getSettings().setLightTouchEnabled( true );
		wv.getSettings().setJavaScriptEnabled(true);
		
		 wv.setWebViewClient(new WebViewClient(){
			 
	          public boolean shouldOverrideUrlLoading(final WebView view, final String url) {
	        	  loadurl(view, url);
	              return true;
	          }          
	          
              @Override  
              public void onPageFinished(WebView view,String url){
            	  	URI uri = null;
					try {
						uri = new URI(url);
					} catch (URISyntaxException e) {
						e.printStackTrace();
					}
					String domain = uri.getHost();
					if(domain.startsWith("www."))
						domain = domain.substring(4);
            	  
            	  if(domain.equals("maps.google.com")){
                      dialog.dismiss();             		  
            	  } 
              }  
	    });
		
		String postDataFix;
		try {
			String fix0 = URLEncoder.encode(GeoLat_str, "UTF-8");
			String fix1 = URLEncoder.encode(GeoLong_str, "UTF-8");
			postDataFix = fix0+","+fix1;
			wv.loadUrl("http://maps.google.com/maps?z=12&t=m&q="+postDataFix);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	}

}
