package com.song.baomu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.location.LocationClient;
import com.song.baomu.MyBaomuService.MyServiceBinder;
import com.song.dingweidb.SaveDingweiService;
import com.song.dingweidb.WeiZhiShowActivity;
import com.song.menu.ResideMenu;
import com.song.menu.ResideMenuItem;
import com.song.shezhiactivity.SetMiwenActivity;
import com.song.shezhiactivity.SetPhoneActivity;
import com.song.smsdatabase.DingweiService;
import com.song.smsdatabase.SmsService;
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

import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private String sharedname_dian = "myconfig_dian";
	private String sharedname_jingdu = "myconfig_jingdu";
	private String sharedname_phone = "myconfig_phone";
	private String sharedname_miwen = "myconfig_miwen";

	private EditText main_edit_phone;

	private Button main_set_phone;

	private Button main_open_service;
	private Button main_close_service;
	private Button main_caidan_left;
	private Button main_caidan_right;

	private ListView main_listview;

	String edit_phone = "";

	int jingdu = 0;
	private long exitTime = 0;

	// 用于接收服务返回的binder
	private MyServiceBinder mybinder = null;
	// 与服务的链接
	private MyServiceConnection conn = new MyServiceConnection();
	
	public List<Map<String, String>> list = new ArrayList<Map<String, String>>();

	private LocationClient mLocationClient;

	private EditText xiejingdu;
	private EditText xiejingdu1;

	AlertDialog.Builder builder;
	AlertDialog.Builder builder2;

	private ResideMenu resideMenu;
	private MainActivity mContext;
	private ResideMenuItem itemHome;
	private ResideMenuItem itemProfile;
	private ResideMenuItem itemCalendar;
	private ResideMenuItem itemCalendar2;
	private ResideMenuItem itemCalendar3;
	private ResideMenuItem itemCalendar4;
	private ResideMenuItem itemSettings;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.mycustomtitle);

		GuideHelper guideHelper = new GuideHelper(this);
		guideHelper.openGuide();

		main_edit_phone = (EditText) findViewById(R.id.main_edit_phone);

		main_set_phone = (Button) findViewById(R.id.main_set_phone);
		main_set_phone.setOnClickListener(mylostener);
		main_open_service = (Button) findViewById(R.id.main_open_service);
		main_open_service.setOnClickListener(mylostener);
		main_close_service = (Button) findViewById(R.id.main_close_service);
		main_close_service.setOnClickListener(mylostener);

		main_caidan_left = (Button) findViewById(R.id.header_left_btn);
		main_caidan_left.setOnClickListener(mylostener);
		main_caidan_right = (Button) findViewById(R.id.header_right_btn);
		main_caidan_right.setOnClickListener(mylostener);

		main_listview = (ListView) findViewById(R.id.main_list_view);

		mLocationClient = ((MyApplication) getApplication()).mLocationClient;

		// 发起定位
		if (!mLocationClient.isStarted())
			mLocationClient.start();
		if (mLocationClient != null && mLocationClient.isStarted())
			mLocationClient.requestLocation();

		// 创建菜单
		mContext = this;
		setUpMenu();
		
		list = getDate();

		// listview 点击事件
		main_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// Toast.makeText(getApplicationContext(), position+"&&"+id,
				// 1).show();
				// 点击item则获取经纬度打开百度地图
				
				Intent inte = new Intent(MainActivity.this,MapSecletActivity.class);
				inte.putExtra("latitude", list.get(position).get("latitude"));
				inte.putExtra("longitude", list.get(position).get("longitude"));
				inte.putExtra("title", list.get(position).get("title"));
				startActivity(inte);
				
				


			}

		});

		// 开启短信监听服务
		Intent in = new Intent(MainActivity.this, SmsService.class);
		startService(in);

		// 开启 定位监听服务 10秒 把定位信息写入到sharedpress中 DingweiService
		Intent in2 = new Intent(MainActivity.this, DingweiService.class);
		startService(in2);
		
		// 开启 定位监听服务 60秒 把定位记录存到数据库中
		Intent in3 = new Intent(MainActivity.this, SaveDingweiService.class);
		startService(in3);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0 && resultCode == 0) {

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
				if (edit_phone.equals(null) || edit_phone.equals("")) {
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

			case R.id.header_left_btn:
				resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
				break;
			case R.id.header_right_btn:
				resideMenu.openMenu(ResideMenu.DIRECTION_LEFT);
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
//		stopService(null);
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

	//返回键 监听事件
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN) {
			if ((System.currentTimeMillis() - exitTime) > 2000) {
				Toast.makeText(getApplicationContext(), "再按一次退出程序",
						Toast.LENGTH_SHORT).show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0);
			}
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void setUpMenu() {

		// attach to current activity;
		resideMenu = new ResideMenu(this);
		resideMenu.setBackground(R.drawable.menu_background);
		resideMenu.attachToActivity(this);
		resideMenu.setMenuListener(menuListener);
		// valid scale factor is between 0.0f and 1.0f. leftmenu'width is
		// 150dip.
		resideMenu.setScaleValue(0.55f);

		// create menu items;
		itemHome = new ResideMenuItem(this, R.drawable.icon_home, "主页");
		itemProfile = new ResideMenuItem(this, R.drawable.icon_profile, "用户帮助");

		itemSettings = new ResideMenuItem(this, R.drawable.icon_settings,
				"设置向导");
		itemCalendar = new ResideMenuItem(this, R.drawable.icon_calendar,
				"设置定点");
		itemCalendar2 = new ResideMenuItem(this, R.drawable.icon_calendar,
				"设置精度");
		itemCalendar3 = new ResideMenuItem(this, R.drawable.icon_calendar,
				"设置密码");
		itemCalendar4 = new ResideMenuItem(this, R.drawable.icon_calendar,
				"查看数据");

		itemHome.setOnClickListener(this);
		itemProfile.setOnClickListener(this);
		itemCalendar.setOnClickListener(this);
		itemCalendar2.setOnClickListener(this);
		itemCalendar3.setOnClickListener(this);
		itemCalendar4.setOnClickListener(this);
		itemSettings.setOnClickListener(this);

		resideMenu.addMenuItem(itemHome, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemProfile, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemCalendar, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemCalendar2, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemCalendar3, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemCalendar4, ResideMenu.DIRECTION_LEFT);
		resideMenu.addMenuItem(itemSettings, ResideMenu.DIRECTION_LEFT);
		resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);

	}

	// public boolean dispatchTouchEvent(MotionEvent ev) {
	// return resideMenu.dispatchTouchEvent(ev);
	// }

	private ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
		@Override
		public void openMenu() {
			// Toast.makeText(mContext, "Menu is opened!",
			// Toast.LENGTH_SHORT).show();
		}

		@Override
		public void closeMenu() {
			// Toast.makeText(mContext, "Menu is closed!",
			// Toast.LENGTH_SHORT).show();
		}
	};

	// What good method is to access resideMenu？
	public ResideMenu getResideMenu() {
		return resideMenu;
	}

	// 点击菜单的处理事件
	@Override
	public void onClick(View view) {
		if (view == itemHome) {
			// 主页
			resideMenu.closeMenu();
		} else if (view == itemProfile) {
			// 帮助

			Intent in = new Intent(mContext, HelpActivity.class);
			startActivity(in);
			
		} else if (view == itemSettings) {
			
			// 设置向导
			Intent in1 = new Intent(mContext, SetPhoneActivity.class);
			startActivity(in1);

			
		} else if (view == itemCalendar) {
			// 设置定点
			Intent intent = new Intent(MainActivity.this, MapActivity.class);
			startActivityForResult(intent, 0);
			Toast.makeText(MainActivity.this,
					"设置定点时，需要在地图中长按，然后会有提示，定点可以设置多个。", 1).show();

		} else if (view == itemCalendar2) {
			// 设置精度
			builder = new AlertDialog.Builder(MainActivity.this);
			xiejingdu = new EditText(MainActivity.this);
			builder.setTitle("请输入你想要设置的精度（米）");
			builder.setIcon(android.R.drawable.ic_dialog_info);
			builder.setView(xiejingdu);
			builder.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							String jingdutext = xiejingdu.getText().toString()
									.trim();
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

								jingdu = mysharedxie_jingdu.getInt("jingdu", 0);
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

		}else if (view == itemCalendar3) {
			
			builder2 = new AlertDialog.Builder(MainActivity.this);
			xiejingdu1 = new EditText(MainActivity.this);
			builder2.setTitle("请输入你想要设置的密码");
			builder2.setIcon(android.R.drawable.ic_dialog_info);
			builder2.setView(xiejingdu1);
			builder2.setPositiveButton("确定",
					new DialogInterface.OnClickListener() {

						public void onClick(DialogInterface dialog, int which) {
							String miwentext = xiejingdu1.getText().toString()
									.trim();
							if (miwentext.length() != 0) {
								System.out.println(miwentext);

								SharedPreferences shared = getSharedPreferences(
										sharedname_miwen,
										Context.MODE_WORLD_WRITEABLE);
								Editor editor = shared.edit();
								editor.putString("miwen",miwentext);
								editor.commit();
							}
							dialog.dismiss();
							dialog.cancel();
							Toast.makeText(MainActivity.this,
									"您已成功将密码设置为 " + miwentext, 1).show();
						}

					});
			builder2.setNegativeButton("取消", null);

			builder2.create();

			builder2.show();

			
		}else if(view == itemCalendar4){
			Intent intent = new Intent(MainActivity.this, WeiZhiShowActivity.class);
			startActivity(intent);	
		}

		resideMenu.closeMenu();

	}

}
