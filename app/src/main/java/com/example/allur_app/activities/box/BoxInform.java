package com.example.allur_app.activities.box;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
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
import com.example.allur_app.model.box.Box;
import com.example.allur_app.model.states.SoundState;
import com.example.allur_app.utils.ScannerUtil;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class BoxInform extends AppCompatActivity {

    public static final String SCAN_DECODING_BROADCAST = ScannerUtil.SCAN_DECODING_BROADCAST;
    public static final String SCAN_DECODING_DATA = ScannerUtil.SCAN_DECODING_DATA;
    private TextView status;

    private Button informButton;
    private TableLayout tableLayout;
    private AlertDialog dialog;

    @SuppressLint({"MissingInflatedId", "CheckResult"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_box_inform);
        status = findViewById(R.id.status);
        tableLayout = findViewById(R.id.tableLayout);
        informButton = findViewById(R.id.informButton);
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(getReceiver(), new IntentFilter(SCAN_DECODING_BROADCAST));
    }


    public void boxClick(View view) {
        Intent intent = new Intent(this, BoxActivity.class);
        tableLayout.removeAllViews();
        intent.putExtra("idBox", status.getText());
        startActivity(intent);
        status.setText("Ожидание сканирования...");
    }

    private BroadcastReceiver getReceiver() {
        AllurApi allurApi = new AllurApi();
        return new BroadcastReceiver() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @SuppressLint({"SetTextI18n", "CheckResult"})
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                assert action != null;
                String barcode = intent.getStringExtra(SCAN_DECODING_DATA);
                tableLayout.removeAllViews();
                assert barcode != null;
                allurApi.getBox(barcode)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            if(result.getStatus() == 200){
                                sound(SoundState.OK);
                                status.setText(barcode);
                                addTableHeader(tableLayout, (Box) result.getBody());
                                informButton.setEnabled(true);
                            }
                            else {
                                sound(SoundState.BAD);
                                showAlert("Ошибка!", "Ящик не найден!");
                                status.setText("Ожидание сканирования...");
                            }
                        }, error -> {
                            Log.e("API_REQUEST", "Error: " + error.getMessage());
                        });
            }
        };
    }
    private void addTableHeader(TableLayout tableLayout, Box box) {
        TableRow[] rows = new TableRow[3];
        TextView[] titleTexts = new TextView[3];
        TextView[] dataTexts = parseBox(box);

        titleTexts[0] = initTextView("Группа");
        titleTexts[1] = initTextView("Номер");
        titleTexts[2] = initTextView("Дата");

        for(int i = 0; i < rows.length; i++){
            rows[i] = new TableRow(this);
            rows[i].addView(titleTexts[i]);
            rows[i].addView(dataTexts[i]);
            tableLayout.addView(rows[i]);
        }
    }

    private TextView[] parseBox(Box box){
        TextView[] texts = new TextView[3];
        texts[0] = initTextView(String.valueOf(box.getGroup()));
        texts[1] = initTextView(String.valueOf(box.getNumber()));
        texts[2] = initTextView(String.valueOf(box.getDate()));
        return texts;
    }

    private TextView initTextView(String text){
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setLayoutParams(new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
        ));
        textView.setPadding(8, 8, 8, 8);
        TableRow.LayoutParams params = (TableRow.LayoutParams) textView.getLayoutParams();
        params.setMargins(1, 1, 1, 1);
        textView.setLayoutParams(params);
        textView.setTextSize(20f);
        textView.setBackgroundColor(Color.WHITE);

        return textView;
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

    private void sound(SoundState type){
        try {
            Uri uri;
            if(type == SoundState.OK){
                uri = Uri.parse("/system/media/audio/notifications/Proxima.ogg");
            } else {
                uri = Uri.parse("/system/media/audio/notifications/Rubidium.ogg");
            }
            Ringtone ringtone = RingtoneManager.getRingtone(getApplicationContext(), uri);
            ringtone.play();
        } catch (Exception e){}
    }
}