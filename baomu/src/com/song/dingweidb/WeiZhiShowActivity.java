package com.song.dingweidb;

import java.util.Date;

import com.song.baomu.MainActivity;
import com.song.baomu.MapSecletActivity;
import com.song.baomu.R;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;

import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

public class WeiZhiShowActivity extends Activity {

	private TextView tv;
	private ListView lv;
	private Cursor cursor;

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.show_weizhi);

		tv = (TextView) findViewById(R.id.show_weizhi_tv);
		lv = (ListView) findViewById(R.id.show_weizhi_listview);

		WeiZhiServer ws = new WeiZhiServer(this.getBaseContext());
		
		java.text.DateFormat format1 = new java.text.SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");	
		String deletetime = format1.format(new Date(new Date().getTime()-10*24*60*60*1000));
		ws.delete(deletetime);
		
		cursor = ws.findcursor();
		


		ListAdapter adapter = new SimpleCursorAdapter(this,
				R.layout.save_listview_weizhi, cursor, new String[] { "time",
						"_id", "latitude", "address" }, new int[] {
						R.id.listview_weizhi_time,
						R.id.listview_weizhi_longitude,
						R.id.listview_weizhi_latitude,
						R.id.listview_weizhi_address });

		lv.setAdapter(adapter);
		
		
		// listview 点击事件
		lv.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				cursor.moveToPosition(position);
				Intent inte = new Intent(WeiZhiShowActivity.this,MapSecletActivity.class);
				inte.putExtra("latitude", cursor.getString(cursor.getColumnIndex("latitude")));
				inte.putExtra("longitude", cursor.getString(cursor.getColumnIndex("_id")));
				inte.putExtra("title", cursor.getString(cursor.getColumnIndex("address")));
				startActivity(inte);

			}
		});
	}

}
