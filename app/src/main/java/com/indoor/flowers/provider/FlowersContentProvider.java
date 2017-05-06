package com.indoor.flowers.provider;

import android.content.ContentProvider;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.OperationApplicationException;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.evgeniysharafan.utils.Utils;
import com.indoor.flowers.BuildConfig;
import com.indoor.flowers.database.DbOpenHelper;
import com.indoor.flowers.database.table.FlowerTable;
import com.indoor.flowers.database.table.Table;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

public class FlowersContentProvider extends ContentProvider {

    public static final String AUTHORITY = BuildConfig.AUTHORITY;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private SQLiteDatabase mDatabase;
    private ContentResolver mContentResolver;
    private boolean isBatchInProgress = false;

    static {
        sUriMatcher.addURI(AUTHORITY, FlowerTable.class.getSimpleName(), MappedUri.FLOWERS);
        sUriMatcher.addURI(AUTHORITY, FlowerTable.class.getSimpleName() + "/#", MappedUri.FLOWER_ITEM);
    }

    @Override
    public boolean onCreate() {
        DbOpenHelper dbHelper = new DbOpenHelper(getContext());
        mDatabase = dbHelper.getWritableDatabase();
        if (getContext() != null) {
            mContentResolver = getContext().getContentResolver();
        }
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Table table = getTable(uri);
        SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
        builder.setTables(table.getJoinStatement());

        // Check if current query is query to single item, not for list of items.
        if (isItemUri(uri)) {
            builder.appendWhere(table.getItemSelectionStatement() + uri.getLastPathSegment());
        }

        Cursor cursor = builder.query(mDatabase, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(mContentResolver, uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        Table table = getTable(uri);
        if (isItemUri(uri)) {
            return table.getContentItemType();
        }

        return table.getContentType();
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        Table table = getTable(uri);
        long rowID = mDatabase.insert(table.getName(), null, values);
        Uri resultUri = table.getItemUri((int) rowID);
        if (!isBatchInProgress) {
            mContentResolver.notifyChange(resultUri, null);
        }

        return resultUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        Table table = getTable(uri);
        if (isItemUri(uri) && Utils.isEmpty(selection)) {
            selection = table.getItemSelectionStatement() + uri.getLastPathSegment();
        }

        int deletedRowNum = mDatabase.delete(table.getName(), selection, selectionArgs);
        if (!isBatchInProgress) {
            mContentResolver.notifyChange(uri, null);
        }

        return deletedRowNum;
    }

    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        Table table = getTable(uri);
        if (isItemUri(uri) && Utils.isEmpty(selection)) {
            selection = table.getItemSelectionStatement() + uri.getLastPathSegment();
        }

        int updatedRowsNum = mDatabase.update(table.getName(), values, selection, selectionArgs);
        if (updatedRowsNum == 0) {
            updatedRowsNum = (int) mDatabase.insert(table.getName(), null, values);
        }

        if (!isBatchInProgress) {
            mContentResolver.notifyChange(uri, null);
        }

        return updatedRowsNum;
    }

    @NonNull
    @Override
    public ContentProviderResult[] applyBatch(@NonNull ArrayList<ContentProviderOperation> operations) throws OperationApplicationException {
        isBatchInProgress = true;
        ContentProviderResult[] results = new ContentProviderResult[operations.size()];
        if (operations.size() > 0) {
            mDatabase.beginTransaction();
            try {
                results = super.applyBatch(operations);
                mDatabase.setTransactionSuccessful();
            } finally {
                mDatabase.endTransaction();
            }
        }

        List<Uri> notifyUris = new ArrayList<>();
        for (ContentProviderOperation operation : operations) {
            if (operation.getUri() != null) {
                Uri tableUri = getTable(operation.getUri()).getContentUri();
                if (!notifyUris.contains(tableUri)) {
                    notifyUris.add(tableUri);
                }
            }
        }

        for (Uri uri : notifyUris) {
            mContentResolver.notifyChange(uri, null);
        }

        isBatchInProgress = false;
        return results;
    }

    @NonNull
    private Table getTable(Uri uri) {
        Table table = null;
        switch (sUriMatcher.match(uri)) {
            case MappedUri.FLOWERS:
            case MappedUri.FLOWER_ITEM:
                table = DbOpenHelper.findTable(FlowerTable.class);
                break;
        }

        if (table == null) {
            throw new IllegalArgumentException("Table for such URI not exists. " + uri);
        }

        return table;
    }

    private boolean isItemUri(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case MappedUri.FLOWER_ITEM:
                return true;
        }

        return false;
    }

    /**
     * Templates for Uri matching
     */
    @Retention(RetentionPolicy.SOURCE)
    public @interface MappedUri {
        int FLOWERS = 1;
        int FLOWER_ITEM = 2;
    }
}
