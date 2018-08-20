package com.kterry.ptassessor;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Mctsib extends AppCompatActivity implements SensorEventListener{

    private SensorManager senSensorManager;
    private Sensor senAccelerometer;
    private long lastUpdate;
    private double xSqSum;
    private double ySqSum;
    private double zSqSum;
    private List<Double> x = new ArrayList<Double>(10);
    private List<Double> y = new ArrayList<Double>(10);
    private List<Double> z = new ArrayList<Double>(10);
    private List<Double> xc = new ArrayList<Double>(10);
    private List<Double> yc = new ArrayList<Double>(10);
    private List<Double> zc = new ArrayList<Double>(10);
    private List<Double> xRMSAcc = new ArrayList<Double>(10);
    private List<Double> yRMSAcc = new ArrayList<Double>(10);
    private List<Double> zRMSAcc = new ArrayList<Double>(10);
    private List<Double> xRMSAcc2 = new ArrayList<Double>(10);
    private List<Double> yRMSAcc2 = new ArrayList<Double>(10);
    private List<Double> zRMSAcc2 = new ArrayList<Double>(10);
    private double[] mn_xRMSAcc = {0, 0, 0, 0};
    private double[] mn_yRMSAcc = {0, 0, 0, 0};
    private double[] mn_zRMSAcc = {0, 0, 0, 0};
    private double[] mn_xRMSAcc2 = {0, 0, 0, 0};
    private double[] mn_yRMSAcc2 = {0, 0, 0, 0};
    private double[] mn_zRMSAcc2 = {0, 0, 0, 0};
    private List<Double> dtrnd_x = new ArrayList<Double>(10);
    private List<Double> dtrnd_y = new ArrayList<Double>(10);
    private List<Double> dtrnd_z = new ArrayList<Double>(10);
    private List<Double> dtrnd_x2 = new ArrayList<Double>(10);
    private List<Double> dtrnd_y2 = new ArrayList<Double>(10);
    private List<Double> dtrnd_z2 = new ArrayList<Double>(10);
    private List<Integer> deltaT = new ArrayList<Integer>(10);
    public List<Integer> sumDeltaT = new ArrayList<Integer>(10);


    SQLiteDatabase assessDB;
    double sum_x;
    double sum_y;
    double sum_z;
    double mn_x;
    double mn_y;
    double mn_z;

    private Boolean timerActive = false;

    private double mnDeltaT;
    private int dataDelay = 1000;
    private long startTime;
    private int recordTime;
    private int pause;

    private Date t = new Date();
    private String time = (t.getTime() / 1000 / 60 / 60) % 24 + "_" + (t.getTime() / 1000 / 60) % 60 + "_" + (t.getTime() / 1000) % 60;
    private String date = t.toString();
    private String month = date.substring(4,7);
    private String day = date.substring(8,10);
    private String year = date.substring(24,28);
    public String tableName = ("accelData" + month + "_" + day + "_" + year + "_" + time);
    private List<String> condString = Arrays.asList("EO-Firm", "EO-Compl", "EC-Firm", "EC-Compl");
    String timerStart;

/*    TextView xRMSTextView;
    TextView yRMSTextView;
    TextView zRMSTextView;
    TextView deltaTextView;*/

    Button startButton;

    private int nReps = 1;
    private int nTrials = 4;

    private int cntr = 0;
    private int endColl = 0;
    private int trialCntr;
    private int trlCntrCalc;
    EditText timerDisplay;
    EditText repsDisplay;
    EditText pauseView;
    TextView currCondView;
    CountDownTimer countDownTimer;

    MediaPlayer mPlayer;
    AudioManager audioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mctsib);
        setTitle(R.string.mctsib);
        mPlayer = MediaPlayer.create(this, R.raw.chime);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        senSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        if (senSensorManager != null) {
            senAccelerometer = senSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);
            startButton = (Button) findViewById(R.id.startButton);
            timerDisplay = (EditText) findViewById(R.id.timerDisplay);
            repsDisplay = (EditText) findViewById(R.id.repsDisplay);
            pauseView = (EditText) findViewById(R.id.pauseView);
            currCondView = (TextView) findViewById(R.id.currCondDisplay);
        } else {
            System.out.println("Sensor not available");
        }

    }

    public void updateTimer(int secondsLeft) {

        String secString = Integer.toString(secondsLeft);
        //updateDisplay();
        timerDisplay.setText(secString);
    }

    public void resetTimer() {

        countDownTimer.cancel();
        timerActive = false;
        mPlayer.reset();
        startButton.setText("Start");
        timerDisplay.setText(getString(R.string.default_time));
        pauseView.setText(getString(R.string.default_pause));
        senSensorManager.unregisterListener(this, senAccelerometer);
        //updateDisplay();
    }

    public void startTimer(View view) {
        if (timerActive) {
            resetTimer();
        } else {
            nReps = Integer.parseInt(repsDisplay.getText().toString());
            nTrials = nReps * nTrials;
            trialCntr = 1;
            currCondView.setText(condString.get(0));
            timerStart = timerDisplay.getText().toString();
            recordTime = Integer.parseInt(timerStart);
            startButton.setText("Reset");
            pause = Integer.parseInt(pauseView.getText().toString());
            mnDeltaT = 0;
            senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
            runTimer();
        }
    }

    public void runTimer(){

        if (!timerActive & trialCntr <= nTrials) {
            timerActive = true;
            cntr = 0;
            endColl = 0;
            startTime = System.currentTimeMillis();
            mPlayer.start();
            countDownTimer = new CountDownTimer((recordTime + pause) * 1000 + dataDelay +100, 1000) {

                @Override
                public void onTick(long millisUntilFinished) {

                    if (endColl == 0 && millisUntilFinished > pause * 1000 + dataDelay) {
                        updateTimer((int) (millisUntilFinished - pause * 1000 - dataDelay) / 1000);
                    }
                    else if (trialCntr < nTrials) {
                        currCondView.setText(condString.get(trialCntr % 4));
                        if (endColl == 0) {
                            trlCntrCalc = trialCntr;
                            new calcRMS().execute();
                            endColl = 1;
                        }
                        mPlayer.start();
                    }
                    else {
                        if (endColl == 0) {
                            trlCntrCalc = trialCntr;
                            new calcRMS().execute();
                            endColl = 1;
                        }
                        countDownTimer.cancel();
                        mPlayer.start();
                        onFinish();
                    }
                }

                @Override
                public void onFinish() {
                    timerActive = false;
                    if (trialCntr < nTrials) {
                        trialCntr++;
                        runTimer();
                        mPlayer.start();
                    } else {
                        resetTimer();
                        mPlayer.stop();
                    }
                }
            }.start();
        }

        else {
            resetTimer();
        }

    }

    protected void onResume () {
        super.onResume();
        senSensorManager.registerListener(this, senAccelerometer, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    public void onSensorChanged (SensorEvent sensorEvent){

        Sensor mySensor = sensorEvent.sensor;
/*        TextView xAcc = (TextView) findViewById(R.id.xTextView);
        TextView yAcc = (TextView) findViewById(R.id.yTextView);
        TextView zAcc = (TextView) findViewById(R.id.zTextView);
        xRMSTextView = (TextView) findViewById(R.id.xRMSTextView);
        yRMSTextView = (TextView) findViewById(R.id.yRMSTextView);
        zRMSTextView = (TextView) findViewById(R.id.zRMSTextView);
        deltaTextView = (TextView) findViewById(R.id.deltaTextView);*/

        if (mySensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            // x is M-L, y is sup/inf, and z is ant/post if phone in portrait orientation

            long curTime = System.currentTimeMillis();
            long startCheck = curTime - startTime;

            if (timerActive) {
                if (cntr == 0) {
                    deltaT.add(cntr, 0);
                    sumDeltaT.add(cntr, 0);
                    mnDeltaT = 0;
                } else {
                    deltaT.add(cntr, (int) (curTime - lastUpdate));
                    sumDeltaT.add(cntr, deltaT.get(cntr) + sumDeltaT.get(cntr - 1));
                    mnDeltaT = sumDeltaT.get(cntr) / (cntr);
                }
                lastUpdate = curTime;

                if (startCheck > dataDelay) {
                    x.add(cntr, (double) sensorEvent.values[0]);
                    y.add(cntr, (double) sensorEvent.values[1]);
                    z.add(cntr, (double) sensorEvent.values[2]);
                    cntr++;
                }

                /*String xText = String.format("%s ", String.format(Locale.US, "%6.2f", sensorEvent.values[0]));
                String yText = String.format("%s ", String.format(Locale.US, "%6.2f", sensorEvent.values[1]));
                String zText = String.format("%s ", String.format(Locale.US, "%6.2f", sensorEvent.values[2]));

                xAcc.setText(xText);
                yAcc.setText(yText);
                zAcc.setText(zText);*/
            }
        }
    }

    @Override
    public void onAccuracyChanged (Sensor sensor,int accuracy){}

    public class calcRMS extends AsyncTask <Object[], Void, Boolean>{
        private int window = 7;
        private int incr = (int) Math.floor(window / 2);
        @Override
        protected Boolean doInBackground(Object[]... objects) {

            xc = x;
            yc = y;
            zc = z;
            xSqSum = 0.0;
            ySqSum = 0.0;
            zSqSum = 0.0;
            double xSqSum2 = 0.0;
            double ySqSum2 = 0.0;
            double zSqSum2 = 0.0;
            sum_x = 0.0;
            sum_y = 0.0;
            sum_z = 0.0;

            for (int i = 0; i <= cntr - 1; i++) {
                sum_x = xc.get(i) + sum_x;
                sum_y = yc.get(i) + sum_y;
                sum_z = zc.get(i) + sum_z;
            }

            mn_x = sum_x / (cntr);
            mn_y = sum_y / (cntr);
            mn_z = sum_z / (cntr);

            for (int i = 0; i <= cntr - 1; i++) {
                dtrnd_x.add(i, (xc.get(i) - mn_x));
                dtrnd_y.add(i, (yc.get(i) - mn_y));
                dtrnd_z.add(i, (zc.get(i) - mn_z));
                dtrnd_x2.add(i, (xc.get(i) - mn_x));
                dtrnd_y2.add(i, (yc.get(i) - mn_y));
                dtrnd_z2.add(i, (zc.get(i) - mn_z));
            }

            new storeData().execute();

            for (int i = incr; i <= cntr - incr - 1; i++) {
                double win_sum_x = 0.0;
                double win_sum_y = 0.0;
                double win_sum_z = 0.0;
                xSqSum = Math.pow(dtrnd_x.get(i), 2) + xSqSum;
                ySqSum = Math.pow(dtrnd_y.get(i), 2) + ySqSum;
                zSqSum = Math.pow(dtrnd_z.get(i), 2) + zSqSum;
                for (int j = -incr; j < incr; j++) {
                    win_sum_x = win_sum_x + dtrnd_x.get(i + j);
                    win_sum_y = win_sum_y + dtrnd_y.get(i + j);
                    win_sum_z = win_sum_z + dtrnd_z.get(i + j);
                }
                dtrnd_x2.add(i, (win_sum_x / window));
                dtrnd_y2.add(i, (win_sum_y / window));
                dtrnd_z2.add(i, (win_sum_z / window));
                xSqSum2 = Math.pow(dtrnd_x2.get(i), 2) + xSqSum2;
                ySqSum2 = Math.pow(dtrnd_y2.get(i), 2) + ySqSum2;
                zSqSum2 = Math.pow(dtrnd_z2.get(i), 2) + zSqSum2;
            }
            Log.i("trlCntrCalc", Integer.toString(trlCntrCalc));
            Log.i("cntr", Integer.toString(cntr));
            Log.i("xsqSum", Double.toString(xSqSum));
            Log.i("ysqSum", Double.toString(ySqSum));
            Log.i("zsqSum", Double.toString(zSqSum));
            Log.i("xsqSum2", Double.toString(xSqSum2));
            Log.i("ysqSum2", Double.toString(ySqSum2));
            Log.i("zsqSum2", Double.toString(zSqSum2));

            xRMSAcc.add(trlCntrCalc - 1, Math.sqrt(xSqSum / (cntr)));
            yRMSAcc.add(trlCntrCalc - 1, Math.sqrt(ySqSum / (cntr)));
            zRMSAcc.add(trlCntrCalc - 1, Math.sqrt(zSqSum / (cntr)));

            xRMSAcc2.add(trlCntrCalc - 1, Math.sqrt(xSqSum2 / (cntr)));
            yRMSAcc2.add(trlCntrCalc - 1, Math.sqrt(ySqSum2 / (cntr)));
            zRMSAcc2.add(trlCntrCalc - 1, Math.sqrt(zSqSum2 / (cntr)));
            double[] sum_xRMSAcc = {0, 0, 0, 0};
            double[] sum_yRMSAcc = {0, 0, 0, 0};
            double[] sum_zRMSAcc = {0, 0, 0, 0};

            double[] sum_xRMSAcc2 = {0, 0, 0, 0};
            double[] sum_yRMSAcc2 = {0, 0, 0, 0};
            double[] sum_zRMSAcc2 = {0, 0, 0, 0};

            if (trlCntrCalc == 4) {
                for (int i = 0; i < nReps; i++) {
                    sum_xRMSAcc[0] = sum_xRMSAcc[0] + xRMSAcc.get(i * 4);
                    sum_xRMSAcc[1] = sum_xRMSAcc[1] + xRMSAcc.get(1 + i * 4);
                    sum_xRMSAcc[2] = sum_xRMSAcc[2] + xRMSAcc.get(2 + i * 4);
                    sum_xRMSAcc[3] = sum_xRMSAcc[3] + xRMSAcc.get(3 + i * 4);

                    sum_yRMSAcc[0] = sum_yRMSAcc[0] + yRMSAcc.get(i * 4);
                    sum_yRMSAcc[1] = sum_yRMSAcc[1] + yRMSAcc.get(1 + i * 4);
                    sum_yRMSAcc[2] = sum_yRMSAcc[2] + yRMSAcc.get(2 + i * 4);
                    sum_yRMSAcc[3] = sum_yRMSAcc[3] + yRMSAcc.get(3 + i * 4);

                    sum_zRMSAcc[0] = sum_zRMSAcc[0] + zRMSAcc.get(i * 4);
                    sum_zRMSAcc[1] = sum_zRMSAcc[1] + zRMSAcc.get(1 + i * 4);
                    sum_zRMSAcc[2] = sum_zRMSAcc[2] + zRMSAcc.get(2 + i * 4);
                    sum_zRMSAcc[3] = sum_zRMSAcc[3] + zRMSAcc.get(3 + i * 4);

                    sum_xRMSAcc2[0] = sum_xRMSAcc2[0] + xRMSAcc2.get(i * 4);
                    sum_xRMSAcc2[1] = sum_xRMSAcc2[1] + xRMSAcc2.get(1 + i * 4);
                    sum_xRMSAcc2[2] = sum_xRMSAcc2[2] + xRMSAcc2.get(2 + i * 4);
                    sum_xRMSAcc2[3] = sum_xRMSAcc2[3] + xRMSAcc2.get(3 + i * 4);

                    sum_yRMSAcc2[0] = sum_yRMSAcc2[0] + yRMSAcc2.get(i * 4);
                    sum_yRMSAcc2[1] = sum_yRMSAcc2[1] + yRMSAcc2.get(1 + i * 4);
                    sum_yRMSAcc2[2] = sum_yRMSAcc2[2] + yRMSAcc2.get(2 + i * 4);
                    sum_yRMSAcc2[3] = sum_yRMSAcc2[3] + yRMSAcc2.get(3 + i * 4);

                    sum_zRMSAcc2[0] = sum_zRMSAcc2[0] + zRMSAcc2.get(i * 4);
                    sum_zRMSAcc2[1] = sum_zRMSAcc2[1] + zRMSAcc2.get(1 + i * 4);
                    sum_zRMSAcc2[2] = sum_zRMSAcc2[2] + zRMSAcc2.get(2 + i * 4);
                    sum_zRMSAcc2[3] = sum_zRMSAcc2[3] + zRMSAcc2.get(3 + i * 4);
                }


            mn_xRMSAcc[0] = sum_xRMSAcc[0] / nReps;
            mn_xRMSAcc[1] = sum_xRMSAcc[1] / nReps;
            mn_xRMSAcc[2] = sum_xRMSAcc[2] / nReps;
            mn_xRMSAcc[3] = sum_xRMSAcc[3] / nReps;

            mn_yRMSAcc[0] = sum_yRMSAcc[0] / nReps;
            mn_yRMSAcc[1] = sum_yRMSAcc[1] / nReps;
            mn_yRMSAcc[2] = sum_yRMSAcc[2] / nReps;
            mn_yRMSAcc[3] = sum_yRMSAcc[3] / nReps;

            mn_zRMSAcc[0] = sum_zRMSAcc[0] / nReps;
            mn_zRMSAcc[1] = sum_zRMSAcc[1] / nReps;
            mn_zRMSAcc[2] = sum_zRMSAcc[2] / nReps;
            mn_zRMSAcc[3] = sum_zRMSAcc[3] / nReps;

            mn_xRMSAcc2[0] = sum_xRMSAcc2[0] / nReps;
            mn_xRMSAcc2[1] = sum_xRMSAcc2[1] / nReps;
            mn_xRMSAcc2[2] = sum_xRMSAcc2[2] / nReps;
            mn_xRMSAcc2[3] = sum_xRMSAcc2[3] / nReps;

            mn_yRMSAcc2[0] = sum_yRMSAcc2[0] / nReps;
            mn_yRMSAcc2[1] = sum_yRMSAcc2[1] / nReps;
            mn_yRMSAcc2[2] = sum_yRMSAcc2[2] / nReps;
            mn_yRMSAcc2[3] = sum_yRMSAcc2[3] / nReps;

            mn_zRMSAcc2[0] = sum_zRMSAcc2[0] / nReps;
            mn_zRMSAcc2[1] = sum_zRMSAcc2[1] / nReps;
            mn_zRMSAcc2[2] = sum_zRMSAcc2[2] / nReps;
            mn_zRMSAcc2[3] = sum_zRMSAcc2[3] / nReps;

            Log.i("x_RMS", Double.toString(xRMSAcc.get(trlCntrCalc - 1)));
            Log.i("y_RMS", Double.toString(yRMSAcc.get(trlCntrCalc - 1)));
            Log.i("z_RMS", Double.toString(zRMSAcc.get(trlCntrCalc - 1)));
            Log.i("x_RMS2", Double.toString(xRMSAcc2.get(trlCntrCalc - 1)));
            Log.i("y_RMS2", Double.toString(yRMSAcc2.get(trlCntrCalc - 1)));
            Log.i("z_RMS2", Double.toString(zRMSAcc2.get(trlCntrCalc - 1)));
        }
            Log.i("cntr", Integer.toString(cntr));

            return null;
        }
    }

    public void viewResults (View view) {
        Intent intent = new Intent(this, MctsibDisplay.class);
        intent.putExtra("xRMSAcc", (ArrayList)xRMSAcc2);
        intent.putExtra("yRMSAcc", (ArrayList)yRMSAcc2);
        intent.putExtra("zRMSAcc", (ArrayList)zRMSAcc2);
        intent.putExtra("mn_xRMSAcc", mn_xRMSAcc2);
        intent.putExtra("mn_yRMSAcc", mn_yRMSAcc2);
        intent.putExtra("mn_zRMSAcc", mn_zRMSAcc2);
        intent.putExtra("nTrials", nTrials);
        startActivity(intent);
    }

    private class storeData  extends AsyncTask<SQLiteDatabase, Void, Boolean> {

        @Override
        protected Boolean doInBackground(SQLiteDatabase... sqLiteDatabases) {

            try {
                SQLiteOpenHelper DBaseHelper = new DBHelper(Mctsib.this);
                assessDB = DBaseHelper.getWritableDatabase();

                Date now = new Date();
                SimpleDateFormat dateFormatter = new SimpleDateFormat("MM_dd_yy_HH_mm_ss");
                String timeDateTag = dateFormatter.format(now);
                String date_time = "mCTSIB_" + timeDateTag;

                String sql = "INSERT INTO mCTSIB " +
                        "(date_time, elapsedTime, x_acc, y_acc, z_acc, x_acc2, y_acc2, z_acc2) " +
                        "VALUES (?,?,?,?,?,?,?,?);";
                assessDB.beginTransaction();
                SQLiteStatement statement = assessDB.compileStatement(sql);

                for (int i = 0; i<=cntr; i++) {
                    if (i < cntr) {
                        statement.bindString(1, date_time);
                        statement.bindLong(2, sumDeltaT.get(i));
                        statement.bindDouble(3, x.get(i));
                        statement.bindDouble(4, y.get(i));
                        statement.bindDouble(5, z.get(i));
                        statement.bindDouble(6, dtrnd_x2.get(i));
                        statement.bindDouble(7, dtrnd_y2.get(i));
                        statement.bindDouble(8, dtrnd_z2.get(i));
                        statement.executeInsert();
                        statement.clearBindings();
                    } else {
                        statement.bindString(1, date_time);
                        statement.bindLong(2, sumDeltaT.get(cntr - 1));
                        statement.bindDouble(3, xRMSAcc.get(trlCntrCalc - 1));
                        statement.bindDouble(4, yRMSAcc.get(trlCntrCalc - 1));
                        statement.bindDouble(5, zRMSAcc.get(trlCntrCalc - 1));
                        statement.bindDouble(6, xRMSAcc2.get(trlCntrCalc - 1));
                        statement.bindDouble(7, yRMSAcc2.get(trlCntrCalc - 1));
                        statement.bindDouble(8, zRMSAcc2.get(trlCntrCalc - 1));
                        statement.executeInsert();
                        statement.clearBindings();
                    }
                }

                assessDB.setTransactionSuccessful();
                assessDB.endTransaction();
                //

                return true;
            }catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }
        protected void onPostExecute (Boolean success) {}
    }

    public void exportResults (View view) {

       ExportData dataExport = new ExportData();
       dataExport.exportDB(this, assessDB);
       //assessDB.close();

    }



}





