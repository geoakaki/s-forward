package com.smsforwarder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;
import java.util.List;

public class SmsReceiver extends BroadcastReceiver {
    private static final String TAG = "SmsReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                String format = bundle.getString("format");

                if (pdus != null) {
                    for (Object pdu : pdus) {
                        SmsMessage smsMessage = SmsMessage.createFromPdu((byte[]) pdu, format);

                        String sender = smsMessage.getDisplayOriginatingAddress();
                        String messageBody = smsMessage.getMessageBody();

                        Log.d(TAG, "SMS received from: " + sender);
                        Log.d(TAG, "Message: " + messageBody);

                        processIncomingSms(context, sender, messageBody);
                    }
                }
            }
        }
    }

    private void processIncomingSms(Context context, String sender, String messageBody) {
        if (sender == null) return;

        RuleManager ruleManager = new RuleManager(context);
        List<ForwardingRule> rules = ruleManager.getEnabledRules();

        for (ForwardingRule rule : rules) {
            if (matchesSender(sender, rule.getSenderFilter())) {
                forwardSms(context, rule.getForwardNumber(), sender, messageBody);
            }
        }
    }

    private boolean matchesSender(String sender, String filter) {
        if (filter == null || filter.isEmpty()) return false;
        return sender.toUpperCase().contains(filter.toUpperCase());
    }

    private void forwardSms(Context context, String forwardNumber, String originalSender, String messageBody) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            String forwardMessage = "From: " + originalSender + "\n" + messageBody;

            // If message is too long, split it
            if (forwardMessage.length() > 160) {
                java.util.ArrayList<String> parts = smsManager.divideMessage(forwardMessage);
                smsManager.sendMultipartTextMessage(forwardNumber, null, parts, null, null);
            } else {
                smsManager.sendTextMessage(forwardNumber, null, forwardMessage, null, null);
            }

            Log.d(TAG, "SMS forwarded to: " + forwardNumber);
            Toast.makeText(context, "SMS forwarded from " + originalSender, Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Log.e(TAG, "Error forwarding SMS: " + e.getMessage());
            Toast.makeText(context, "Error forwarding SMS", Toast.LENGTH_SHORT).show();
        }
    }
}
