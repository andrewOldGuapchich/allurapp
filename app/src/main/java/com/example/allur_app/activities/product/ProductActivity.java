package com.example.allur_app.activities.product;

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
import com.example.allur_app.model.product.Product;
import com.example.allur_app.model.product.ProductInform;
import com.example.allur_app.utils.ScannerUtil;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ProductActivity extends AppCompatActivity {
    public static final String SCAN_DECODING_BROADCAST = ScannerUtil.SCAN_DECODING_BROADCAST;
    public static final String SCAN_DECODING_DATA = ScannerUtil.SCAN_DECODING_DATA;
    public static final String SCAN_SYMBOLOGY_TYPE = ScannerUtil.SCAN_SYMBOLOGY_TYPE;
    private TextView productTextView;
    private TableLayout tableLayout;
    private Button menuButton;
    private AlertDialog dialog;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        productTextView = findViewById(R.id.productTitle);
        tableLayout = findViewById(R.id.tableLayout);
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(getReceiver(), new IntentFilter(SCAN_DECODING_BROADCAST));
    }

    private BroadcastReceiver getReceiver(){
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
                allurApi.getProductInform(barcode)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            if(result.getStatus() == 200){
                                ProductInform productInform = (ProductInform) result.getBody();
                                Log.i("result", productInform.getName());
                                productTextView.setText(productInform.getName());
                                createTableHeader(productInform.getProductInforms());
                            }
                            else {
                                showAlert("Ошибка!", "Товар не найден!");
                                productTextView.setText("Ожидание сканирования...");
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (dialog != null && dialog.isShowing()) {
                                            dialog.dismiss();
                                        }
                                    }
                                }, 1000);
                            }
                        }, error -> {
                            Log.e("API_REQUEST", "Error: " + error.getMessage());
                        });
            }
        };
    }

    private void createTableHeader(List<Product> products){
        TextView idBoxTitle = initTextView("Ящик", TableRow.LayoutParams.WRAP_CONTENT, 1f);
        TextView countTitle = initTextView("Количество", TableRow.LayoutParams.WRAP_CONTENT, 1f);
        TableRow headerRow = new TableRow(this);

        headerRow.addView(idBoxTitle);
        headerRow.addView(countTitle);
        tableLayout.addView(headerRow);
        for (Product p : products){
            TableRow row = new TableRow(this);
            TextView idBox = initTextView(String.valueOf(p.getBoxId()), TableRow.LayoutParams.WRAP_CONTENT, 1f);
            TextView count = initTextView(String.valueOf(p.getCount()), TableRow.LayoutParams.WRAP_CONTENT, 1f);
            row.addView(idBox);
            row.addView(count);

            tableLayout.addView(row);
        }
    }

    private TextView initTextView(String text, int height, float initWeight){
        TextView textView = new TextView(this);
        textView.setText(text);
        textView.setLayoutParams(new TableRow.LayoutParams(
                0,
                height,
                initWeight));
        textView.setPadding(8,8,8,8);
        TableRow.LayoutParams params = (TableRow.LayoutParams) textView.getLayoutParams();
        params.setMargins(1, 1, 1, 1);
        textView.setLayoutParams(params);

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
}