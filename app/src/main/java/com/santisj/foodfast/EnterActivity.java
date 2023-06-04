package com.santisj.foodfast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.journeyapps.barcodescanner.ScanContract;
import com.journeyapps.barcodescanner.ScanOptions;
import com.santisj.foodfast.controllers.FirebaseFoodfast;
import com.santisj.foodfast.objects.FoodItem;
import com.santisj.foodfast.objects.Table;
import com.santisj.foodfast.objects.User;
import com.santisj.foodfast.scanner.CaptureAct;
import com.santisj.foodfast.utils.FoodItemSerializer;
import com.santisj.foodfast.utils.SaveData;

public class EnterActivity extends AppCompatActivity {

    private Button btnEnterCode, btnScanQr;
    private EditText input;

    public static User user;

    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter);

        btnEnterCode = findViewById(R.id.btn_entercode);
        btnScanQr = findViewById(R.id.btn_qrcode);

        context = this;

        btnEnterCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setTitle(R.string.dialog_code_title);
                builder.setMessage(R.string.dialog_code_description);
                builder.setIcon(R.drawable.ic_key_green);

                input = new EditText(context);
                builder.setView(input);
                builder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        checkCode(input.getText().toString());
                    }
                });
                builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                builder.show();
            }
        });

        btnScanQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanCode();
            }
        });

        // Comprueba si existen datos y si es así, te entra directamente a la aplicación.
        dataExists();
    }

    private void scanCode() {
        ScanOptions options = new ScanOptions();
        options.setPrompt("Escanea el código QR de acceso al restaurante");
        options.setBeepEnabled(false);
        options.setOrientationLocked(true);
        options.setCaptureActivity(CaptureAct.class);
        scanLauncher.launch(options);
    }

    ActivityResultLauncher<ScanOptions> scanLauncher = registerForActivityResult(new ScanContract(), result -> {
        if (result.getContents() != null) {
            checkCode(result.getContents());
        }
    });

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences(SaveData.LOGGED_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new GsonBuilder().registerTypeAdapter(FoodItem.class, new FoodItemSerializer())
                .create();
        String json = gson.toJson(EnterActivity.user);
        editor.putString("UserLogged", json);
        editor.apply();
    }

    private void loadData(int tableId) {
        SharedPreferences sharedPreferences = getSharedPreferences(SaveData.LOGGED_USER, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("UserLogged", null);
        System.out.println("JSOON" + json);
        if (json != null) {
            Gson gson = new GsonBuilder().registerTypeAdapter(FoodItem.class, new FoodItemSerializer())
                    .create();
            EnterActivity.user = gson.fromJson(json, User.class);
        } else {
            user = new User(tableId);
            saveData();
        }
    }

    private void dataExists() {
        SharedPreferences sharedPreferences = getSharedPreferences(SaveData.LOGGED_USER, Context.MODE_PRIVATE);
        String json = sharedPreferences.getString("UserLogged", null);
        if (json != null) {
            Gson gson = new GsonBuilder().registerTypeAdapter(FoodItem.class, new FoodItemSerializer())
                    .create();
            EnterActivity.user = gson.fromJson(json, User.class);
            if (EnterActivity.user != null) {
                Intent intent = new Intent(EnterActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }

    private void checkCode(String code) {
        FirebaseFoodfast.tables().addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long ocurrencies = 0;
                long totalOcurrencies = snapshot.getChildrenCount();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Table table = snap.getValue(Table.class);
                    if (table != null) {
                        int tableId = Integer.parseInt(snap.getKey());
                        table.setTableAvailable(Boolean.valueOf(snap.child("isTableAvailable").getValue().toString()));
                        System.out.println(table);
                        if (table.getAccess().equals(code)) {
                            if (table.isTableAvailable()) {
                                FirebaseFoodfast.setAvailable(tableId, false);
                                enterHome(tableId);
                            } else {
                                new AlertDialog.Builder(context)
                                        .setTitle(R.string.dialog_scan_notavailable_title)
                                        .setMessage(R.string.dialog_scan_notavailable_description)
                                        .setIcon(R.drawable.navigation_error)
                                        .setPositiveButton("Ok", null).show();
                            }
                            return;
                        }
                    }
                    ocurrencies += 1;
                    if (ocurrencies == totalOcurrencies) {
                        new AlertDialog.Builder(context)
                                .setTitle(R.string.dialog_scan_failure_title)
                                .setMessage(R.string.dialog_scan_failure_description)
                                .setIcon(R.drawable.navigation_error)
                                .setPositiveButton("Ok", null).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void enterHome(int tableId) {
        loadData(tableId);
        Intent intent = new Intent(EnterActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}