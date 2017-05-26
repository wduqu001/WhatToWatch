package com.wduqu001.android.whattowatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.test.InstrumentationRegistry;

import com.wduqu001.android.whattowatch.data.MoviesDbHelper;

import org.junit.Before;
import org.junit.Test;

import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_AVERAGE_RANKING;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_BACKDROP_PATH;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_MOVIE_ID;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_OVERVIEW;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_POSTER_PATH;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_TITLE;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.TABLE_NAME;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class TestDatabase {

    private final Context mContext = InstrumentationRegistry.getTargetContext();

    private final Class<MoviesDbHelper> mDbHelperClass = MoviesDbHelper.class;

    @Before
    public void deleteAllRecords() {
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        /* The delete method deletes all of the desired rows from the table, not the table itself */
        db.delete(TABLE_NAME, null, null);
        dbHelper.close();
    }

    @Test
    public void get_database_instance_test() throws Exception {
        SQLiteOpenHelper dbHelper =
                mDbHelperClass.getConstructor(Context.class).newInstance(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        assertNotEquals("Unable to get writable database", db, null);

        String databaseIsNotOpen = "The database should be open and isn't";
        assertEquals(databaseIsNotOpen,
                true,
                db.isOpen());
    }

    @Test
    public void create_database_test() throws Exception {

        SQLiteOpenHelper dbHelper =
                mDbHelperClass.getConstructor(Context.class).newInstance(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        Cursor tableNameCursor = db.rawQuery(
                "SELECT name FROM sqlite_master WHERE type='table' AND name='" +
                        TABLE_NAME + "'",
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
                TABLE_NAME, tableNameCursor.getString(0));

        tableNameCursor.close();
        dbHelper.close();
    }

    /**
     * This method tests inserting a single record into an empty table from a brand new database.
     *
     * @throws Exception in case the constructor hasn't been implemented yet
     */
    @Test
    public void insert_single_record_test() throws Exception {

        SQLiteOpenHelper dbHelper =
                mDbHelperClass.getConstructor(Context.class).newInstance(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        assertNotEquals("Unable to insert into the database", -1, insertSingleRecord(db));

        dbHelper.close();
    }

    /**
     * This method inserts a single record into the database.
     */
    private long insertSingleRecord(SQLiteDatabase db) throws Exception {

        ContentValues values = new ContentValues();
        values.put(COLUMN_MOVIE_ID, "166426");
        values.put(COLUMN_TITLE, "Pirates of the Caribbean: Dead Men Tell No Tales");
        values.put(COLUMN_AVERAGE_RANKING, 6);
        values.put(COLUMN_BACKDROP_PATH, "/3DVKG54lqYbdh8RNylXeCf4MBPw.jpg");
        values.put(COLUMN_ORIGINAL_TITLE, "Pirates of the Caribbean: Dead Men Tell No Tales");
        values.put(COLUMN_RELEASE_DATE, "2017-05-23");
        values.put(COLUMN_POSTER_PATH, "/xbpSDU3p7YUGlu9Mr6Egg2Vweto.jpg");
        values.put(COLUMN_OVERVIEW, "Captain Jack Sparrow is pursued by an old rival, Captain Salazar...");

        /* If the insert fails, database.insert returns -1 */
        return db.insert(
                TABLE_NAME,
                null,
                values);
    }

    /**
     * This method tests retrieving all records from the database.
     *
     * @throws Exception in case the constructor hasn't been implemented yet
     */
    @Test
    public void get_all_records_test() throws Exception {

        SQLiteOpenHelper dbHelper =
                mDbHelperClass.getConstructor(Context.class).newInstance(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        insertSingleRecord(db);

        Cursor allRecordsCursor = db.query(
                TABLE_NAME,
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
        assertTrue("Error: No Records returned from the query",
                allRecordsCursor.moveToFirst());
        allRecordsCursor.close();
        dbHelper.close();
    }

    /**
     * This method tests retrieving a single record from the database.
     *
     * @throws Exception in case the constructor hasn't been implemented yet
     */
    @Test
    public void get_record_by_movie_id_test() throws Exception {

        SQLiteOpenHelper dbHelper =
                mDbHelperClass.getConstructor(Context.class).newInstance(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        insertSingleRecord(db);

        String movieId = "166426";
        String columns[] = {};
        String where = COLUMN_MOVIE_ID + "=" + movieId;

        Cursor byIdCursor = db.query(true, TABLE_NAME, columns, where, null, null, null, null, null);
        byIdCursor.moveToFirst();
        String storedId = byIdCursor.getString(byIdCursor.getColumnIndex(COLUMN_MOVIE_ID));

        assertEquals("Unable to locate movie by its ID", storedId, movieId);
        byIdCursor.close();
        dbHelper.close();
    }
}