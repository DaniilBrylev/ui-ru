package com.example.learningplatform.service;

import com.example.learningplatform.dto.EnrollmentResponse;
import com.example.learningplatform.entity.Course;
import com.example.learningplatform.entity.Enrollment;
import com.example.learningplatform.entity.EnrollmentStatus;
import com.example.learningplatform.entity.User;
import com.example.learningplatform.entity.UserRole;
import com.example.learningplatform.exception.BadRequestException;
import com.example.learningplatform.exception.ConflictException;
import com.example.learningplatform.exception.NotFoundException;
import com.example.learningplatform.repository.CourseRepository;
import com.example.learningplatform.repository.EnrollmentRepository;
import com.example.learningplatform.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EnrollmentService {
    private static final Logger log = LoggerFactory.getLogger(EnrollmentService.class);

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                             CourseRepository courseRepository,
                             UserRepository userRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public EnrollmentResponse enrollStudent(Long courseId, Long studentId) {
        if (enrollmentRepository.existsByStudentIdAndCourseId(studentId, courseId)) {
            throw new ConflictException("Student already enrolled in course");
        }
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found: " + courseId));
        User student = userRepository.findById(studentId)
                .orElseThrow(() -> new NotFoundException("Student not found: " + studentId));
        if (student.getRole() != UserRole.STUDENT) {
            throw new BadRequestException("User is not a student");
        }

        Enrollment enrollment = new Enrollment();
        enrollment.setCourse(course);
        enrollment.setStudent(student);
        enrollment.setEnrollDate(LocalDate.now());
        enrollment.setStatus(EnrollmentStatus.ACTIVE);
        course.addEnrollment(enrollment);
        student.addEnrollment(enrollment);

        Enrollment saved = enrollmentRepository.save(enrollment);
        log.info("Enrolled student id={} to course id={}", studentId, courseId);
        return toEnrollmentResponse(saved);
    }

    @Transactional
    public void unenrollStudent(Long courseId, Long studentId) {
        Enrollment enrollment = enrollmentRepository.findByStudentIdAndCourseId(studentId, courseId)
                .orElseThrow(() -> new NotFoundException("Enrollment not found for student " + studentId + " and course " + courseId));
        Course course = enrollment.getCourse();
        User student = enrollment.getStudent();
        if (course != null) {
            course.removeEnrollment(enrollment);
        }
        if (student != null) {
            student.removeEnrollment(enrollment);
        }
        enrollmentRepository.delete(enrollment);
        log.info("Unenrolled student id={} from course id={}", studentId, courseId);
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> listStudentCourses(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId).stream()
                .map(this::toEnrollmentResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<EnrollmentResponse> listCourseStudents(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId).stream()
                .map(this::toEnrollmentResponse)
                .toList();
    }

    private EnrollmentResponse toEnrollmentResponse(Enrollment enrollment) {
        return new EnrollmentResponse(
                enrollment.getId(),
                enrollment.getStudent() != null ? enrollment.getStudent().getId() : null,
                enrollment.getCourse() != null ? enrollment.getCourse().getId() : null,
                enrollment.getEnrollDate(),
                enrollment.getStatus().name()
        );
    }
}
