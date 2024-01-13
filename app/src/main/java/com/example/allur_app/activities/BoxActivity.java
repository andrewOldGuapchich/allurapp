package com.example.allur_app.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.allur_app.R;
import com.example.allur_app.api.AllurApi;
import com.example.allur_app.model.BoxInformEntity;
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
    private TextView boxInformTextView;
    private NestedScrollView nestedScrollView;
    private TableLayout tableLayout;

    private final BroadcastReceiver dataReceiver = new BroadcastReceiver() {
        @SuppressLint({"SetTextI18n", "CheckResult"})
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            assert action != null;
            if(action.equals(SCAN_DECODING_BROADCAST)) {
                String barcode = intent.getStringExtra(SCAN_DECODING_DATA);
                barcodeTextView.setText(barcode);
                AllurApi api = new AllurApi();
                assert barcode != null;
                tableLayout.removeAllViews();
                api.request(barcode)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                                    Log.d("API_REQUEST", "Success: " + result);
                                    for(BoxInformEntity b : result){
                                        Log.d("FOR", "Ok!");
                                        addTableRow(tableLayout,
                                                String.valueOf(b.getId()),
                                                b.getDescription(),
                                                String.valueOf(b.getCount()));
                                    }
                                },
                                error -> {
                                    Log.e("API_REQUEST", "Error: " + error.getMessage());
                                });
            }
        }
    };

    @SuppressLint({"MissingInflatedId", "CheckResult"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box);
        barcodeTextView = findViewById(R.id.boxLabel);
        nestedScrollView = findViewById(R.id.nestedScrollView);
        tableLayout = findViewById(R.id.tableLayout);
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

    private void addTableRow(TableLayout tableLayout, String col1, String col2, String col3) {
        TableRow row = new TableRow(this);

        TextView textView1 = createTextView(col1);
        TextView textView2 = createTextView(col2);
        TextView textView3 = createTextView(col3);

        textView1.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 2f));
        textView2.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 3f));
        textView3.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f));

        row.addView(textView1);
        row.addView(textView2);
        row.addView(textView3);

        tableLayout.addView(row);
    }

    private void addBorder(TextView textView) {
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(0xFFFFFFFF);
        gd.setStroke(2, 0xFF000000);
        textView.setBackground(gd);
    }

    private TextView createTextView(String text) {
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setPadding(16, 16, 16, 16);
        return textView;
    }
}