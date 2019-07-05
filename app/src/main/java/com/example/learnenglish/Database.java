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
    public static final String COLUMN_WORD = "WORD";
    public static final String COLUMN_TRANSLATE = "TRANSLATE";
    public static final String COLUMN_LEARNED = "LEARNED";

    // ��������� ������ ��� �������� ���� ������
    private static final String DATABASE_CREATE = "create table "
            + DATABASE_TABLE + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_WORD + " text not null, " + COLUMN_TRANSLATE + " text not null,"
            + COLUMN_LEARNED + " text not null" + ");";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        //������� �������
        database.execSQL(DATABASE_CREATE);

        //��������� ������
        ContentValues initialValues = createContentValues("", "", false);
        database.insert(DATABASE_TABLE, null, initialValues);
    }

    /*
     * ��������� ��������� ��� ������, ������� ��������� (��� 1 ������ � �������)
     */
    private ContentValues createContentValues(String word, String translate, Boolean learned) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_WORD, word);
        values.put(COLUMN_TRANSLATE, translate);
        values.put(COLUMN_LEARNED, learned);
        return values;
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS table1");
        onCreate(database);
    }

    /**
     * ��������� ����� � �������. ���� �������� �������, �� ������������
     * ����� ������ rowId, ����� -1
     */
    public long addWord(String word, String translate, Boolean learned) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues initialValues = createContentValues(word, translate, learned);

        long row = database.insert(DATABASE_TABLE, null, initialValues);
        database.close();

        return row;
    }

    /**
     * ��������� ������
     */
    public boolean updateLearned(long rowId, String word, String translate, Boolean learned) {
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

    public void deleteAll(){
        SQLiteDatabase database = this.getWritableDatabase();
        // ���� whereClause = null, �� �������� ��
        database.delete(DATABASE_TABLE, null, null);
        database.close();
    }

    /**
     * ��������� ���� ����
     */
    public Cursor getAllWords() {
        SQLiteDatabase database = this.getWritableDatabase();
        return database.query(DATABASE_TABLE, new String[]{COLUMN_ID,
                        COLUMN_WORD, COLUMN_TRANSLATE, COLUMN_LEARNED}, null,
                null, null, null, null);
    }

    /**
     * �������� ���������� �����
     */
    public Cursor getWord(long rowId) throws SQLException {
        SQLiteDatabase database = this.getReadableDatabase();
        Cursor mCursor = database.query(true, DATABASE_TABLE, new String[]{COLUMN_ID,
                        COLUMN_WORD, COLUMN_TRANSLATE, COLUMN_LEARNED},
                COLUMN_ID + "=" + rowId, null,
                null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }
}
