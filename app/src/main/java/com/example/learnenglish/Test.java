package com.example.learnenglish;

import android.app.Activity;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class Test extends Activity implements View.OnClickListener {

    Animation incorrect_enter;
    Animation anim_incorrect;
    double countOfRightAnswers;
    Animation anim_correct;
    String kindOfWords;
    String rightAnswer;
    EditText translate;
    Database database;
    TextView word;
    Button button;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        kindOfWords = preferences.getString("newWords", "almost");

        // получаем слова, знание которых нужно проверить у пользователя
        database = new Database(this);
        cursor = database.getKindWords(kindOfWords);

        assert kindOfWords != null;
        if (cursor.getCount() != 0) {
            startTest();
        }
        countOfRightAnswers = 0;
    }

    // начинаем тест
    private void startTest() {

        //добавляем для кнопки переход в главное меню
        findViewById(R.id.back_button_test).setOnClickListener(this);

        cursor.moveToFirst();

        word = findViewById(R.id.test_word);
        translate = findViewById(R.id.test_translate);
        button = findViewById(R.id.test_button);

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

    private void addListeners() {

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
                translate.setTextColor(getResources().getColor(R.color.darkGreen));
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
                    countOfRightAnswers++;
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
            findViewById(R.id.test_relative_layout)
                    .setBackgroundColor(getResources().getColor(R.color.white_blue));

            ImageView leftStar = findViewById(R.id.left_star);
            final ImageView centralStar = findViewById(R.id.central_star);
            final ImageView rightStar = findViewById(R.id.right_star);

            // показываем оценку
            showMark(leftStar, centralStar, rightStar);

            // подключаем появление звёзд
            startAnimation(leftStar, centralStar, rightStar);
        }
    }

    /**
     * Показываем результат теста и меняем изображения звёзд
     * в зависимости от результата.
     */
    private void showMark(ImageView leftStar, ImageView centralStar, ImageView rightStar) {

        // оценка в процентах
        double mark = countOfRightAnswers / cursor.getCount() * 100;

        TextView text = findViewById(R.id.mark);
        text.setVisibility(View.VISIBLE);

        StringBuilder result = new StringBuilder();
        result.append("Вы ответили правильно на \n");
        result.append((int) countOfRightAnswers).append(" из ")
                .append(cursor.getCount()).append(" слов").append("\n\n");

        if (mark < 60) {
            text.setText("Плохо");
            text.setTextColor(getResources().getColor(R.color.colorAccent));
            result.append("Как так?\nВы определённо можете лучше!");

        } else if (mark < 80) {
            text.setText("Удовлетворительно");
            text.setTextColor(getResources().getColor(R.color.orange));
            result.append("Что ж, неплохо, но можно и лучше." +
                    " В следующий раз постарайтесь, пожалуйста! ");
            leftStar.setImageResource(R.drawable.star_orange);
            centralStar.setImageResource(R.drawable.star_orange);

        } else if (mark < 90) {
            text.setText("Хорошо");
            text.setTextColor(getResources().getColor(R.color.yellow));
            result.append("Неплохой результат, " +
                    "вам не хватило совсем чуть-чуть до отличной оценки.");
            leftStar.setImageResource(R.drawable.star_yellow);
            centralStar.setImageResource(R.drawable.star_yellow);

        } else {
            text.setText("Отлично!");
            text.setTextColor(getResources().getColor(R.color.oliveGreen));
            result.append("Поздравляем, вы успешно справились с заданием!");
            leftStar.setImageResource(R.drawable.green_star);
            centralStar.setImageResource(R.drawable.green_star);
            rightStar.setImageResource(R.drawable.green_star);
        }

        translate.setVisibility(View.INVISIBLE);
        word.setVisibility(View.INVISIBLE);
        button.setVisibility(View.INVISIBLE);

        TextView end = findViewById(R.id.end);
        end.setVisibility(View.VISIBLE);
        end.setText(result);
    }

    /**
     * Последовательное появление звёзд, которые
     * показывают результат прохождения теста
     */
    private void startAnimation(final ImageView leftStar, final ImageView centralStar,
                                final ImageView rightStar) {

        // Анимация появления первой звезды и начало вызов средней
        Animation startLeft = AnimationUtils
                .loadAnimation(this, R.anim.anim_fast_appear_and_jump_down);

        // Анимация вызова средней звезды
        final Animation startCentral = AnimationUtils
                .loadAnimation(this, R.anim.anim_fast_appear_and_jump_down);

        // Анимация вызова правой звезды
        final Animation startRight = AnimationUtils
                .loadAnimation(this, R.anim.anim_fast_appear_and_jump_down);

        startLeft.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                leftStar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                centralStar.setVisibility(View.VISIBLE);
                centralStar.startAnimation(startCentral);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        startCentral.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rightStar.setVisibility(View.VISIBLE);
                rightStar.startAnimation(startRight);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        startRight.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                buttonsAfterTest(leftStar, centralStar, rightStar);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });

        // начинаем череду анимаций
        leftStar.startAnimation(startLeft);
    }

    private void buttonsAfterTest(final ImageView leftStar, final ImageView centralStar,
                                  final ImageView rightStar) {

        Animation appear =
                AnimationUtils.loadAnimation(this, R.anim.anim_fast_appear_and_jump_up);

        final Button goTOMenu = findViewById(R.id.menu);
        goTOMenu.setVisibility(View.VISIBLE);
        goTOMenu.startAnimation(appear);

        // идём в главное меню
        goTOMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences preferences =
                        PreferenceManager.getDefaultSharedPreferences(Test.this);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean("toMenu", true);
                editor.apply();
                finish();
            }
        });

        final Button repeatTest = findViewById(R.id.repeat);

        database = new Database(this);
        cursor = database.getKindWords(kindOfWords);

        if (cursor.getCount() != 0) {
            repeatTest.setVisibility(View.VISIBLE);
            repeatTest.startAnimation(appear);

            // повторяем тест
            repeatTest.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    translate.setText("");

                    goTOMenu.setVisibility(View.INVISIBLE);
                    repeatTest.setVisibility(View.INVISIBLE);
                    leftStar.setVisibility(View.INVISIBLE);
                    centralStar.setVisibility(View.INVISIBLE);
                    rightStar.setVisibility(View.INVISIBLE);
                    findViewById(R.id.mark).setVisibility(View.INVISIBLE);
                    findViewById(R.id.end).setVisibility(View.INVISIBLE);

                    countOfRightAnswers = 0;
                    findViewById(R.id.test_relative_layout)
                            .setBackgroundColor(getResources().getColor(R.color.lightBlue));
                    startTest();
                }
            });
        }
    }
}

