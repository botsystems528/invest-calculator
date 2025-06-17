package com.example.myapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.SharedPreferences;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.text.TextUtils;

public class ThreeActivity3 extends AppCompatActivity {
    private TextView textView14;
    private EditText editTextText, editTextText2, editTextText3, editTextText5;
    private Spinner spinner;
    private Button button14;
    private SharedPreferences sharedPreferences5;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_three3);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        textView14 = findViewById(R.id.textView14);
        button14 = findViewById(R.id.raschitat);
        editTextText = findViewById(R.id.editTextText);
        editTextText2 = findViewById(R.id.editTextText2);
        editTextText3 = findViewById(R.id.editTextText3);
        editTextText5 = findViewById(R.id.editTextText5);
        spinner = findViewById(R.id.spinner);

        // Настройка Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.investment_periods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        sharedPreferences5 = getSharedPreferences("VolumeCashPrefs", MODE_PRIVATE);
        loadPreferences();

        button14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(editTextText.getText().toString()) ||
                        TextUtils.isEmpty(editTextText2.getText().toString()) ||
                        TextUtils.isEmpty(editTextText3.getText().toString()) ||
                        TextUtils.isEmpty(editTextText5.getText().toString())) {
                    textView14.setText("Все поля должны быть заполнены!");
                    return;
                }

                float start_capital = Float.parseFloat(editTextText.getText().toString());
                float srok_investirovania = Float.parseFloat(editTextText2.getText().toString());
                float stavka = Float.parseFloat(editTextText3.getText().toString());
                float dop_vlogenia = Float.parseFloat(editTextText5.getText().toString());

                // Получение выбранного периода инвестирования
                String period_investirovania_str = spinner.getSelectedItem().toString();
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

                // Исправленная формула для расчета конечной суммы
                double finalAmount = start_capital * Math.pow(1 + stavka / (100 * period_investirovania), period_investirovania * srok_investirovania);
                for (int i = 1; i <= period_investirovania * srok_investirovania; i++) {
                    finalAmount += dop_vlogenia * Math.pow(1 + stavka / (100 * period_investirovania), period_investirovania * srok_investirovania - i);
                }

                textView14.setText(String.format("Конечная сумма: %.2f", finalAmount));

                savePreferences();
            }
        });

    }

    private void savePreferences() {
        SharedPreferences.Editor editor = sharedPreferences5.edit();
        editor.putString("editTextText", editTextText.getText().toString());
        editor.putString("editTextText2", editTextText2.getText().toString());
        editor.putString("editTextText3", editTextText3.getText().toString());
        editor.putString("editTextText5", editTextText5.getText().toString());
        editor.apply();
    }

    private void loadPreferences() {
        editTextText.setText(sharedPreferences5.getString("editTextText", ""));
        editTextText2.setText(sharedPreferences5.getString("editTextText2", ""));
        editTextText3.setText(sharedPreferences5.getString("editTextText3", ""));
        editTextText5.setText(sharedPreferences5.getString("editTextText5", ""));
    }

    public void goback(View v) {
        Intent intent = new Intent(this, FirstActivity2.class);
        startActivity(intent);
    }
}