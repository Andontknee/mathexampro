package com.example.mathexampro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionBank {
    private List<Question> allQuestions;

    public QuestionBank() {
        allQuestions = new ArrayList<>();
        populateQuestions();
    }

    private void populateQuestions() {
        allQuestions.add(new Question(
                "What is the sum of the infinite series: 1 − ½ + ¼ − ⅛ + ...?",
                new String[]{"A) 1", "B) 0", "C) 2", "D) Does not converge"},
                0,
                "The sum converges to 2/3."
        ));
        allQuestions.add(new Question(
                "Evaluate the limit: lim(x → 0)[sin(3x)/ x]",
                new String[]{"A) 0", "B) 1", "C) 3", "D) Undefined"},
                2,
                "As x approaches 0, sin(3x)/x approaches 3."
        ));
        allQuestions.add(new Question(
                "How many integers between 1 and 1000 are divisible by neither 2 nor 5?",
                new String[]{"A) 400", "B) 500", "C) 300", "D) 600"},
                2,
                "Total = 1000 - (500+200-100) = 400"
        ));
        allQuestions.add(new Question(
                "Find the determinant of the matrix: [[2,-1],[4, 3]]",
                new String[]{"A) 10", "B) -10", "C) 11", "D) 5"},
                0,
                "Determinant = (2×3) - (-1×4) = 6 + 4 = 10"
        ));
        allQuestions.add(new Question(
                "If P(A)= 0.6, P(B)= 0.5, and P(A ∩ B)= 0.3, what is P(A ∪ B)?",
                new String[]{"A) 0.8", "B) 1.1", "C) 0.9", "D) 0.7"},
                2,
                "P(A ∪ B) = P(A) + P(B) - P(A ∩ B) = 0.6 + 0.5 - 0.3 = 0.8"
        ));
        allQuestions.add(new Question(
                "What is the smallest positive integer x such that: 3x ≡ 1(mod 7)",
                new String[]{"A) 1", "B) 2", "C) 3", "D) 5"},
                3,
                "3×5=15 ≡1 mod7"
        ));
        allQuestions.add(new Question(
                "Solve the equation: log₂(x² − 1)= 3",
                new String[]{"A) x= 4", "B) x= ±3", "C) x= 3", "D) x= 2"},
                1,
                "log₂(x²−1)=3 → x²−1=8 → x²=9 → x=±3"
        ));
    }

    public List<Question> getRandomQuestions(int count) {
        List<Question> copy = new ArrayList<>(allQuestions);
        Collections.shuffle(copy);
        return copy.subList(0, count);
    }
}