package com.example.mathexampro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class TimedModeActivity extends Activity {

    private static final String TAG = "TimedModeActivity";

    private TextView questionTextView;
    private TextView timerTextView;
    private TextView skipCounterTextView;
    private RadioGroup optionsRadioGroup;
    private Button prevButton;
    private Button nextButton;
    private Button skipButton;
    private Button submitButton; // New
    private Button resetButton; // New

    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int skipsLeft = 2;
    private int[] userAnswers;

    private CountDownTimer countDownTimer;
    private boolean isTimerRunning = false;
    private long timeLeftInMillis = 10 * 60 * 1000; // 10 minutes

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timed_mode);
        Log.d(TAG, "TimedModeActivity started");

        // Initialize views
        questionTextView = findViewById(R.id.question_text_view_timed);
        timerTextView = findViewById(R.id.timer_text_view);
        skipCounterTextView = findViewById(R.id.skip_counter_timed);
        optionsRadioGroup = findViewById(R.id.options_group_timed);
        prevButton = findViewById(R.id.prev_button_timed);
        nextButton = findViewById(R.id.next_button_timed);
        skipButton = findViewById(R.id.skip_button_timed);
        submitButton = findViewById(R.id.submit_button_timed); // New
        resetButton = findViewById(R.id.reset_button_timed); // New

        // Load question bank
        QuestionBank questionBank = new QuestionBank();
        questionList = questionBank.getRandomQuestions(7);

        // Initialize tracking arrays
        int questionCount = questionList.size();
        userAnswers = new int[questionCount];
        for (int i = 0; i < questionCount; i++) {
            userAnswers[i] = -1;
        }

        updateSkipCounter();
        displayCurrentQuestion();
        startTimer();

        // Button listeners
        prevButton.setOnClickListener(v -> showPreviousQuestion());
        nextButton.setOnClickListener(v -> showNextQuestion());
        skipButton.setOnClickListener(v -> skipCurrentQuestion());
        submitButton.setOnClickListener(v -> openResultSummary()); // New
        resetButton.setOnClickListener(v -> resetExam()); // New

        // Save selected answer
        optionsRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId != -1) {
                int selectedIndex = group.indexOfChild(findViewById(checkedId));
                userAnswers[currentQuestionIndex] = selectedIndex;
            }
        });
    }

    private void displayCurrentQuestion() {
        Question current = questionList.get(currentQuestionIndex);
        questionTextView.setText(current.getQuestionText());

        optionsRadioGroup.removeAllViews();
        String[] options = current.getOptions();
        for (String option : options) {
            RadioButton rb = new RadioButton(this);
            rb.setText(option);
            rb.setId(View.generateViewId());
            optionsRadioGroup.addView(rb);
        }

        if (userAnswers[currentQuestionIndex] != -1) {
            RadioButton selected = (RadioButton) optionsRadioGroup.getChildAt(userAnswers[currentQuestionIndex]);
            if (selected != null) {
                selected.setChecked(true);
            }
        }

        prevButton.setEnabled(currentQuestionIndex > 0);
        nextButton.setEnabled(currentQuestionIndex < questionList.size() - 1);
        skipButton.setEnabled(skipsLeft > 0);
    }

    private void updateSkipCounter() {
        skipCounterTextView.setText("Skips left: " + skipsLeft);
        skipButton.setEnabled(skipsLeft > 0);
    }

    private void skipCurrentQuestion() {
        if (skipsLeft <= 0) {
            Toast.makeText(this, "No skips remaining", Toast.LENGTH_SHORT).show();
            return;
        }

        skipsLeft--;
        userAnswers[currentQuestionIndex] = -1;
        updateSkipCounter();

        if (currentQuestionIndex < questionList.size() - 1) {
            currentQuestionIndex++;
            displayCurrentQuestion();
        }
    }

    private void showPreviousQuestion() {
        if (currentQuestionIndex > 0) {
            currentQuestionIndex--;
            displayCurrentQuestion();
        }
    }

    private void showNextQuestion() {
        if (currentQuestionIndex < questionList.size() - 1) {
            currentQuestionIndex++;
            displayCurrentQuestion();
        }
    }

    private void resetExam() {
        QuestionBank questionBank = new QuestionBank();
        questionList = questionBank.getRandomQuestions(7);

        currentQuestionIndex = 0;
        skipsLeft = 2;

        userAnswers = new int[questionList.size()];
        for (int i = 0; i < userAnswers.length; i++) {
            userAnswers[i] = -1;
        }

        updateSkipCounter();
        displayCurrentQuestion();

        Toast.makeText(this, "Exam has been reset", Toast.LENGTH_SHORT).show();
    }

    private void openResultSummary() {
        countDownTimer.cancel(); // Stop the timer when submitting

        int answered = 0;
        int skipped = 0;

        for (int answer : userAnswers) {
            if (answer != -1) {
                answered++;
            } else {
                skipped++;
            }
        }

        int score = 0;
        for (int i = 0; i < userAnswers.length; i++) {
            if (userAnswers[i] != -1 && userAnswers[i] == questionList.get(i).getCorrectAnswerIndex()) {
                score++;
            }
        }

        double percentage = ((double) score / questionList.size()) * 100;

        Intent intent = new Intent(this, ResultActivity.class);
        intent.putExtra("total", questionList.size());
        intent.putExtra("answered", answered);
        intent.putExtra("skipped", skipped);
        intent.putExtra("correct", score);
        intent.putExtra("score", percentage);

        // Pass extra data for review mode
        intent.putExtra("questions", Question.listToStringArray(questionList));
        intent.putExtra("options", Question.optionsListToStringArrays(questionList));
        intent.putExtra("userAnswers", userAnswers);
        intent.putExtra("correctIndices", Question.correctIndicesToArray(questionList));
        intent.putExtra("explanations", Question.explanationsListToStringArray(questionList));

        startActivity(intent);
        finish(); // Close TimedModeActivity
    }

    private void startTimer() {
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                updateTimerText();
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                Toast.makeText(TimedModeActivity.this, "Time's up!", Toast.LENGTH_LONG).show();
                openResultSummary();
            }
        }.start();

        isTimerRunning = true;
    }

    private void updateTimerText() {
        int minutes = (int) (timeLeftInMillis / 1000) / 60;
        int seconds = (int) (timeLeftInMillis / 1000) % 60;
        timerTextView.setText(String.format("Time Left: %02d:%02d", minutes, seconds));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}