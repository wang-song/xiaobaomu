package com.song.dingweidb;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.baidu.location.LocationClient;
import com.song.baomu.MyApplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

public class SaveDingweiService extends Service {

	// 定位信息
	private String latitude;
	private String longitude;
	private String time;
	private String address;

	private Context mcontext;
	private LocationClient mLocationClient;

	// 返回定位详细信息，存放在listaddress集合中
	private Map<String, String> listaddress = new HashMap<String, String>();

	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("储存服务开启。。。");
		mcontext = this.getBaseContext();

		mLocationClient = ((MyApplication) getApplication()).mLocationClient;

		// 发起定位
		if (!mLocationClient.isStarted())
			mLocationClient.start();
		if (mLocationClient != null && mLocationClient.isStarted())
			mLocationClient.requestLocation();

		final WeiZhiServer ws = new WeiZhiServer(mcontext);
		final java.text.DateFormat format1 = new java.text.SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");

		new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {
					// 绑定服务时，把定位信息获取到
					listaddress = ((MyApplication) getApplicationContext())
							.getList();

					try {
						Thread.sleep(60000);
					} catch (InterruptedException e) {

						e.printStackTrace();
					}

				    time  = format1.format(new Date());
					//time = listaddress.get("time");
					longitude = listaddress.get("longitude");
					latitude = listaddress.get("latitude");
					address = listaddress.get("address");
					System.out.println(time + "qqqqqqq");
					ws.add(new WeiZhi(time, longitude, latitude, address));
				}
			}
		}).start();

	}

}
