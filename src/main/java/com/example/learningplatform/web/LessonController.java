package com.example.learningplatform.web;

import com.example.learningplatform.dto.AssignmentResponse;
import com.example.learningplatform.dto.CreateAssignmentRequest;
import com.example.learningplatform.service.AssignmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/lessons")
public class LessonController {
    private final AssignmentService assignmentService;

    public LessonController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping("/{id}/assignments")
    public ResponseEntity<AssignmentResponse> createAssignment(@PathVariable Long id,
                                                               @Valid @RequestBody CreateAssignmentRequest request) {
        AssignmentResponse response = assignmentService.createAssignment(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
