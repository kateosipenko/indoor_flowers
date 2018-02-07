package com.indoor.flowers.database.provider;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;

import com.evgeniysharafan.utils.Utils;
import com.indoor.flowers.FlowersApp;
import com.indoor.flowers.database.Columns;
import com.indoor.flowers.database.FlowersDatabase;

public abstract class DatabaseProvider {

    public static final int DEFAULT_ID = -1;

    protected FlowersDatabase database;

    private Context context;

    public DatabaseProvider(Context context) {
        this.context = context;
        this.database = FlowersApp.getDatabase();
    }

    public boolean isAttached() {
        return database != null;
    }

    public void unbind() {
        if (this.database != null && this.database.inTransaction()) {
            this.database.endTransaction();
        }

        this.context = null;
        this.database = null;
    }

    public boolean hasConnection() {
        return database != null;
    }

    public static String prepareSearchString(String data) {
        return "%" + data + "%";
    }

    public void switchSearchCaseSensitive(boolean isSensitive) {
        database.getOpenHelper().getWritableDatabase().execSQL("PRAGMA case_sensitive_like = " + isSensitive + ";");
    }

    /**
     * In roofing application not set id has value -1.
     * In Room database If the field type is long or int
     * (or its TypeConverter converts it to a long or int),
     * Insert methods treat 0 as not-set while inserting the item.
     *
     * @param id Id of inserting entity
     * @return Invalidated id.
     */
    public static int invalidateIdForInsert(int id) {
        return id == DEFAULT_ID ? 0 : id;
    }

    public static long invalidateIdForInsert(long id) {
        return id == DEFAULT_ID ? 0 : id;
    }

    /**
     * In roofing application not set id has value -1.
     * In Room database If the field type is long or int
     * (or its TypeConverter converts it to a long or int),
     * Insert methods treat 0 as not-set while inserting the item.
     *
     * @param contentValues Inserting entity
     */
    static void invalidateIdForInsert(ContentValues contentValues) {
        if (contentValues != null && contentValues.containsKey(Columns.ID)) {
            int id = contentValues.getAsInteger(Columns.ID);
            id = invalidateIdForInsert(id);
            contentValues.put(Columns.ID, id);
        }
    }

    protected Context getContext() {
        Context result = context;
        if (context == null
                || context instanceof Activity
                && ((Activity) context).isDestroyed()) {
            result = Utils.getApp();
        }

        return result;
    }
}
