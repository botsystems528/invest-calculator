package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Srok_Dostigenia extends AppCompatActivity {
    private TextView textView33;
    private EditText editTextText12, editTextText14, editTextText15, editTextText17;
    private Spinner spinner4;
    private Button button15, button11;
    private SharedPreferences sharedPreferences2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_srok_dostigenia);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        textView33 = findViewById(R.id.textView33);
        button15 = findViewById(R.id.button15);
        editTextText12 = findViewById(R.id.editTextText12);
        editTextText14 = findViewById(R.id.editTextText14);
        editTextText15 = findViewById(R.id.editTextText15);
        editTextText17 = findViewById(R.id.editTextText17);
        spinner4 = findViewById(R.id.spinner4);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.investment_periods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner4.setAdapter(adapter);
        String period_investirovania_str = spinner4.getSelectedItem().toString();

        sharedPreferences2 = getSharedPreferences("VolumeCashPrefs", MODE_PRIVATE);
        loadPreferences();

        button15.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(editTextText12.getText().toString())
                        || TextUtils.isEmpty(editTextText14.getText().toString())
                        || TextUtils.isEmpty(editTextText15.getText().toString())
                        || TextUtils.isEmpty(editTextText17.getText().toString())) {
                    textView33.setText("Все поля должны быть заполнены!");
                    return;
                }
                double vasha_cel = Double.parseDouble(editTextText12.getText().toString());
                double start_capital = Double.parseDouble(editTextText14.getText().toString());
                double stavka = Double.parseDouble(editTextText15.getText().toString()) / 100;
                double dop_vlogenia = Double.parseDouble(editTextText17.getText().toString());

                String period_investirovania_str = spinner4.getSelectedItem().toString();
                int period_investirovania = 12; // по умолчанию раз в месяц
                switch (period_investirovania_str) {
                    case "раз в квартал":
                        period_investirovania = 4;
                        break;
                    case "раз в полгода":
                        period_investirovania = 2;
                        break;
                    case "раз в год":
                        period_investirovania = 1;
                        break;
                }

                double n = period_investirovania;
                double s = start_capital;
                double r = stavka;
                double A = vasha_cel;
                double PMT = dop_vlogenia;

                double t = Math.log((A * r / n + PMT) / (s * r / n + PMT)) / (n * Math.log(1 + r / n));

                textView33.setText(String.format("Срок достижения цели: %.2f года", t));

                savePreferences();
            }
        });
    }
    private void savePreferences() {
        SharedPreferences.Editor editor = sharedPreferences2.edit();
        editor.putString("editTextText12", editTextText12.getText().toString());
        editor.putString("editTextText14", editTextText14.getText().toString());
        editor.putString("editTextText15", editTextText15.getText().toString());
        editor.putString("editTextText17", editTextText17.getText().toString());
        editor.apply();
    }

    private void loadPreferences() {
        editTextText12.setText(sharedPreferences2.getString("editTextText12", ""));
        editTextText14.setText(sharedPreferences2.getString("editTextText14", ""));
        editTextText15.setText(sharedPreferences2.getString("editTextText15", ""));
        editTextText17.setText(sharedPreferences2.getString("editTextText17", ""));
    }
    public void GoBack(View v) {
        Intent intent = new Intent(this, FirstActivity2.class);
        startActivity(intent);
    }
}