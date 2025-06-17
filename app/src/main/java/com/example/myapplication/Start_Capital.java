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
    private Spinner spinner3, spinnerDepositPeriod;
    private Button button14;
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
        spinnerDepositPeriod = findViewById(R.id.spinnerDepositPeriod);

        // Настройка Spinner для периода реинвестирования
        ArrayAdapter<CharSequence> adapterReinvestment = ArrayAdapter.createFromResource(this,
                R.array.investment_periods, android.R.layout.simple_spinner_item);
        adapterReinvestment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapterReinvestment);

        // Установка значения по умолчанию для Spinner реинвестирования
        spinner3.setSelection(1); // "раз в месяц"

        // Настройка Spinner для периода дополнительных вложений
        ArrayAdapter<CharSequence> adapterDeposit = ArrayAdapter.createFromResource(this,
                R.array.deposit_periods, android.R.layout.simple_spinner_item);
        adapterDeposit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepositPeriod.setAdapter(adapterDeposit);

        // Установка значения по умолчанию для Spinner дополнительных вложений
        spinnerDepositPeriod.setSelection(1); // "раз в месяц"

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

                // Период реинвестирования
                String period_investirovania_str = spinner3.getSelectedItem().toString();
                int period_investirovania = getPeriodInMonths(period_investirovania_str);

                // Период дополнительных вложений
                String deposit_period_str = spinnerDepositPeriod.getSelectedItem().toString();
                int deposit_period = getPeriodInMonths(deposit_period_str);

                // Расчет стартового капитала
                double P = calculateStartCapital(vasha_cel, srok_investirovania, stavka, dop_vlogenia, period_investirovania, deposit_period);

                textView26.setText(String.format("Стартовая сумма: %.2f", P));

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

    // Метод для расчета стартового капитала
    private double calculateStartCapital(double targetAmount, double investmentPeriod, double annualRate, double depositAmount, int reinvestmentPeriod, int depositPeriod) {
        double monthlyRate = annualRate / 12; // Месячная ставка
        double futureValue = targetAmount;
        int totalMonths = (int) (investmentPeriod * 12); // Общее количество месяцев

        // Обратный расчет: от будущей суммы к начальной
        for (int month = totalMonths; month >= 1; month--) {
            // Отменяем начисление процентов (если реинвестирование включено)
            if (reinvestmentPeriod > 0 && month % reinvestmentPeriod == 0) {
                futureValue /= (1 + monthlyRate * reinvestmentPeriod);
            }

            // Вычитаем дополнительные вложения (если вложения включены)
            if (depositPeriod > 0 && month % depositPeriod == 0) {
                futureValue -= depositAmount;
            }
        }

        return futureValue;
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

    public void GoBack(View v) {
        Intent intent = new Intent(this, FirstActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}