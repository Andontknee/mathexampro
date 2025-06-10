package com.example.mathexampro;

import java.util.List;

public class Question {
    private String questionText;
    private String[] options;
    private int correctAnswerIndex; // 0-based index
    private String explanation;

    public Question(String questionText, String[] options, int correctAnswerIndex, String explanation) {
        this.questionText = questionText;
        this.options = options;
        this.correctAnswerIndex = correctAnswerIndex;
        this.explanation = explanation;
    }

    public String getQuestionText() {
        return questionText;
    }

    public String[] getOptions() {
        return options;
    }

    public int getCorrectAnswerIndex() {
        return correctAnswerIndex;
    }

    public String getExplanation() {
        return explanation;
    }

    // Convert List<Question> to String[] of question texts
    public static String[] listToStringArray(List<Question> questions) {
        String[] result = new String[questions.size()];
        for (int i = 0; i < questions.size(); i++) {
            result[i] = questions.get(i).getQuestionText();
        }
        return result;
    }

    // Get options for all questions as String[][]
    public static String[][] optionsListToStringArrays(List<Question> questions) {
        String[][] result = new String[questions.size()][];
        for (int i = 0; i < questions.size(); i++) {
            result[i] = questions.get(i).getOptions();
        }
        return result;
    }

    // Get explanations for all questions
    public static String[] explanationsListToStringArray(List<Question> questions) {
        String[] result = new String[questions.size()];
        for (int i = 0; i < questions.size(); i++) {
            result[i] = questions.get(i).getExplanation();
        }
        return result;
    }

    // Get correct indices for all questions
    public static int[] correctIndicesToArray(List<Question> questions) {
        int[] result = new int[questions.size()];
        for (int i = 0; i < questions.size(); i++) {
            result[i] = questions.get(i).getCorrectAnswerIndex();
        }
        return result;
    }
}