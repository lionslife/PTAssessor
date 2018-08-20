package com.kterry.ptassessor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public void goToBalMobMenu (View view) {

        Intent intent = new Intent(getApplicationContext(), BalMobMenu.class);

        startActivity(intent);

    }

    public void goToCogMenu (View view) {

        Intent intent = new Intent(getApplicationContext(), CogMenu.class);

        startActivity(intent);

    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
}