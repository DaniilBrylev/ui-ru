package com.example.learningplatform.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "quizzes")
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", unique = true)
    private Module module;

    @Column(nullable = false)
    private String title;

    private Integer timeLimit;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderColumn(name = "question_order")
    private List<Question> questions = new ArrayList<>();

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<QuizSubmission> submissions = new ArrayList<>();

    public void addQuestion(Question question) {
        if (question == null) {
            return;
        }
        questions.add(question);
        question.setQuiz(this);
    }

    public void removeQuestion(Question question) {
        if (question == null) {
            return;
        }
        questions.remove(question);
        if (question.getQuiz() == this) {
            question.setQuiz(null);
        }
    }

    public void addSubmission(QuizSubmission submission) {
        if (submission == null) {
            return;
        }
        submissions.add(submission);
        submission.setQuiz(this);
    }

    public void removeSubmission(QuizSubmission submission) {
        if (submission == null) {
            return;
        }
        submissions.remove(submission);
        if (submission.getQuiz() == this) {
            submission.setQuiz(null);
        }
    }
}
