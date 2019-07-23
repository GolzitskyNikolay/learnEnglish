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

        myPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        needOpenTest = myPreferences.getBoolean("needOpenTest", true);

        needOpen = myPreferences.getBoolean("needOpen", true);

        checkLocks();

        editor = myPreferences.edit();

        boolean createDatabaseOrNot =
                myPreferences.getBoolean("createDatabaseOrNot", true);

        if (createDatabaseOrNot) {

            Database database = new Database(this);
            database.crateDatabaseInAndroid();
            database.openDataBase();

            editor.putBoolean("createDatabaseOrNot", false);
            editor.apply();
        }
    }

    private void checkLocks() {
        if (!myPreferences.getBoolean("needOpenTest", true)) {
            findViewById(R.id.menu_test_closed).setVisibility(View.INVISIBLE);
            test = findViewById(R.id.menu_test);
            test.setOnClickListener(this);
        }
        if (!myPreferences.getBoolean("needOpen", true)) {
            findViewById(R.id.menu_my_words_closed).setVisibility(View.INVISIBLE);
            myWords = findViewById(R.id.my_words);
            myWords.setOnClickListener(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        needOpen = myPreferences.getBoolean("needOpen", true);

        if (needOpen) {
            Database database = new Database(this);

            needOpenTest = myPreferences.getBoolean("needOpenTest", true);

            ImageView openTestOrMyWords = null;

            if (database.getKindOfWords("almost").getCount() >=
                    myPreferences.getInt("count of words", 5) && needOpenTest) {
                openTestOrMyWords = findViewById(R.id.menu_test_closed);
                editor.putBoolean("needOpenTest", false);

            } else if (database.getKindOfWords("yes").getCount() != 0) {
                openTestOrMyWords = findViewById(R.id.menu_my_words_closed);
                editor.putBoolean("needOpen", false);
            }

            if (openTestOrMyWords != null) {
                openTestOrMyWords(openTestOrMyWords);
                editor.apply();
            }
        }
    }

    private void openTestOrMyWords(final ImageView imageView) {

        Animation open = AnimationUtils.loadAnimation(this, R.anim.anim_open);
        final Animation disappear = AnimationUtils.loadAnimation(this, R.anim.anim_disappear);
        imageView.startAnimation(open);

        open.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                learnWords.setEnabled(false);
                if (imageView != findViewById(R.id.menu_test_closed)) {
                    test.setEnabled(false);
                }
                dictionary.setEnabled(false);
                settings.setEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                imageView.setImageResource(R.drawable.opened);
                imageView.startAnimation(disappear);

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
}
