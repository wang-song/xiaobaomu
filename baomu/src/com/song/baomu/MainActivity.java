package com.song.baomu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.song.baomu.MyBaomuService.MyServiceBinder;
import com.song.utils.GuideHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.IBinder;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends Activity {

	private String sharedname_dian = "myconfig_dian";
	private String sharedname_jingdu = "myconfig_jingdu";
	private String sharedname_phone = "myconfig_phone";

	private EditText main_edit_phone;

	private Button main_set_phone;
	private Button main_set_dingdian1;
	private Button main_set_jingdu;

	private Button main_open_service;
	private Button main_close_service;
	private Button main_one_set;
	private Button main_help;

	private ListView main_listview;

	String edit_phone = "";

	int jingdu = 0;

	// 用于接收服务返回的binder
	private MyServiceBinder mybinder = null;
	// 与服务的链接
	private MyServiceConnection conn = new MyServiceConnection();

	private LocationClient mLocationClient;

	private EditText xiejingdu;

	AlertDialog.Builder builder;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		GuideHelper guideHelper = new GuideHelper(this);
		guideHelper.openGuide();

		main_edit_phone = (EditText) findViewById(R.id.main_edit_phone);

		main_set_phone = (Button) findViewById(R.id.main_set_phone);
		main_set_phone.setOnClickListener(mylostener);
		main_set_dingdian1 = (Button) findViewById(R.id.main_set_dingdian1);
		main_set_dingdian1.setOnClickListener(mylostener);
		main_open_service = (Button) findViewById(R.id.main_open_service);
		main_open_service.setOnClickListener(mylostener);
		main_close_service = (Button) findViewById(R.id.main_close_service);
		main_close_service.setOnClickListener(mylostener);
		main_one_set = (Button) findViewById(R.id.main_one_set);
		main_one_set.setOnClickListener(mylostener);
		main_help = (Button) findViewById(R.id.main_help);
		main_help.setOnClickListener(mylostener);

		main_set_jingdu = (Button) findViewById(R.id.main_set_jingdu);
		main_set_jingdu.setOnClickListener(mylostener);

		main_listview = (ListView) findViewById(R.id.main_list_view);

		mLocationClient = ((MyApplication) getApplication()).mLocationClient;

		// 发起定位
		if (!mLocationClient.isStarted())
			mLocationClient.start();
		if (mLocationClient != null && mLocationClient.isStarted())
			mLocationClient.requestLocation();

		// listview 点击事件
		main_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Toast.makeText(getApplicationContext(), position+"&&"+id,
				// 1).show();
				// 点击item则获取经纬度打开百度地图

			}

		});

	}
	
	
	

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0 && resultCode == 0) {

			List<Map<String, String>> list = new ArrayList<Map<String, String>>();
			list = getDate();
			if (list.size() != 0) {
				main_listview.setAdapter(new SimpleAdapter(
						getApplicationContext(), list,
						R.layout.my_listview_layout, new String[] { "title" },
						new int[] { R.id.my_listview_title }));
			}

		}
	}

	/**
	 * 按钮事件处理内部类
	 */
	private OnClickListener mylostener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Button btn = (Button) v;
			switch (btn.getId()) {
			case R.id.main_set_phone:
				edit_phone = main_edit_phone.getText().toString().trim();
				if (edit_phone.equals(null) || edit_phone.equals("")
						|| edit_phone.length() != 11) {
					Toast.makeText(MainActivity.this, "你的电话号码填写不正确，请重新输入。", 1)
							.show();
				} else {
					SharedPreferences myshared = getSharedPreferences(
							sharedname_phone, Context.MODE_WORLD_WRITEABLE);
					SharedPreferences.Editor editor = myshared.edit();
					editor.putString("phone", edit_phone);
					editor.commit();
					Toast.makeText(MainActivity.this, "你的报警电话号码设置成功。", 1)
							.show();
				}
				break;
			case R.id.main_set_dingdian1:
				Intent intent = new Intent(MainActivity.this, MapActivity.class);
				startActivityForResult(intent, 0);
				Toast.makeText(MainActivity.this,
						"设置定点时，需要在地图中长按，然后会有提示，定点可以设置多个。", 1).show();
				break;

			case R.id.main_set_jingdu:// 设置精度

				builder = new AlertDialog.Builder(MainActivity.this);
				xiejingdu = new EditText(MainActivity.this);
				builder.setTitle("请输入你想要设置的精度（米）");
				builder.setIcon(android.R.drawable.ic_dialog_info);
				builder.setView(xiejingdu);
				builder.setPositiveButton("确定",
						new DialogInterface.OnClickListener() {

							public void onClick(DialogInterface dialog,
									int which) {
								String jingdutext = xiejingdu.getText()
										.toString().trim();
								if (jingdutext.length() != 0
										&& jingdutext.matches("^[0-9]*$")) {
									System.out.println(jingdutext);
									SharedPreferences mysharedxie_jingdu = getSharedPreferences(
											sharedname_jingdu,
											Context.MODE_WORLD_WRITEABLE);
									Editor editor = mysharedxie_jingdu.edit();
									editor.putInt("jingdu",
											Integer.parseInt(jingdutext));
									editor.commit();

									jingdu = mysharedxie_jingdu.getInt(
											"jingdu", 0);
								}
								dialog.dismiss();
								dialog.cancel();
								Toast.makeText(MainActivity.this,
										"您已成功将报警精度范围设置为 " + jingdu, 1).show();
							}

						});
				builder.setNegativeButton("取消", null);

				builder.create();

				builder.show();

				break;
			case R.id.main_open_service:

				// 读取精度
				SharedPreferences mysharedxie_jingdu = getSharedPreferences(
						sharedname_jingdu, Context.MODE_WORLD_READABLE);
				int jingdu1 = mysharedxie_jingdu.getInt("jingdu", 0);

				if (jingdu1 == 0) {
					jingdu1 = 50;
				}

				// 先判断手机号设置了没
				SharedPreferences myshared = getSharedPreferences(
						sharedname_phone, Context.MODE_WORLD_READABLE);
				String phone = myshared.getString("phone", "");

				if (phone.equals("") || phone.length() == 0) {

					new AlertDialog.Builder(MainActivity.this)
							.setTitle("你还没有设置报警手机号，请返回设置")
							.setIcon(android.R.drawable.ic_dialog_info)
							.setPositiveButton("确定", null).show();

				} else {
					// 绑定服务
					Intent intentservice = new Intent(MainActivity.this,
							MyBaomuService.class);
					bindService(intentservice, conn, BIND_AUTO_CREATE);
					Toast.makeText(MainActivity.this,
							"您设置的报警电话为 " + phone + " 精度为 " + jingdu1, 1).show();

				}

				break;
			case R.id.main_close_service:

				// 解除服务的绑定
				unbindService(conn);
				Toast.makeText(MainActivity.this, "服务已关闭。", 1).show();
				break;

			case R.id.main_one_set:

				break;
			case R.id.main_help:

				break;

			}

		}
	};



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
	 * 绑定服务的链接类
	 */
	private class MyServiceConnection implements ServiceConnection {

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			// 返回服务返回的binder
			mybinder = (MyServiceBinder) service;
		}

		@Override
		public void onServiceDisconnected(ComponentName name) {
			// TODO Auto-generated method stub

		}

	}

	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	protected void onResume() {
		super.onResume();

		List<Map<String, String>> list = new ArrayList<Map<String, String>>();
		list = getDate();
		if (list.size() != 0) {
			main_listview.setAdapter(new SimpleAdapter(getApplicationContext(),
					list, R.layout.my_listview_layout,
					new String[] { "title" },
					new int[] { R.id.my_listview_title }));
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

}
