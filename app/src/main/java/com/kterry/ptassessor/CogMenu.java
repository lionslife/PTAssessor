package com.kterry.ptassessor;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class CogMenu extends AppCompatActivity {

    public void startPasat (View view) {

        Intent intent = new Intent(getApplicationContext(), Pasat.class);

        startActivity(intent);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_cog);
        setTitle("Choose Cognitive Assessment");
    }
}
