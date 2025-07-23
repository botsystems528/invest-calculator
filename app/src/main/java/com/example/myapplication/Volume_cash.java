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

public class Volume_cash extends AppCompatActivity {
    private TextView textView41;
    private EditText editTextText16, editTextText18, editTextText19, editTextText20;
    private Spinner spinner5, spinnerDepositPeriod;
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
        spinnerDepositPeriod = findViewById(R.id.spinnerDepositPeriod);

        setupNumberFormatting(editTextText16);
        setupNumberFormatting(editTextText18);
        setupNumberFormatting(editTextText19);
        setupNumberFormatting(editTextText20);

        // Настройка Spinner для периода реинвестирования
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(this,
                R.array.investment_periods, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner5.setAdapter(adapter1);

        // Установка значения по умолчанию для Spinner реинвестирования
        spinner5.setSelection(1); // "раз в месяц"

        // Настройка Spinner для периода дополнительных вложений
        ArrayAdapter<CharSequence> adapter2 = ArrayAdapter.createFromResource(this,
                R.array.deposit_periods, android.R.layout.simple_spinner_item);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepositPeriod.setAdapter(adapter2);

        // Установка значения по умолчанию для Spinner дополнительных вложений
        spinnerDepositPeriod.setSelection(1); // "раз в месяц"

        sharedPreferences = getSharedPreferences("VolumeCashPrefs", MODE_PRIVATE);
        loadPreferences();

        button16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    if (TextUtils.isEmpty(editTextText16.getText().toString())
                            || TextUtils.isEmpty(editTextText18.getText().toString())
                            || TextUtils.isEmpty(editTextText19.getText().toString())
                            || TextUtils.isEmpty(editTextText20.getText().toString())) {
                        textView41.setText("Все поля должны быть заполнены!");
                        return;
                    }

                    // Убираем пробелы перед парсингом
                    double vasha_cel = Double.parseDouble(removeSpaces(editTextText16.getText().toString()));
                    double start_capital = Double.parseDouble(removeSpaces(editTextText18.getText().toString()));
                    double srok_investirovania = Double.parseDouble(removeSpaces(editTextText19.getText().toString()));
                    double stavka = Double.parseDouble(removeSpaces(editTextText20.getText().toString())) / 100;

                    // Период реинвестирования
                    String period_investirovania_str = spinner5.getSelectedItem().toString();
                    int period_investirovania = getPeriodInMonths(period_investirovania_str);

                    // Период дополнительных вложений
                    String deposit_period_str = spinnerDepositPeriod.getSelectedItem().toString();
                    int deposit_period = getPeriodInMonths(deposit_period_str);

                    // Расчет необходимого размера пополнений
                    double PMT = calculateRequiredDeposits(vasha_cel, start_capital, srok_investirovania, stavka,
                            period_investirovania, deposit_period);

                    textView41.setText(String.format("Необходимый размер пополнений: %.2f", PMT));

                    savePreferences();
                } catch (NumberFormatException e) {
                    textView41.setText("Ошибка: введите корректные числа!");
                    e.printStackTrace();
                } catch (Exception e) {
                    textView41.setText("Ошибка при расчетах!");
                    e.printStackTrace();
                }
            }
        });
    }

    // Убирает пробелы из строки перед парсингом числа
    private String removeSpaces(String numberWithSpaces) {
        return numberWithSpaces.replace(" ", "");
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
    // Метод для преобразования периода в месяцы
    private int getPeriodInMonths(String period) {
        switch (period) {
            case "не реинвестировать":
                return 0; // Простые проценты
            case "без доп. вложений":
                return 0; // Без дополнительных вложений
            case "ежемесячно":
                return 1;
            case "ежеквартально":
                return 3;
            case "раз в полгода":
                return 6;
            case "раз в год":
                return 12;
            default:
                return 0; // По умолчанию
        }
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
    // Метод для расчета необходимого размера пополнений
    private double calculateRequiredDeposits(double targetAmount, double startCapital, double investmentPeriod, double annualRate, int reinvestmentPeriod, int depositPeriod) {
        double futureValue = startCapital;
        int totalMonths = (int) (investmentPeriod * 12); // Общее количество месяцев

        // Если реинвестирование отключено, используем простые проценты
        if (reinvestmentPeriod == 0) {
            futureValue *= (1 + annualRate * investmentPeriod);
        } else {
            // Сложные проценты с реинвестированием
            double monthlyRate = annualRate / 12;
            for (int month = 1; month <= totalMonths; month++) {
                if (month % reinvestmentPeriod == 0) {
                    futureValue *= (1 + monthlyRate * reinvestmentPeriod);
                }
            }
        }

        // Если дополнительные вложения отключены
        if (depositPeriod == 0) {
            return (targetAmount - futureValue);
        } else {
            // Расчет необходимых дополнительных вложений
            double requiredDeposits = (targetAmount - futureValue) / ((Math.pow(1 + annualRate / 12 * depositPeriod, totalMonths / depositPeriod) - 1) / (annualRate / 12 * depositPeriod));
            return requiredDeposits;
        }
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
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}