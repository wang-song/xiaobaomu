package com.song.smsdatabase;

import java.util.HashMap;
import java.util.Map;

import com.baidu.location.LocationClient;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.share.LocationShareURLOption;
import com.baidu.mapapi.search.share.OnGetShareUrlResultListener;
import com.baidu.mapapi.search.share.ShareUrlResult;
import com.baidu.mapapi.search.share.ShareUrlSearch;
import com.song.baomu.MyApplication;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;

public class DingweiService extends Service {

	private LocationClient mLocationClient;
	// 把定位点信息 每隔10秒写入文件中
	private String sharedname_dingwei = "myconfig_dingwei";
	private String shareurl = "";

	// 判断距离的线程标志
	public boolean xianchengflag = true;
	private LatLng dingwei;

	// 地址分享组件
	private ShareUrlSearch mShareUrlSearch = null;
	private ShareUrlListener sharelistener = new ShareUrlListener();

	double juli;
	double julimin = 0;

	// 返回定位详细信息，存放在listaddress集合中
	private Map<String, String> listaddress = new HashMap<String, String>();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	/**
	 * 服务创建是开始定位，并计算处理
	 */
	@Override
	public void onCreate() {
		super.onCreate();
		System.out.println("定位监听服务开启");
		mLocationClient = ((MyApplication) getApplication()).mLocationClient;

		// 发起定位
		if (!mLocationClient.isStarted())
			mLocationClient.start();
		if (mLocationClient != null && mLocationClient.isStarted())
			mLocationClient.requestLocation();

		mShareUrlSearch = ShareUrlSearch.newInstance();
		mShareUrlSearch.setOnGetShareUrlResultListener(sharelistener);

		new Thread(new Runnable() {
			public void run() {
				// 定位信息
				double mylatitude = 0f;
				double mylongitude = 0f;
				while (xianchengflag) {
					try {
						Thread.sleep(30000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					// 绑定服务时，把定位信息获取到
					listaddress = ((MyApplication) getApplicationContext())
							.getList();
					if (listaddress.size() >= 4) {
						System.out.println(listaddress);
						mylatitude = Double.parseDouble(listaddress
								.get("latitude"));
						mylongitude = Double.parseDouble(listaddress
								.get("longitude"));
						dingwei = new LatLng(mylatitude, mylongitude);

						// 生成定位地址的分享URL
						mShareUrlSearch
								.requestLocationShareUrl(new LocationShareURLOption()
										.location(dingwei)
										.snippet("你所查看对象现在的位置").name("他的位置"));
						try {
							Thread.sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

						SharedPreferences mysharedxie_jingdu1 = getSharedPreferences(
								sharedname_dingwei,
								Context.MODE_WORLD_WRITEABLE);
						Editor editor2 = mysharedxie_jingdu1.edit();
						editor2.putString("latitude",
								listaddress.get("latitude") + "");
						editor2.putString("longitude",
								listaddress.get("longitude") + "");
						editor2.putString("shareurl", shareurl);
						editor2.commit();

						// 测试
						System.out.println("定位线程   正在运行");
					}
				}
			}
		}).start();

	}

	/**
	 * 地址分享监听类
	 */
	private class ShareUrlListener implements OnGetShareUrlResultListener {

		@Override
		public void onGetLocationShareUrlResult(ShareUrlResult result) {
			shareurl = result.getUrl();
		}

		@Override
		public void onGetPoiDetailShareUrlResult(ShareUrlResult result) {

		}

	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		xianchengflag = false;
		super.onDestroy();
	}

}
