package com.example.learningplatform.service;

import com.example.learningplatform.dto.*;
import com.example.learningplatform.entity.Assignment;
import com.example.learningplatform.entity.Category;
import com.example.learningplatform.entity.Course;
import com.example.learningplatform.entity.Lesson;
import com.example.learningplatform.entity.Module;
import com.example.learningplatform.entity.Tag;
import com.example.learningplatform.entity.User;
import com.example.learningplatform.entity.UserRole;
import com.example.learningplatform.exception.BadRequestException;
import com.example.learningplatform.exception.NotFoundException;
import com.example.learningplatform.repository.CategoryRepository;
import com.example.learningplatform.repository.CourseRepository;
import com.example.learningplatform.repository.LessonRepository;
import com.example.learningplatform.repository.ModuleRepository;
import com.example.learningplatform.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class CourseService {

    private static final Logger log = LoggerFactory.getLogger(CourseService.class);

    private final CourseRepository courseRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final ModuleRepository moduleRepository;
    private final LessonRepository lessonRepository;

    public CourseService(
            CourseRepository courseRepository,
            CategoryRepository categoryRepository,
            UserRepository userRepository,
            ModuleRepository moduleRepository,
            LessonRepository lessonRepository
    ) {
        this.courseRepository = courseRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
        this.moduleRepository = moduleRepository;
        this.lessonRepository = lessonRepository;
    }

    /* ========================= CREATE ========================= */

    @Transactional
    public CourseResponse createCourse(CreateCourseRequest request) {
        Category category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new NotFoundException("Category not found: " + request.categoryId()));

        User teacher = userRepository.findById(request.teacherId())
                .orElseThrow(() -> new NotFoundException("Teacher not found: " + request.teacherId()));

        if (teacher.getRole() != UserRole.TEACHER && teacher.getRole() != UserRole.ADMIN) {
            throw new BadRequestException("User is not a teacher");
        }

        Course course = new Course();
        course.setTitle(request.title());
        course.setDescription(request.description());
        course.setDuration(request.duration());
        course.setStartDate(request.startDate());
        course.setCategory(category);
        course.setTeacher(teacher);

        Course saved = courseRepository.save(course);
        log.info("Created course id={} title={}", saved.getId(), saved.getTitle());

        return toCourseResponse(saved);
    }

    /* ========================= READ ========================= */

    @Transactional(readOnly = true)
    public CourseResponse getCourse(Long id) {
        Course course = courseRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Course not found: " + id));
        return toCourseResponse(course);
    }

    @Transactional(readOnly = true)
    public List<CourseResponse> getAllCourses() {
        return courseRepository.findAll().stream()
                .map(this::toCourseResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CourseStructureResponse getCourseStructure(Long id) {
        Course course = courseRepository.findWithStructureById(id)
                .orElseThrow(() -> new NotFoundException("Course not found: " + id));
        return toCourseStructureResponse(course);
    }

    /* ========================= MODULES ========================= */

    @Transactional
    public ModuleResponse addModule(Long courseId, CreateModuleRequest request) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new NotFoundException("Course not found: " + courseId));

        Module module = new Module();
        module.setTitle(request.title());
        module.setDescription(request.description());
        module.setOrderIndex(request.orderIndex());
        course.addModule(module);

        Module saved = moduleRepository.save(module);
        log.info("Added module id={} to course id={}", saved.getId(), courseId);

        return toModuleResponse(saved);
    }

    /* ========================= LESSONS ========================= */

    @Transactional
    public LessonResponse addLesson(Long moduleId, CreateLessonRequest request) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new NotFoundException("Module not found: " + moduleId));

        Lesson lesson = new Lesson();
        lesson.setTitle(request.title());
        lesson.setContent(request.content());
        lesson.setVideoUrl(request.videoUrl());
        module.addLesson(lesson);

        Lesson saved = lessonRepository.save(lesson);
        log.info("Added lesson id={} to module id={}", saved.getId(), moduleId);

        return toLessonResponse(saved);
    }

    /* ========================= MAPPERS ========================= */

    private CourseResponse toCourseResponse(Course course) {
        return new CourseResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getDuration(),
                course.getStartDate(),
                course.getCategory() != null ? course.getCategory().getId() : null,
                course.getTeacher() != null ? course.getTeacher().getId() : null,
                course.getTags().stream()
                        .map(Tag::getName)
                        .sorted()
                        .toList()
        );
    }

    private CourseStructureResponse toCourseStructureResponse(Course course) {
        List<ModuleResponse> modules = course.getModules().stream()
                .sorted(Comparator.comparing(Module::getOrderIndex))
                .map(this::toModuleResponseWithLessons)
                .toList();

        return new CourseStructureResponse(
                course.getId(),
                course.getTitle(),
                course.getDescription(),
                course.getDuration(),
                course.getStartDate(),
                course.getCategory() != null ? course.getCategory().getId() : null,
                course.getTeacher() != null ? course.getTeacher().getId() : null,
                course.getTags().stream().map(Tag::getName).sorted().toList(),
                modules
        );
    }

    private ModuleResponse toModuleResponse(Module module) {
        return new ModuleResponse(
                module.getId(),
                module.getTitle(),
                module.getDescription(),
                module.getOrderIndex(),
                List.of()
        );
    }

    private ModuleResponse toModuleResponseWithLessons(Module module) {
        List<LessonResponse> lessons = module.getLessons().stream()
                .map(this::toLessonResponseWithAssignments)
                .toList();

        return new ModuleResponse(
                module.getId(),
                module.getTitle(),
                module.getDescription(),
                module.getOrderIndex(),
                lessons
        );
    }

    private LessonResponse toLessonResponse(Lesson lesson) {
        return new LessonResponse(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getContent(),
                lesson.getVideoUrl(),
                List.of()
        );
    }

    private LessonResponse toLessonResponseWithAssignments(Lesson lesson) {
        List<AssignmentResponse> assignments = lesson.getAssignments().stream()
                .map(this::toAssignmentResponse)
                .toList();

        return new LessonResponse(
                lesson.getId(),
                lesson.getTitle(),
                lesson.getContent(),
                lesson.getVideoUrl(),
                assignments
        );
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
}
