package com.example.learningplatform.web;

import com.example.learningplatform.dto.QuizSubmissionResponse;
import com.example.learningplatform.service.QuizService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final QuizService quizService;

    public UserController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/{id}/quiz-results")
    public List<QuizSubmissionResponse> getQuizResults(@PathVariable Long id) {
        return quizService.listStudentQuizResults(id);
    }
}
