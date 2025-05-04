package com.example.calc

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SinusoidDatabaseHelper(context: Context) : SQLiteOpenHelper(
    context,
    DATABASE_NAME, null, DATABASE_VERSION
) {
    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(
            "CREATE TABLE $TABLE_POINTS (" +
                    "$COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "$COLUMN_X REAL, " +
                    "$COLUMN_Y REAL)"
        )
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_POINTS")
        onCreate(db)
    }

    fun clearPoints() {
        writableDatabase.execSQL("DELETE FROM $TABLE_POINTS")
    }

    fun insertPoint(x: Double, y: Double) {
        val values = ContentValues().apply {
            put(COLUMN_X, x)
            put(COLUMN_Y, y)
        }
        writableDatabase.insert(TABLE_POINTS, null, values)
    }

    fun getAllPoints(): List<Pair<Double, Double>> {
        val points = mutableListOf<Pair<Double, Double>>()
        val cursor = readableDatabase.rawQuery("SELECT $COLUMN_X, $COLUMN_Y FROM $TABLE_POINTS", null)
        while (cursor.moveToNext()) {
            val x = cursor.getDouble(0)
            val y = cursor.getDouble(1)
            points.add(x to y)
        }
        cursor.close()
        return points
    }

    companion object {
        const val DATABASE_NAME = "sinusoid.db"
        const val DATABASE_VERSION = 1

        const val TABLE_POINTS = "points"
        const val COLUMN_ID = "id"
        const val COLUMN_X = "x"
        const val COLUMN_Y = "y"
    }
}
