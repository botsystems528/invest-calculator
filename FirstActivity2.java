package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class FirstActivity2 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_first2);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }
    public void startActivity(View v){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public void Finishsum(View v){
        Intent intent = new Intent(this, ThreeActivity3.class);
        startActivity(intent);
    }
    public void Stavka(View v){
        Intent intent = new Intent(this, Stavka.class);
        startActivity(intent);
    }
    public void Startcapital(View v){
        Intent intent = new Intent(this, Start_Capital.class);
        startActivity(intent);
    }
    public void SrokDostigenia(View v){
        Intent intent = new Intent(this, Srok_Dostigenia.class);
        startActivity(intent);
    }
    public void VolumeCash(View v){
        Intent intent = new Intent(this, Volume_cash.class);
        startActivity(intent);
    }
}