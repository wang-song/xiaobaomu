package com.song.baomu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.IBinder;
import android.widget.Toast;

public class MyBaomuService extends Service {

	private IBinder mybinder = new MyServiceBinder();

	private LocationClient mLocationClient;
	private String sharedname_dian = "myconfig_dian";
	private String sharedname_jingdu = "myconfig_jingdu";
	private String sharedname_phone = "myconfig_phone";
	private List<Map<String, String>> sharedperence = null;

	private int jingdu = 0;
	private String phone = "";

	// 是否超出范围的标志
	private boolean flag = false;
	public boolean xianchengflag = true;

	private LatLng dingwei;
	private LatLng bianli;

	double juli;
	double julimin = 0;

	// 返回定位详细信息，存放在listaddress集合中
	private Map<Object, Object> listaddress = new HashMap<Object, Object>();

	@Override
	public IBinder onBind(Intent intent) {

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
						Thread.sleep(5000);
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
					System.out.println("服务中新线程正在运行"+flag+julimin);
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
		// 定位初始化
		InitLocation();
		// 发起定位
		if (!mLocationClient.isStarted())
			mLocationClient.start();

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
	 * 定位初始化函数
	 */
	private void InitLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 设置定位模式
		option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度，默认值gcj02
		int span = 5000;
		option.setScanSpan(span);// 设置发起定位请求的间隔时间为5000ms
		option.isOpenGps();
		option.setIsNeedAddress(true);
		mLocationClient.setLocOption(option);
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
	
	
	
	
	
	
	
	

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		xianchengflag = false;
		super.onDestroy();
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		xianchengflag = false;
		return super.onUnbind(intent);
	
	}

}