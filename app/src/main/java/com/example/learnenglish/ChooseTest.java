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
import android.widget.ImageButton;
import android.widget.ImageView;

public class ChooseTest extends Activity implements View.OnClickListener {

    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    Database database;
    ImageButton backButton;
    Button newWords;
    Button oldWords;
    ImageView lock;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_test);

        database = new Database(this);
        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        backButton = findViewById(R.id.back_button_choose_test);
        newWords = findViewById(R.id.test_new_words);
        oldWords = findViewById(R.id.test_old_words);
        lock = findViewById(R.id.old_words_closed);

        backButton.setOnClickListener(this);
        newWords.setOnClickListener(this);

        if (!preferences.getBoolean("openOldWords", true)) {
            oldWords.setOnClickListener(this);
            lock.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View v) {

        if (v == backButton) {
            finish();
        } else if (v == newWords) {
            chooseKindOfWords("almost");
        } else if (v == oldWords) {
            chooseKindOfWords("yes");
        }
    }

    private void chooseKindOfWords(String kindOfWords) {
        editor = preferences.edit();
        editor.putString("newWords", kindOfWords);
        editor.apply();
        startActivity(new Intent(this, Test.class));
    }

    @Override
    public void onResume() {
        super.onResume();
        editor = preferences.edit();

        if (preferences.getBoolean("toMenu", false)) {
            editor.putBoolean("toMenu", false);
            editor.apply();
            finish();

        } else {
            if (preferences.getBoolean("openOldWords", true) &&
                    database.getKindOfWords("yes").getCount() >= 5) {

                Animation openLock = AnimationUtils.loadAnimation(this, R.anim.anim_open);
                final Animation disappear =
                        AnimationUtils.loadAnimation(this, R.anim.anim_disappear);
                lock.startAnimation(openLock);

                openLock.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {
                        backButton.setEnabled(false);
                        newWords.setEnabled(false);
                        oldWords.setEnabled(false);
                    }

                    @Override
                    public void onAnimationEnd(Animation animation) {
                        lock.setImageResource(R.drawable.opened);
                        lock.startAnimation(disappear);
                        lock.setVisibility(View.INVISIBLE);

                        backButton.setEnabled(true);
                        newWords.setEnabled(true);
                        oldWords.setEnabled(true);
                    }

                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });

                editor.putBoolean("openOldWords", false);
                oldWords.setOnClickListener(this);
            }
        }
    }

}
