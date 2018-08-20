package com.kterry.ptassessor;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.widget.Toast.*;

public class ExportData extends AppCompatActivity {


    private String fileName = createFileName();
    private static final String TAG = ExportData.class.getSimpleName();

    public static final String DB_BACKUP_DB_VERSION_KEY = "dbVersion";
    public static final String DB_BACKUP_TABLE_NAME = "table";

    /*public void exportDB(Context context, SQLiteDatabase db) {
        List<String> tables = getTablesOnDataBase(db);
        File exportFile = new File(context.getFilesDir(), fileName);
        writeCsv(exportFile, db, tables);
        Log.i("file", String.valueOf(exportFile));
        Toast.makeText(context, "DB Exported!", LENGTH_LONG).show();
    } */

    public void exportDB(Context context, SQLiteDatabase db){
        List<String> tables = getTablesOnDataBase(db);
        File exportDir = context.getExternalFilesDir(null);
        File exportFile = new File(exportDir, fileName);
        writeCsv(exportFile, db, tables);
        Log.i("file", String.valueOf(exportFile));
        Toast.makeText(context, "DB Exported!", LENGTH_LONG).show();
    }

    private static String createFileName(){
        Date now = new Date();
        java.text.SimpleDateFormat dateFormatter = new java.text.SimpleDateFormat("MM_dd_yy_HH_mm_ss", Locale.US);
        String timeDateTag = dateFormatter.format(now);
        String date_time = timeDateTag;
        return "/db_" + date_time + ".csv";
    }

    public static List<String> getTablesOnDataBase(SQLiteDatabase db){
        Cursor c = null;
        List<String> tables = new ArrayList<>();
        try{
            c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
            if (c.moveToFirst()) {
                while ( !c.isAfterLast() ) {
                    tables.add(c.getString(0));
                    c.moveToNext();
                }
            }
        }
        catch(Exception throwable){
            Log.e(TAG, "Could not get the table names from db", throwable);
        }
        finally{
            if(c!=null)
                c.close();
        }
        return tables;
    }

    private static void writeCsv(File exportFile, SQLiteDatabase db, List<String> tables){
        CSVWriter csvWrite = null;
        Cursor curCSV = null;

        try {
            csvWrite = new CSVWriter(new FileWriter(exportFile));
            writeSingleValue(csvWrite, DB_BACKUP_DB_VERSION_KEY + "=" + db.getVersion());
            for(String table: tables){
                writeSingleValue(csvWrite, DB_BACKUP_TABLE_NAME + "=" + table);
                curCSV = db.rawQuery("SELECT * FROM " + table,null);
                csvWrite.writeNext(curCSV.getColumnNames());
                while(curCSV.moveToNext()) {
                    int columns = curCSV.getColumnCount();
                    String[] columnArr = new String[columns];
                    for( int i = 0; i < columns; i++){
                        columnArr[i] = curCSV.getString(i);
                    }
                    csvWrite.writeNext(columnArr);
                }
            }
        }
        catch(Exception sqlEx) {
            Log.e(TAG, sqlEx.getMessage(), sqlEx);
        }finally {
            if(csvWrite != null){
                try {
                    csvWrite.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if( curCSV != null ){
                curCSV.close();
            }
        }
    }

    private static void writeSingleValue(CSVWriter writer, String value){
        writer.writeNext(new String[]{value});
    }

}
