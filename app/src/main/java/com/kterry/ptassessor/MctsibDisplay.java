package com.kterry.ptassessor;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MctsibDisplay extends AppCompatActivity {


    double[] mn_xRMSAcc;
    double[] mn_yRMSAcc;
    double[] mn_zRMSAcc;
    int nTrials;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mctsib_display);

        TextView c1t1APresult = (TextView) findViewById(R.id.c1t1APresult);
        TextView c1t2APresult = (TextView) findViewById(R.id.c1t2APresult);
        TextView c1t3APresult = (TextView) findViewById(R.id.c1t3APresult);
        TextView c2t1APresult = (TextView) findViewById(R.id.c2t1APresult);
        TextView c2t2APresult = (TextView) findViewById(R.id.c2t2APresult);
        TextView c2t3APresult = (TextView) findViewById(R.id.c2t3APresult);
        TextView c3t1APresult = (TextView) findViewById(R.id.c3t1APresult);
        TextView c3t2APresult = (TextView) findViewById(R.id.c3t2APresult);
        TextView c3t3APresult = (TextView) findViewById(R.id.c3t3APresult);
        TextView c4t1APresult = (TextView) findViewById(R.id.c4t1APresult);
        TextView c4t2APresult = (TextView) findViewById(R.id.c4t2APresult);
        TextView c4t3APresult = (TextView) findViewById(R.id.c4t3APresult);

        TextView c1AvgAPresult = (TextView) findViewById(R.id.c1AvgAPresult);
        TextView c2AvgAPresult = (TextView) findViewById(R.id.c2AvgAPresult);
        TextView c3AvgAPresult = (TextView) findViewById(R.id.c3AvgAPresult);
        TextView c4AvgAPresult = (TextView) findViewById(R.id.c4AvgAPresult);

        TextView c1t1MLresult = (TextView) findViewById(R.id.c1t1MLresult);
        TextView c1t2MLresult = (TextView) findViewById(R.id.c1t2MLresult);
        TextView c1t3MLresult = (TextView) findViewById(R.id.c1t3MLresult);
        TextView c2t1MLresult = (TextView) findViewById(R.id.c2t1MLresult);
        TextView c2t2MLresult = (TextView) findViewById(R.id.c2t2MLresult);
        TextView c2t3MLresult = (TextView) findViewById(R.id.c2t3MLresult);
        TextView c3t1MLresult = (TextView) findViewById(R.id.c3t1MLresult);
        TextView c3t2MLresult = (TextView) findViewById(R.id.c3t2MLresult);
        TextView c3t3MLresult = (TextView) findViewById(R.id.c3t3MLresult);
        TextView c4t1MLresult = (TextView) findViewById(R.id.c4t1MLresult);
        TextView c4t2MLresult = (TextView) findViewById(R.id.c4t2MLresult);
        TextView c4t3MLresult = (TextView) findViewById(R.id.c4t3MLresult);

        TextView c1AvgMLresult = (TextView) findViewById(R.id.c1AvgMLresult);
        TextView c2AvgMLresult = (TextView) findViewById(R.id.c2AvgMLresult);
        TextView c3AvgMLresult = (TextView) findViewById(R.id.c3AvgMLresult);
        TextView c4AvgMLresult = (TextView) findViewById(R.id.c4AvgMLresult);


        List<Double> xRMSAcc = new ArrayList<Double>();
        List<Double> yRMSAcc = new ArrayList<Double>();
        List<Double> zRMSAcc = new ArrayList<Double>();

        xRMSAcc = (List<Double>) getIntent().getSerializableExtra("xRMSAcc");
        yRMSAcc = (List<Double>) getIntent().getSerializableExtra("yRMSAcc");
        zRMSAcc = (List<Double>) getIntent().getSerializableExtra("zRMSAcc");
        mn_xRMSAcc = getIntent().getDoubleArrayExtra("mn_xRMSAcc");
        mn_yRMSAcc = getIntent().getDoubleArrayExtra("mn_yRMSAcc");
        mn_zRMSAcc = getIntent().getDoubleArrayExtra("mn_zRMSAcc");
        nTrials = (int) getIntent().getIntExtra("nTrials", nTrials);


        c1t1APresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", zRMSAcc.get(0))));
        c2t1APresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", zRMSAcc.get(1))));
        c3t1APresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", zRMSAcc.get(2))));
        c4t1APresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", zRMSAcc.get(3))));

        c1AvgAPresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", mn_zRMSAcc[0])));
        c2AvgAPresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", mn_zRMSAcc[1])));
        c3AvgAPresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", mn_zRMSAcc[2])));
        c4AvgAPresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", mn_zRMSAcc[3])));

        c1t1MLresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", yRMSAcc.get(0))));
        c2t1MLresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", yRMSAcc.get(1))));
        c3t1MLresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", yRMSAcc.get(2))));
        c4t1MLresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", yRMSAcc.get(3))));

        c1AvgMLresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", mn_yRMSAcc[0])));
        c2AvgMLresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", mn_yRMSAcc[1])));
        c3AvgMLresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", mn_yRMSAcc[2])));
        c4AvgMLresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", mn_yRMSAcc[3])));


        if (nTrials > 4) {
            c1t2APresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", zRMSAcc.get(4))));
            c2t2APresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", zRMSAcc.get(5))));
            c3t2APresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", zRMSAcc.get(6))));
            c4t2APresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", zRMSAcc.get(7))));

            c1t2MLresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", yRMSAcc.get(4))));
            c2t2MLresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", yRMSAcc.get(5))));
            c3t2MLresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", yRMSAcc.get(6))));
            c4t2MLresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", yRMSAcc.get(7))));
        }

        if (nTrials > 8) {
            c1t3APresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", zRMSAcc.get(8))));
            c2t3APresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", zRMSAcc.get(9))));
            c3t3APresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", zRMSAcc.get(10))));
            c4t3APresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", zRMSAcc.get(11))));

            c1t3MLresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", yRMSAcc.get(8))));
            c2t3MLresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", yRMSAcc.get(9))));
            c3t3MLresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", yRMSAcc.get(10))));
            c4t3MLresult.setText(String.format("%s", String.format(Locale.US, "%5.2f", yRMSAcc.get(11))));
        }

    }

}
