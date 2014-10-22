package com.song.dingweidb;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class WeiZhiServer {
	
	private MyDBHandler mydbhandler= null;
	 
	public WeiZhiServer(Context context){
		this.mydbhandler = new MyDBHandler(context);
	}
	
	
	
	//添加
	public void add(WeiZhi weizhi){	
		SQLiteDatabase db= mydbhandler.getWritableDatabase();
		ContentValues values = new ContentValues();
		values.put("time", weizhi.getTime());
		values.put("longitude", weizhi.getLongitude());
		values.put("latitude", weizhi.getLatitude());
		values.put("address", weizhi.getAddress());
		db.insert("weizhi", null, values);
		System.out.println(values);
		
	}
	
	//删除
	public void delete(String time){
		SQLiteDatabase db= mydbhandler.getWritableDatabase();
		db.delete("weizhi", "time<?", new String[]{time});	
	}
	
	
	public List<WeiZhi> find(){
		SQLiteDatabase db= mydbhandler.getReadableDatabase();
		List<WeiZhi> list = new ArrayList<WeiZhi>();
		Cursor cursor = db.query("weizhi", null, null, null, null, null, "time desc");
		while(cursor.moveToNext()){
			String time = cursor.getString(cursor.getColumnIndex("time"));
			String longitude = cursor.getString(cursor.getColumnIndex("longitude"));
			String latitude = cursor.getString(cursor.getColumnIndex("latitude"));
			String address = cursor.getString(cursor.getColumnIndex("address"));
			String lingwai = cursor.getString(cursor.getColumnIndex("lingwai"));
			list.add(new WeiZhi(time, longitude, latitude, address, lingwai));
		}
		return list;
	}
	
	//查询 绑定到cursor上
	public Cursor findcursor(){
		SQLiteDatabase db= mydbhandler.getReadableDatabase();
		Cursor cursor = db.query("weizhi", new String[]{"time","longitude as _id","latitude","address"}, null, null, null, null, "time desc");
		return cursor;

	}
	
	
	
	
	

}
