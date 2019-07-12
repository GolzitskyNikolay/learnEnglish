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

// SQLiteOpenHelper - абстрактный класс, с помощью которого можно создавать, открывать
// и обновлять базы данных.
class Database extends SQLiteOpenHelper {

    private static String DATABASE_PATH = "/data/data/com.example.learnenglish/databases/";
    private static final String DATABASE_NAME = "vocabulary.db";
    private static final String DATABASE_TABLE = "table1";
    private static final int DATABASE_VERSION = 1;
    private SQLiteDatabase database;
    private final Context mContext;

    // поля таблицы для хранения СЛОВА, его ПЕРЕВОДА, ФЛАГА, который показывает
    // изучено слово или нет, и ID, который формируется автоматически
    private static final String COLUMN_ID = "_id";
    private static final String COLUMN_ENG_WORD = "WORD";
    private static final String COLUMN_RUS_TRANSLATE = "TRANSLATE";

    // Может принимать 3 значения: "no", "almost" и "yes"
    private static final String COLUMN_LEARNED = "LEARNED";

    // формируем запрос для создания базы данных
    private static final String DATABASE_CREATE = "create table "
            + DATABASE_TABLE + "(" + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_ENG_WORD + " text not null, " + COLUMN_RUS_TRANSLATE + " text not null,"
            + COLUMN_LEARNED + " text not null" + ");";

    Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(database);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        //создаем таблицу
        database.execSQL(DATABASE_CREATE);
    }

    void createDatabase() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DROP TABLE IF EXISTS " + DATABASE_TABLE);
        onCreate(database);
    }

    /**
     * Создает пустую базу данных и перезаписывает ее базой из assets
     */
    void crateDatabaseInAndroid() {
        //вызывая этот метод создаем пустую базу, позже она будет перезаписана
        this.getReadableDatabase();
        try {
            copyDataBase();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void openDataBase() throws SQLException {
        //открываем БД
        String myPath = DATABASE_PATH + DATABASE_NAME;
        database = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
    }

    /**
     * Копирует базу из папки assets заместо созданной локальной БД
     * Выполняется путем копирования потока байтов.
     */
    private void copyDataBase() throws IOException {
        //Открываем БД из assets как входящий поток
        InputStream myInput = mContext.getAssets().open(DATABASE_NAME);

        //Путь к БД андроид
        String outFileName = DATABASE_PATH + DATABASE_NAME;

        //Открываем пустую новую базу данных как исходящий поток
        OutputStream myOutput = new FileOutputStream(outFileName);

        //перемещаем байты из входящего файла в исходящий
        byte[] buffer = new byte[1024];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }

        //закрываем потоки
        myOutput.flush();
        myOutput.close();
        myInput.close();
    }

    /**
     * Добавляем слово и перевод. Если добавили успешно, то возвращается
     * номер строки rowId, иначе -1
     */
    void addWord(String engWord, String rusTranslate, String learned) {
        database = this.getWritableDatabase();
        ContentValues initialValues = createContentValues(engWord, rusTranslate, learned);

        long row = database.insert(DATABASE_TABLE, null, initialValues);
        database.close();

    }

    /*
     * Описываем структуру для данных, которые добавляем (для 1 строки в таблице)
     */
    private ContentValues createContentValues(String word, String translate, String learned) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_ENG_WORD, word);
        values.put(COLUMN_RUS_TRANSLATE, translate);
        values.put(COLUMN_LEARNED, learned);
        return values;
    }

    /**
     * Изменение строки
     */
    void changeLearned(long rowId, String word, String translate, String learned) {
        database = this.getWritableDatabase();
        ContentValues updateValues = createContentValues(word, translate, learned);

        database.update(DATABASE_TABLE, updateValues,
                COLUMN_ID + "=" + rowId, null);
    }

    /**
     * Получаем либо неизученные, либо почти выученные, либо выученные слова
     */
    Cursor getKindWords(String kindOfWords) {
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

    /**
     * Получаем русское слово по английскому
     */
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

    /**
     * Получаем английское слово по русскому
     */
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
}