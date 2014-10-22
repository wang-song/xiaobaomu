package com.song.dingweidb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DingDianServer {
	
	private MyDBHandler mydbhandler= null;
	 
	public DingDianServer(Context context){
		this.mydbhandler = new MyDBHandler(context);
	}
	
	
	//添加
	public void add(DingDian dingdian){	
		SQLiteDatabase db= mydbhandler.getWritableDatabase();
		ContentValues values = new ContentValues();
		
		values.put("title", dingdian.getTitle());
		values.put("longitude", dingdian.getLongitude());
		values.put("latitude", dingdian.getLatitude());
		values.put("address", dingdian.getAddress());
		
		db.insert("dingdian", null, values);
		System.out.println(values);
		
	}
	
	//查询
	public List<DingDian> findDingdian(String mytitle){
		SQLiteDatabase db= mydbhandler.getReadableDatabase();
		Cursor cursor = db.query("dingdian", null, "title=?", new String[]{mytitle}, null, null, null);
		List<DingDian> list = new ArrayList<DingDian>();
		while(cursor.moveToNext()){
			String title = cursor.getString(cursor.getColumnIndex("title"));
			String longitude = cursor.getString(cursor.getColumnIndex("longitude"));
			String latitude = cursor.getString(cursor.getColumnIndex("latitude"));
			String address = cursor.getString(cursor.getColumnIndex("address"));
			list.add(new DingDian(title, longitude, latitude, address));
		}
		return list;	
	}
	
	//查询title 不重复的项目
	public List<Map<String,String>> findTitle(){
		SQLiteDatabase db= mydbhandler.getReadableDatabase();
		Cursor cursor = db.query(true, "dingdian", new String[]{"title"}, null, null, null, null, null,null);
		List<Map<String,String>> list = new ArrayList<Map<String,String>>();
		Map<String,String> m = null;
		while(cursor.moveToNext()){
			m = new HashMap<String, String>();
			String title = cursor.getString(cursor.getColumnIndex("title"));
			m.put("title", title);
			list.add(m);
		}
		return list;	
	}
	
	//删除
	public void delete(String title){
		SQLiteDatabase db= mydbhandler.getWritableDatabase();
		db.delete("dingdian", "title=?", new String[]{title});	
	}
	
	
	
	
	/**
	
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
	
	
	
	*/
	
	
	

}
