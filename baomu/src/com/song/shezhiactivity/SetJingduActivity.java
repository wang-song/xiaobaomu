package com.song.shezhiactivity;

import com.song.baomu.MainActivity;
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

public class SetJingduActivity extends Activity implements OnClickListener{
	

	private String sharedname_jingdu = "myconfig_jingdu";
	
	private EditText et;
	private Button quxiao;
	private Button xiayibu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.set_jingdu_view);
		
		et = (EditText) findViewById(R.id.set_jigndu_et);
		quxiao = (Button) findViewById(R.id.set_jingdu_view_quxiao);
		quxiao.setOnClickListener(this);
		xiayibu = (Button) findViewById(R.id.set_jingdu_view_xiayibu);
		xiayibu.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		int id = v.getId();
		if(id==R.id.set_jingdu_view_quxiao){
			
		
			et.setText("");
			
		}else if(id == R.id.set_jingdu_view_xiayibu){
			
			String jingdutext = et.getText().toString()
					.trim();
			if (jingdutext.length() != 0
					&& jingdutext.matches("^[0-9]*$")) {

				SharedPreferences mysharedxie_jingdu = getSharedPreferences(
						sharedname_jingdu,
						Context.MODE_WORLD_WRITEABLE);
				Editor editor = mysharedxie_jingdu.edit();
				editor.putInt("jingdu",
						Integer.parseInt(jingdutext));
				editor.commit();
				Toast.makeText(SetJingduActivity.this,
						"您已成功将报警精度范围设置为 " + jingdutext, 1).show();
				//下一步设置密文
				Intent in = new Intent(SetJingduActivity.this, SetMiwenActivity.class);
				startActivity(in);
				finish();
				
				
				
			}else{
				Toast.makeText(SetJingduActivity.this,
						"您所设置的数据有误，请重新输入", 1).show();
						
			}
			
			
		}
		
	}
	
	

}
