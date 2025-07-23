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
import android.text.Editable;
import android.text.TextWatcher;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Stavka extends AppCompatActivity {
    private Button raschitat;
    private TextView Itog;
    private EditText editTextText4, editTextText6, editTextText7, editTextText8;
    private Spinner spinner2, spinnerDepositPeriod;
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
        spinnerDepositPeriod = findViewById(R.id.spinnerDepositPeriod);

        // Настройка форматирования для всех EditText
        setupNumberFormatting(editTextText4);
        setupNumberFormatting(editTextText6);
        setupNumberFormatting(editTextText7);
        setupNumberFormatting(editTextText8);

        // Настройка Spinner для периода реинвестирования
        ArrayAdapter<CharSequence> adapterReinvestment = ArrayAdapter.createFromResource(this,
                R.array.investment_periods, android.R.layout.simple_spinner_item);
        adapterReinvestment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner2.setAdapter(adapterReinvestment);

        // Установка значения по умолчанию для Spinner реинвестирования
        spinner2.setSelection(1); // "раз в месяц"

        // Настройка Spinner для периода дополнительных вложений
        ArrayAdapter<CharSequence> adapterDeposit = ArrayAdapter.createFromResource(this,
                R.array.deposit_periods, android.R.layout.simple_spinner_item);
        adapterDeposit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDepositPeriod.setAdapter(adapterDeposit);

        // Установка значения по умолчанию для Spinner дополнительных вложений
        spinnerDepositPeriod.setSelection(1); // "раз в месяц"

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

                double vasha_cel = parseFormattedNumber(editTextText4.getText().toString());
                double start_capital = parseFormattedNumber(editTextText6.getText().toString());
                double srok_investirovania = parseFormattedNumber(editTextText7.getText().toString());
                double dop_vlogenia = parseFormattedNumber(editTextText8.getText().toString());

                // Период реинвестирования
                String period_investirovania_str = spinner2.getSelectedItem().toString();
                int period_investirovania = getPeriodInMonths(period_investirovania_str);

                // Период дополнительных вложений
                String deposit_period_str = spinnerDepositPeriod.getSelectedItem().toString();
                int deposit_period = getPeriodInMonths(deposit_period_str);

                // Подбор процентной ставки
                double rate = calculateRequiredRate(start_capital, vasha_cel, srok_investirovania, dop_vlogenia, period_investirovania, deposit_period);

                // Вывод результата
                if (rate == -1) {
                    Itog.setText("Цель недостижима даже при максимальной ставке.");
                } else {
                    Itog.setText(String.format("Необходимая процентная ставка: %.2f%%", rate * 100));
                }

                savePreferences();
            }
        });
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

    private float parseFormattedNumber(String formatted) {
        try {
            // Удаляем все пробелы и другие нецифровые символы
            String clean = formatted.replaceAll("[^\\d]", "");
            return Float.parseFloat(clean);
        } catch (NumberFormatException e) {
            return 0;
        }
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

    // Метод для расчета необходимой процентной ставки
    private double calculateRequiredRate(double startCapital, double targetAmount, double investmentPeriod, double depositAmount, int reinvestmentPeriod, int depositPeriod) {
        double lowRate = 0.0; // Минимальная ставка
        double highRate = 3.0; // Максимальная ставка (300%)
        double rate = 0.0; // Найденная ставка
        double precision = 0.0001; // Точность

        // Проверка, достижима ли цель даже при максимальной ставке
        double maxFutureValue = calculateFutureValue(startCapital, targetAmount, investmentPeriod, depositAmount, reinvestmentPeriod, depositPeriod, highRate);
        if (maxFutureValue < targetAmount) {
            return -1; // Цель недостижима
        }

        // Бинарный поиск ставки
        while (highRate - lowRate > precision) {
            rate = (lowRate + highRate) / 2;
            double futureValue = calculateFutureValue(startCapital, targetAmount, investmentPeriod, depositAmount, reinvestmentPeriod, depositPeriod, rate);

            if (futureValue < targetAmount) {
                lowRate = rate; // Увеличиваем ставку
            } else {
                highRate = rate; // Уменьшаем ставку
            }
        }

        return rate;
    }

    // Метод для расчета будущей стоимости
    private double calculateFutureValue(double startCapital, double targetAmount, double investmentPeriod, double depositAmount, int reinvestmentPeriod, int depositPeriod, double rate) {
        double futureValue = startCapital;
        int totalMonths = (int) (investmentPeriod * 12); // Общее количество месяцев

        for (int month = 1; month <= totalMonths; month++) {
            // Начисление процентов (если реинвестирование включено)
            if (reinvestmentPeriod > 0 && month % reinvestmentPeriod == 0) {
                futureValue *= (1 + rate / 12 * reinvestmentPeriod);
            }

            // Добавление дополнительных вложений (если вложения включены)
            if (depositPeriod > 0 && month % depositPeriod == 0) {
                futureValue += depositAmount;
            }
        }

        return futureValue;
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
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
