package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.content.SharedPreferences;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Stavka extends AppCompatActivity {
    private Button raschitat, button10;
    private TextView textView14, Itog;
    private EditText editTextText4, editTextText6, editTextText7, editTextText8;
    private Spinner spinner2;
    private SharedPreferences sharedPreferences4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_stavka);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Itog = findViewById(R.id.Itog);
        raschitat = findViewById(R.id.raschitat);
        editTextText4 = findViewById(R.id.editTextText4);
        editTextText6 = findViewById(R.id.editTextText6);
        editTextText7 = findViewById(R.id.editTextText7);
        editTextText8 = findViewById(R.id.editTextText8);
        spinner2 = findViewById(R.id.spinner2);

        // Настройка Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.investment_periods, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapter);

        sharedPreferences4 = getSharedPreferences("VolumeCashPrefs", MODE_PRIVATE);
        loadPreferences();

        raschitat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editTextText4.getText().toString()) ||
                        TextUtils.isEmpty(editTextText6.getText().toString()) ||
                        TextUtils.isEmpty(editTextText7.getText().toString()) ||
                        TextUtils.isEmpty(editTextText8.getText().toString())) {
                    Itog.setText("Все поля должны быть заполнены!");
                    return;
                }

                double vasha_cel = Double.parseDouble(editTextText4.getText().toString());
                double start_capital = Double.parseDouble(editTextText6.getText().toString());
                double srok_investirovania = Double.parseDouble(editTextText7.getText().toString());
                double dop_vlogenia = Double.parseDouble(editTextText8.getText().toString());

                String period_investirovania_str = spinner2.getSelectedItem().toString();
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
                double n = period_investirovania; // период реинвестирования
                double t = srok_investirovania; // срок инвестирования
                double A = vasha_cel; // сумма которую нужно накопить
                double P = start_capital; // стартовый капитал
                double PMT = dop_vlogenia; // дополнительные вложения каждый месяц

                // Примерный расчет процентной ставки
                double rate = 0.1; // начальное предположение
                double futureValue;
                double increment = 0.0001; // шаг изменения ставки

                do {
                    futureValue = P * Math.pow(1 + rate / n, n * t) + PMT * (Math.pow(1 + rate / n, n * t) - 1) / (rate / n);
                    rate += increment;
                } while (futureValue < A);

                Itog.setText(String.format("Необходимая процентная ставка: %.2f%%", rate * 100));

                savePreferences();
            }
        });
    }
    private void savePreferences() {
        SharedPreferences.Editor editor = sharedPreferences4.edit();
        editor.putString("editTextText4", editTextText4.getText().toString());
        editor.putString("editTextText6", editTextText6.getText().toString());
        editor.putString("editTextText7", editTextText7.getText().toString());
        editor.putString("editTextText8", editTextText8.getText().toString());
        editor.apply();
    }

    private void loadPreferences() {
        editTextText4.setText(sharedPreferences4.getString("editTextText4", ""));
        editTextText6.setText(sharedPreferences4.getString("editTextText6", ""));
        editTextText7.setText(sharedPreferences4.getString("editTextText7", ""));
        editTextText8.setText(sharedPreferences4.getString("editTextText8", ""));
    }

    public void GoBack(View v) {
        Intent intent = new Intent(this, FirstActivity2.class);
        startActivity(intent);
    }
}