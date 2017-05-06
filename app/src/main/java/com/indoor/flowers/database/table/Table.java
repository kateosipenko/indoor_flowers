package com.indoor.flowers.database.table;

import android.content.ContentResolver;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import com.indoor.flowers.provider.FlowersContentProvider;

public abstract class Table {

    private final String CONTENT_ITEM_PATH = "content://" + FlowersContentProvider.AUTHORITY
            + "/" + getName() + "/";

    public final Uri CONTENT_URI = Uri.parse("content://" + FlowersContentProvider.AUTHORITY
            + "/" + getName());

    private static final String CREATE_TABLE_QUERY_FORMAT = "create table %s (%s);";

    /**
     * Return columns declaration in format: Columns.ID + " integer primary key autoincrement," + Columns.DATE + " text," etc.
     *
     * @return
     */
    public abstract String getColumnsDeclaration();

    public String getName() {
        return getClass().getSimpleName();
    }

    public boolean isVirtual() {
        return getColumnsDeclaration() == null;
    }

    /**
     * used to create select quarries, override for virtual tables
     */
    public String getJoinStatement() {
        return getName();
    }

    public String getItemSelectionStatement() {
        return Columns.ID + " = ";
    }

    public String getCreationQuery() {
        return String.format(CREATE_TABLE_QUERY_FORMAT, getName(), getColumnsDeclaration());
    }

    /**
     * Content type of current table.
     */
    public String getContentType() {
        return ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/" + getName() + "s";
    }

    /**
     * Content type of item in current table.
     */
    public String getContentItemType() {
        return ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/" + getName();
    }

    /**
     * Returns uri to single row in table with specified id.
     *
     * @param id Id of row.
     */
    public Uri getItemUri(int id) {
        return Uri.parse(CONTENT_ITEM_PATH + id);
    }

    /**
     * Return uri to table.
     *
     * @return
     */
    public Uri getContentUri() {
        return CONTENT_URI;
    }

    /**
     * Invokes create sql command
     */
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(getCreationQuery());
    }

    /**
     * Override this function if you need to alter table on upgrade
     * For example, delete or add new columns into table
     */
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
