package com.kterry.ptassessor;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class BalMobMenu extends AppCompatActivity {

    public void startMctsib (View view) {

        Intent intent = new Intent(getApplicationContext(), Mctsib.class);

        startActivity(intent);

    }

    public void startTenMeterWalk (View view) {

        Intent intent = new Intent(getApplicationContext(), TenMeterWalk.class);

        startActivity(intent);

    }

    public void startABC (View view) {

        Intent intent = new Intent(getApplicationContext(), ABC.class);

        startActivity(intent);

    }

    public void selectBalMobAssess (View view) {



    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_bal_mob);
        setTitle("Choose Balance/Mobility Assessment");
    }
}
