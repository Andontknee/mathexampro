package com.example.mathexampro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ResultActivity extends Activity {

    private boolean isReviewVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        TextView resultSummary = findViewById(R.id.result_summary_text);
        LinearLayout reviewContainer = findViewById(R.id.review_container);
        LinearLayout reviewWrapper = findViewById(R.id.review_container_wrapper);
        Button toggleReviewButton = findViewById(R.id.show_review_button);
        Button retakeButton = findViewById(R.id.retake_button);
        Button exitButton = findViewById(R.id.exit_button);
        Button mainMenuButton = findViewById(R.id.main_menu_button);

        Intent intent = getIntent();

        int total = intent.getIntExtra("total", 0);
        int answered = intent.getIntExtra("answered", 0);
        int skipped = intent.getIntExtra("skipped", 0);
        int correct = intent.getIntExtra("correct", 0);
        double score = intent.getDoubleExtra("score", 0.0);
        String[] questions = intent.getStringArrayExtra("questions");
        String[][] options = (String[][]) intent.getSerializableExtra("options");
        int[] userAnswers = intent.getIntArrayExtra("userAnswers");
        int[] correctIndices = intent.getIntArrayExtra("correctIndices");
        String[] explanations = intent.getStringArrayExtra("explanations");

        // Display Summary
        String summary = String.format(
                "Total Questions: %d\n" +
                        "Questions Answered: %d\n" +
                        "Questions Skipped: %d\n" +
                        "Correct Answers: %d\n" +
                        "Final Score: %.1f%%",
                total, answered, skipped, correct, score
        );
        resultSummary.setText(summary);

        // Populate Review Section
        for (int i = 0; i < questions.length; i++) {
            TextView qView = new TextView(this);
            TextView userView = new TextView(this);
            TextView correctView = new TextView(this);
            TextView explanationView = new TextView(this);

            qView.setText("Q" + (i + 1) + ": " + questions[i]);
            qView.setTextSize(16);
            qView.setPadding(0, 16, 0, 4);

            if (userAnswers[i] == -1) {
                userView.setText("Your Answer: Skipped");
            } else {
                String userAnswer = options[i][userAnswers[i]];
                boolean isCorrect = userAnswers[i] == correctIndices[i];
                userView.setText("Your Answer: " + userAnswer + (isCorrect ? " ✔" : " ✘"));
            }

            correctView.setText("Correct Answer: " + options[i][correctIndices[i]]);
            explanationView.setText("Explanation: " + explanations[i]);

            userView.setTextSize(14);
            correctView.setTextSize(14);
            explanationView.setTextSize(14);

            userView.setPadding(0, 0, 0, 4);
            correctView.setPadding(0, 0, 0, 4);
            explanationView.setPadding(0, 0, 0, 16);

            reviewContainer.addView(qView);
            reviewContainer.addView(userView);
            reviewContainer.addView(correctView);
            reviewContainer.addView(explanationView);
        }

        // Toggle Review Visibility
        toggleReviewButton.setOnClickListener(v -> {
            if (isReviewVisible) {
                reviewWrapper.setVisibility(View.GONE);
                toggleReviewButton.setText("Show Question Review");
            } else {
                reviewWrapper.setVisibility(View.VISIBLE);
                toggleReviewButton.setText("Hide Question Review");
            }
            isReviewVisible = !isReviewVisible;
        });

        // Retake Button
        retakeButton.setOnClickListener(v -> {
            finish(); // Close ResultActivity
            startActivity(new Intent(ResultActivity.this, MainActivity.class));
        });

        // Exit Button
        exitButton.setOnClickListener(v -> finishAffinity());

        // Return to Main Menu Button
        mainMenuButton.setOnClickListener(v -> {
            finish(); // Close ResultActivity
            startActivity(new Intent(ResultActivity.this, MainMenuActivity.class));
        });
    }
}