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

import android.os.Bundle;
import android.app.Activity;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AboutPageActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.about_page_layout);
		
		LinearLayout vg = new LinearLayout(this);
		vg.setOrientation(LinearLayout.VERTICAL);
		vg.setGravity(Gravity.CENTER);
		
		ImageView imghome = new ImageView(this);
		imghome.setLayoutParams(new ViewGroup.LayoutParams(this.getWindowManager().getDefaultDisplay().getWidth()-50, this.getWindowManager().getDefaultDisplay().getWidth()-50));
		imghome.setImageResource(R.drawable.circols_logo_home);

		TextView titleText = new TextView(this);
		titleText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		titleText.setText("About Circols");
		titleText.setTextSize(23);
		titleText.setGravity(Gravity.CENTER);
		
		TextView aboutText = new TextView(this);
		aboutText.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
		aboutText.setText(Html.fromHtml("version: 1.0<br /><br />Author:<br /><b>BaconBao</b><br /><a href='http://baconbao.blogspot.com'><small>http://baconbao.blogspot.com</small></a>"));
		aboutText.setClickable(true);
		aboutText.setMovementMethod (LinkMovementMethod.getInstance());
		aboutText.setGravity(Gravity.CENTER);
		
		vg.addView(titleText);
		vg.addView(imghome);
		vg.addView(aboutText);
		this.setContentView(vg);
	}

}
