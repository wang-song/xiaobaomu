package com.song.baomu;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.widget.Toast;

public class MySMSListen extends BroadcastReceiver {

	private String msgTxt;
	private String receiveTime;
	private String senderNumber;
	private String sharedname_dingwei = "myconfig_dingwei";
	private String content="";
	private Context mycontext;

	@Override
	public void onReceive(Context context, Intent intent) {
		
		// TODO Auto-generated method stub

		mycontext = context;
		SmsMessage msg = null;
		Bundle bundle = intent.getExtras();
		if (bundle != null) {
			Object[] pdusObj = (Object[]) bundle.get("pdus");
			for (Object p : pdusObj) {
				msg = SmsMessage.createFromPdu((byte[]) p);

				msgTxt = msg.getMessageBody();// 得到消息的内容

				Date date = new Date(msg.getTimestampMillis());// 时间

				SimpleDateFormat format = new SimpleDateFormat(
						"yyyy-MM-dd HH:mm:ss");
				receiveTime = format.format(date);

				senderNumber = msg.getOriginatingAddress();

				if (msgTxt.startsWith("weizhi")) {

					new Thread(new Runnable() {

						public void run() {

							SharedPreferences myshared = mycontext
									.getSharedPreferences(sharedname_dingwei,
											Context.MODE_WORLD_READABLE);
							String latitude = myshared
									.getString("latitude", "");
							String longitude = myshared.getString("longitude",
									"");
							String shareurl = myshared
									.getString("shareurl", "");

							if (latitude.length() != 0
									&& longitude.length() != 0
									&& shareurl.length() != 0) {

								content = "你的监管的对象现在位于百度经度： "
										+ longitude + "纬度" + latitude
										+ shareurl+"##"+senderNumber;

								SmsManager smsmanger = SmsManager.getDefault();
								List<String> texts = smsmanger
										.divideMessage(content);
								for (String text : texts) {
									smsmanger.sendTextMessage(senderNumber,
											null, text, null, null);
								}
//								smsmanger.sendTextMessage(senderNumber,
//										null, content, null, null);
								System.out.println(content);

							}

						}
					}).start();

				
					return;
				} else {
					Toast.makeText(context, msgTxt, Toast.LENGTH_LONG).show();
					System.out.println("发送人：" + senderNumber + "  短信内容："
							+ msgTxt + "接受时间：" + receiveTime);
					return;
				}
			}
			return;

		}
	}

}
