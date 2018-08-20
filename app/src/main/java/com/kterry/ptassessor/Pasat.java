package com.kterry.ptassessor;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.os.CountDownTimer;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Pasat extends AppCompatActivity {


    Button startButton;
    Button saveDataButton;
    Button reviewDataButton;

    RadioButton twoSecondButton;
    RadioButton threeSecondButton;

    RadioGroup ansButtons;
    RadioButton correctButton;
    RadioButton wrongButton;

    CheckBox practiceBox;

    TextView lastNumber;
    TextView currentNumber;
    TextView correctAnswer;
    TextView pcScore;
    TextView rawScore;
    CountDownTimer countDownTimer;
    private SQLiteDatabase assessDB;

    private int currNum;
    private int lastNum;
    private int correctNum;
    private int numCorrect = 0;
    private int numWrong = 0;
    private int totalNum = 60;
    private double pcCorrect;
    private List<Integer> numArray = new ArrayList<Integer>(61);
    private int cntr = 0;
    private int delayTime = 3;
    private Boolean assessActive = false;

    public String sql;
    String tableName = "PASAT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pasat);
        setTitle(R.string.pasat);
        startButton = (Button) findViewById(R.id.startButton);
        saveDataButton = (Button) findViewById(R.id.saveDataButton);
        reviewDataButton = (Button) findViewById(R.id.reviewDataButton);
        twoSecondButton = (RadioButton) findViewById(R.id.twoSecondButton);
        threeSecondButton = (RadioButton) findViewById(R.id.threeSecondButton);
        threeSecondButton.setChecked(true);
        practiceBox = (CheckBox) findViewById(R.id.practiceBox);
        ansButtons = (RadioGroup) findViewById(R.id.ansButtons);
        correctButton = (RadioButton) findViewById(R.id.correctButton);
        wrongButton = (RadioButton) findViewById(R.id.wrongButton);
        correctAnswer = (TextView) findViewById(R.id.correctAnswer);
        currentNumber = (TextView) findViewById(R.id.currentNumber);
        lastNumber = (TextView) findViewById(R.id.lastNumber);
        rawScore = (TextView) findViewById(R.id.rawScore);
        pcScore = (TextView) findViewById(R.id.pcScore);
    }

    public void start(View view) {
        if(!assessActive) {
            numCreator();
            if (practiceBox.isChecked()) {
                totalNum = 10;
            } else {
                totalNum = 60;
            }
            if (!assessActive) {
                if (twoSecondButton.isChecked()) {
                    delayTime = 2;
                } else if (threeSecondButton.isChecked()){
                    delayTime = 3;
                }
            }
            assessActive = true;
            startButton.setText("Reset");
            startTimer();
        } else {
            resetAssess();
        }
    }

    public void resetAssess() {
        countDownTimer.cancel();
        assessActive = false;
        cntr = 0;
        numCorrect = 0;
        numWrong = 0;
        startButton.setText("Start");
        ansButtons.clearCheck();
        currentNumber.setText("");
        correctAnswer.setText("");
        lastNumber.setText("");
        rawScore.setText(String.format(Locale.US, "%1d/%1d", 0, 0));
        pcScore.setText(String.format(Locale.US,  "%5.1f %%", 0.0));
    }

    public void numCreator() {
        currNum = (int) (Math.random()*8 +1);
        numArray.add(cntr, currNum);
        currentNumber.setText(Integer.toString(currNum));
        if (cntr > 0) {
            correctNum = currNum + numArray.get(cntr - 1);
            correctAnswer.setText(Integer.toString(correctNum));
            lastNum = numArray.get(cntr - 1);
            lastNumber.setText(Integer.toString(lastNum));
        }
    }

    public void startTimer() {
        countDownTimer = new CountDownTimer(delayTime * 1000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {

                if ((int) millisUntilFinished < ((delayTime * 1000) - 800)) {
                    currentNumber.setText(" ");
                } else {
                    numCreator();
                }
            }
            @Override
            public void onFinish() {
                if ((cntr <= totalNum) & (cntr > 0)) {
                    if (correctButton.isChecked()) {
                        numCorrect++;
                        ansButtons.clearCheck();
                    } else if (wrongButton.isChecked()){
                        numWrong++;
                        ansButtons.clearCheck();
                    }
                    pcCorrect = ((double)numCorrect / (cntr) * 100);
                    rawScore.setText(String.format(Locale.US, "%2d/%2d", numCorrect, cntr));
                    pcScore.setText(String.format(Locale.US,  "%5.1f %%", pcCorrect));
                    cntr++;
                    startTimer();
                } else if (cntr == 0) {
                    cntr++;
                    startTimer();
                } else {
                    new storeData().execute();
                }
            }
        }.start();
    }

    private class storeData  extends AsyncTask<SQLiteDatabase, Void, Boolean> {

        @Override
        protected Boolean doInBackground(SQLiteDatabase... sqLiteDatabases) {


            try {
                SQLiteOpenHelper DBaseHelper = new DBHelper(Pasat.this);
                assessDB = DBaseHelper.getWritableDatabase();

                Date now = new Date();
                SimpleDateFormat dateFormatter = new SimpleDateFormat("MM_dd_yy_HH_mm_ss");
                String timeDateTag = dateFormatter.format(now);
                String date_time = "PASAT_" + timeDateTag;

                sql = "INSERT INTO PASAT"  +
                        "(date_time, Correct, Wrong, Total, Score) " +
                        "VALUES(?,?,?,?,?);";
                assessDB.beginTransaction();
                SQLiteStatement statement = assessDB.compileStatement(sql);

                statement.bindString(1, date_time);
                statement.bindLong(2, numCorrect);
                statement.bindLong(3, numWrong);
                statement.bindLong(4, totalNum);
                statement.bindDouble(5, pcCorrect);
                statement.executeInsert();
                statement.clearBindings();

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

