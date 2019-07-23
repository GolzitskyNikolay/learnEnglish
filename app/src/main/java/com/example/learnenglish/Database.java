package com.example.learnenglish;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "vocabulary.db";
    private static final String DATABASE_TABLE = "table1";
    private static final int DATABASE_VERSION = 1;
    private static String DATABASE_PATH_AND_NAME;
    private SQLiteDatabase database;
    private final Context mContext;

    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_ENG_WORD = "WORD";
    private static final String COLUMN_RUS_TRANSLATE = "TRANSLATE";

    private static final String COLUMN_LEARNED = "LEARNED";

    private static final String DATABASE_CREATE = "create table "
            + DATABASE_TABLE + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_ENG_WORD + " text not null, " + COLUMN_RUS_TRANSLATE + " text not null,"
            + COLUMN_LEARNED + " text not null" + ");";

    Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
        DATABASE_PATH_AND_NAME = (context.getDatabasePath(DATABASE_NAME)).toString();
    }

    private ContentValues createContentValues(String word, String translate, String learned) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ENG_WORD, word);
        values.put(COLUMN_RUS_TRANSLATE, translate);
        values.put(COLUMN_LEARNED, learned);
        return values;
    }

    void crateDatabaseInAndroid() {
        this.getReadableDatabase();
        try {
            copyDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void openDataBase() throws SQLException {
        String myPath = DATABASE_PATH_AND_NAME;
        database = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    private void copyDataBase() throws IOException {
        InputStream myInput = mContext.getAssets().open(DATABASE_NAME);

        String outFileName = DATABASE_PATH_AND_NAME;

        OutputStream myOutput = new FileOutputStream(outFileName);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    void changeLearned(long rowId, String word, String translate, String learned) {
        database = this.getWritableDatabase();
        ContentValues updateValues = createContentValues(word, translate, learned);

        database.update(DATABASE_TABLE, updateValues,
                COLUMN_ID + "=" + rowId, null);
    }

    Cursor getKindOfWords(String kindOfWords) {
        database = this.getWritableDatabase();
        Cursor mCursor = database.query(DATABASE_TABLE, new String[]{COLUMN_ID,
                        COLUMN_RUS_TRANSLATE, COLUMN_ENG_WORD, COLUMN_LEARNED},
                COLUMN_LEARNED + " = ?", new String[]{kindOfWords},
                null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    Cursor getRusByEng(String engWord) throws SQLException {
        database = this.getReadableDatabase();
        Cursor mCursor = database.query(DATABASE_TABLE, new String[]{COLUMN_ID,
                        COLUMN_ENG_WORD, COLUMN_RUS_TRANSLATE, COLUMN_LEARNED},
                COLUMN_ENG_WORD + " like ?", new String[]{engWord.toLowerCase()},
                null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    Cursor getEngByRus(String rusWord) throws SQLException {
        database = this.getReadableDatabase();
        Cursor mCursor = database.query(DATABASE_TABLE, new String[]{COLUMN_ID,
                        COLUMN_RUS_TRANSLATE, COLUMN_ENG_WORD, COLUMN_LEARNED},
                COLUMN_RUS_TRANSLATE + " like ?", new String[]{rusWord.toLowerCase()},
                null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(database);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(DATABASE_CREATE);
    }
}