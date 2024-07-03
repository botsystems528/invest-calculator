package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Volume_cash extends AppCompatActivity {
    private TextView textView41;
    private EditText editTextText16, editTextText18, editTextText19, editTextText20;
    private Spinner spinner5, spinner6;
    private Button button16, button11;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_volume_cash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        textView41 = findViewById(R.id.textView41);
        button16 = findViewById(R.id.button16);
        editTextText16 = findViewById(R.id.editTextText16);
        editTextText18 = findViewById(R.id.editTextText18);
        editTextText19 = findViewById(R.id.editTextText19);
        editTextText20 = findViewById(R.id.editTextText20);

        spinner5 = findViewById(R.id.spinner5);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.investment_periods, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner5.setAdapter(adapter1);

        sharedPreferences = getSharedPreferences("VolumeCashPrefs", MODE_PRIVATE);
        loadPreferences();

        button16.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(editTextText16.getText().toString())
                        || TextUtils.isEmpty(editTextText18.getText().toString())
                        || TextUtils.isEmpty(editTextText19.getText().toString())
                        || TextUtils.isEmpty(editTextText20.getText().toString())) {
                    textView41.setText("Все поля должны быть заполнены!");
                    return;
                }
                double vasha_cel = Double.parseDouble(editTextText16.getText().toString());
                double start_capital = Double.parseDouble(editTextText18.getText().toString());
                double srok_investirovania = Double.parseDouble(editTextText19.getText().toString());
                double stavka = Double.parseDouble(editTextText20.getText().toString()) / 100;

                String period_investirovania_str = spinner5.getSelectedItem().toString();
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
                double t = srok_investirovania;
                double r = stavka;
                double A = vasha_cel;
                double P = start_capital;

                double PMT = (A - P * Math.pow(1 + r / n, n * t)) / ((Math.pow(1 + r / n, n * t) - 1) / (r / n));

                textView41.setText(String.format("Необходимый размер пополнений: %.2f", PMT));

                savePreferences();
            }
        });
    }
    private void savePreferences() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("editTextText16", editTextText16.getText().toString());
        editor.putString("editTextText18", editTextText18.getText().toString());
        editor.putString("editTextText19", editTextText19.getText().toString());
        editor.putString("editTextText20", editTextText20.getText().toString());
        editor.apply();
    }

    private void loadPreferences() {
        editTextText16.setText(sharedPreferences.getString("editTextText16", ""));
        editTextText18.setText(sharedPreferences.getString("editTextText18", ""));
        editTextText19.setText(sharedPreferences.getString("editTextText19", ""));
        editTextText20.setText(sharedPreferences.getString("editTextText20", ""));
    }

    public void GoBack(View v) {
        Intent intent = new Intent(this, FirstActivity2.class);
        startActivity(intent);
    }
}