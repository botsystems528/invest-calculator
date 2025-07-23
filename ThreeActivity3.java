package com.example.myapplication;

import android.annotation.SuppressLint;
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

public class ThreeActivity3 extends AppCompatActivity {
    private TextView textView14;
    private EditText editTextText, editTextText2, editTextText3, editTextText5;
    private Spinner spinner, spinnerDepositPeriod;
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
        spinnerDepositPeriod = findViewById(R.id.spinnerDepositPeriod);

        // Настройка форматирования для всех EditText
        setupNumberFormatting(editTextText);
        setupNumberFormatting(editTextText2);
        setupNumberFormatting(editTextText3);
        setupNumberFormatting(editTextText5);

        // Остальной код остается без изменений
        ArrayAdapter<CharSequence> adapterReinvestment = ArrayAdapter.createFromResource(this,
                R.array.investment_periods, android.R.layout.simple_spinner_item);
        adapterReinvestment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapterReinvestment);
        spinner.setSelection(1);

        ArrayAdapter<CharSequence> adapterDeposit = ArrayAdapter.createFromResource(this,
                R.array.deposit_periods, android.R.layout.simple_spinner_item);
        adapterDeposit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepositPeriod.setAdapter(adapterDeposit);
        spinnerDepositPeriod.setSelection(1);

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

                // Получаем значения, удаляя пробелы
                float start_capital = parseFormattedNumber(removeSpaces(editTextText.getText().toString()));
                float srok_investirovania = parseFormattedNumber(removeSpaces(editTextText2.getText().toString()));
                float stavka = parseFormattedNumber(removeSpaces(editTextText3.getText().toString()));
                float dop_vlogenia = parseFormattedNumber(removeSpaces(editTextText5.getText().toString()));


                String period_investirovania_str = spinner.getSelectedItem().toString();
                int period_investirovania = getPeriodInMonths(period_investirovania_str);

                String deposit_period_str = spinnerDepositPeriod.getSelectedItem().toString();
                int deposit_period = getPeriodInMonths(deposit_period_str);

                double finalAmount = calculateFinalAmount(start_capital, srok_investirovania, stavka, dop_vlogenia, period_investirovania, deposit_period);

                textView14.setText(String.format("Конечная сумма: %.2f", finalAmount));

                savePreferences();
            }
        });
    }

    // Метод для настройки форматирования чисел
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
    // Метод для преобразования форматированного числа обратно в float
    private float parseFormattedNumber(String formatted) {
        try {
            String clean = formatted.replaceAll("[^\\d]", "");
            return Float.parseFloat(clean);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    // Остальные методы остаются без изменений
    private int getPeriodInMonths(String period) {
        switch (period) {
            case "не реинвестировать":
            case "без доп. вложений":
                return 0;
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
                return 1;
        }
    }

    private double calculateFinalAmount(double startCapital, double investmentPeriod, double annualRate, double depositAmount, int reinvestmentPeriod, int depositPeriod) {
        double monthlyRate = annualRate / 100 / 12;
        double finalAmount = startCapital;
        int totalMonths = (int) (investmentPeriod * 12);

        for (int month = 1; month <= totalMonths; month++) {
            if (reinvestmentPeriod > 0 && month % reinvestmentPeriod == 0) {
                finalAmount *= (1 + monthlyRate * reinvestmentPeriod);
            }

            if (depositPeriod > 0 && month % depositPeriod == 0) {
                finalAmount += depositAmount;
            }
        }

        return finalAmount;
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
        editTextText.setText(formatNumber(sharedPreferences5.getString("editTextText", "")));
        editTextText2.setText(formatNumber(sharedPreferences5.getString("editTextText2", "")));
        editTextText3.setText(formatNumber(sharedPreferences5.getString("editTextText3", "")));
        editTextText5.setText(formatNumber(sharedPreferences5.getString("editTextText5", "")));
    }

    public void goback(View v) {
        Intent intent = new Intent(this, FirstActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
