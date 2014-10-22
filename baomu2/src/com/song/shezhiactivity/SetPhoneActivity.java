package com.song.shezhiactivity;

import com.song.baomu.MainActivity;
import com.song.baomu.R;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;



public class SetPhoneActivity extends Activity implements OnClickListener{
	
	
	private String sharedname_phone = "myconfig_phone";

	private EditText et;
	private Button quxiao;
	private Button xiayibu;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_phone_view);
		
		et = (EditText) findViewById(R.id.set_phone_et);
		quxiao = (Button) findViewById(R.id.set_phone_view_quxiao);
		quxiao.setOnClickListener(this);
		xiayibu = (Button) findViewById(R.id.set_phone_view_xiayibu);
		xiayibu.setOnClickListener(this);
		
		
	}


	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		if(id==R.id.set_phone_view_quxiao){
			
			et.setText("");
			
		}else if(id == R.id.set_phone_view_xiayibu){
			
			String et_text = et.getText().toString().trim();
			if (et_text.equals(null) || et_text.equals("")) {
				Toast.makeText(SetPhoneActivity.this, "你的电话号码填写不正确，请重新输入。", 1)
						.show();
			} else {
				SharedPreferences myshared = getSharedPreferences(
						sharedname_phone, Context.MODE_WORLD_WRITEABLE);
				SharedPreferences.Editor editor = myshared.edit();
				editor.putString("phone", et_text);
				editor.commit();
				Toast.makeText(SetPhoneActivity.this, "你的报警电话号码设置成功。", 1)
						.show();
				//转到下一步 设置精度
				Intent in = new Intent(SetPhoneActivity.this,SetJingduActivity.class);
				startActivity(in);
				finish();
				
			}

		}
		
	}
}
