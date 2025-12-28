package com.example.learningplatform.service;

import com.example.learningplatform.dto.AssignmentResponse;
import com.example.learningplatform.dto.AssignmentSummaryResponse;
import com.example.learningplatform.dto.CreateAssignmentRequest;
import com.example.learningplatform.dto.GradeSubmissionRequest;
import com.example.learningplatform.dto.StudentSubmissionView;
import com.example.learningplatform.dto.SubmissionResponse;
import com.example.learningplatform.dto.SubmitAssignmentRequest;
import com.example.learningplatform.entity.Assignment;
import com.example.learningplatform.entity.Lesson;
import com.example.learningplatform.entity.Submission;
import com.example.learningplatform.entity.User;
import com.example.learningplatform.entity.UserRole;
import com.example.learningplatform.exception.BadRequestException;
import com.example.learningplatform.exception.ConflictException;
import com.example.learningplatform.exception.NotFoundException;
import com.example.learningplatform.repository.AssignmentRepository;
import com.example.learningplatform.repository.LessonRepository;
import com.example.learningplatform.repository.SubmissionRepository;
import com.example.learningplatform.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AssignmentService {
    private static final Logger log = LoggerFactory.getLogger(AssignmentService.class);

    private final LessonRepository lessonRepository;
    private final AssignmentRepository assignmentRepository;
    private final SubmissionRepository submissionRepository;
    private final UserRepository userRepository;

    public AssignmentService(LessonRepository lessonRepository,
                             AssignmentRepository assignmentRepository,
                             SubmissionRepository submissionRepository,
                             UserRepository userRepository) {
        this.lessonRepository = lessonRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public AssignmentResponse createAssignment(Long lessonId, CreateAssignmentRequest request) {
        Lesson lesson = lessonRepository.findById(lessonId)
                .orElseThrow(() -> new NotFoundException("Lesson not found: " + lessonId));

        Assignment assignment = new Assignment();
        assignment.setLesson(lesson);
        assignment.setTitle(request.title());
        assignment.setDescription(request.description());
        assignment.setDueDate(request.dueDate());
        assignment.setMaxScore(request.maxScore());
        lesson.addAssignment(assignment);

        Assignment saved = assignmentRepository.save(assignment);
        log.info("Created assignment id={} for lesson id={}", saved.getId(), lessonId);
        return toAssignmentResponse(saved);
    }

    @Transactional
    public SubmissionResponse submitAssignment(Long assignmentId, Long studentId, SubmitAssignmentRequest request) {
        if (submissionRepository.existsByStudentIdAndAssignmentId(studentId, assignmentId)) {
            throw new ConflictException("Submission already exists for this student and assignment");
        }
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found: " + assignmentId));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found: " + studentId));
        if (student.getRole() != UserRole.STUDENT) {
            throw new BadRequestException("User is not a student");
        }

        Submission submission = new Submission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setSubmittedAt(LocalDateTime.now());
        submission.setContent(request.content());
        assignment.addSubmission(submission);
        student.addSubmission(submission);

        Submission saved = submissionRepository.save(submission);
        log.info("Student id={} submitted assignment id={}", studentId, assignmentId);
        return toSubmissionResponse(saved);
    }

    @Transactional
    public SubmissionResponse gradeSubmission(Long submissionId, GradeSubmissionRequest request) {
        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new NotFoundException("Submission not found: " + submissionId));
        submission.setScore(request.score());
        submission.setFeedback(request.feedback());

        Submission saved = submissionRepository.save(submission);
        return toSubmissionResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<AssignmentSummaryResponse> listAssignments() {
        return assignmentRepository.findAll().stream()
                .map(this::toAssignmentSummary)
                .toList();
    }

    @Transactional(readOnly = true)
    public AssignmentSummaryResponse getAssignmentSummary(Long assignmentId) {
        Assignment assignment = assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new NotFoundException("Assignment not found: " + assignmentId));
        return toAssignmentSummary(assignment);
    }

    @Transactional(readOnly = true)
    public List<StudentSubmissionView> listStudentSubmissions(Long studentId) {
        return submissionRepository.findByStudentId(studentId).stream()
                .map(this::toStudentSubmissionView)
                .toList();
    }

    private AssignmentResponse toAssignmentResponse(Assignment assignment) {
        return new AssignmentResponse(
                assignment.getId(),
                assignment.getTitle(),
                assignment.getDescription(),
                assignment.getDueDate(),
                assignment.getMaxScore()
        );
    }

    private AssignmentSummaryResponse toAssignmentSummary(Assignment assignment) {
        return new AssignmentSummaryResponse(
                assignment.getId(),
                assignment.getTitle(),
                assignment.getDescription(),
                assignment.getDueDate(),
                assignment.getMaxScore(),
                assignment.getLesson() != null ? assignment.getLesson().getId() : null,
                assignment.getLesson() != null ? assignment.getLesson().getTitle() : null
        );
    }

    private SubmissionResponse toSubmissionResponse(Submission submission) {
        return new SubmissionResponse(
                submission.getId(),
                submission.getAssignment() != null ? submission.getAssignment().getId() : null,
                submission.getStudent() != null ? submission.getStudent().getId() : null,
                submission.getSubmittedAt(),
                submission.getContent(),
                submission.getScore(),
                submission.getFeedback()
        );
    }

    private StudentSubmissionView toStudentSubmissionView(Submission submission) {
        return new StudentSubmissionView(
                submission.getId(),
                submission.getAssignment() != null ? submission.getAssignment().getId() : null,
                submission.getAssignment() != null ? submission.getAssignment().getTitle() : null,
                submission.getScore(),
                submission.getSubmittedAt(),
                submission.getFeedback()
        );
    }
}
