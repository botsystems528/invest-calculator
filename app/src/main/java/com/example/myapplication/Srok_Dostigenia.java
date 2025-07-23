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

        setupNumberFormatting(editTextText12);
        setupNumberFormatting(editTextText14);
        setupNumberFormatting(editTextText15);
        setupNumberFormatting(editTextText17);

        // Настройка Spinner для периода реинвестирования
        ArrayAdapter<CharSequence> adapterReinvestment = ArrayAdapter.createFromResource(this,
                R.array.investment_periods, android.R.layout.simple_spinner_item);
        adapterReinvestment.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner4.setAdapter(adapterReinvestment);
        spinner4.setSelection(1); // "раз в месяц"

        // Настройка Spinner для периода дополнительных вложений
        ArrayAdapter<CharSequence> adapterDeposit = ArrayAdapter.createFromResource(this,
                R.array.deposit_periods, android.R.layout.simple_spinner_item);
        adapterDeposit.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner6.setAdapter(adapterDeposit);
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

                double vasha_cel = parseNumber(editTextText12.getText().toString());
                double start_capital = parseNumber(editTextText14.getText().toString());
                double stavka = parseNumber(editTextText15.getText().toString()) / 100;
                double dop_vlogenia = parseNumber(editTextText17.getText().toString());

                String period_investirovania_str = spinner4.getSelectedItem().toString();
                int period_investirovania = getPeriodInMonths(period_investirovania_str);

                String deposit_period_str = spinner6.getSelectedItem().toString();
                int deposit_period = getPeriodInMonths(deposit_period_str);

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

    private double calculateInvestmentPeriod(double startCapital, double targetAmount, double annualRate,
                                             double depositAmount, int reinvestmentPeriod, int depositPeriod) {
        if (startCapital >= targetAmount) {
            return 0;
        }

        double monthlyRate = annualRate / 12;
        double futureValue = startCapital;
        int months = 0;
        int maxMonths = 100 * 12;

        while (futureValue < targetAmount && months < maxMonths) {
            if (reinvestmentPeriod > 0 && months % reinvestmentPeriod == 0) {
                futureValue *= (1 + monthlyRate * reinvestmentPeriod);
            }

            if (depositPeriod > 0 && months % depositPeriod == 0) {
                futureValue += depositAmount;
            }

            months++;
        }

        return months >= maxMonths ? -1 : months / 12.0;
    }

    private void savePreferences() {
        SharedPreferences.Editor editor = sharedPreferences2.edit();
        editor.putString("editTextText12", editTextText12.getText().toString().replace(" ", ""));
        editor.putString("editTextText14", editTextText14.getText().toString().replace(" ", ""));
        editor.putString("editTextText15", editTextText15.getText().toString().replace(" ", ""));
        editor.putString("editTextText17", editTextText17.getText().toString().replace(" ", ""));
        editor.apply();
    }

    private void loadPreferences() {
        editTextText12.setText(formatNumber(sharedPreferences2.getString("editTextText12", "")));
        editTextText14.setText(formatNumber(sharedPreferences2.getString("editTextText14", "")));
        editTextText15.setText(formatNumber(sharedPreferences2.getString("editTextText15", "")));
        editTextText17.setText(formatNumber(sharedPreferences2.getString("editTextText17", "")));
    }

    private String formatNumber(String number) {
        if (number.isEmpty()) return "";
        String cleanNumber = number.replaceAll("[^\\d]", "");
        StringBuilder formatted = new StringBuilder();

        for (int i = 0; i < cleanNumber.length(); i++) {
            if (i > 0 && (cleanNumber.length() - i) % 3 == 0) {
                formatted.append(" ");
            }
            formatted.append(cleanNumber.charAt(i));
        }
        return formatted.toString();
    }

    public void GoBack(View v) {
        Intent intent = new Intent(this, FirstActivity2.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}