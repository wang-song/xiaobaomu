package com.song.moredian;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

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
import com.song.baomu.MyApplication;
import com.song.baomu.R;
import com.song.dingweidb.DingDian;
import com.song.dingweidb.DingDianServer;

public class MoreDianActivity extends Activity implements
		OnGetGeoCoderResultListener {

	private Button map_view_queding;
	private Button map_view_qingchu;
	private Button map_view_baocun;

	private String sharedname_dian = "myconfig_dian";
	private String title = "";

	MapView mMapView = null;

	private LatLng dingwei, lat;
	private BaiduMap mymap;
	GeoCoder mSearch = null; // 搜索模块，也可去掉地图模块独立使用
	private String address = "";
	private MarkerOptions marker = null;

	private List<MarkerOptions> listoption = new ArrayList<MarkerOptions>();
	private Map<String, String> listaddress = new HashMap<String, String>();
	private DingDianServer dingdianserver = null;

	private Intent intent;
	private int time = 0;
	private boolean timeflag = true;
	private EditText xiedizhi;
	private EditText map_view_edittext;
	AlertDialog.Builder builder;

	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setTitle("多路线设置");
		setContentView(R.layout.my_map_view2);
		dingdianserver = new DingDianServer(getBaseContext());
		mMapView = (MapView) findViewById(R.id.bmapView);
		map_view_queding = (Button) findViewById(R.id.map_view_queding);
		map_view_queding.setOnClickListener(mylostener);
		map_view_qingchu = (Button) findViewById(R.id.map_view_qingchu);
		map_view_qingchu.setOnClickListener(mylostener);
		map_view_baocun = (Button) findViewById(R.id.map_view_baocun);
		map_view_baocun.setOnClickListener(mylostener);
		map_view_edittext = (EditText) findViewById(R.id.map_view_edittext);

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

		intent = new Intent(MoreDianActivity.this, MoreDianActivity.class);
		
		Toast.makeText(MoreDianActivity.this,
				"请在文本框中输入你的路线名称，不能重复，如果重复，就会覆盖以前的路线", 1).show();

	}

	/**
	 * 百度地图上长时间点击事件处理
	 */
	BaiduMap.OnMapLongClickListener maplongclicklestener = new BaiduMap.OnMapLongClickListener() {

		@Override
		public void onMapLongClick(LatLng arg0) {

			dingwei = arg0;

			if (panNetwork()) {
				// 反向地理编码
				mSearch.reverseGeoCode(new ReverseGeoCodeOption()
						.location(dingwei));
			} else {

				builder = new AlertDialog.Builder(MoreDianActivity.this);
				xiedizhi = new EditText(MoreDianActivity.this);
				builder.setTitle("提示");
				builder.setMessage("没有联网，请输入您自定义的地址：");
				builder.setView(xiedizhi);
				builder.setPositiveButton("确定", new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {

						marker = new MarkerOptions()
								.position(dingwei)
								.icon(BitmapDescriptorFactory
										.fromResource(R.drawable.tuding))
								.title(xiedizhi.getText().toString().trim());

						listoption.add(marker);
						mymap.addOverlay(marker);
						// mymap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));

					}

				});
				builder.setNegativeButton("取消", null);
				builder.create();
				builder.show();

			}
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
		if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR
				|| result.getAddress().equals("")
				|| result.getAddress().equals(null)) {

			Toast.makeText(MoreDianActivity.this, "抱歉，未能找到结果", 0).show();

			builder = new AlertDialog.Builder(MoreDianActivity.this);
			xiedizhi = new EditText(MoreDianActivity.this);
			builder.setTitle("提示");
			builder.setMessage("没有结果，请输入您自定义的地址：");
			builder.setView(xiedizhi);
			builder.setPositiveButton("确定", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {

					marker = new MarkerOptions()
							.position(dingwei)
							.icon(BitmapDescriptorFactory
									.fromResource(R.drawable.tuding))
							.title(xiedizhi.getText().toString().trim());

					listoption.add(marker);
					mymap.addOverlay(marker);
					// mymap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));

				}

			});
			builder.setNegativeButton("取消", null);
			builder.create();
			builder.show();

		} else {
			marker = new MarkerOptions().position(result.getLocation()).icon(
					BitmapDescriptorFactory.fromResource(R.drawable.tuding));

			address = result.getAddress();

			builder = new AlertDialog.Builder(MoreDianActivity.this);
			xiedizhi = new EditText(MoreDianActivity.this);
			xiedizhi.setText(address);
			builder.setTitle("确认");
			builder.setMessage("是否将 " + address + " 加入定点？(在下面输入框中可以修改)");
			builder.setView(xiedizhi);
			builder.setPositiveButton("确定", new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					marker.title(xiedizhi.getText().toString().trim());
					listoption.add(marker);
					mymap.addOverlay(marker);
					// mymap.setMapStatus(MapStatusUpdateFactory.newLatLng(result.getLocation()));

				}
			});
			builder.setNegativeButton("取消", null);
			builder.create();
			builder.show();

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
				finish();
				break;
			case R.id.map_view_qingchu:
				// 点击清除，listoption集合黄总的数据清除
				listoption.clear();
				mymap.clear();
				editor.clear();
				editor.commit();
				mymap.addOverlay(new DotOptions().center(lat).color(Color.BLUE)
						.visible(true).radius(15).zIndex(15));
				break;

			case R.id.map_view_baocun:
				// 点击保存路线按钮，把路线保存到数据库中，附上名字

				if (listoption.equals(null) || listoption == null || size == 0) {

					new AlertDialog.Builder(MoreDianActivity.this)
							.setTitle("提示").setMessage("您还没有设置定点。")
							.setPositiveButton("确定", null).show();
				} else {
					// 设置路线名称
					String edittext = map_view_edittext.getText().toString()
							.trim();
					if (edittext.length() != 0) {
						
						dingdianserver.delete(edittext);
						
						for (int i = 0; i < size; i++) {
							MarkerOptions m = listoption.get(i);
							dingdianserver.add(new DingDian(edittext, Double
									.toString(m.getPosition().longitude),
									Double.toString(m.getPosition().latitude),
									m.getTitle()));
						}
						
						listoption.clear();
						mymap.clear();
						mymap.addOverlay(new DotOptions().center(lat).color(Color.BLUE)
								.visible(true).radius(15).zIndex(15));
						map_view_edittext.setText("");
						Toast.makeText(MoreDianActivity.this,
								"保存路线"+edittext+"成功，可以继续保存。如果退出，请点击确定返回", 1).show();
					} else {
						Toast.makeText(MoreDianActivity.this,
								"请输入你该条路线的名称 ", 1).show();
					}

				}

				break;

			}
		}
	};

	// 判断手机是否联网
	public boolean panNetwork() {
		ConnectivityManager cManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = cManager.getActiveNetworkInfo();
		if (info != null && info.isAvailable()) {
			// 能联网
			return true;
		} else {
			// 不能联网
			return false;
		}
	}

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

	/*
	 * 
	 * // 点击确定，listoption集合里的数据都存到SharedPreferences中，并返回 if
	 * (listoption.equals(null) || listoption == null || size == 0) {
	 * 
	 * new AlertDialog.Builder(MoreDianActivity.this).setTitle("提示")
	 * .setMessage("您还没有设置定点。") .setPositiveButton("确定", null).show(); } else {
	 * // 如果listoption集合中不为空 遍历集合 并把数据储存到SharedPreferences中 editor.clear();
	 * 
	 * for (int i = 0; i < size; i++) { editor.putString("title" + i,
	 * listoption.get(i) .getTitle()); editor.putString( "latitude" + i,
	 * Double.toString(listoption.get(i).getPosition().latitude));
	 * editor.putString( "longitude" + i,
	 * Double.toString(listoption.get(i).getPosition().longitude));
	 * editor.commit();
	 * 
	 * // intent.putExtra("title" + i, listoption.get(i) // .getTitle()); //
	 * intent.putExtra("latitude" + i, listoption.get(i) //
	 * .getPosition().latitude); // intent.putExtra("longitude" + i,
	 * listoption.get(i) // .getPosition().longitude); } //
	 * intent.putExtra("count", size);
	 * 
	 * editor.putInt("count", size); editor.commit();
	 * 
	 * new AlertDialog.Builder(MoreDianActivity.this).setTitle("确认")
	 * .setMessage("定点设定完毕，是否返回？") .setPositiveButton("是", new OnClickListener()
	 * {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { //
	 * TODO Auto-generated method stub setResult(0, intent); // 关闭掉这个Activity
	 * finish(); } }).setNegativeButton("否", new OnClickListener() {
	 * 
	 * @Override public void onClick(DialogInterface dialog, int which) { //
	 * TODO Auto-generated method stub
	 * 
	 * } }).show(); }
	 */

}
