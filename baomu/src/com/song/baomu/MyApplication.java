package com.song.baomu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;

import android.app.Application;

public class MyApplication extends Application {
	
	public LocationClient mLocationClient;
	public GeofenceClient mGeofenceClient;
	public MyLocationListener mMyLocationListener;
	
	private int mcode ;
	private double mlatitude;
	private double mlongitude;
	
	//定位间隔时间
	int span = 7000;
	
	
	public Map<Object,Object> listaddress = new HashMap<Object,Object>();
	
	
	//返回定位的相信信息
	public Map<Object,Object> getList(){
		return listaddress;
	}
	
	//返回定位的经纬度坐标
	public LatLng getLocationProvider(){
		return new LatLng(mlatitude, mlongitude);
	}
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		SDKInitializer.initialize(getApplicationContext());  
		mLocationClient = new LocationClient(this.getApplicationContext());
		InitLocation();
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		mGeofenceClient = new GeofenceClient(getApplicationContext());
		
		
		// 发起定位  软件启动时就开始定位  定位结果放在listaddress中
		if (!mLocationClient.isStarted())
			mLocationClient.start();
		if (mLocationClient != null && mLocationClient.isStarted())
			mLocationClient.requestLocation();

		
	}

	/**
	 * 实现实位回调监听
	 */
	public class MyLocationListener implements BDLocationListener {
		


		@Override
		public void onReceiveLocation(BDLocation location) {

			//先清空listaddress
			listaddress.clear();
			
			StringBuffer sb = new StringBuffer(256);
			
			//时间
			sb.append("time : ");
			sb.append(location.getTime());
			listaddress.put("time", location.getTime());
			
			//返回码
			sb.append("\nerror code : ");
			sb.append(location.getLocType());
			mcode =location.getLocType();
			listaddress.put("code", location.getLocType());
			
			//纬度
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			mlatitude = location.getLatitude();
			listaddress.put("latitude", location.getLatitude());
			
			//经度
			sb.append("\nlongitude : ");
			sb.append(location.getLongitude());
			mlongitude = location.getLongitude();
			listaddress.put("longitude", location.getLongitude());
			
			//精度
			sb.append("\nradius : ");
			sb.append(location.getRadius());
			listaddress.put("radius", location.getRadius());
			
			if (location.getLocType() == BDLocation.TypeGpsLocation){
				sb.append("\nspeed : ");
				sb.append(location.getSpeed());
				sb.append("\nsatellite : ");
				sb.append(location.getSatelliteNumber());
				sb.append("\ndirection : ");
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				sb.append(location.getDirection());
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation){
				
				//详细地址
				sb.append("\naddr : ");
				sb.append(location.getAddrStr());
				listaddress.put("addr",location.getAddrStr());
				//运营商信息
				sb.append("\noperationers : ");
				sb.append(location.getOperators());
			}
			//System.out.println(sb.toString());
			System.out.println("正在定位"+location.getLocType());
		}


	}
	
	/**
	 * 定位初始化函数
	 */
	private void InitLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度，默认值gcj02
		option.setScanSpan(span);// 设置发起定位请求的间隔时间为5000ms
		option.isOpenGps();
		option.setIsNeedAddress(true);
		option.setNeedDeviceDirect(true);//返回的定位结果包含手机机头的方向
		mLocationClient.setLocOption(option);
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		mLocationClient.stop();
		super.onTerminate();
	}
	
	
	
	
	
	
	
}
