package com.example.learnenglish;

import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

// SQLiteOpenHelper - ����������� �����, � ������� �������� ����� ���������, ���������
// � ��������� ���� ������.
class Database extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "dictionary.db";
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_TABLE = "table1";

    // ���� ������� ��� �������� �����, ��� ��������, �����, ������� ����������
    // ������� ����� ��� ���, � ID, ������� ����������� �������������
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_ENG_WORD = "WORD";
    public static final String COLUMN_RUS_TRANSLATE = "TRANSLATE";

    // ����� ��������� 3 ��������: "no", "almost" � "yes"
    public static final String COLUMN_LEARNED = "LEARNED";

    // ��������� ������ ��� �������� ���� ������
    private static final String DATABASE_CREATE = "create table "
            + DATABASE_TABLE + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_ENG_WORD + " text not null, " + COLUMN_RUS_TRANSLATE + " text not null,"
            + COLUMN_LEARNED + " text not null" + ");";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    void createDatabase() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(database);
    }

    void dropTable() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        //������� �������
        database.execSQL(DATABASE_CREATE);
    }

    /*
     * ��������� ��������� ��� ������, ������� ��������� (��� 1 ������ � �������)
     */
    private ContentValues createContentValues(String word, String translate, String learned) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ENG_WORD, word);
        values.put(COLUMN_RUS_TRANSLATE, translate);
        values.put(COLUMN_LEARNED, learned);
        return values;
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(database);
    }

    /**
     * ��������� ����� � �������. ���� �������� �������, �� ������������
     * ����� ������ rowId, ����� -1
     */
    public long addWord(String engWord, String rusTranslate, String learned) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues initialValues = createContentValues(engWord, rusTranslate, learned);

        long row = database.insert(DATABASE_TABLE, null, initialValues);
        database.close();

        return row;
    }

    /**
     * ��������� ������
     */
    public boolean changeLearned(long rowId, String word, String translate, String learned) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues updateValues = createContentValues(word, translate, learned);

        return database.update(DATABASE_TABLE, updateValues,
                COLUMN_ID + "=" + rowId, null) > 0;
    }

    /**
     * �������� ������
     */
    public void deleteWord(long rowId) {
        SQLiteDatabase database = this.getWritableDatabase();
        database.delete(DATABASE_TABLE, COLUMN_ID + "=" + rowId, null);
        database.close();
    }

    /**
     * ��������� ���� ����
     */
    public Cursor getAllWords() {
        SQLiteDatabase database = this.getWritableDatabase();
        return database.query(DATABASE_TABLE, new String[]{COLUMN_ID,
                        COLUMN_ENG_WORD, COLUMN_RUS_TRANSLATE, COLUMN_LEARNED}, null,
                null, null, null, null);
    }

    public Cursor getUnlearnedWords() {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor mCursor = database.query(DATABASE_TABLE, new String[]{COLUMN_ID,
                        COLUMN_RUS_TRANSLATE, COLUMN_ENG_WORD, COLUMN_LEARNED},
                COLUMN_LEARNED + " = ?", new String[]{"no"},
                null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor getAlmostLearnedWords() {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor mCursor = database.query(DATABASE_TABLE, new String[]{COLUMN_ID,
                        COLUMN_RUS_TRANSLATE, COLUMN_ENG_WORD, COLUMN_LEARNED},
                COLUMN_LEARNED + " = ?", new String[]{"almost"},
                null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    public Cursor getLearnedWords() {
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor mCursor = database.query(DATABASE_TABLE, new String[]{COLUMN_ID,
                        COLUMN_RUS_TRANSLATE, COLUMN_ENG_WORD, COLUMN_LEARNED},
                COLUMN_LEARNED + " = ?", new String[]{"yes"},
                null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * �������� ����� �� id
     */
    public Cursor getWordById(long rowId) throws SQLException {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor mCursor = database.query(true, DATABASE_TABLE, new String[]{COLUMN_ID,
                        COLUMN_ENG_WORD, COLUMN_RUS_TRANSLATE, COLUMN_LEARNED},
                COLUMN_ID + "=" + rowId, null,
                null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * �������� ������� ����� �� �����������
     */
    public Cursor getRusByEng(String engWord) throws SQLException {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor mCursor = database.query(DATABASE_TABLE, new String[]{COLUMN_ID,
                        COLUMN_ENG_WORD, COLUMN_RUS_TRANSLATE, COLUMN_LEARNED},
                COLUMN_ENG_WORD + " = ?", new String[]{engWord.toLowerCase()},
                null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * �������� ���������� ����� �� ��������
     */
    public Cursor getEngByRus(String rusWord) throws SQLException {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor mCursor = database.query(DATABASE_TABLE, new String[]{COLUMN_ID,
                        COLUMN_RUS_TRANSLATE, COLUMN_ENG_WORD, COLUMN_LEARNED},
                COLUMN_RUS_TRANSLATE + " = ?", new String[]{rusWord.toLowerCase()},
                null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
}