package com.song.baomu;

import android.app.Activity;
import android.os.Bundle;

public class HelpActivity extends Activity {
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.baomu_help);
	}
	
	
	 public static String ToSBC(String input) {
	        char c[] = input.toCharArray();
	        for (int i = 0; i < c.length; i++) {
	            if (c[i] == ' ') {
	                c[i] = '\u3000';
	            } else if (c[i] < '\177') {
	                c[i] = (char) (c[i] + 65248);
	            }
	        }
	        return new String(c);
	    }
	

}
