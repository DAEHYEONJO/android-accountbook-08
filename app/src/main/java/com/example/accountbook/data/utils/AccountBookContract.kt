package com.example.accountbook.data.utils

object AccountBookContract {

    const val DB_NAME = "accountBook.db"
    const val DB_VERSION = 1
    const val SQL_DELETE_BASE_QUERY = "DROP TABLE IF EXISTS"
    const val SQL_ACTIVE_FOREIGN_KEY = "PRAGMA foreign_keys = 1"
    const val ASC = "ASC"
    const val DESC = "DESC"

    fun getDeleteAllSql(tableName: String) = "DELETE FROM $tableName"
    fun getSelectAllSql(tableName: String) = "SELECT * FROM $tableName"
    fun getSelectAllSqlOrderBy(tableName: String, colName: String, order: String) = "SELECT * FROM $tableName ORDER BY $colName $order"

}

object AccountBookHistories {
    const val TABLE_NAME = "Histories"
    const val COLUMN_NAME_ID = "id"
    const val COLUMN_NAME_DATE = "date"
    const val COLUMN_NAME_PRICE = "price"
    const val COLUMN_NAME_DESCRIPTION = "description"
    const val COLUMN_NAME_PAYMENT_ID = "payment_id"
    const val COLUMN_NAME_CATEGORY_ID = "category_id"
    const val SQL_CREATE_TABLE = "CREATE TABLE $TABLE_NAME (" +
            "$COLUMN_NAME_ID INTEGER NOT NULL, " +
            "$COLUMN_NAME_DATE INTEGER NOT NULL, " +
            "$COLUMN_NAME_PRICE INTEGER NOT NULL, " +
            "$COLUMN_NAME_DESCRIPTION TEXT," +
            "$COLUMN_NAME_PAYMENT_ID INTEGER NOT NULL, " +
            "$COLUMN_NAME_CATEGORY_ID INTEGER NOT NULL, " +
            "PRIMARY KEY($COLUMN_NAME_ID), " +
            "FOREIGN KEY ($COLUMN_NAME_PAYMENT_ID) REFERENCES ${AccountBookPayments.TABLE_NAME}(${AccountBookPayments.COLUMN_NAME_ID}), " +
            "FOREIGN KEY ($COLUMN_NAME_CATEGORY_ID) REFERENCES ${AccountBookCategories.TABLE_NAME}(${AccountBookCategories.COLUMN_NAME_ID})" +
            ");"
    const val SQL_CREATE_INDEX = "CREATE INDEX date_index ON $TABLE_NAME($COLUMN_NAME_DATE DESC);"

}

object AccountBookPayments {
    const val TABLE_NAME = "Payments"
    const val COLUMN_NAME_ID = "payment_id"
    const val COLUMN_NAME_PAYMENT = "payment"
    const val SQL_CREATE_TABLE = "CREATE TABLE $TABLE_NAME (" +
            "$COLUMN_NAME_ID INTEGER NOT NULL," +
            "$COLUMN_NAME_PAYMENT TEXT NOT NULL," +
            "PRIMARY KEY($COLUMN_NAME_ID)" +
            ");"
    const val SQL_CREATE_TABLE_PAYMENT_UNIQUE = "CREATE TABLE $TABLE_NAME (" +
            "$COLUMN_NAME_ID INTEGER NOT NULL," +
            "$COLUMN_NAME_PAYMENT TEXT NOT NULL UNIQUE," +
            "PRIMARY KEY($COLUMN_NAME_ID)" +
            ");"
    //fun getPaymentCountSql(payment: String) = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_NAME_PAYMENT = $payment;"
    const val SQL_SELECT_BY_PAYMENT_QUERY = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_NAME_PAYMENT = ?"
    const val SQL_SELECT_BY_ID_QUERY = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_NAME_ID = ?"
    const val SQL_CREATE_INDEX = "CREATE INDEX payment_id_index ON $TABLE_NAME($COLUMN_NAME_ID);"
}

object AccountBookCategories {
    const val TABLE_NAME = "Categories"
    const val COLUMN_NAME_ID = "category_id"
    const val COLUMN_NAME_CATEGORY = "category"
    const val COLUMN_NAME_IS_EXPENSE = "is_expense"
    const val COLUMN_NAME_LABEL_COLOR = "label_color"
    const val SQL_CREATE_TABLE = "CREATE TABLE $TABLE_NAME (" +
            "$COLUMN_NAME_ID INTEGER NOT NULL, " +
            "$COLUMN_NAME_LABEL_COLOR TEXT NOT NULL, " +
            "$COLUMN_NAME_CATEGORY TEXT NOT NULL, " +
            "$COLUMN_NAME_IS_EXPENSE INTEGER NOT NULL, " +
            "PRIMARY KEY ($COLUMN_NAME_ID)" +
            ");"
    const val SQL_CREATE_TABLE_CATEGORY_UNIQUE = "CREATE TABLE $TABLE_NAME (" +
            "$COLUMN_NAME_ID INTEGER NOT NULL, " +
            "$COLUMN_NAME_LABEL_COLOR TEXT NOT NULL, " +
            "$COLUMN_NAME_CATEGORY TEXT NOT NULL UNIQUE, " +
            "$COLUMN_NAME_IS_EXPENSE INTEGER NOT NULL, " +
            "PRIMARY KEY ($COLUMN_NAME_ID)" +
            ");"
    const val SQL_SELECT_BY_CATEGORY_QUERY = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_NAME_CATEGORY = ?"
    const val SQL_SELECT_BY_ID_QUERY = "SELECT * FROM $TABLE_NAME WHERE $COLUMN_NAME_ID = ?"
    const val SQL_CREATE_INDEX = "CREATE INDEX category_id_index ON $TABLE_NAME($COLUMN_NAME_ID);"
}