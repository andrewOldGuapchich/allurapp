package com.example.allur_app.activities.move;

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
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.allur_app.R;
import com.example.allur_app.api.AllurApi;
import com.example.allur_app.model.product.IdModel;
import com.example.allur_app.model.product.Product;
import com.example.allur_app.model.product.ProductInform;
import com.example.allur_app.model.product.ScanProduct;
import com.example.allur_app.model.states.MoveState;
import com.example.allur_app.model.states.SoundState;
import com.example.allur_app.utils.ScannerUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MoveActivityBoxOnBox extends AppCompatActivity {
    private MoveState moveState;
    public static final String SCAN_DECODING_BROADCAST = ScannerUtil.SCAN_DECODING_BROADCAST;
    public static final String SCAN_DECODING_DATA = ScannerUtil.SCAN_DECODING_DATA;

    private TableLayout tableLayout;
    private TableLayout productTableLayout;
    private Button scanButton;
    private Button moveButton;
    private Button deleteButton;
    private AlertDialog dialog;

    private Spinner fromBox;
    private TextView toBox;
    private TableRow boxRow;

    private final LinkedHashMap<String, ScanProduct> productMap = new LinkedHashMap<>();
    private final Map<String, Integer> idMap = new HashMap<>();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        moveState = MoveState.CLOSE;
        setContentView(R.layout.activity_move_box_on_box);
        tableLayout = findViewById(R.id.buttonTable);
        scanButton = findViewById(R.id.productScanButton);
        moveButton = findViewById(R.id.moveButton);
        deleteButton = findViewById(R.id.deleteButton);
        productTableLayout = findViewById(R.id.productTable);
        fromBox = findViewById(R.id.cellFrom);
        toBox = findViewById(R.id.cellTo);
        boxRow = findViewById(R.id.tableRow);

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("idBox")) {
            String idBox = intent.getStringExtra("idBox");
            String[] value = {intent.getStringExtra("idBox")};
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, value);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            fromBox.setAdapter(adapter);
            assert idBox != null;
            scanButton.setVisibility(View.VISIBLE);
            setSelectColor(fromBox, 0xFF98fb98);
        }
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(getDataReceiver(), new IntentFilter(SCAN_DECODING_BROADCAST));
    }

    @Override
    public void onBackPressed() {
        if (!productMap.isEmpty())
            showExitConfirmationDialog();
        else
            super.onBackPressed();
    }

    public void onFromCellClick(View view){
        if(productMap.isEmpty()) {
            this.moveState = MoveState.BOX_FROM;
            setSelectColor(fromBox, 0xFF98fb98);
            setSelectColor(toBox, 0xFFFFFFFF);
            setSelectColor(scanButton, 0xFFffcfab);
        }
    }

    public void onToCellClick(View view){
        this.moveState = MoveState.BOX_TO;
        setSelectColor(toBox,0xFF98fb98);
        setSelectColor(fromBox, 0xFFFFFFFF);
        setSelectColor(scanButton, 0xFFffcfab);
    }

    public void onButtonClick(View view){
        this.moveState = MoveState.PRODUCT_SCAN;
        setSelectColor(toBox,0xFFFFFFFF);
        setSelectColor(fromBox, 0xFFFFFFFF);
        setSelectColor(scanButton, 0xFF98fb98);
    }

    public void onDeleteClick(View view){
        productTableLayout.removeAllViews();
        deleteButton.setEnabled(false);
        moveButton.setEnabled(false);
        productMap.clear();
        idMap.clear();
    }

    @SuppressLint("CheckResult")
    private void setData(String barcode){
        switch (moveState){
            case BOX_TO:{
                String boxFromText = fromBox.getSelectedItem() != null ?
                        fromBox.getSelectedItem().toString() : "";
                if(barcode.equals(boxFromText)){
                    if(productMap.isEmpty()) {
                        sound(SoundState.BAD);
                        setAdapter(new String[]{});
                        toBox.setText(barcode);
                        //showAlert("Ошибка!", "Выбери другой ящик!");
                    } else {
                        sound(SoundState.BAD);
                        showAlert("Ошибка!", "Выбери другой ящик!");
                    }
                } else
                    toBox.setText(barcode);
                break;
            }
            case BOX_FROM:{
                /**
                 * в этом месте делать проверку если код ящика то вставляем, а если код продукта то запрос к серверу и добавляем список ящиков где продукт есть
                 * если в списке есть элемент "В" его удалить
                 */
                String boxToText = toBox.getText().toString();
                if(barcode.toCharArray()[0] == 'Y' || barcode.toCharArray()[0] == 'y') {
                    if (barcode.equals(boxToText)) {
                        sound(SoundState.BAD);
                        showAlert("Ошибка!", "Выбери другой ящик!");
                        /*if(productMap.isEmpty()) {
                            sound(SoundState.BAD);
                            //showAlert("Ошибка!", "Выбери другой ящик!");
                            fromBox.setText("");
                        } else {
                            sound(SoundState.BAD);
                            showAlert("Ошибка!", "Выбери другой ящик!");
                        }*/
                    } else {
                        setAdapter(new String[]{barcode});
                        scanButton.setVisibility(View.VISIBLE);
                    }
                } else {
                    AllurApi allurApi = new AllurApi();
                    allurApi.getProductInform(barcode)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(result -> {
                                if(result.getStatus() == 200){
                                    ProductInform productInform = (ProductInform) result.getBody();
                                    String[] value = getArrayStringResult(productInform.getProductInforms(), boxToText);
                                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, value);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    fromBox.setAdapter(adapter);
                                    String boxFromText = fromBox.getSelectedItem() != null ?
                                            fromBox.getSelectedItem().toString() : "";
                                    scanButton.setVisibility(View.VISIBLE);
                                    if (boxFromText.equals(boxToText)) {
                                        sound(SoundState.BAD);
                                        showAlert("Ошибка!", "Выбери другой ящик!");
                                        scanButton.setVisibility(View.INVISIBLE);
                                    }
                                }
                                else {
                                    showAlert("Ошибка!", "Не найдено!");
                                }
                            }, error -> {
                                Log.e("API_REQUEST", "Error: " + error.getMessage());
                            });
                }
                break;
            }
            case PRODUCT_SCAN: {
                sound(SoundState.OK);
                if(!productMap.containsKey(barcode)){
                    ScanProduct scanProduct = new ScanProduct(barcode, 1, true);
                    productMap.put(barcode, scanProduct);
                    createTable(scanProduct.getId(), scanProduct.getCount());
                } else {
                    int tempCount = Objects.requireNonNull(productMap.get(barcode)).getCount();
                    Objects.requireNonNull(productMap.get(barcode)).setCount(tempCount + 1);
                    TextView temp = findViewById(idMap.get(barcode));
                    temp.setText(String.valueOf(Objects.requireNonNull(productMap.get(barcode)).getCount()));
                }
            }
        }
        String boxFromText = fromBox.getSelectedItem() != null ?
                fromBox.getSelectedItem().toString() : "";
        if(!boxFromText.equals("Из: ") &&
                !toBox.getText().equals("В: ") &&
                !productMap.isEmpty()){
            moveButton.setEnabled(true);
            deleteButton.setEnabled(true);
        }

        if (boxFromText.isEmpty()){
            scanButton.setVisibility(View.INVISIBLE);
        } else {
            scanButton.setVisibility(View.VISIBLE);
        }
    }

    private String[] getArrayStringResult(List<Product> productInforms, String textBox){
        int remIndex = -1;
        for(int i = 0; i < productInforms.size(); i++){
            if(textBox.equals("Y" + productInforms.get(i).getBoxId()))
                remIndex = i;
        }
        if (remIndex != -1) productInforms.remove(remIndex);
        String[] array = new String[productInforms.size()];
        for(int i = 0; i < productInforms.size(); i++){
            array[i] = "Y" + productInforms.get(i).getBoxId();
        }

        return array;
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

    private void createTable(String id, int count){
        TableRow tableRow = new TableRow(this);
        TextView name = new TextView(this);
        int idCell = View.generateViewId();
        name.setText(id);
        name.setLayoutParams(new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                6f
        ));
        name.setPadding(8, 8, 8, 8);

        TableRow.LayoutParams params = (TableRow.LayoutParams) name.getLayoutParams();
        params.setMargins(2, 1, 1, 1);
        name.setLayoutParams(params);
        name.setTextSize(20f);
        name.setBackgroundColor(Color.WHITE);

        EditText countCell = new EditText(this);
        countCell.setInputType(InputType.TYPE_CLASS_NUMBER);
        countCell.setKeyListener(DigitsKeyListener.getInstance("0123456789"));
        idMap.put(id, idCell);
        countCell.setId(idCell);
        countCell.setText(String.valueOf(count));
        countCell.setLayoutParams(new TableRow.LayoutParams(
                0,
                TableRow.LayoutParams.WRAP_CONTENT,
                2f
        ));
        countCell.setPadding(8, 8, 8, 8);
        TableRow.LayoutParams paramsCount = (TableRow.LayoutParams) countCell.getLayoutParams();
        paramsCount.setMargins(2, 2, 2, 2);
        countCell.setLayoutParams(paramsCount);
        countCell.setTextSize(20f);
        countCell.setBackgroundColor(Color.WHITE);

        countCell.setShowSoftInputOnFocus(false);

        countCell.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if(productMap.containsKey(id)){
                    int newValue = s.length()> 0 ? Integer.parseInt(s.toString()) : 0;
                    Objects.requireNonNull(productMap.get(id)).setCount(newValue);
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(countCell.getWindowToken(), 0);
                }
            }
        });
        tableRow.addView(name);
        tableRow.addView(countCell);
        productTableLayout.addView(tableRow);
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

    private void showExitConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Вы уверены, что хотите выйти?")
                .setPositiveButton("Да", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
                .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
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


    private void setAdapter(String[] value){
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, value);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        fromBox.setAdapter(adapter);
    }
}