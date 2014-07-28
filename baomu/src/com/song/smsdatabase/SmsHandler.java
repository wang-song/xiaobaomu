package com.song.smsdatabase;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.telephony.SmsManager;

/**
 * @author 短信的处理
 * 
 */
public class SmsHandler extends Handler {
	private Context mcontext;
	private String sharedname_dingwei = "myconfig_dingwei";

	public SmsHandler(Context context) {
		this.mcontext = context;
	}

	@Override
	public void handleMessage(Message msg) {
		SmsInfo smsInfo = (SmsInfo) msg.obj;
		if (smsInfo.action == 0) {
			System.out.println(smsInfo.smsBody);
			String msgTxt = smsInfo.smsBody;

			if (msgTxt.startsWith("weizhi")) {

				SharedPreferences myshared = mcontext.getSharedPreferences(
						sharedname_dingwei, Context.MODE_WORLD_READABLE);
				String latitude = myshared.getString("latitude", "");
				String longitude = myshared.getString("longitude", "");
				String shareurl = myshared.getString("shareurl", "");

				if (latitude.length() != 0 && longitude.length() != 0) {

					String content = "他的位置百度经纬度： " + longitude + ","
							+ latitude +"点击获得详细位置"+shareurl;

					SmsManager smsmanger = SmsManager.getDefault();
					List<String> texts = smsmanger.divideMessage(content);
					for (String text : texts) {
						smsmanger.sendTextMessage(smsInfo.smsAddress, null,
								text, null, null);
					}
					System.out.println(content);
					
					Uri mUri = Uri.parse("content://sms/");
					mcontext.getContentResolver().delete(mUri, "_id=?",
							new String[] { smsInfo._id });

				}else{
					System.out.println("定位失败");
				}
			}

		}

		if (smsInfo.action == 1) {
			ContentValues values = new ContentValues();
			values.put("read", "1");
			mcontext.getContentResolver().update(
					Uri.parse("content://sms/inbox"), values, "_id=?",
					new String[] { smsInfo._id });
		} else if (smsInfo.action == 2) {
			Uri mUri = Uri.parse("content://sms/");
			mcontext.getContentResolver().delete(mUri, "_id=?",
					new String[] { smsInfo._id });
		}
	}
}
