package com.wduqu001.android.whattowatch;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.wduqu001.android.whattowatch.data.MoviesDbHelper;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Map;
import java.util.Set;

import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_MOVIE_ID;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_ORIGINAL_TITLE;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_OVERVIEW;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_RELEASE_DATE;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_TITLE;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.COLUMN_VOTE_AVERAGE;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.CONTENT_URI;
import static com.wduqu001.android.whattowatch.data.MoviesContract.MoviesEntry.TABLE_NAME;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static junit.framework.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class TestContentProvider {
    private final Context mContext = InstrumentationRegistry.getTargetContext();

    /**
     * Ensures there is a non empty cursor and validates the cursor's data by checking it against
     * a set of expected values. This method will then close the cursor.
     *
     * @param error          Message when an error occurs
     * @param valueCursor    The Cursor containing the actual values received from an arbitrary query
     * @param expectedValues The values we expect to receive in valueCursor
     */
    static void validateThenCloseCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertNotNull(
                "This cursor is null. Did you make sure to register your ContentProvider in the manifest?",
                valueCursor);

        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }

    /**
     * This method iterates through a set of expected values and makes various assertions that
     * will pass if our app is functioning properly.
     *
     * @param error          Message when an error occurs
     * @param valueCursor    The Cursor containing the actual values received from an arbitrary query
     * @param expectedValues The values we expect to receive in valueCursor
     */
    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();

        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int index = valueCursor.getColumnIndex(columnName);

            /* Test to see if the column is contained within the cursor */
            String columnNotFoundError = "Column '" + columnName + "' not found. " + error;
            assertFalse(columnNotFoundError, index == -1);

            /* Test to see if the expected value equals the actual value (from the Cursor) */
            String expectedValue = entry.getValue().toString();
            String actualValue = valueCursor.getString(index);

            String valuesDontMatchError = "Actual value '" + actualValue
                    + "' did not match the expected value '" + expectedValue + "'. "
                    + error;

            assertEquals(valuesDontMatchError,
                    expectedValue,
                    actualValue);
        }
    }

    public ContentValues getTestValues() {
        ContentValues values = new ContentValues();
        values.put(COLUMN_MOVIE_ID, "001");
        values.put(COLUMN_TITLE, "movie test");
        values.put(COLUMN_VOTE_AVERAGE, 2.5);
        values.put(COLUMN_OVERVIEW, "database test");
        values.put(COLUMN_RELEASE_DATE, "2001-01-01");
        values.put(COLUMN_ORIGINAL_TITLE, "test movie");
        return values;
    }

    @Before
    public void setUp() {
        deleteAllRecords();
    }

    @Test
    public void testBasicInsert() {
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = getTestValues();

        try {
            long rowId = db.insert(TABLE_NAME,
                    null,
                    testValues);
            String errorMessage = "Unable to insert into the database";
            assertTrue(errorMessage, rowId != -1);
            db.close();
        } catch (Exception e) {
            db.close();
            fail("Error: Unable to insert data. " + e.toString());
        }
    }

    @Test
    public void testBasicQuery() {
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = getTestValues();

        long rowId = db.insert(TABLE_NAME,
                null,
                testValues);

        assertTrue("Unable to insert data", rowId != -1);
        db.close();

        Cursor cursor = mContext.getContentResolver().query(
                CONTENT_URI,
                null,
                null,
                null,
                null);

        validateThenCloseCursor("testBasicQuery", cursor, testValues);
    }

    private void deleteAllRecords() {
        MoviesDbHelper helper = new MoviesDbHelper(InstrumentationRegistry.getTargetContext());
        SQLiteDatabase db = helper.getWritableDatabase();

        /* The delete method deletes all of the desired rows from the table, not the table itself */
        db.delete(TABLE_NAME, null, null);
        db.close();
    }
}