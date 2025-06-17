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
    private Spinner spinner4, spinner6;
    private Button button15;
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
        spinner6 = findViewById(R.id.spinner6);

        // Настройка Spinner для периода реинвестирования
        ArrayAdapter<CharSequence> adapterReinvestment = ArrayAdapter.createFromResource(this,
                R.array.investment_periods, android.R.layout.simple_spinner_item);
        adapterReinvestment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner4.setAdapter(adapterReinvestment);

        // Установка значения по умолчанию для Spinner реинвестирования
        spinner4.setSelection(1); // "раз в месяц"

        // Настройка Spinner для периода дополнительных вложений
        ArrayAdapter<CharSequence> adapterDeposit = ArrayAdapter.createFromResource(this,
                R.array.deposit_periods, android.R.layout.simple_spinner_item);
        adapterDeposit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner6.setAdapter(adapterDeposit);

        // Установка значения по умолчанию для Spinner дополнительных вложений
        spinner6.setSelection(1); // "раз в месяц"

        sharedPreferences2 = getSharedPreferences("VolumeCashPrefs", MODE_PRIVATE);
        loadPreferences();

        button15.setOnClickListener(new View.OnClickListener() {
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

                // Период реинвестирования
                String period_investirovania_str = spinner4.getSelectedItem().toString();
                int period_investirovania = getPeriodInMonths(period_investirovania_str);

                // Период дополнительных вложений
                String deposit_period_str = spinner6.getSelectedItem().toString();
                int deposit_period = getPeriodInMonths(deposit_period_str);

                // Вызов метода для расчета срока
                double t = calculateInvestmentPeriod(start_capital, vasha_cel, stavka, dop_vlogenia, period_investirovania, deposit_period);

                if (t == -1) {
                    textView33.setText("Цель недостижима при текущих условиях.");
                } else if (t == 0) {
                    textView33.setText("Цель уже достигнута!");
                } else {
                    textView33.setText(String.format("Срок достижения цели: %.2f года", t));
                }

                savePreferences();
            }
        });
    }

    // Метод для преобразования периода в месяцы
    private int getPeriodInMonths(String period) {
        switch (period) {
            case "не реинвестировать":
            case "без доп. вложений":
                return 0; // Период равен 0, если реинвестирование или вложения отключены
            case "раз в месяц":
            case "ежемесячно":
                return 1;
            case "раз в квартал":
            case "ежеквартально":
                return 3;
            case "раз в полгода":
                return 6;
            case "раз в год":
                return 12;
            default:
                return 1; // по умолчанию раз в месяц
        }
    }

    // Метод для расчета срока достижения цели
    private double calculateInvestmentPeriod(double startCapital, double targetAmount, double annualRate, double depositAmount, int reinvestmentPeriod, int depositPeriod) {
        if (startCapital >= targetAmount) {
            return 0; // Если цель уже достигнута, возвращаем 0 лет
        }

        double monthlyRate = annualRate / 12; // Месячная ставка
        double futureValue = startCapital;
        int months = 0;
        int maxMonths = 100 * 12; // Максимальный срок расчета (100 лет)

        while (futureValue < targetAmount && months < maxMonths) {
            // Начисление процентов (если реинвестирование включено)
            if (reinvestmentPeriod > 0 && months % reinvestmentPeriod == 0) {
                futureValue *= (1 + monthlyRate * reinvestmentPeriod);
            }

            // Добавление дополнительных вложений (если вложения включены)
            if (depositPeriod > 0 && months % depositPeriod == 0) {
                futureValue += depositAmount;
            }

            months++;
        }

        if (months >= maxMonths) {
            return -1; // Если цель не достигнута за максимальный срок, возвращаем -1
        }

        return months / 12.0; // Преобразуем месяцы в годы
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
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}