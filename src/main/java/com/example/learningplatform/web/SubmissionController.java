package com.example.learningplatform.web;

import com.example.learningplatform.dto.GradeSubmissionRequest;
import com.example.learningplatform.dto.SubmissionResponse;
import com.example.learningplatform.service.AssignmentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {
    private final AssignmentService assignmentService;

    public SubmissionController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @PatchMapping("/{id}/grade")
    public SubmissionResponse gradeSubmission(@PathVariable Long id,
                                              @Valid @RequestBody GradeSubmissionRequest request) {
        return assignmentService.gradeSubmission(id, request);
    }
}
