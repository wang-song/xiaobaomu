package com.song.baomu;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.share.LocationShareURLOption;
import com.baidu.mapapi.search.share.OnGetShareUrlResultListener;
import com.baidu.mapapi.search.share.ShareUrlResult;
import com.baidu.mapapi.search.share.ShareUrlSearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class TextActivity extends Activity {

	private Button textbutton;

	private ShareUrlSearch mShareUrlSearch = null;
	private LatLng mPoint = new LatLng(40.056878, 116.308141);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.text_layout);

		mShareUrlSearch = ShareUrlSearch.newInstance();
		mShareUrlSearch
				.setOnGetShareUrlResultListener(new OnGetShareUrlResultListener() {

					@Override
					public void onGetPoiDetailShareUrlResult(ShareUrlResult arg0) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onGetLocationShareUrlResult(ShareUrlResult result) {
						// 分享短串结果
						Intent it = new Intent(Intent.ACTION_SEND);
						it.putExtra(Intent.EXTRA_TEXT, "您的朋友通过百度地图SDK与您分享一个位置: " 
								+ " -- " + result.getUrl());
						it.setType("text/plain");
						startActivity(Intent.createChooser(it, "将短串分享到"));

					}
				});

		textbutton = (Button) findViewById(R.id.text_button);
		textbutton.setOnClickListener(new OnClickListener() {

			public void onClick(View v) {
				// TODO Auto-generated method stub

				mShareUrlSearch
						.requestLocationShareUrl(new LocationShareURLOption()
								.location(mPoint).snippet("111").name("name"));

			}
		});
	}
}
