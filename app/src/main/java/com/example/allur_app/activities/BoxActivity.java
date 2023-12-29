package com.example.allur_app.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.example.allur_app.R;
import com.example.allur_app.api.AllurApi;
import com.example.allur_app.utils.ScannerUtil;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BoxActivity extends AppCompatActivity {
    public static final String SCAN_DECODING_BROADCAST = ScannerUtil.SCAN_DECODING_BROADCAST;
    public static final String SCAN_DECODING_DATA = ScannerUtil.SCAN_DECODING_DATA;
    public static final String SCAN_SYMBOLOGY_TYPE = ScannerUtil.SCAN_SYMBOLOGY_TYPE;
    private TextView barcodeTextView;
    private TextView infoTextView;
    private Button button;

    private CompositeDisposable compositeDisposable = new CompositeDisposable();


    private final BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            assert action != null;
            if(action.equals(SCAN_DECODING_BROADCAST)){
                String barcode = intent.getStringExtra(SCAN_DECODING_DATA);
                String symbology = intent.getStringExtra(SCAN_SYMBOLOGY_TYPE);
                barcodeTextView.setText(barcode);
                infoTextView.setText("dfdfg");
                //setAnswer();
            }
        }
    };



    @SuppressLint({"MissingInflatedId", "CheckResult"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box);

        barcodeTextView = findViewById(R.id.barcodeView);
        infoTextView = findViewById(R.id.infoView);
        button = findViewById(R.id.button);

        button.setOnClickListener(v -> {
            AllurApi api = new AllurApi();

            api.request()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(result -> {
                                Log.d("API_REQUEST", "Success: " + result);
                                barcodeTextView.setText(result); // Обновление barcodeTextView
                                infoTextView.setText(result); // Обновление infoTextView
                            },
                            error -> {
                                Log.e("API_REQUEST", "Error: " + error.getMessage());
                                infoTextView.setText("Error occurred");
                            });
        });
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(dataReceiver, new IntentFilter(SCAN_DECODING_BROADCAST));
    }

    @SuppressLint({"CheckResult", "SetTextI18n"})
    private void setAnswer() {

    }

}