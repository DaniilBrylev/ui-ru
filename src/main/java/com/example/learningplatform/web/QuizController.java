package com.example.learningplatform.web;

import com.example.learningplatform.dto.CreateQuestionRequest;
import com.example.learningplatform.dto.QuestionResponse;
import com.example.learningplatform.dto.QuizSubmissionResponse;
import com.example.learningplatform.dto.TakeQuizRequest;
import com.example.learningplatform.service.QuizService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/quizzes")
public class QuizController {
    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @PostMapping("/{id}/questions")
    public ResponseEntity<QuestionResponse> addQuestion(@PathVariable Long id,
                                                        @Valid @RequestBody CreateQuestionRequest request) {
        QuestionResponse response = quizService.addQuestion(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/take")
    public ResponseEntity<QuizSubmissionResponse> takeQuiz(@PathVariable Long id,
                                                           @Valid @RequestBody TakeQuizRequest request) {
        QuizSubmissionResponse response = quizService.takeQuiz(id, request.studentId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
