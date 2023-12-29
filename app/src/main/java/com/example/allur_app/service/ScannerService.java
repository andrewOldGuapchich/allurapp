package com.example.allur_app.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.allur_app.utils.ScannerUtil;

public class ScannerService extends BroadcastReceiver {
    public static final String SCAN_DECODING_BROADCAST = ScannerUtil.SCAN_DECODING_BROADCAST;
    public static final String SCAN_DECODING_DATA = ScannerUtil.SCAN_DECODING_DATA;
    public static final String SCAN_SYMBOLOGY_TYPE = ScannerUtil.SCAN_SYMBOLOGY_TYPE;
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        assert action != null;
        if(action.equals(SCAN_DECODING_BROADCAST)){
            String barcode = intent.getStringExtra(SCAN_DECODING_DATA);
            String symbology = intent.getStringExtra(SCAN_SYMBOLOGY_TYPE);

        }
    }
}
