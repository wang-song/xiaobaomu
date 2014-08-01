package com.song.shezhiactivity;

import com.song.baomu.MapActivity;
import com.song.baomu.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetMiwenActivity extends Activity implements OnClickListener{
	
	private String sharedname_miwen = "myconfig_miwen";
	
	private EditText et;
	private Button quxiao;
	private Button xiayibu;
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_miwen_view);
		
		
		et = (EditText) findViewById(R.id.set_miwen_et);
		quxiao = (Button) findViewById(R.id.set_miwen_view_quxiao);
		quxiao.setOnClickListener(this);
		xiayibu = (Button) findViewById(R.id.set_miwen_view_xiayibu);
		xiayibu.setOnClickListener(this);
		
	}



	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		
		
		int id = v.getId();
		if(id==R.id.set_miwen_view_quxiao){
			
			et.setText("");
			
		}else if(id == R.id.set_miwen_view_xiayibu){
			
			String miwentext = et.getText().toString().trim();
			if (miwentext.length() != 0) {

				SharedPreferences shared = getSharedPreferences(
						sharedname_miwen,
						Context.MODE_WORLD_WRITEABLE);
				Editor editor = shared.edit();
				editor.putString("miwen",miwentext);
				editor.commit();
				Toast.makeText(SetMiwenActivity.this,
						"您已成功将报警精度范围设置为 " + miwentext, 1).show();
				//下一步  设置定点
				Intent in = new Intent(SetMiwenActivity.this,SetDingdianActivity.class);
				startActivity(in);
				finish();
				
			
			}else{
				Toast.makeText(SetMiwenActivity.this,
						"您所设置的数据有误，请重新输入", 1).show();
			}
		
			
		}
		
	}

}
