package com.song.dingweidb;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDBHandler extends SQLiteOpenHelper {

	public MyDBHandler(Context context) {
		super(context, "dingwei.db", null, 1);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		// TODO Auto-generated method stub
		
		db.execSQL("create table weizhi (id INTEGER PRIMARY KEY AUTOINCREMENT,time varchar(20),longitude varchar(20),latitude varchar(20),address varchar(40),lingwai varchar(25))");
		db.execSQL("create table dingdian (id INTEGER PRIMARY KEY AUTOINCREMENT,title varchar(20),longitude varchar(20),latitude varchar(20),address varchar(40),lingwai varchar(25))");

	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		// TODO Auto-generated method stub

	}

}
