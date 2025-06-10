package com.example.mathexampro;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends Activity {

    private static final String TAG = "MathExamPro";

    // UI Components
    private TextView questionTextView;
    private TextView skipCounterTextView;
    private RadioGroup optionsRadioGroup;
    private Button prevButton;
    private Button nextButton;
    private Button submitButton;
    private Button skipButton;
    private Button resetButton;

    // App Data
    private List<Question> questionList;
    private int currentQuestionIndex = 0;
    private int skipsLeft = 2;
    private int[] userAnswers; // -1 = skipped/not answered

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "MainActivity started");

        // Initialize views
        questionTextView = findViewById(R.id.question_text_view);
        skipCounterTextView = findViewById(R.id.skip_counter);
        optionsRadioGroup = findViewById(R.id.options_group);
        prevButton = findViewById(R.id.prev_button);
        nextButton = findViewById(R.id.next_button);
        submitButton = findViewById(R.id.submit_button);
        skipButton = findViewById(R.id.skip_button);
        resetButton = findViewById(R.id.reset_button);

        // Load question bank
        QuestionBank questionBank = new QuestionBank();
        questionList = questionBank.getRandomQuestions(7); // Get all 7 questions

        // Initialize tracking arrays
        int questionCount = questionList.size();
        userAnswers = new int[questionCount];
        for (int i = 0; i < questionCount; i++) {
            userAnswers[i] = -1; // Not answered yet
        }

        updateSkipCounter();
        displayCurrentQuestion();

        // Button Listeners
        prevButton.setOnClickListener(v -> showPreviousQuestion());
        nextButton.setOnClickListener(v -> showNextQuestion());
        submitButton.setOnClickListener(v -> handleFinalSubmission());
        skipButton.setOnClickListener(v -> skipCurrentQuestion());
        resetButton.setOnClickListener(v -> resetExam());

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

        // Clear existing options
        optionsRadioGroup.removeAllViews();
        String[] options = current.getOptions();
        for (String option : options) {
            RadioButton rb = new RadioButton(this);
            rb.setText(option);
            rb.setId(View.generateViewId());
            optionsRadioGroup.addView(rb);
        }

        // Restore previous answer if any
        if (userAnswers[currentQuestionIndex] != -1) {
            RadioButton selected = (RadioButton) optionsRadioGroup.getChildAt(userAnswers[currentQuestionIndex]);
            if (selected != null) {
                selected.setChecked(true);
            }
        }

        // Update navigation buttons
        prevButton.setEnabled(currentQuestionIndex > 0);
        nextButton.setEnabled(currentQuestionIndex < questionList.size() - 1);
        submitButton.setEnabled(currentQuestionIndex == questionList.size() - 1);
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
        userAnswers[currentQuestionIndex] = -1; // Mark as skipped
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

    private void handleFinalSubmission() {
        int unansweredCount = countUnansweredQuestions();

        if (unansweredCount > 0) {
            showUnansweredWarningDialog(unansweredCount);
        } else {
            openResultSummary();
        }
    }

    private int countUnansweredQuestions() {
        int count = 0;
        for (int answer : userAnswers) {
            if (answer == -1) {
                count++;
            }
        }
        return count;
    }

    private void showUnansweredWarningDialog(int unansweredCount) {
        new AlertDialog.Builder(this)
                .setTitle("Unanswered Questions")
                .setMessage("You still have " + unansweredCount + " unanswered question(s). Do you want to proceed with submission?")
                .setPositiveButton("Yes", (dialog, which) -> openResultSummary())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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
        finish(); // Optional: close MainActivity
    }
}