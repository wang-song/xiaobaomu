package com.song.baomu;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.share.LocationShareURLOption;
import com.baidu.mapapi.search.share.ShareUrlResult;
import com.baidu.mapapi.search.share.ShareUrlSearch;
import com.baidu.mapapi.utils.DistanceUtil;
import com.baidu.mapapi.search.share.OnGetShareUrlResultListener;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.telephony.SmsManager;
import android.widget.Toast;

public class MyBaomuService extends Service {

	private IBinder mybinder = new MyServiceBinder();

	private LocationClient mLocationClient;
	private String sharedname_dian = "myconfig_dian";
	private String sharedname_jingdu = "myconfig_jingdu";
	private String sharedname_phone = "myconfig_phone";
	private List<Map<String, String>> sharedperence = null;
	private String shareurl = "";

	private int jingdu = 0;
	private String phone = "";

	// 是否超出范围的标志
	private boolean flag = false;
	// 判断距离的线程标志
	public boolean xianchengflag = true;
	// 查看是否超出距离线程
	public boolean chaochuxianchengflag = true;

	private LatLng dingwei;
	private LatLng bianli;

	// 地址分享组件
	private ShareUrlSearch mShareUrlSearch = null;
	private ShareUrlListener sharelistener = new ShareUrlListener();

	double juli;
	double julimin = 0;

	// 返回定位详细信息，存放在listaddress集合中
	private Map<Object, Object> listaddress = new HashMap<Object, Object>();

	@Override
	public IBinder onBind(Intent intent) {

		// 计算距离新线程
		new Thread(new Runnable() {
			public void run() {
				// 定位信息
				double mylatitude;
				double mylongitude;

				// 遍历的信息
				double bianlilatitude;
				double bianlilongitude;

				while (xianchengflag) {
					try {
						Thread.sleep(10000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					// 绑定服务时，把定位信息获取到
					listaddress = ((MyApplication) getApplicationContext())
							.getList();
					mylatitude = (Double) listaddress.get("latitude");
					mylongitude = (Double) listaddress.get("longitude");
					dingwei = new LatLng(mylatitude, mylongitude);

					// 生成定位地址的分享URL
					mShareUrlSearch
							.requestLocationShareUrl(new LocationShareURLOption()
									.location(dingwei).snippet("你所查看对象现在的位置")
									.name("他的位置"));

					// 遍历sharedperence集合，一个一个的判断是否超出范围
					for (Map<String, String> m : sharedperence) {

						bianlilatitude = Double.parseDouble(m.get("latitude"));
						bianlilongitude = Double.parseDouble(m.get("longitude"));
						bianli = new LatLng(bianlilatitude, bianlilongitude);
						juli = DistanceUtil.getDistance(dingwei, bianli);

						// 先给最小距离赋值
						if (julimin == 0) {
							julimin = juli;
						}
						if (julimin > juli) {
							julimin = juli;
						}
					}
					// 遍历完毕，得到定位点 和设置的定点中 最小的距离julimin
					// 如果做小距离julimin小于精度，则正常，如果大于精度，则不正常，超出范围，需要报警
					if (julimin > jingdu) {
						flag = true;
					}

					// 测试
					System.out.println("服务中新线程正在运行" + flag + julimin);
				}

			}
		}).start();

		// 查看是否超出距离线程
		new Thread(new Runnable() {

			@Override
			public void run() {
				while (chaochuxianchengflag) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					// 如果超出范围则报警
					if (flag) {
						// 报警处理
						String content = "你的监管的对象现在已经超出所设定的范围，点击下面链接可以查看他当前的位置 "
								+ shareurl;
						chaochuxianchengflag = false;
						SmsManager smsmanger = SmsManager.getDefault();
						List<String> texts = smsmanger.divideMessage(content);
						for(String text:texts){
							smsmanger.sendTextMessage(phone, null, text, null, null);
						}
						
						System.out.println(content+chaochuxianchengflag);

					}
				}

			}

		}).start();

		return mybinder;
	}

	/**
	 * 自定义binder对象，作为返回的binder
	 * 
	 * @author wangsong
	 * 
	 */
	class MyServiceBinder extends Binder {

		// 返回服务的本身
		public MyBaomuService getService() {
			return MyBaomuService.this;
		}

	}

	/**
	 * 服务创建是开始定位，并计算处理
	 */
	@Override
	public void onCreate() {
		super.onCreate();

		mLocationClient = ((MyApplication) getApplication()).mLocationClient;

		// 发起定位
		if (!mLocationClient.isStarted())
			mLocationClient.start();
		if (mLocationClient != null && mLocationClient.isStarted())
			mLocationClient.requestLocation();

		mShareUrlSearch = ShareUrlSearch.newInstance();
		mShareUrlSearch.setOnGetShareUrlResultListener(sharelistener);

		// 获取经度，默认为50
		SharedPreferences myshared_jingdu = getSharedPreferences(
				sharedname_jingdu, Context.MODE_WORLD_READABLE);
		jingdu = myshared_jingdu.getInt("jingdu", 0);

		SharedPreferences myshared = getSharedPreferences(sharedname_phone,
				Context.MODE_WORLD_READABLE);
		phone = myshared.getString("phone", "");

		if (jingdu == 0) {
			jingdu = 50;
			Toast.makeText(MyBaomuService.this,
					"你没有设置精度，已经默认为 50 米，需要设置，请关闭服务后设置。你设置的报警电话为 " + phone, 1)
					.show();
		}
		sharedperence = getDate();

	}



	/**
	 * 把SharedPreferences中的数据转化成list集合
	 */
	public List<Map<String, String>> getDate() {

		List<Map<String, String>> listdata = new ArrayList<Map<String, String>>();

		SharedPreferences myshared = getSharedPreferences(sharedname_dian,
				Context.MODE_WORLD_READABLE);
		int count = myshared.getInt("count", 0);

		Map<String, String> map = null;

		for (int i = 0; i < count; i++) {
			map = new HashMap<String, String>();
			map.put("latitude", myshared.getString("latitude" + i, ""));
			map.put("longitude", myshared.getString("longitude" + i, ""));
			map.put("title", myshared.getString("title" + i, ""));
			listdata.add(map);
		}

		return listdata;

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
		chaochuxianchengflag = false;
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		xianchengflag = false;
		chaochuxianchengflag = false;
		return super.onUnbind(intent);

	}

}
