package com.song.baomu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.DotOptions;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MapActivity extends Activity implements
		OnGetGeoCoderResultListener {

	private Button map_view_queding;
	private Button map_view_qingchu;

	private String sharedname_dian = "myconfig_dian";

	MapView mMapView = null;

	private LatLng dingwei, lat;
	private BaiduMap mymap;
	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private String address = "";
	private MarkerOptions marker = null;

	private List<MarkerOptions> listoption = new ArrayList<MarkerOptions>();
	private Map<String, String> listaddress = new HashMap<String, String>();

	private Intent intent;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle("设置定点");
		setContentView(R.layout.my_map_view);
		mMapView = (MapView) findViewById(R.id.bmapView);
		map_view_queding = (Button) findViewById(R.id.map_view_queding);
		map_view_queding.setOnClickListener(mylostener);
		map_view_qingchu = (Button) findViewById(R.id.map_view_qingchu);
		map_view_qingchu.setOnClickListener(mylostener);

		mymap = mMapView.getMap();

		mymap.setMapType(BaiduMap.MAP_TYPE_NORMAL);

		mymap.setOnMapClickListener(mapclicklistener);
		mymap.setOnMarkerClickListener(markerlistener);
		mymap.setOnMapLongClickListener(maplongclicklestener);

		// 初始化搜索模块，注册事件监听
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(this);

		listaddress = ((MyApplication) getApplicationContext()).getList();
		System.out.println(listaddress);

		if (!(listaddress.equals(null) || listaddress.equals("") || listaddress
				.size() == 0)) {
			lat = new LatLng(Double.parseDouble((listaddress.get("latitude"))),
					Double.parseDouble(listaddress.get("longitude")));
			mymap.setMapStatus(MapStatusUpdateFactory.newLatLngZoom(lat, 16.0f));
			mymap.addOverlay(new DotOptions().center(lat).color(Color.BLUE)
					.visible(true).radius(15).zIndex(15));
		}

		intent = new Intent(MapActivity.this, MainActivity.class);

	}

	/**
	 * 百度地图上长时间点击事件处理
	 */
	BaiduMap.OnMapLongClickListener maplongclicklestener = new BaiduMap.OnMapLongClickListener() {

		@Override
		public void onMapLongClick(LatLng arg0) {
			// TODO Auto-generated method stub
			dingwei = arg0;
			// 反向地理编码
			mSearch.reverseGeoCode(new ReverseGeoCodeOption().location(dingwei));

		}
	};

	/**
	 * 百度地图上单击事件处理
	 */
	OnMapClickListener mapclicklistener = new OnMapClickListener() {

		@Override
		public boolean onMapPoiClick(MapPoi arg0) {
			// TODO Auto-generated method stub

			return false;
		}

		/**
		 * 在地图上的点击事件，显示详细地址，是否加入定点 加入定点后，就存放在listoption集合中
		 */
		public void onMapClick(LatLng arg0) {
			// TODO Auto-generated method stub

		}
	};

	/**
	 * 正向地理编码
	 */
	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		// TODO Auto-generated method stub

	}

	/**
	 * 反向地理编码 解码成功 则出现对话框 是 则 地图上 出现marker覆盖物 并把marker加入到listoption集合中 否则不做任何操作
	 */
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
		// TODO Auto-generated method stub
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(MapActivity.this, "抱歉，未能找到结果", Toast.LENGTH_LONG)
					.show();
		} else {
			marker = new MarkerOptions()
					.position(result.getLocation())
					.icon(BitmapDescriptorFactory
							.fromResource(R.drawable.tuding))
					.title(result.getAddress());

			address = result.getAddress();

			new AlertDialog.Builder(MapActivity.this).setTitle("确认")
					.setMessage("是否将 " + address + " 加入定点？")
					.setPositiveButton("是", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							listoption.add(marker);
							mymap.addOverlay(marker);
							// mymap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));

						}
					}).setNegativeButton("否", new OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub

						}
					}).show();

		}

	}

	/**
	 * 标签上单击触发事件
	 */
	OnMarkerClickListener markerlistener = new OnMarkerClickListener() {

		@Override
		public boolean onMarkerClick(Marker arg0) {
			// TODO Auto-generated method stub
			return false;
		}
	};

	/**
	 * 界面按钮 点击触发事件处理
	 */
	private android.view.View.OnClickListener mylostener = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {
			int size = listoption.size();
			SharedPreferences myshared = getSharedPreferences(sharedname_dian,
					Context.MODE_WORLD_READABLE);
			SharedPreferences.Editor editor = myshared.edit();

			Button btn = (Button) v;
			switch (btn.getId()) {
			case R.id.map_view_queding:

				// 点击确定，listoption集合里的数据都存到SharedPreferences中，并返回
				if (listoption.equals(null) || listoption == null || size == 0) {

					new AlertDialog.Builder(MapActivity.this).setTitle("提示")
							.setMessage("您还没有设置定点。")
							.setPositiveButton("确定", null).show();
				} else {
					// 如果listoption集合中不为空 遍历集合 并把数据储存到SharedPreferences中
					editor.clear();

					for (int i = 0; i < size; i++) {
						editor.putString("title" + i, listoption.get(i)
								.getTitle());
						editor.putString(
								"latitude" + i,
								Double.toString(listoption.get(i).getPosition().latitude));
						editor.putString(
								"longitude" + i,
								Double.toString(listoption.get(i).getPosition().longitude));
						editor.commit();

						// intent.putExtra("title" + i, listoption.get(i)
						// .getTitle());
						// intent.putExtra("latitude" + i, listoption.get(i)
						// .getPosition().latitude);
						// intent.putExtra("longitude" + i, listoption.get(i)
						// .getPosition().longitude);
					}
					// intent.putExtra("count", size);

					editor.putInt("count", size);
					editor.commit();

					new AlertDialog.Builder(MapActivity.this).setTitle("确认")
							.setMessage("定点设定完毕，是否返回？")
							.setPositiveButton("是", new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub
									setResult(0, intent);
									// 关闭掉这个Activity
									finish();
								}
							}).setNegativeButton("否", new OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									// TODO Auto-generated method stub

								}
							}).show();

				}

				break;
			case R.id.map_view_qingchu:
				// 点击清除，listoption集合黄总的数据清除
				listoption.clear();
				mymap.clear();
				editor.clear();
				editor.commit();
				break;

			}
		}
	};

	protected void onDestroy() {
		super.onDestroy();
		// 在activity执行onDestroy时执行mMapView.onDestroy()，实现地图生命周期管理
		mMapView.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 在activity执行onResume时执行mMapView. onResume ()，实现地图生命周期管理
		mMapView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		// 在activity执行onPause时执行mMapView. onPause ()，实现地图生命周期管理
		mMapView.onPause();
	}

}
