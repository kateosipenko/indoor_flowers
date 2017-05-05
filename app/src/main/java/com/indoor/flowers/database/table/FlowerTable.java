package com.indoor.flowers.database.table;

public class FlowerTable extends Table {


    public static final String COLUMNS_DECLARATION =
            Columns.ID + " integer primary key autoincrement,"
                    + Columns.NAME + " text";

    @Override
    public String getColumnsDeclaration() {
        return COLUMNS_DECLARATION;
    }
}
