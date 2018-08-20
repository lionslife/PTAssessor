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
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ABC extends AppCompatActivity {

    private List<String> specQuestion = new ArrayList<String>(16);
    private int[] score = new int[16];
    private int quesCntr = 1;
    private int totalScore = 0;
    private Boolean assessActive = true;
    private Boolean scoreSelect = false;


    TextView specQuestionText;
    TextView questView;
    FrameLayout scoreView;
    Button nextButton;
    RadioGroup ansGroup;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abc);
        setTitle(R.string.abc);
        nextButton = (Button) findViewById(R.id.nextButton);
        specQuestionText = (TextView) findViewById(R.id.spec_question);
        questView = (TextView) findViewById(R.id.questView);
        scoreView = (FrameLayout) findViewById(R.id.scoreView);
        ansGroup =(RadioGroup) findViewById(R.id.ansGroup);

        specQuestion.add(0, "...walk around the house?");
        specQuestion.add(1, "...walk up or down stairs?");
        specQuestion.add(2, "...bend over and pick up a slipper from front of a closet floor?");
        specQuestion.add(3, "...reach for a small can off a shelf at eye level?");
        specQuestion.add(4, "...stand on tip toes and reach for something above your head?");
        specQuestion.add(5, "...stand on a chair and reach for something?");
        specQuestion.add(6, "...sweep the floor?");
        specQuestion.add(7, "...walk outside the house to a car parked in the driveway?");
        specQuestion.add(8, "...get into or out of a car?");
        specQuestion.add(9, "...walk across a parking lot to the mall?");
        specQuestion.add(10, "...walk up or down a ramp?");
        specQuestion.add(11, "...walk in a crowded mall where people rapidly walk past you?");
        specQuestion.add(12, "...are bumped into by people as you walk through the mall?");
        specQuestion.add(13, "...step onto or off of an escalator while you are holding onto a railing?");
        specQuestion.add(14, "...step onto or off an escalator while holding onto parcels such that you" +
                "cannot hold onto the railing?");
        specQuestion.add(15, "...walk outside on icy sidewalks?");
    }

    public void ansSubmit(View view) {
        if (assessActive) {
            score[quesCntr - 1] = Integer.valueOf((String) view.getTag());
            scoreSelect = true;
        }
    }

    public void goBack(View view) {
        if (assessActive & quesCntr > 1) {
            quesCntr--;
            totalScore = totalScore - score[quesCntr - 1];
            specQuestionText.setText(specQuestion.get(quesCntr - 1));
            questView.setText("Question: " + Integer.toString(quesCntr) + "/16");
        }
    }

    public void nextQuestion(View view) {
        if (scoreSelect) {
            String nextButtonText = (String) nextButton.getText();
            if (nextButtonText.equals(getString(R.string.next_question))) {
                ansGroup.clearCheck();

                if (quesCntr < 16 & assessActive) {
                    totalScore = totalScore + score[quesCntr - 1];
                    quesCntr++;
                    specQuestionText.setText(specQuestion.get(quesCntr - 1));
                    questView.setText("Question: " + Integer.toString(quesCntr) + "/16");
                    scoreSelect = false;
                } else {
                    totalScore = totalScore + score[quesCntr - 1];
                    double meanScore = totalScore / 16;
                    assessActive = false;
                    questView.setText("Final Score: " + Double.toString(meanScore));
                    nextButton.setText("Repeat");
                    scoreView.setVisibility(View.INVISIBLE);
                }
            } else {
                repeatAssess();
            }
        } else {
            Toast.makeText(this,"Please select a score", Toast.LENGTH_SHORT).show();
        }

    }

    public void repeatAssess() {
        quesCntr = 1;
        totalScore = 0;
        assessActive = true;
        specQuestionText.setText(specQuestion.get(quesCntr - 1));
        questView.setText("Question: " + Integer.toString(quesCntr) + "/16");
        nextButton.setText(R.string.next_question);
        scoreView.setVisibility(View.VISIBLE);
    }

    public void restartAssess(View view) {
        quesCntr = 1;
        totalScore = 0;
        assessActive = true;
        specQuestionText.setText(specQuestion.get(quesCntr - 1));
        questView.setText("Question: " + Integer.toString(quesCntr) + "/16");
        nextButton.setText(R.string.next_question);
        scoreView.setVisibility(View.VISIBLE);
    }
}




