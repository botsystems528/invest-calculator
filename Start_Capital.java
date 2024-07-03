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

public class Start_Capital extends AppCompatActivity {
    private TextView textView26;
    private EditText editTextText9, editTextText10, editTextText11, editTextText13;
    private Spinner spinner3;
    private Button button14, button11;
    private SharedPreferences sharedPreferences3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_start_capital);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        textView26 = findViewById(R.id.textView26);
        button14 = findViewById(R.id.button14);
        editTextText9 = findViewById(R.id.editTextText9);
        editTextText10 = findViewById(R.id.editTextText10);
        editTextText11 = findViewById(R.id.editTextText11);
        editTextText13 = findViewById(R.id.editTextText13);
        spinner3 = findViewById(R.id.spinner3);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.investment_periods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapter);
        String period_investirovania_str = spinner3.getSelectedItem().toString();

        sharedPreferences3 = getSharedPreferences("VolumeCashPrefs", MODE_PRIVATE);
        loadPreferences();

        button14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(editTextText9.getText().toString())
                        || TextUtils.isEmpty(editTextText10.getText().toString())
                        || TextUtils.isEmpty(editTextText11.getText().toString())
                        || TextUtils.isEmpty(editTextText13.getText().toString())) {
                    textView26.setText("Все поля должны быть заполнены!");
                    return;
                }
                double vasha_cel = Double.parseDouble(editTextText9.getText().toString());
                double srok_investirovania = Double.parseDouble(editTextText10.getText().toString());
                double stavka = Double.parseDouble(editTextText11.getText().toString()) / 100;
                double dop_vlogenia = Double.parseDouble(editTextText13.getText().toString());

                String period_investirovania_str = spinner3.getSelectedItem().toString();
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
                double PMT = dop_vlogenia;

                double P = (A - PMT * (Math.pow(1 + r / n, n * t) - 1) / (r / n)) / Math.pow(1 + r / n, n * t);

                textView26.setText(String.format("Стартовая сумма: %.2f", P));

                savePreferences();
            }
        });
    }
    private void savePreferences() {
        SharedPreferences.Editor editor = sharedPreferences3.edit();
        editor.putString("editTextText9", editTextText9.getText().toString());
        editor.putString("editTextText10", editTextText10.getText().toString());
        editor.putString("editTextText11", editTextText11.getText().toString());
        editor.putString("editTextText13", editTextText13.getText().toString());
        editor.apply();
    }

    private void loadPreferences() {
        editTextText9.setText(sharedPreferences3.getString("editTextText9", ""));
        editTextText10.setText(sharedPreferences3.getString("editTextText10", ""));
        editTextText11.setText(sharedPreferences3.getString("editTextText11", ""));
        editTextText13.setText(sharedPreferences3.getString("editTextText13", ""));
    }

        public void GoBack (View v){
            Intent intent = new Intent(this, FirstActivity2.class);
            startActivity(intent);
        }

}