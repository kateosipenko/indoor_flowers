package com.indoor.flowers.database;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.indoor.flowers.database.table.FlowerTable;
import com.indoor.flowers.database.table.Table;

import java.util.HashMap;

public class DbOpenHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "indoorFlowers.db";
    private static final int DATABASE_VERSION = 1;

    private static final HashMap<Class, Table> TABLES;

    static {
        TABLES = new HashMap<>();
        TABLES.put(FlowerTable.class, new FlowerTable());
    }

    public DbOpenHelper(Context context) {
        this(context, null);
    }

    public DbOpenHelper(Context context, DatabaseErrorHandler errorHandler) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, errorHandler);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for (Table table : TABLES.values()) {
            if (!table.isVirtual()) {
                table.onCreate(db);
            }
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        int currentVersion = oldVersion;

        while (currentVersion < newVersion) {
            for (Table table : TABLES.values()) {
                if (!table.isVirtual()) {
                    if (!isTableExists(table.getName(), db)) {
                        table.onCreate(db);
                    }

                    table.onUpgrade(db, currentVersion, currentVersion + 1);
                }
            }

            currentVersion++;
        }
    }

    @SuppressWarnings("unchecked")
    public static <T extends Table> T findTable(Class tableType) {
        return (T) TABLES.get(tableType);
    }

    private boolean isTableExists(String tableName, SQLiteDatabase database) {
        Cursor cursor = database.rawQuery("select DISTINCT tbl_name from sqlite_master where tbl_name = '"
                + tableName + "'", null);
        if (cursor != null) {
            if (cursor.getCount() > 0) {
                cursor.close();
                return true;
            }
            cursor.close();
        }
        return false;
    }
}