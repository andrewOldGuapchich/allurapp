package com.example.allur_app.activities.move;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.allur_app.R;
import com.example.allur_app.model.product.ScanProduct;
import com.example.allur_app.model.states.MoveState;
import com.example.allur_app.utils.ScannerUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class MoveActivityBoxOnBox extends AppCompatActivity {
    private MoveState moveState;
    public static final String SCAN_DECODING_BROADCAST = ScannerUtil.SCAN_DECODING_BROADCAST;
    public static final String SCAN_DECODING_DATA = ScannerUtil.SCAN_DECODING_DATA;
    public static final String SCAN_SYMBOLOGY_TYPE = ScannerUtil.SCAN_SYMBOLOGY_TYPE;

    private TableLayout tableLayout;
    private TableLayout productTableLayout;
    private Button scanButton;

    private final LinkedHashMap<String, ScanProduct> productMap = new LinkedHashMap<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moveState = MoveState.CLOSE;
        setContentView(R.layout.activity_move_box_on_box);
        tableLayout = findViewById(R.id.buttonTable);
        scanButton = findViewById(R.id.productScanButton);
        productTableLayout = findViewById(R.id.productTable);
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(getDataReceiver(), new IntentFilter(SCAN_DECODING_BROADCAST));
    }

    public void onFromCellClick(View view){
        this.moveState = MoveState.BOX_FROM;
        TableRow row = tableLayout.findViewById(R.id.tableRow);
        TextView textFrom = row.findViewById(R.id.cellFrom);
        TextView textTo = row.findViewById(R.id.cellTo);
        setSelectColor(textFrom, 0xFF98fb98);
        setSelectColor(textTo, 0xFFFFFFFF);
        setSelectColor(scanButton, 0xFFffcfab);
    }

    public void onToCellClick(View view){
        this.moveState = MoveState.BOX_TO;
        TableRow row = tableLayout.findViewById(R.id.tableRow);
        TextView textFrom = row.findViewById(R.id.cellFrom);
        TextView textTo = row.findViewById(R.id.cellTo);
        setSelectColor(textTo,0xFF98fb98);
        setSelectColor(textFrom, 0xFFFFFFFF);
        setSelectColor(scanButton, 0xFFffcfab);
    }

    public void onButtonClick(View view){
        this.moveState = MoveState.PRODUCT_SCAN;
        TableRow row = tableLayout.findViewById(R.id.tableRow);
        TextView textFrom = row.findViewById(R.id.cellFrom);
        TextView textTo = row.findViewById(R.id.cellTo);
        setSelectColor(textTo,0xFFFFFFFF);
        setSelectColor(textFrom, 0xFFFFFFFF);
        setSelectColor(scanButton, 0xFF98fb98);
    }

    private void setData(String barcode){
        TableRow row = tableLayout.findViewById(R.id.tableRow);
        switch (moveState){
            case BOX_FROM:{
                TextView textFrom = row.findViewById(R.id.cellFrom);
                textFrom.setText(barcode);
                break;
            }
            case BOX_TO:{
                TextView textTo = row.findViewById(R.id.cellTo);
                textTo.setText(barcode);
                break;
            }
            case PRODUCT_SCAN:{

            }
        }
    }


    private BroadcastReceiver getDataReceiver() {
        return new BroadcastReceiver() {
            @SuppressLint({"SetTextI18n", "CheckResult"})
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                assert action != null;
                if (action.equals(SCAN_DECODING_BROADCAST)) {
                    setData(intent.getStringExtra(SCAN_DECODING_DATA));
                }
            }
        };
    }

    private void setSelectColor(View view, int color){
        view.setBackgroundColor(color);
    }

    private void createTable(String text, int count){
        TableRow tableRow = new TableRow(this);
        tableRow.setId(text.hashCode());
        CheckBox select = new CheckBox(this);
        select.setText("");
        select.setLayoutParams(new TableRow.LayoutParams(
                0, TableRow.LayoutParams.WRAP_CONTENT, 1f
        ));

        TextView name = new TextView(this);
        name.setText(text);
        name.setLayoutParams(new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                6f
        ));
        name.setPadding(8, 8, 8, 8);

        TableRow.LayoutParams params = (TableRow.LayoutParams) name.getLayoutParams();
        params.setMargins(1, 1, 1, 1);
        name.setLayoutParams(params);
        name.setTextSize(20f);
        name.setBackgroundColor(Color.WHITE);

        TextView countCell = new TextView(this);
        countCell.setId(View.generateViewId());
        countCell.setText(count);
        countCell.setLayoutParams(new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                1f
        ));
        countCell.setPadding(8, 8, 8, 8);

        TableRow.LayoutParams paramsCount = (TableRow.LayoutParams) countCell.getLayoutParams();
        paramsCount.setMargins(1, 1, 1, 1);
        countCell.setLayoutParams(paramsCount);
        countCell.setTextSize(20f);
        countCell.setBackgroundColor(Color.WHITE);

        tableRow.addView(select);
        tableRow.addView(name);
        tableRow.addView(countCell);
        tableLayout.addView(tableRow);
    }

    private void setDataOnProductTable(String name, int count){
        if(productMap.containsKey(name)){
            TableRow row = tableLayout.findViewById(name.hashCode());
            Objects.requireNonNull(productMap.get(name)).setCount(count);
        }
    }
}