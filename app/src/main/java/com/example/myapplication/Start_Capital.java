package com.example.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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


import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

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

        setupNumberFormatting(editTextText9);
        setupNumberFormatting(editTextText10);
        setupNumberFormatting(editTextText11);
        setupNumberFormatting(editTextText13);

        // Настройка Spinner
        ArrayAdapter<CharSequence> adapterReinvestment = ArrayAdapter.createFromResource(this,
                R.array.investment_periods, android.R.layout.simple_spinner_item);
        adapterReinvestment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner3.setAdapter(adapterReinvestment);
        spinner3.setSelection(1);

        ArrayAdapter<CharSequence> adapterDeposit = ArrayAdapter.createFromResource(this,
                R.array.deposit_periods, android.R.layout.simple_spinner_item);
        adapterDeposit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepositPeriod.setAdapter(adapterDeposit);
        spinnerDepositPeriod.setSelection(1);

        sharedPreferences3 = getSharedPreferences("VolumeCashPrefs", MODE_PRIVATE);
        loadPreferences();

        button14.setOnClickListener(v -> calculateAndSave());
    }

    private void setupNumberFormatting(EditText editText) {
        final DecimalFormat decimalFormat = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        decimalFormat.setGroupingSize(3);
        DecimalFormatSymbols symbols = decimalFormat.getDecimalFormatSymbols();
        symbols.setGroupingSeparator(' ');
        decimalFormat.setDecimalFormatSymbols(symbols);

        editText.addTextChangedListener(new TextWatcher() {
            private boolean isFormatting = false;
            private int lastCursorPosition;
            private String previousText = "";
            private boolean isDeleting = false;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!isFormatting) {
                    previousText = s.toString();
                    lastCursorPosition = editText.getSelectionStart();
                    isDeleting = count > after;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isFormatting) return;

                isFormatting = true;

                try {
                    String original = s.toString().replaceAll("[^\\d]", "");

                    if (!original.isEmpty()) {
                        long value = Long.parseLong(original);
                        String formatted = decimalFormat.format(value);

                        // Обработка Backspace на пробеле
                        if (isDeleting && lastCursorPosition > 0 &&
                                previousText.length() > 0 &&
                                lastCursorPosition <= previousText.length() &&
                                previousText.charAt(lastCursorPosition - 1) == ' ') {

                            // Удаляем пробел и предыдущую цифру
                            String newText = previousText.substring(0, lastCursorPosition - 2) +
                                    previousText.substring(lastCursorPosition);
                            original = newText.replaceAll("[^\\d]", "");
                            value = Long.parseLong(original);
                            formatted = decimalFormat.format(value);

                            editText.setText(formatted);
                            editText.setSelection(Math.max(0, lastCursorPosition - 2));
                            isFormatting = false;
                            return;
                        }

                        if (!s.toString().equals(formatted)) {
                            editText.setText(formatted);
                            int newCursorPos = formatted.length();
                            if (isDeleting && lastCursorPosition <= formatted.length()) {
                                newCursorPos = lastCursorPosition;
                            }
                            editText.setSelection(newCursorPos);
                        }
                    } else {
                        editText.setText("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    isFormatting = false;
                }
            }
        });
    }

    private String removeSpaces(String numberWithSpaces) {
        return numberWithSpaces.replace(" ", "");
    }

    private String formatNumber(String number) {
        StringBuilder formatted = new StringBuilder();
        String cleanNumber = number.replaceAll("[^\\d]", "");

        for (int i = 0; i < cleanNumber.length(); i++) {
            if (i > 0 && (cleanNumber.length() - i) % 3 == 0) {
                formatted.append(" ");
            }
            formatted.append(cleanNumber.charAt(i));
        }
        return formatted.toString();
    }

    private void calculateAndSave() {
        try {
            if (TextUtils.isEmpty(editTextText9.getText()) ||
                    TextUtils.isEmpty(editTextText10.getText()) ||
                    TextUtils.isEmpty(editTextText11.getText()) ||
                    TextUtils.isEmpty(editTextText13.getText())) {
                textView26.setText("Все поля должны быть заполнены!");
                return;
            }

            double vasha_cel = parseNumber(removeSpaces(editTextText9.getText().toString()));
            double srok_investirovania = parseNumber(removeSpaces(editTextText10.getText().toString()));
            double stavka = parseNumber(removeSpaces(editTextText11.getText().toString())) / 100;
            double dop_vlogenia = parseNumber(removeSpaces(editTextText13.getText().toString()));

            String period_str = spinner3.getSelectedItem().toString();
            String deposit_str = spinnerDepositPeriod.getSelectedItem().toString();

            int period = getPeriodInMonths(period_str);
            int depositPeriod = getPeriodInMonths(deposit_str);

            double P = calculateStartCapital(vasha_cel, srok_investirovania, stavka,
                    dop_vlogenia, period, depositPeriod);

            textView26.setText(String.format(Locale.getDefault(), "Стартовая сумма: %.2f", P));
            savePreferences();

        } catch (NumberFormatException e) {
            textView26.setText("Ошибка ввода чисел!");
        }
    }

    private double parseNumber(String text) {
        return Double.parseDouble(text.replace(" ", ""));
    }

    private int getPeriodInMonths(String period) {
        switch (period) {
            case "не реинвестировать":
            case "без доп. вложений": return 0;
            case "раз в месяц":
            case "ежемесячно": return 1;
            case "раз в квартал":
            case "ежеквартально": return 3;
            case "раз в полгода": return 6;
            case "раз в год": return 12;
            default: return 1;
        }
    }

    private double calculateStartCapital(double targetAmount, double investmentPeriod,
                                         double annualRate, double depositAmount,
                                         int reinvestmentPeriod, int depositPeriod) {
        double monthlyRate = annualRate / 12;
        int totalMonths = (int) (investmentPeriod * 12);

        double futureValueOfDeposits = 0;
        if (depositPeriod > 0) {
            int numberOfDeposits = totalMonths / depositPeriod;
            for (int i = 1; i <= numberOfDeposits; i++) {
                int monthsLeft = totalMonths - (i - 1) * depositPeriod;
                futureValueOfDeposits += depositAmount * Math.pow(1 + monthlyRate, monthsLeft);
            }
        }

        return (targetAmount - futureValueOfDeposits) / Math.pow(1 + monthlyRate, totalMonths);
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = sharedPreferences3.edit();
        editor.putString("editTextText9", editTextText9.getText().toString().replace(" ", ""));
        editor.putString("editTextText10", editTextText10.getText().toString().replace(" ", ""));
        editor.putString("editTextText11", editTextText11.getText().toString().replace(" ", ""));
        editor.putString("editTextText13", editTextText13.getText().toString().replace(" ", ""));
        editor.apply();
    }

    private void loadPreferences() {
        editTextText9.setText(formatNumber(sharedPreferences3.getString("editTextText9", "")));
        editTextText10.setText(formatNumber(sharedPreferences3.getString("editTextText10", "")));
        editTextText11.setText(formatNumber(sharedPreferences3.getString("editTextText11", "")));
        editTextText13.setText(formatNumber(sharedPreferences3.getString("editTextText13", "")));
    }

    public void GoBack(View v) {
        Intent intent = new Intent(this, FirstActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}