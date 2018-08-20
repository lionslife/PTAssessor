package com.kterry.ptassessor;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "ptassessorDB";
    private static final int DB_VERSION = 1;

    DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        updateDatabase(db, 0, DB_VERSION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        updateDatabase(db, oldVersion, newVersion);
    }

    private void updateDatabase(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS mCTSIB(date_time TEXT, elapsedTime INTEGER," +
                    " x_acc REAL, y_acc REAL, z_acc REAL,  x_acc2 REAL, y_acc2 REAL, z_acc2 REAL)";
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            String sql = "CREATE TABLE IF NOT EXISTS PASAT(date_time TEXT, Correct INTEGER, Wrong INTEGER," +
                    " Total INTEGER, Score REAL) ";
            db.execSQL(sql);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


