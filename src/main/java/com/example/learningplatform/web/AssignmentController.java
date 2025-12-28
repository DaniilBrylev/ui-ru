package com.example.learningplatform.web;

import com.example.learningplatform.dto.SubmissionResponse;
import com.example.learningplatform.dto.SubmitAssignmentRequest;
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
@RequestMapping("/api/assignments")
public class AssignmentController {
    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PostMapping("/{id}/submit")
    public ResponseEntity<SubmissionResponse> submitAssignment(@PathVariable Long id,
                                                               @Valid @RequestBody SubmitAssignmentRequest request) {
        SubmissionResponse response = assignmentService.submitAssignment(id, request.studentId(), request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
