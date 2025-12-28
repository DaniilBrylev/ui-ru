package com.example.learningplatform.web;

import com.example.learningplatform.dto.CreateLessonRequest;
import com.example.learningplatform.dto.CreateQuizRequest;
import com.example.learningplatform.dto.LessonResponse;
import com.example.learningplatform.dto.QuizResponse;
import com.example.learningplatform.service.CourseService;
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
@RequestMapping("/api/modules")
public class ModuleController {
    private final CourseService courseService;
    private final QuizService quizService;

    public ModuleController(CourseService courseService, QuizService quizService) {
        this.courseService = courseService;
        this.quizService = quizService;
    }

    @PostMapping("/{id}/lessons")
    public ResponseEntity<LessonResponse> addLesson(@PathVariable Long id,
                                                    @Valid @RequestBody CreateLessonRequest request) {
        LessonResponse response = courseService.addLesson(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/quiz")
    public ResponseEntity<QuizResponse> createQuiz(@PathVariable Long id,
                                                   @Valid @RequestBody CreateQuizRequest request) {
        QuizResponse response = quizService.createQuizForModule(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
