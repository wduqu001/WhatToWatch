package com.wduqu001.android.whattowatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.test.InstrumentationRegistry;

import com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry;
import com.wduqu001.android.whattowatch.data.MoviesDbHelper;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class TestDatabase {

    private final Context mContext = InstrumentationRegistry.getTargetContext();

    private final Class<MoviesDbHelper> mDbHelperClass = MoviesDbHelper.class;

    @Test
    public void create_database_test() throws Exception {

        SQLiteOpenHelper dbHelper =
                mDbHelperClass.getConstructor(Context.class).newInstance(mContext);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String databaseIsNotOpen = "The database should be open and isn't";
        assertEquals(databaseIsNotOpen,
                true,
                db.isOpen());

        Cursor tableNameCursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='" +
                        MoviesEntry.TABLE_NAME + "'",
                null);

        /*
         * If tableNameCursor.moveToFirst returns false from this query,
         * the database contains no tables.
         */
        String errorInCreatingDatabase =
                "Error: Database contains no tables";
        assertTrue(errorInCreatingDatabase,
                tableNameCursor.moveToFirst());

        assertEquals("Error: Database was created without the expected tables.",
                MoviesEntry.TABLE_NAME, tableNameCursor.getString(0));

        tableNameCursor.close();
    }

    /**
     * This method tests inserting a single record into an empty table from a brand new database.
     * The purpose is to test that the database is working as expected
     *
     * @throws Exception in case the constructor hasn't been implemented yet
     */
    @Test
    public void insert_single_record_test() throws Exception {

        SQLiteOpenHelper dbHelper =
                mDbHelperClass.getConstructor(Context.class).newInstance(mContext);

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues testValues = new ContentValues();
        testValues.put(MoviesEntry.COLUMN_MOVIE_ID, "001");
        testValues.put(MoviesEntry.COLUMN_TITLE, "movie test");
        testValues.put(MoviesEntry.COLUMN_VOTE_AVERAGE, 2.5);
        testValues.put(MoviesEntry.COLUMN_OVERVIEW, "database test");
        testValues.put(MoviesEntry.COLUMN_RELEASE_DATE, "2001-01-01");
        testValues.put(MoviesEntry.COLUMN_ORIGINAL_TITLE, "test movie");

        long firstRowId = db.insert(
                MoviesEntry.TABLE_NAME,
                null,
                testValues);

        /* If the insert fails, database.insert returns -1 */
        assertNotEquals("Unable to insert into the database", -1, firstRowId);

        Cursor cursor = db.query(
                MoviesEntry.TABLE_NAME,
                /* Columns; leaving this null returns every column in the table */
                null,
                /* Optional specification for columns in the "where" clause above */
                null,
                /* Values for "where" clause */
                null,
                /* Columns to group by */
                null,
                /* Columns to filter by row groups */
                null,
                /* Sort order to return in Cursor */
                null);

        /* Cursor.moveToFirst will return false if there are no records returned from the query */
        String emptyQueryError = "Error: No Records returned from the query";
        assertTrue(emptyQueryError,
                cursor.moveToFirst());

        cursor.close();
        dbHelper.close();
    }

}