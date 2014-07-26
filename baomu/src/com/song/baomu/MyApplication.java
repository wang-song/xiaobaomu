package com.song.baomu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.GeofenceClient;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;

import android.app.Application;

public class MyApplication extends Application {
	
	public LocationClient mLocationClient;
	public GeofenceClient mGeofenceClient;
	public MyLocationListener mMyLocationListener;
	
	
	private Map<Object,Object> listaddress = new HashMap<Object,Object>();
	
	
	public Map<Object,Object> getList(){
		return listaddress;
	}
	
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		SDKInitializer.initialize(getApplicationContext());  
		mLocationClient = new LocationClient(this.getApplicationContext());
		mMyLocationListener = new MyLocationListener();
		mLocationClient.registerLocationListener(mMyLocationListener);
		mGeofenceClient = new GeofenceClient(getApplicationContext());
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
			listaddress.put("code", location.getLocType());
			
			//纬度
			sb.append("\nlatitude : ");
			sb.append(location.getLatitude());
			listaddress.put("latitude", location.getLatitude());
			
			//经度
			sb.append("\nlongitude : ");
			sb.append(location.getLongitude());
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
	
	
	
	
	
	
	
}
