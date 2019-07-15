package com.example.learnenglish;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Test extends Activity implements View.OnClickListener {

    TextView word;
    EditText translate;
    Button button;
    Cursor cursor;
    String rightAnswer;
    Animation anim_incorrect;
    Animation anim_correct;
    Animation incorrect_enter;
    Database database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        // получаем слова, знание которых нужно проверить у пользователя
        database = new Database(this);
        cursor = database.getKindWords("almost");

        // если пользователь уже нажимал в "Учить слова" кнопку "дальше",
        // и слова ещё не выучены
        if (cursor.getCount() != 0) {
            cursor.moveToFirst();

            word = findViewById(R.id.test_word);
            translate = findViewById(R.id.test_translate);
            button = findViewById(R.id.test_button);

            //так как теперь есть что учить, то прячем изначальный текст,
            // а всё остальное - показываем
            findViewById(R.id.test_1st_text).setVisibility(View.INVISIBLE);
            word.setVisibility(View.VISIBLE);
            translate.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);

            //выводим на экран слово, которое надо перевести
            word.setText(cursor.getString(1));

            //анимация для слова, которое введено некорректно
            incorrect_enter = AnimationUtils.loadAnimation(this, R.anim.anim_incorrect_enter);
            //анимация для неверного ответа
            anim_incorrect = AnimationUtils.loadAnimation(this, R.anim.anim_disappear);
            //анимация для правильного ответа
            anim_correct = AnimationUtils.loadAnimation(this, R.anim.anim_appear);
            //добавляем к анимации Listener, чтобы она была последовательной
            addListeners();
        }
    }

    private void addListeners() {

        //добавляем для кнопки переход в главное меню
        findViewById(R.id.back_button_test).setOnClickListener(this);

        //добавляем проверку и переход на следующее слово
        button.setOnClickListener(this);

        //добавляем Listener для неправильного ответа
        anim_incorrect.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // показываем, что слово введено неверно
                translate.setTextColor(getResources().getColor(R.color.colorAccent));
                translate.setEnabled(false);
                translate.getBackground().setColorFilter(getResources().
                        getColor(R.color.lightBlue), PorterDuff.Mode.SRC_IN);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                //показываем ответ
                translate.startAnimation(anim_correct);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        //добавляем Listener для правильного ответа
        anim_correct.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                translate.setEnabled(false);
                translate.setTextColor(getResources().getColor(R.color.green));
                translate.getBackground().setColorFilter(getResources()
                        .getColor(R.color.lightBlue), PorterDuff.Mode.SRC_IN);
                translate.setText(rightAnswer);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                translate.setTextColor(getResources().getColor(R.color.darkBlue));
                translate.getBackground().setColorFilter(getResources()
                        .getColor(R.color.colorAccent), PorterDuff.Mode.SRC_IN);
                button.setVisibility(View.VISIBLE);
                translate.setEnabled(true);
                showNextWord();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.test_button) {

            //если слово не введено, то подключаем анимацию слова, введённого некорректно
            if (translate.getText().toString().matches(" *")) {
                translate.setText("");
                translate.startAnimation(incorrect_enter);

            } else {
                rightAnswer = cursor.getString(2);
                button.setVisibility(View.INVISIBLE);
                // если перевод слова введён верно, то добавляем его в выученные
                if (String.valueOf(translate.getText()).toLowerCase().equals(rightAnswer)) {
                    database.changeLearned(cursor.getInt(0), cursor.getString(1),
                            cursor.getString(2), "yes");
                    // показываем анимацию, для которой слово введено верно
                    translate.startAnimation(anim_correct);
                } else {
                    //пользователь ответил неверно - пусть повторяет слово,
                    // показываем анимацию для слова, введённого неверно
                    translate.startAnimation(anim_incorrect);
                }
            }

        } else if (v.getId() == R.id.back_button_test) {
            //переход в главное меню
            finish();
        }
    }

    /**
     * Если неизученные слова есть, то показываем следующее слово,
     * для которого нужно ввести перевод. А если их нет - пишем, что они закончились.
     */
    private void showNextWord() {
        if (cursor.getPosition() != cursor.getCount() - 1) {
            cursor.moveToNext();
            word.setText(cursor.getString(1));
            translate.setText("");
        } else {
            findViewById(R.id.end).setVisibility(View.VISIBLE);
            translate.setVisibility(View.INVISIBLE);
            word.setVisibility(View.INVISIBLE);
            button.setVisibility(View.INVISIBLE);
        }
    }
}

