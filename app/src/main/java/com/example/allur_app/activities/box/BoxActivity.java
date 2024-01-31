package com.example.allur_app.activities.box;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.allur_app.R;
import com.example.allur_app.activities.StartActivity;
import com.example.allur_app.api.AllurApi;
import com.example.allur_app.model.box.BoxInformEntity;
import com.example.allur_app.utils.ScannerUtil;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BoxActivity extends AppCompatActivity {
    public static final String SCAN_DECODING_BROADCAST = ScannerUtil.SCAN_DECODING_BROADCAST;
    public static final String SCAN_DECODING_DATA = ScannerUtil.SCAN_DECODING_DATA;
    public static final String SCAN_SYMBOLOGY_TYPE = ScannerUtil.SCAN_SYMBOLOGY_TYPE;
    private TextView barcodeTextView;
    private AlertDialog dialog;

    private Button backButton;
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
                addTableHeader(tableLayout);
                displayTable(barcode);
            }
        }
    };

    @SuppressLint("CheckResult")
    private void displayTable(String barcode){
        AllurApi api = new AllurApi();
        assert barcode != null;
        tableLayout.removeAllViews();
        api.getBoxInform(barcode)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    if(result.getStatus() == 200) {
                        addTableHeader(tableLayout);
                        for (BoxInformEntity b : (List<BoxInformEntity>) result.getBody()) {
                            addTableRow(tableLayout, b);
                        }
                    } else {
                        showAlert("Ошибка!", "Ящик не найден!");
                        barcodeTextView.setText("Ожидание сканирования...");
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (dialog != null && dialog.isShowing()) {
                                    dialog.dismiss();
                                }
                            }
                        }, 1000);
                    }
                        },
                        error -> {
                            Log.e("API_REQUEST", "Error: " + error.getMessage());
                        });
    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message)
                .setTitle(title)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        dialog = builder.create();
        dialog.show();
    }
    @SuppressLint({"MissingInflatedId", "CheckResult"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box);
        tableLayout = findViewById(R.id.tableLayout);
        barcodeTextView = findViewById(R.id.boxLabel);
        backButton = findViewById(R.id.menuButton);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("idBox")) {
            String idBox = intent.getStringExtra("idBox");
            barcodeTextView.setText(idBox);
            assert idBox != null;
            displayTable(idBox.substring(1));
        }

    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(dataReceiver, new IntentFilter(SCAN_DECODING_BROADCAST));
    }

    public void backClick(View view){
        Intent intent = new Intent(this, StartActivity.class);
        startActivities(new Intent[]{intent});
        finish();
    }

    private void addTableRow(TableLayout tableLayout, BoxInformEntity boxInformEntity) {
        TableRow row = new TableRow(this);
        TextView description = initTextView(boxInformEntity.getDescription(), TableRow.LayoutParams.WRAP_CONTENT, 4f);
        TextView id = initTextView(String.valueOf(boxInformEntity.getId()), description.getMaxHeight(), 2f);
        TextView count = initTextView(String.valueOf(boxInformEntity.getCount()), description.getMaxHeight(),2f);
        row.addView(id);
        row.addView(description);
        row.addView(count);

        tableLayout.addView(row);
    }

    private void addTableHeader(TableLayout tableLayout) {
        TableRow row = new TableRow(this);
        TextView idHeader = initTextView("id", TableRow.LayoutParams.WRAP_CONTENT, 2f);
        TextView descHeader = initTextView("desc", TableRow.LayoutParams.WRAP_CONTENT, 4f);
        TextView countHeader = initTextView("count", TableRow.LayoutParams.WRAP_CONTENT, 2f);

        row.addView(idHeader);
        row.addView(descHeader);
        row.addView(countHeader);

        tableLayout.addView(row);
    }

    private TextView initTextView(String text, int height, float initWeight){
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setLayoutParams(new TableRow.LayoutParams(
                0,
                height,
                initWeight
        ));
        textView.setPadding(8, 8, 8, 8);

        TableRow.LayoutParams params = (TableRow.LayoutParams) textView.getLayoutParams();
        params.setMargins(1, 1, 1, 1);
        textView.setLayoutParams(params);

        textView.setBackgroundColor(Color.WHITE);

        return textView;
    }
}