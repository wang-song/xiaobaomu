package com.song.baomu;

import java.util.HashMap;
import java.util.Map;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

/**
 * 在主界面点击listview条目跳转该界面，并显示当前位置和点击的定点位置
 * @author wangsong
 *
 */

public class MapSecletActivity extends Activity {

	private double latitude;
	private double longitude;
	private String title;

	private MapView mMapView = null;
	private LatLng lat;
	private BaiduMap mymap;
	private MarkerOptions marker;

	private Map<String, String> listaddress = new HashMap<String, String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.seclect_map_view);
		Intent intn = getIntent();
		latitude = Double.parseDouble(intn.getStringExtra("latitude"));
		longitude = Double.parseDouble(intn.getStringExtra("longitude"));
		title = intn.getStringExtra("title");
		
		System.out.println(latitude+"##"+longitude+"##"+title+"ggggggggggg");

		mMapView = (MapView) findViewById(R.id.baidumapView);

		mymap = mMapView.getMap();

		mymap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

		listaddress = ((MyApplication) getApplicationContext()).getList();
		System.out.println(listaddress);

		if (!(listaddress.equals(null) || listaddress.equals("") || listaddress
				.size() == 0)) {
			//当前位置的相信地理位置  在地图上用圆点显示
			lat = new LatLng(Double.parseDouble((listaddress.get("latitude"))),
					Double.parseDouble(listaddress.get("longitude")));
			mymap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 15.0f));
			mymap.addOverlay(new DotOptions().center(lat).color(Color.BLUE)
					.visible(true).radius(15).zIndex(15));
		}

		//定点的详细位置  在地图上用图钉显示
		marker = new MarkerOptions().position(new LatLng(latitude, longitude))
				.icon(BitmapDescriptorFactory.fromResource(R.drawable.tuding))
				.title(title);
		mymap.addOverlay(marker);
	}

}
