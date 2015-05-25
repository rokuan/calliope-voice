package com.rokuan.calliope.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import com.rokuan.calliope.HomeActivity;
import com.rokuan.calliope.source.SmsSource;
import com.rokuan.calliope.source.SourceObjectProvider;

/**
 * Created by LEBEAU Christophe on 25/05/15.
 */
public class IncomingSmsReceiver extends BroadcastReceiver {
    private SourceObjectProvider sourceProvider;

    public IncomingSmsReceiver(SourceObjectProvider prov){
        sourceProvider = prov;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final Bundle bundle = intent.getExtras();

        try {
            if (bundle != null) {
                final Object[] pdusObj = (Object[]) bundle.get("pdus");

                for (int i = 0; i < pdusObj.length; i++) {
                    SmsMessage currentMessage = SmsMessage.createFromPdu((byte[]) pdusObj[i]);
                    String phoneNumber = currentMessage.getDisplayOriginatingAddress();

                    String senderNum = phoneNumber;
                    String message = currentMessage.getDisplayMessageBody();

                    Log.i("SmsReceiver", "from: " + senderNum + ", message: " + message);

                    // TODO: chercher le numero dans les contacts
                    String sender = senderNum;

                    SmsData data = new SmsData(sender, null, message);
                    sourceProvider.addSource(new SmsSource(data));
                }
            }
        } catch (Exception e) {
            Log.e("IncomingSMS", e.toString());
        }
    }
}
