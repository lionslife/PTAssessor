package com.kterry.ptassessor;

import android.content.Context;
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
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;


    public class TenMeterWalk extends AppCompatActivity implements SensorEventListener{
        SQLiteDatabase assessDB;
        private int cntr;
        private int gyroCntr;
        private int cntrTmp;
        private int cntrGyroTmp;
        private int rotCntr;
        private int rotCntrTmp;
        private int gravCntr;
        Button startButton;


        private double mn_x;
        private double mn_y;
        private double mn_z;
        private double mn_zTmp;

        private double displ;
        private double vel0 = 0;
        private double vel1;
        private double mnVel;
        private double mnAcc;
        private double xMnAcc;
        private double zMnAcc;
        private double accTime0;
        private double accTime1;
        private double xAccFilt0;
        private double xAccFilt1;
        private double zAccFilt0;
        private double zAccFilt1;
        private double dt;
        private double dt2;

        private float[] gravityValues = null;
        private float[] magneticValues = null;


        private int window = 9;
        private int incr = (int) Math.floor(window / 2);


        private List<Double> dtrnd_x = new ArrayList<Double>(10);
        private List<Double> dtrnd_y = new ArrayList<Double>(10);
        private List<Double> dtrnd_z = new ArrayList<Double>(10);

        private List<Double> dtrnd_xf = new ArrayList<Double>(10);
        private List<Double> dtrnd_yf = new ArrayList<Double>(10);
        private List<Double> dtrnd_zf = new ArrayList<Double>(10);

        private SensorManager senMgr;
        private Sensor accSensor;
        private Sensor magSensor;
        private Sensor gyroSensor;
        private Sensor gravSensor;
        private Sensor rotVecSensor;
        private String senType;

        float[] rMat = new float[16];
        float[] rVals = new float[5];
        float[] gravs = new float[3];
        float[] mags = new float[3];
        float[] angVels = new float[3];
        float[] orientations = new float[3];

        private double grav;

        private double xComp;
        private double zComp;
        private double zSign;
        private double accComp;
        private double gravComp;

        private static final double pi = 3.1416;

        int delayTime = 6; // delay between start button and start walking tone
        private long startTime;
        private long lastUpdate;
        private long lastUpdate2;
        private long lastUpdate3;

        private double sum_x = 0.0;
        private double sum_y = 0.0;
        private double sum_z = 0.0;
        private double sum_xTmp = 0.0;
        private double sum_yTmp = 0.0;
        private double sum_zTmp = 0.0;

        private List<Double> xAcc = new ArrayList<Double>();
        private List<Double> yAcc = new ArrayList<Double>();
        private List<Double> zAcc = new ArrayList<Double>();
        private List<Double> xTmpAcc = new ArrayList<Double>();
        private List<Double> yTmpAcc = new ArrayList<Double>();
        private List<Double> zTmpAcc = new ArrayList<Double>();

        private float[] earthAcc = new float[16];
        private List<Double> xEarthAcc = new ArrayList<Double>();
        private List<Double> yEarthAcc = new ArrayList<Double>();
        private List<Double> zEarthAcc = new ArrayList<Double>();

        private List<Double> xRot = new ArrayList<Double>();
        private List<Double> yRot = new ArrayList<Double>();
        private List<Double> zRot = new ArrayList<Double>();
        private List<Double> xRotTmp = new ArrayList<Double>();
        private List<Double> yRotTmp = new ArrayList<Double>();
        private List<Double> zRotTmp = new ArrayList<Double>();

        private List<Double> xGrav = new ArrayList<Double>();
        private List<Double> yGrav = new ArrayList<Double>();
        private List<Double> zGrav = new ArrayList<Double>();

        private double alphaTmp;
        private List<Double> alphaGyro = new ArrayList<Double>();
        private List<Double> alphaRot = new ArrayList<Double>();

        private List<Double> xAngVel = new ArrayList<Double>();
        private List<Double> yAngVel = new ArrayList<Double>();
        private List<Double> zAngVel = new ArrayList<Double>();

        private List<Double> yTmpAngVel = new ArrayList<Double>();
        private double sumYTmpAngVel = 0.0;
        private double yAngVelf;
        private double gyroBias;

        private List<Integer> deltaT = new ArrayList<Integer>(10);
        private List<Integer> deltaTg = new ArrayList<Integer>(10);
        private List<Integer> deltaTr = new ArrayList<Integer>(10);
        private List<Integer> elapsedTime = new ArrayList<Integer>(10);
        private List<Integer> elapsedTimeRot = new ArrayList<Integer>(10);

        TextView xRMSTextView;
        TextView yRMSTextView;
        TextView zRMSTextView;
        TextView deltaTextView;
        TextView displView;

        CountDownTimer countDownTimer;
        MediaPlayer mPlayer;
        AudioManager audioManager;

        private Boolean assessActive = false;
        private Boolean startCal = false;

        Globals gv = Globals.getInstance();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_ten_meter_walk);
            startButton = findViewById(R.id.startButton);
            displView = findViewById(R.id.displView);
            setTitle(R.string.walk);
            mPlayer = MediaPlayer.create(this, R.raw.chime);
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

            senMgr = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
            if (senMgr != null) {
                accSensor = senMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
                magSensor = senMgr.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
                gyroSensor = senMgr.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
                gravSensor = senMgr.getDefaultSensor(Sensor.TYPE_GRAVITY);
                rotVecSensor = senMgr.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);

                startButton = (Button) findViewById(R.id.startButton);
            } else {
                System.out.println("Sensor not available");
            }
        }

        public void startWalk(View view) {
            if (!assessActive) {
                cntr = 0;
                gyroCntr = 0;
                cntrTmp = 0;
                cntrGyroTmp = 0;
                rotCntr = 0;
                rotCntrTmp = 0;
                gravCntr = 0;
                sum_z = 0.0;
                sum_xTmp = 0.0;
                sum_yTmp = 0.0;
                sum_zTmp = 0.0;
                sumYTmpAngVel = 0.0;
                startCal = true;
                displ = 0.0;
                vel0 = 0.0;
                displView.setText(String.format("%s", String.format(Locale.US, "%5.2f", displ)));
                startButton.setText("Calibrating");
                senMgr.registerListener(this, accSensor, senMgr.SENSOR_DELAY_FASTEST);
                senMgr.registerListener(this, magSensor,senMgr.SENSOR_DELAY_FASTEST);
                senMgr.registerListener(this, gravSensor,senMgr.SENSOR_DELAY_FASTEST);
                senMgr.registerListener(this, gyroSensor,senMgr.SENSOR_DELAY_FASTEST);
                senMgr.registerListener(this, rotVecSensor,senMgr.SENSOR_DELAY_FASTEST);
                mPlayer.start();
                countDownTimer = new CountDownTimer(delayTime * 1000 + 100, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        updateTimer((int) (millisUntilFinished) / 1000);
                    }

                    @Override
                    public void onFinish() {

                        assessActive = true;
                        startCal = false;
                        startTime = System.currentTimeMillis();
                        startButton.setText("Running");
                    }
                }.start();
            }
        }

        public void updateTimer(int secondsLeft) {

            String secString = Integer.toString(secondsLeft);
            //updateDisplay();
        }

        public void resetTimer() {
            countDownTimer.cancel();
            startButton.setText("Start");
            displView.setText(String.format("%s", String.format(Locale.US, "%5.2f", displ)));
            assessActive = false;
            startCal = false;
            //xAcc.clear();
            //yAcc.clear();
            //zAcc.clear();
            deltaT.clear();
            deltaTg.clear();
            //elapsedTime.clear();
            senMgr.unregisterListener(this, accSensor);
            senMgr.unregisterListener(this, magSensor);
            senMgr.unregisterListener(this, gyroSensor);
            senMgr.unregisterListener(this, gravSensor);
            senMgr.unregisterListener(this, rotVecSensor);
        }

        @Override
        public void onSensorChanged (SensorEvent sensorEvent) {


            switch (sensorEvent.sensor.getType()) {
                case Sensor.TYPE_ACCELEROMETER:
                    senType = "accel";
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    senType = "mag";
                    break;
                case Sensor.TYPE_GYROSCOPE:
                    senType = "gyro";
                    break;
                case Sensor.TYPE_ROTATION_VECTOR:
                    senType = "rotMat";
                    break;
                case Sensor.TYPE_GRAVITY:
                    senType = "grav";
                    break;
                default:
                    return;

            }
            if (senType.equals("grav")) {
                gravityValues = sensorEvent.values;
                if (assessActive) {
                    xGrav.add(gravCntr, (double) gravityValues[0]);
                    yGrav.add(gravCntr, (double) gravityValues[1]);
                    zGrav.add(gravCntr, (double) gravityValues[2]);
                    Log.i("gravCntr= ", Integer.toString(gravCntr));
                    gravCntr++;
                }
            }
            if (senType.equals("mag")) {
                magneticValues = sensorEvent.values;
            }

            if (senType.equals("rotMat")) {
                long curTime3 = System.currentTimeMillis();

                if (assessActive) {
                    senMgr.getRotationMatrixFromVector(
                            rMat, sensorEvent.values);

                    senMgr.getOrientation(rMat, orientations);
                    xRot.add(rotCntr, (double) orientations[0]);
                    zRot.add(rotCntr, (double) orientations[1]);
                    yRot.add(rotCntr, (double) orientations[2]);

                    alphaRot.add(rotCntr,  Math.round(yRot.get(rotCntr) * 1000.0) / 1000.0);
                    elapsedTimeRot.add(cntr, (int) (curTime3 - startTime));

                    Log.i("alphaRot= ", Double.toString(alphaRot.get(rotCntr)));

                    if (rotCntr == 0) {
                        deltaTr.add(rotCntr , 0);
                        lastUpdate3 = 0;
                    } else {
                        deltaTr.add(rotCntr , (int) (curTime3 - lastUpdate3));
                    }
                    Log.i("rotCntr= ", Integer.toString(rotCntr));
                    Log.i("deltaTr= ", Double.toString(deltaTr.get(rotCntr)));
                    Log.i("yRot= ", Double.toString(yRot.get(rotCntr)));
                    rotCntr++;

                } else if (startCal){
                    zRotTmp.add(rotCntrTmp, (double) orientations[0]);
                    xRotTmp.add(rotCntrTmp, (double) orientations[1]);
                    yRotTmp.add(rotCntrTmp, (double) orientations[2]);
                    alphaRot.add(0, alphaTmp);
                    rotCntrTmp++;
                }
                lastUpdate3 = curTime3;
            }

        /*
        xRMSTextView = (TextView) findViewById(R.id.xRMSTextView);
        yRMSTextView = (TextView) findViewById(R.id.yRMSTextView);
        zRMSTextView = (TextView) findViewById(R.id.zRMSTextView);
        deltaTextView = (TextView) findViewById(R.id.deltaTextView);*/

            if (senType.equals("gyro")) {

                long curTime2 = System.currentTimeMillis();

                if (assessActive) {
                    xAngVel.add(gyroCntr, (double) sensorEvent.values[0]);
                    yAngVel.add(gyroCntr, (double) sensorEvent.values[1]);
                    zAngVel.add(gyroCntr, (double) sensorEvent.values[2]);

                    if (gyroCntr == 0) {
                        deltaTg.add(gyroCntr, 0);
                        lastUpdate2 = 0;
                    } else {
                        deltaTg.add(gyroCntr, (int) (curTime2 - lastUpdate2));
                    }
                    Log.i("gyroCntr= ", Integer.toString(gyroCntr));
                    Log.i("deltaTg= ", Double.toString(deltaTg.get(gyroCntr)));
                    Log.i("yAngVel= ", Double.toString(yAngVel.get(gyroCntr)));
                    double win_sum_y = 0.0;
                    double win_sum_et = 0.0;

                    if (gyroCntr >= window - 1) {
                        for (int j = -2 * incr; j <= 0; j++) {
                            win_sum_y = win_sum_y + yAngVel.get(gyroCntr + j);
                            win_sum_et = win_sum_et + deltaTg.get(gyroCntr + j);
                        }

                        yAngVelf = win_sum_y / window;
                        Log.i("yAngVelf= ", Double.toString(yAngVelf));
                        dt2 = Math.round(win_sum_et / window * 1000.0) / 1000.0;
                        alphaGyro.add(gyroCntr, Math.round((alphaGyro.get(gyroCntr - 1) + (yAngVelf - gyroBias) * dt2 / 1000.0) * 10000.0) / 10000.0);
                        Log.i("alphaGyro= ", Double.toString(alphaGyro.get(gyroCntr)));
                    }
                    gyroCntr++;
                } else if (startCal){
                    yTmpAngVel.add(cntrGyroTmp, (double) sensorEvent.values[1]);

                    sumYTmpAngVel = sumYTmpAngVel + yTmpAngVel.get(cntrGyroTmp);
                    gyroBias = sumYTmpAngVel / (cntrGyroTmp + 1);
                    alphaGyro.add(0, alphaTmp);

                    cntrGyroTmp++;
                }

                lastUpdate2 = curTime2;
            }

            if (senType.equals("accel")) {
                // y is M-L, x is sup/inf, and z is ant/post if phone in landscape orientation

                long curTime = System.currentTimeMillis();
                long elTime = curTime - startTime;

                if (assessActive && rotCntr >= 1 && gravityValues != null && magneticValues != null) {


                    xAcc.add(cntr, (double) sensorEvent.values[0]);
                    yAcc.add(cntr, (double) sensorEvent.values[1]);
                    zAcc.add(cntr, (double) sensorEvent.values[2]);
//                 float[] I = new float[16];
//                 senMgr.getRotationMatrix(rMat, I, gravityValues, magneticValues);
//
//                 senMgr.getOrientation(rMat, orientations);

                    float[] deviceRelativeAcceleration = new float[4];
                    deviceRelativeAcceleration[0] = sensorEvent.values[0];
                    deviceRelativeAcceleration[1] = sensorEvent.values[1];
                    deviceRelativeAcceleration[2] = sensorEvent.values[2];
                    deviceRelativeAcceleration[3] = 0;
                    float[] inv = new float[16];

                    android.opengl.Matrix.invertM(inv, 0, rMat, 0);
                    android.opengl.Matrix.multiplyMV(earthAcc, 0, inv, 0, deviceRelativeAcceleration, 0);
                    Log.d("Acceleration", "Values: (" + earthAcc[0] + ", " + earthAcc[1] + ", " + earthAcc[2] + ")");

                    xEarthAcc.add(cntr, (double) earthAcc[0]);
                    yEarthAcc.add(cntr, (double) earthAcc[1]);
                    zEarthAcc.add(cntr, (double) earthAcc[2]);

                    xComp = (xAcc.get(cntr) + grav * Math.sin(alphaRot.get(rotCntr - 1))) * Math.cos(alphaRot.get(rotCntr - 1));
                    zComp = -(zAcc.get(cntr) - grav * Math.cos(alphaRot.get(rotCntr - 1))) * Math.sin(alphaRot.get(rotCntr - 1));
                    zSign = Math.signum(zComp);

                    Log.i("xRaw= ", Double.toString(xAcc.get(cntr)));
                    Log.i("zRaw= ", Double.toString(zAcc.get(cntr)));
                    Log.i("cntr= ", Integer.toString(cntr));
                    Log.i("xComp= ", Double.toString(xComp));
                    Log.i("zComp= ", Double.toString(zComp));

                    dtrnd_x.add(cntr, xAcc.get(cntr));
                    dtrnd_z.add(cntr, zAcc.get(cntr));

                    elapsedTime.add(cntr, (int) (curTime - startTime));

                    if (cntr == 0) {
                        deltaT.add(cntr, 0);
                        accTime0 = 0.0;
                        lastUpdate = 0;
                    } else {
                        deltaT.add(cntr, (int) (curTime - lastUpdate));
                    }

                    Log.i("deltaT= ", Double.toString(deltaT.get(cntr)));
                    lastUpdate = curTime;
                    double stopCheck = Double.valueOf(elapsedTime.get(cntr)) / 1000.0;

                    double win_sum_x = 0.0;
                    double win_sum_z = 0.0;
                    double win_sum_et = 0.0;
                    if (cntr >= window - 1) {
                        for (int j = -2 * incr; j <= 0; j++) {
                            win_sum_x = win_sum_x + dtrnd_x.get(cntr + j);
                            win_sum_z = win_sum_z + dtrnd_z.get(cntr + j);
                            win_sum_et = win_sum_et + deltaT.get(cntr + j);
                        }

                        xAccFilt1 = (win_sum_x / window );// + grav * Math.sin(alphaRot)) * Math.cos(alphaRot);
                        zAccFilt1 = (win_sum_z / window );// - grav * Math.cos(alphaRot)) * Math.sin(alphaRot);
                        dt = Math.round(win_sum_et / window * 1000.0) / 1000.0;

                        xMnAcc = (xAccFilt1 + xAccFilt0) / 2.0;
                        zMnAcc = (zAccFilt1 + zAccFilt0) / 2.0;
                        mnAcc = zSign * Math.round((Math.sqrt(Math.pow(xMnAcc, 2) + Math.pow(zMnAcc, 2)) - grav) * 500.0) / 500.0;
                        Log.i("xAcc0= ", Double.toString(xAccFilt0));
                        Log.i("xAcc1= ", Double.toString(xAccFilt1));
                        Log.i("xMnA= ", Double.toString(xMnAcc));
                        Log.i("zMnA= ", Double.toString(zMnAcc));
                        Log.i("zSign= ", Double.toString(zSign));
                        xAccFilt0 = xAccFilt1;
                        zAccFilt0 = zAccFilt1;
                        vel1 = vel0 + mnAcc * dt / 1000.0;
                        mnVel = Math.round((vel1 + vel0)/ 2.0 * 100.0) / 100.0;
                        vel0 = vel1;
                        displ =  Math.round((displ + mnVel * dt / 1000.0) * 100.0) / 100.0;
                        Log.i("win= ", Double.toString(win_sum_et));
                        Log.i("displacement= ", Double.toString(displ));
                        Log.i("vel= ", Double.toString(mnVel));
                        Log.i("mnAcc= ", Double.toString(mnAcc));
                    }

                    //displ = gv.getDispl();
                    displView.setText(String.format("%s", String.format(Locale.US, "%5.2f", displ)));
                    if (stopCheck > 15) {
                        new StoreData().execute();
                        mPlayer.start();
                        resetTimer();
                    }
                    cntr++;
                } else if (startCal && elTime > 1000){

                    xTmpAcc.add(cntrTmp, (double) sensorEvent.values[0]);
                    yTmpAcc.add(cntrTmp, (double) sensorEvent.values[1]);
                    zTmpAcc.add(cntrTmp, (double) sensorEvent.values[2]);

                    sum_xTmp = xTmpAcc.get(cntrTmp) + sum_xTmp;
                    sum_yTmp = yTmpAcc.get(cntrTmp) + sum_yTmp;
                    sum_zTmp = zTmpAcc.get(cntrTmp) + sum_zTmp;

                    mn_x = sum_xTmp / (cntrTmp + 1);
                    mn_y = sum_yTmp / (cntrTmp + 1);
                    mn_z = sum_zTmp / (cntrTmp + 1);

                    xAccFilt0 = xTmpAcc.get(cntrTmp);
                    zAccFilt0 = -(zTmpAcc.get(cntrTmp));
                    //alphaGyro = rVals[2];
                    alphaTmp = Math.asin(mn_z/grav) - pi / 2;

                    grav = Math.sqrt(Math.pow(mn_x, 2) + Math.pow(mn_y, 2) + Math.pow(mn_z, 2));

                    Log.i("grav= ", Double.toString(grav));
                    cntrTmp++;
                }
            }
        }

        @Override
        public void onAccuracyChanged (Sensor sensor,int accuracy){}

        protected void onResume () {
            super.onResume();
            xAcc.clear();
            yAcc.clear();
            zAcc.clear();
            xTmpAcc.clear();
            yTmpAcc.clear();
            zTmpAcc.clear();
            dtrnd_x.clear();
            dtrnd_z.clear();
            senMgr.registerListener( this, accSensor, SensorManager.SENSOR_DELAY_FASTEST);
            senMgr.registerListener( this, magSensor, SensorManager.SENSOR_DELAY_FASTEST);
            senMgr.registerListener( this, gyroSensor, SensorManager.SENSOR_DELAY_FASTEST);
            senMgr.registerListener( this, gravSensor, SensorManager.SENSOR_DELAY_FASTEST);
            senMgr.registerListener( this, rotVecSensor, SensorManager.SENSOR_DELAY_FASTEST);}

        public class StoreData  extends AsyncTask<SQLiteDatabase, Void, Boolean> {

            @Override
            protected Boolean doInBackground(SQLiteDatabase... sqLiteDatabases) {

//            List<Double> dtrnd_xf2 = gv.getXAcc();
//            List<Double> dtrnd_yf2 = gv.getYAcc();
//            List<Double> dtrnd_zf2 = gv.getZAcc();
//            List<Double> xRot = gv.getXRot();
//            List<Double> yRot = gv.getYRot();
//            List<Double> zRot = gv.getZRot();

                try {
                    SQLiteOpenHelper DBaseHelper = new DBHelper(TenMeterWalk.this);
                    assessDB = DBaseHelper.getWritableDatabase();

                    Date now = new Date();
                    SimpleDateFormat dateFormatter = new SimpleDateFormat("MM_dd_yy_HH_mm_ss");
                    String timeDateTag = dateFormatter.format(now);
                    String date_time = "10mWalk_" + timeDateTag;

                    String sql = "INSERT INTO TenMWalk " +
                            "(date_time, elapsedTime, elapsedTimeRot, x_acc, y_acc, z_acc, x_Eacc, y_Eacc, z_Eacc, x_rot, y_rot, z_rot, x_avel, y_avel, z_avel, alphaGyro, alphaRot) " +
                            "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);";
                    assessDB.beginTransaction();
                    SQLiteStatement statement = assessDB.compileStatement(sql);

                    for (int i = 0; i<=cntr; i++) {
                        if (i == cntr) {
                            statement.bindString(1, date_time);
                            statement.bindDouble(2, grav);
                            statement.bindDouble(3, mn_z);
                            statement.bindDouble(4, gyroBias);
                        } else{
                            statement.bindString(1, date_time);
                            statement.bindLong(2, elapsedTime.get(i));
                            statement.bindLong(3, elapsedTimeRot.get(i));
                            statement.bindDouble(4, xAcc.get(i));
                            statement.bindDouble(5, yAcc.get(i));
                            statement.bindDouble(6, zAcc.get(i));
                            statement.bindDouble(7, xEarthAcc.get(i));
                            statement.bindDouble(8, yEarthAcc.get(i));
                            statement.bindDouble(9, zEarthAcc.get(i));
                            statement.bindDouble(10, xRot.get(i));
                            statement.bindDouble(11, yRot.get(i));
                            statement.bindDouble(12, zRot.get(i));
                            statement.bindDouble(13, xAngVel.get(i));
                            statement.bindDouble(14, yAngVel.get(i));
                            statement.bindDouble(15, zAngVel.get(i));
                            statement.bindDouble(16, alphaGyro.get(i));
                            statement.bindDouble(17, alphaRot.get(i));
                        }
                        statement.executeInsert();
                        statement.clearBindings();
                    }

                    assessDB.setTransactionSuccessful();
                    assessDB.endTransaction();

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