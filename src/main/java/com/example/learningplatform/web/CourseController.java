package com.example.learningplatform.web;

import com.example.learningplatform.dto.CourseResponse;
import com.example.learningplatform.dto.CourseStructureResponse;
import com.example.learningplatform.dto.CreateCourseRequest;
import com.example.learningplatform.dto.CreateModuleRequest;
import com.example.learningplatform.dto.EnrollmentResponse;
import com.example.learningplatform.dto.ModuleResponse;
import com.example.learningplatform.service.CourseService;
import com.example.learningplatform.service.EnrollmentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final EnrollmentService enrollmentService;

    public CourseController(CourseService courseService, EnrollmentService enrollmentService) {
        this.courseService = courseService;
        this.enrollmentService = enrollmentService;
    }

    @PostMapping
    public ResponseEntity<CourseResponse> createCourse(@Valid @RequestBody CreateCourseRequest request) {
        CourseResponse response = courseService.createCourse(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<CourseResponse> getAllCourses() {
        return courseService.getAllCourses();
    }

    @GetMapping("/{id}")
    public CourseResponse getCourse(@PathVariable Long id) {
        return courseService.getCourse(id);
    }

    @GetMapping("/{id}/structure")
    public CourseStructureResponse getCourseStructure(@PathVariable Long id) {
        return courseService.getCourseStructure(id);
    }

    @PostMapping("/{id}/modules")
    public ResponseEntity<ModuleResponse> addModule(
            @PathVariable Long id,
            @Valid @RequestBody CreateModuleRequest request
    ) {
        ModuleResponse response = courseService.addModule(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/enroll/{studentId}")
    public ResponseEntity<EnrollmentResponse> enrollStudent(
            @PathVariable Long id,
            @PathVariable Long studentId
    ) {
        EnrollmentResponse response = enrollmentService.enrollStudent(id, studentId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @DeleteMapping("/{id}/enroll/{studentId}")
    public ResponseEntity<Void> unenrollStudent(
            @PathVariable Long id,
            @PathVariable Long studentId
    ) {
        enrollmentService.unenrollStudent(id, studentId);
        return ResponseEntity.noContent().build();
    }
}
