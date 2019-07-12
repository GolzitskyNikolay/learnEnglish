package com.study.learnenglish;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;

import java.io.*;
import java.util.*;

public class MainActivity extends Activity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity);

        findViewById(R.id.learn_words).setOnClickListener(this);
        findViewById(R.id.test).setOnClickListener(this);
        findViewById(R.id.my_words).setOnClickListener(this);
        findViewById(R.id.dictionary).setOnClickListener(this);
        findViewById(R.id.settings).setOnClickListener(this);

        //ссылаемся на объект, который может быть использован для чтения и записи
        //в файл настроек по умолчанию
        SharedPreferences myPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        int createDatabaseOrNot = myPreferences.getInt("createDatabaseOrNot", 1);

        if (createDatabaseOrNot == 1) {
            Database database = new Database(this);
            database.crateDatabaseInAndroid();
            database.openDataBase();

            // создаём объект Editor для записи в файл настроек
            SharedPreferences.Editor editor = myPreferences.edit();
            editor.putInt("createDatabaseOrNot", 0);
            //сохраняем
            editor.apply();
        }
    }

    /**
     * Данный метод вызывается только при первом запуске приложения.
     * Для более быстрой работы приложения создаётся база данных, которая
     * сохраняет в себя слова с переводом из исходного файла со словами "dictionary".
     */
    void addInDataBase() throws IOException {

        // создаём базу данных, которая хранит в себе все слова, их перевод и флаги,
        // которые показывают выучены слова или нет
        Database database = new Database(this);
        database.createDatabase();

        InputStream fileInputStream =
                this.getResources().openRawResource(R.raw.dictionary);
        BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(fileInputStream));

        String line = bufferedReader.readLine().toLowerCase();

        List<String> list;

        int i = 1;
        while (line != null) {
            list = new ArrayList<>(Arrays.asList(line.split("#")));
            System.out.println(i);
            database.addWord(list.get(0), list.get(1), "no");
            line = bufferedReader.readLine();
            i++;
        }
        bufferedReader.close();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.learn_words) {
            startActivity(new Intent(this, LearnWords.class));
        } else if (v.getId() == R.id.test) {
            startActivity(new Intent(this, Test.class));
        } else if (v.getId() == R.id.my_words) {
            startActivity(new Intent(this, MyWords.class));
        } else if (v.getId() == R.id.dictionary) {
            startActivity(new Intent(this, Dictionary.class));
        } else if (v.getId() == R.id.settings) {
            startActivity(new Intent(this, Settings.class));
        }
    }
}
