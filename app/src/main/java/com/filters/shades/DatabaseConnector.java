package com.filters.shades;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

/**
 * Created by Federica on 15/03/2018.
 */

public class DatabaseConnector extends SQLiteOpenHelper {


    public DatabaseConnector(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public void queryData(String sqlString) {
        SQLiteDatabase database = getWritableDatabase();
        database.execSQL(sqlString);
    }

    public void insertData(int id, String name, int imgUrl) {
        SQLiteDatabase database = getWritableDatabase();
        String sqlString = "INSERT INTO FILTER VALUES (?, ?, ?);";

        SQLiteStatement statement = database.compileStatement(sqlString);
        statement.bindLong(1, id);
        statement.bindString(2, name);
        statement.bindLong(3, imgUrl);
        statement.execute();
    }

    public Cursor getData(String sqlString) {
        SQLiteDatabase database = getReadableDatabase();

        return database.rawQuery(sqlString, null);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }
}

