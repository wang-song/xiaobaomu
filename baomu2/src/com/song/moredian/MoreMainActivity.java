package com.song.moredian;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.song.baomu.R;
import com.song.dingweidb.DingDian;
import com.song.dingweidb.DingDianServer;

public class MoreMainActivity extends Activity {

	private ListView listview;
	private Button queding;
	private Button shanchu;
	private Button chankan;
	private Button shezhi;

	private SimpleAdapter myadapter = null;

	private int myindex = -1;

	private DingDianServer dingdianserver = null;
	private String sharedname_dian = "myconfig_dian";

	private List<Map<String, String>> list = new ArrayList<Map<String, String>>();

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("路线选择");
		setContentView(R.layout.more_main_view);

		dingdianserver = new DingDianServer(getBaseContext());

		listview = (ListView) findViewById(R.id.more_main_list_view);
		queding = (Button) findViewById(R.id.more_mian_queding);
		queding.setOnClickListener(mylistener);
		shanchu = (Button) findViewById(R.id.more_mian_qingchu);
		shanchu.setOnClickListener(mylistener);
		chankan = (Button) findViewById(R.id.more_mian_cakan);
		chankan.setOnClickListener(mylistener);
		shezhi = (Button) findViewById(R.id.more_mian_shezhi);
		shezhi.setOnClickListener(mylistener);

	}

	/**
	 * 界面按钮 点击触发事件处理
	 */
	private android.view.View.OnClickListener mylistener = new android.view.View.OnClickListener() {

		@Override
		public void onClick(View v) {

			Button btn = (Button) v;
			switch (btn.getId()) {
			case R.id.more_mian_queding:
				if (myindex == -1) {
					Toast.makeText(MoreMainActivity.this, "请您先选择你要选择的路线", 1)
							.show();
				} else {

					List<DingDian> dingdians = dingdianserver.findDingdian(list
							.get(myindex).get("title"));

					int size = dingdians.size();
					SharedPreferences myshared = getSharedPreferences(
							sharedname_dian, Context.MODE_WORLD_READABLE);
					SharedPreferences.Editor editor = myshared.edit();			
					editor.clear();
					
					for (int i = 0; i < size; i++) {
						editor.putString("title" + i, dingdians.get(i)
								.getAddress());
						editor.putString(
								"latitude" + i,
								dingdians.get(i).getLatitude());
						editor.putString(
								"longitude" + i,
								dingdians.get(i).getLongitude());
						editor.commit();
						
					}
					editor.putInt("count", size);
					editor.commit();
					Toast.makeText(MoreMainActivity.this,
							"您选择新路线成功，具体请查看首页定点列表", 1).show();
				finish();	
				}

				break;

			case R.id.more_mian_qingchu:
				// 删除路线 跟新数据库 更新listView列表
				if (myindex == -1) {
					Toast.makeText(MoreMainActivity.this,
							"您还没有选择路线，请先选择你要删除的路线", 1).show();
				} else {
					new AlertDialog.Builder(MoreMainActivity.this)
							.setTitle("提示").setMessage("您确定要删除这条路线吗？")
							.setPositiveButton("确定", new OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {

									String title = list.get(myindex).get(
											"title");
									dingdianserver.delete(title);
									list.remove(myindex);
									myadapter.notifyDataSetChanged();
									Toast.makeText(MoreMainActivity.this,
											"删除路线：" + title + " 成功", 1).show();
								}
							}).setNegativeButton("取消", null).show();
				}

				break;

			case R.id.more_mian_cakan:
				// 查看路线
				break;

			case R.id.more_mian_shezhi:
				// 测试多路线，跳转到设置路线界面
				Intent intent = new Intent(MoreMainActivity.this,
						MoreDianActivity.class);
				startActivity(intent);
				break;

			}
		}
	};

	protected void onResume() {

		
		list = dingdianserver.findTitle();
		if (list.size() == 0) {
			Toast.makeText(MoreMainActivity.this, "您还没有设置路线，请点击“设置路线”进行路线的设置",
					1).show();
		} else {

			myadapter = new SimpleAdapter(MoreMainActivity.this, list,
					R.layout.more_listview_layout, new String[] { "title" },
					new int[] { R.id.more_listview_title });
			myadapter.notifyDataSetChanged();

			listview.setAdapter(myadapter);
			listview.setOnItemClickListener(new OnItemClickListener() {

				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					myindex = position;
					System.out.println("选择了" + myindex);

				}
			});
		}
		super.onResume();
	}

	protected void onDestroy() {
		super.onDestroy();
	}

}
