package com.example.learnenglish;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends Activity implements View.OnClickListener {

    SharedPreferences myPreferences;
    SharedPreferences.Editor editor;
    boolean needOpenTest;
    Button learnWords;
    Button dictionary;
    boolean needOpen;
    Button settings;
    Button myWords;
    Button test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_activity);

        learnWords = findViewById(R.id.learn_words);
        learnWords.setOnClickListener(this);

        dictionary = findViewById(R.id.dictionary);
        dictionary.setOnClickListener(this);

        settings = findViewById(R.id.settings);
        settings.setOnClickListener(this);

        //ссылаемся на объект, который может быть использован для чтения и записи
        //в файл настроек по умолчанию
        myPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        // проверка на то, нужно ли изменить изображение у пункта "Тест"
        needOpenTest = myPreferences.getBoolean("needOpenTest", true);

        // true, пока не изменится изображение у пункта "Мои слова"
        needOpen = myPreferences.getBoolean("needOpen", true);

        // если изображения поменялись - кнопкам присваиваются слушатели
        checkLocks();

        // создаём объект Editor для записи в файл настроек
        editor = myPreferences.edit();

        boolean createDatabaseOrNot =
                myPreferences.getBoolean("createDatabaseOrNot", true);

        // при первом запуске копируем базу данных из assets в девайс
        if (createDatabaseOrNot) {
            Database database = new Database(this);
            database.crateDatabaseInAndroid();
            database.openDataBase();

            editor.putBoolean("createDatabaseOrNot", false);
            //сохраняем
            editor.apply();
        }
    }

    // Поверяем состояние замков
    private void checkLocks() {
        if (!myPreferences.getBoolean("needOpenTest", true)) {
            // если замок уже "открыли", то не показываем его
            findViewById(R.id.menu_test_closed).setVisibility(View.INVISIBLE);
            // теперь можно переходить в пункт "Тест"
            test = findViewById(R.id.menu_test);
            test.setOnClickListener(this);
        }
        if (!myPreferences.getBoolean("needOpen", true)) {
            // замок в пункте "Мои слова" "открыли" => не показываем его
            findViewById(R.id.menu_my_words_closed).setVisibility(View.INVISIBLE);
            // можно переходить в пункт "Мои слова"
            myWords = findViewById(R.id.my_words);
            myWords.setOnClickListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // true, пока не изменится изображение у пункта "Мои слова"
        needOpen = myPreferences.getBoolean("needOpen", true);

        if (needOpen) {
            Database database = new Database(this);

            // проверка на то, нужно ли изменить изображение у пункта "Тест"
            needOpenTest = myPreferences.getBoolean("needOpenTest", true);

            ImageView openTestOrMyWords = null;

            // "открываем" замок у пункта "Тест"
            if (database.getKindWords("almost").getCount() >=
                    myPreferences.getInt("count of words", 5) && needOpenTest) {
                openTestOrMyWords = findViewById(R.id.menu_test_closed);
                editor.putBoolean("needOpenTest", false);

                // "открываем" замок у пункта "Мои слова"
            } else if (database.getKindWords("yes").getCount() != 0) {
                openTestOrMyWords = findViewById(R.id.menu_my_words_closed);
                // больше изображение меняться не будет
                editor.putBoolean("needOpen", false);
            }

            // если все условия выполнены, то меняем изображения и
            // подключаем анимацию
            if (openTestOrMyWords != null) {
                openTestOrMyWords(openTestOrMyWords);
                // сохраняем
                editor.apply();
            }
        }
    }

    // меняем замок на открытый, а после - прячем его
    private void openTestOrMyWords(final ImageView imageView) {

        Animation open = AnimationUtils.loadAnimation(this, R.anim.anim_open);
        final Animation disappear = AnimationUtils.loadAnimation(this, R.anim.anim_disappear);
        imageView.startAnimation(open);

        open.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // на время анимации запрещаем нажимать кнопки
                learnWords.setEnabled(false);
                if (imageView != findViewById(R.id.menu_test_closed)) {
                    test.setEnabled(false);
                }
                dictionary.setEnabled(false);
                settings.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                // "открываем" замок и прячем его
                imageView.setImageResource(R.drawable.opened);
                imageView.startAnimation(disappear);

                // разрешаем нажимать кнопки
                learnWords.setEnabled(true);
                if (imageView != findViewById(R.id.menu_test_closed)) {
                    test.setEnabled(true);
                }
                dictionary.setEnabled(true);
                settings.setEnabled(true);

                checkLocks();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.learn_words) {
            startActivity(new Intent(this, LearnWords.class));
        } else if (v.getId() == R.id.menu_test) {
            startActivity(new Intent(this, ChooseTest.class));
        } else if (v.getId() == R.id.my_words) {
            startActivity(new Intent(this, MyWords.class));
        } else if (v.getId() == R.id.dictionary) {
            startActivity(new Intent(this, Dictionary.class));
        } else if (v.getId() == R.id.settings) {
            startActivity(new Intent(this, Settings.class));
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
}
